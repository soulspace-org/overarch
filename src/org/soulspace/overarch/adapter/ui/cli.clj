(ns org.soulspace.overarch.adapter.ui.cli
  "Functions for the command line interface of overarch."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.pprint :as pp]
            [clojure.edn :as edn]
            [clojure.tools.cli :as cli]
            [nextjournal.beholder :as beholder]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.application.model-repository :as repo]
            [org.soulspace.overarch.application.export :as exp]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.application.template :as tmpl]
            ; require adapters here to register multimethods
            ; require dynamically?
            [org.soulspace.overarch.adapter.exports.json :as json]
            [org.soulspace.overarch.adapter.exports.structurizr :as structurizr]
            [org.soulspace.overarch.adapter.reader.model-reader :as reader]
            [org.soulspace.overarch.adapter.reader.input-model-reader :as imreader]
            [org.soulspace.overarch.adapter.reader.file-input-model-reader :as fimreader]
            [org.soulspace.overarch.adapter.render.graphviz :as graphviz]
            [org.soulspace.overarch.adapter.render.markdown :as markdown]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]
            [org.soulspace.overarch.adapter.render.plantuml.c4 :as c4]
            [org.soulspace.overarch.adapter.render.plantuml.uml :as uml]
            [org.soulspace.overarch.adapter.template.comb :as comb]
            [org.soulspace.overarch.application.render :as render])
  (:gen-class))

;;;
;;; CLI definition
;;;
(def appname "overarch")
(def description
  "Overarch CLI
   
   Reads your model and view specifications and renders or exports
   into the specified formats.

   For more information see https://github.com/soulspace-org/overarch")

(def cli-opts
  [["-m" "--model-dir PATH" "Models directory or path" :default "models"]
   ["-r" "--render-format FORMAT" "Render format (all, graphviz, markdown, plantuml)" :parse-fn keyword] ; :default :all :default-desc "all"]
   ["-R" "--render-dir DIRNAME" "Render directory" :default "export"]
   [nil  "--[no-]render-format-subdirs" "Use subdir per render format" :default true]
   ["-x" "--export-format FORMAT" "Export format (json, structurizr)" :parse-fn keyword]
   ["-X" "--export-dir DIRNAME" "Export directory" :default "export"]
   ["-w" "--watch" "Watch model dir for changes and trigger action" :default false]
   ["-s" "--select-elements CRITERIA" "Select and print model elements by criteria" :parse-fn edn/read-string]
   ["-S" "--select-references CRITERIA" "Select model elements by criteria and print as references" :parse-fn edn/read-string]
   [nil  "--select-views CRITERIA" "Select and print views by criteria" :parse-fn edn/read-string]
   [nil  "--select-view-references CRITERIA" "Select views by criteria and print as references" :parse-fn edn/read-string]
   ["-T" "--template-dir DIRNAME" "Template directory" :default "templates"]
   ["-g" "--generation-config FILE" "Generation configuration"]
   ["-G" "--generation-dir DIRNAME" "Generation artifact directory" :default "generated"]
   ["-B" "--backup-dir DIRNAME" "Generation backup directory" :default "backup"]
;   [nil  "--[no-]ignore-unresolved" "Ignore unresolved elements" :default false]
   [nil  "--scope NAMESPACE" "Sets the internal scope by namespace prefix"]
   [nil  "--[no-]model-warnings" "Returns warnings for the loaded model" :default true]
   [nil  "--[no-]model-info" "Returns infos for the loaded model" :default false]
   [nil  "--plantuml-list-sprites" "Lists the loaded PlantUML sprites" :default false]
;  [nil  "--plantuml-find-sprite" "Searches the loaded PlantUML sprites for the given name"]
   ["-h" "--help" "Print help"]
   [nil  "--debug" "Print debug messages" :default false]])

; TODO merge with options and use in reader
(def default-options
  {:input-model-format :overarch-input
   :reader-type :file})

;;;
;;; Output messages
;;;
(defn usage-msg
  "Returns a message containing the program usage."
  ([summary]
   (usage-msg (str "java --jar " appname ".jar [options]") "" summary))
  ([name summary]
   (usage-msg name "" summary))
  ([name description summary]
   (str/join "\n\n"
             [description
              (str "Usage: java -jar " name ".jar [options].")
              "Options:"
              summary])))

(defn error-msg
  "Returns a message containing the parsing errors."
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit
  "Exits the process."
  [status msg]
  (println msg)
  (System/exit status))

(defn validate-args
  "Validate command line arguments `args` according to the given `cli-opts`.
   Either returns a map indicating the program should exit
   (with an error message and optional success status), or a map
   indicating the options provided."
  [args cli-opts]
  (try
    (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-opts)]
      (cond
        errors ; errors => exit with description of errors
        {:exit-message (error-msg errors)}
        (:help options) ; help => exit OK with usage summary
        {:exit-message (usage-msg appname description summary) :success true}
        (= 0 (count arguments)) ; no args
        {:options options}
        (seq options)
        {:options options}
        :else ; failed custom validation => exit with usage summary
        {:exit-message (usage-msg appname description summary)}))
    (catch Exception e
           (println "Error validating the CLI arguments" args ".")
           (println (ex-message e))
           (.printStacktrace e))))

;;;
;;; Handler logic
;;;
(defn model-warnings
  "Reports warnings about the given `model` and views."
  [model options]
  {:build-problems (:build-problems model)
   :unresolved-refs-in-views (model/check-views model)
   :unresolved-refs-in-relations (model/check-relations model)})
   :unnamespaced (el/unnamespaced-elements (repo/model-elements))
   :unidentifiable (el/unidentifiable-elements (repo/model-elements))
   :unnamed (el/unnamed-elements (repo/model-elements))
   :unrelated (model/unrelated-nodes (repo/model-elements))
   
(defn model-info
  "Reports information about the given `model` and views."
  [model options]
  {:nodes-count                 (count (repo/nodes))
   :nodes-by-type-count         (el/count-nodes-per-type (repo/nodes))
   :relations-count             (count (repo/relations))
   :relations-by-type-count     (el/count-relations-per-type (repo/relations))
   :views-count                 (count (repo/views))
   :views-by-type-count         (el/count-views-per-type (repo/views))
   :elements-by-namespace-count (el/count-elements-per-namespace (repo/model-elements))
   :external-count              (el/count-external (repo/model-elements))
   :synthetic-count             (el/count-synthetic (repo/model-elements))})
   
(defn print-sprite-mappings
  "Prints the given list of the sprite mappings."
  ([]
   (print-sprite-mappings (puml/sorted-sprite-mappings puml/tech->sprite)))
  ([sprite-mappings]
   (doseq [sprite sprite-mappings]
     (println (str (:key sprite) ": " (puml/sprite-path sprite))))))

(defn select-elements
  "Returns the model elements selected by criteria specified in the `options`."
  [options]
  (when-let [criteria (spec/check-selection-criteria (:select-elements options))]
    (repo/model-elements-by-criteria criteria)))

(defn select-references
  "Returns references to the model elements selected by criteria specified in the `options`."
  [options]
  (when-let [criteria (spec/check-selection-criteria (:select-references options))]
    (->> criteria
         (repo/model-elements-by-criteria)
         (map el/element->ref)
         into [])))

(defn select-views
  "Returns the views selected by criteria specified in the `options`."
  [options]
  (when-let [criteria (spec/check-selection-criteria (:select-views options))]
    (into #{} (model/filter-xf @repo/state criteria)
          (repo/views))))

(defn select-view-references
  "Returns references to the views selected by criteria specified in the `options`."
  [options]
  (when-let [criteria (spec/check-selection-criteria (:select-view-references options))]
    (into [] (comp (model/filter-xf @repo/state criteria)
                   (map el/element->ref))
          (repo/views))))

(defn dispatch
  "Dispatch on `options` to the requested actions."
  [model options]
  (when (:model-warnings options)
    (println "Model Warnings:")
    (pp/pprint (model-warnings model options)))
  (when (:model-info options)
    (println "Model Information:")
    (pp/pprint (model-info model options)))
  (when (:select-elements options)
    (println "Selected Elements for" (:select-elements options))
    (pp/pprint (select-elements options)))
  (when (:select-references options)
    (println "Selected References for" (:select-references options))
    (pp/pprint (select-references options)))
  (when (:select-views options)
    (println "Selected Views for" (:select-views options))
    (pp/pprint (select-views options)))
  (when (:select-view-references options)
    (println "Selected Views for" (:select-view-references options))
    (pp/pprint (select-view-references options)))
  (when (:plantuml-list-sprites options)
    (print-sprite-mappings))
  (when (:render-format options)
    (rndr/render model (:render-format options) options))
  (when (:export-format options)
    (exp/export model (:export-format options) options))
  (when (:generation-config options)
    (tmpl/generate model options)))

(defn update-and-dispatch!
  "Read models and export the data according to the given `options`."
  [options]
  (let [model (reader/update-state! options)]
    (dispatch model options)))

(defn handle
  "Handle the `options` and generate the requested outputs."
  [options]
  (update-and-dispatch! options)
  (when (:watch options)
    (beholder/watch
     (fn [m]
       (when (:debug options)
         (println "Filesystem watch:")
         (println "event: " (:type m))
         (println "context: " (:path m))
         (println options))
       (update-and-dispatch! options))
     (:model-dir options))
    (while true
      (Thread/sleep 5000))))

;;;
;;; CLI entry 
;;;
(defn -main
  "Main function as CLI entry point."
  [& args] 
  (let [{:keys [options exit-message success]} (validate-args args cli-opts)
        options (merge default-options options)
        exit-message (or exit-message
                         (when (:help options)
                           (usage-msg appname description (:summary options))))]
    (when (:debug options)
      (println options))
    (if exit-message
      ; exit with message
      (exit (if success 0 1) exit-message)
      ; handle options and generate the requested outputs
      (handle options))))

;;;
;;; Rich comments for tests and explorations
;;;
(comment ; state update
  (update-and-dispatch! (merge
                         default-options
                         {:model-dir "models"
                          :export-dir "export"
                          :render-dir "export"
                          :render-format :plantuml
                          :debug true}))

  (model-info (reader/update-state! (merge
                                     default-options
                                     {:model-dir "models/banking:models/overarch"}))
              {:model-info true})
  (reader/update-state! (merge
                         default-options
                         {:model-dir "models/banking"}))
  (reader/update-state! (merge
                         default-options
                         {:model-dir "models/overarch"}))
  (reader/update-state! (merge
                         default-options
                         {:model-dir "models/test/collapsed"}))
  (reader/update-state! (merge
                         default-options
                         {:model-dir "models/test/sprite-issue"}))
  (reader/update-state! (merge
                         default-options
                         {:model-dir "../my-bank-model/models/"}))

  ;
  )

(comment ; model repository
  (repo/elements)
  (repo/namespaces)
  ;
  )

(comment ; model analytics 
  (el/count-elements-per-namespace (concat (repo/nodes) (repo/relations)))
  (el/count-elements-per-type (concat (repo/nodes) (repo/relations)))
  (el/count-nodes-per-type (repo/nodes))
  (el/count-relations-per-type (repo/relations))
  (el/count-views-per-type (repo/views))

  (el/count-external (concat (repo/nodes) (repo/relations)))
  (el/count-synthetic (concat (repo/nodes) (repo/relations)))

  (el/all-keys (repo/nodes))
  (el/all-keys (repo/relations))
  (el/all-values-for-key :el (repo/nodes))
  (el/all-values-for-key :subtype (repo/nodes))
  (el/all-values-for-key :tech (repo/nodes))

  (el/unidentifiable-elements (concat (repo/nodes) (repo/relations)))
  (el/unnamespaced-elements (concat (repo/nodes) (repo/relations)))
  (el/unmatched-relation-namespaces (repo/relations))

  (model/unrelated-nodes (repo/model))
  (model/check-relations (repo/model))
  (model/check-views (repo/model))
  (model/unresolved-refs  (repo/model) (model/resolve-element @repo/state :test/missing-elements))
 
  (el/elements-by-namespace (repo/nodes))
  (el/elements-by-namespace (repo/relations))
  (el/elements-by-namespace (repo/views))

  ; Filter all techs staring with Azure 
  (filter #(str/starts-with? % "Azure") (model/traverse :tech model/tech-collector (repo/model-elements)))
  ;
  )


(comment ; selections
  (into #{} (model/filter-xf (repo/model) {:subtype :queue}) (repo/nodes))

  (into #{} (model/filter-xf (repo/model) {:namespace "banking"}) (repo/nodes))
  (into #{} (model/filter-xf (repo/model) {:namespace "banking"}) (repo/relations))

  (into #{}
        (model/filter-xf (repo/model) {:namespace "overarch.data-model"})
        (repo/model-elements))
  (into #{} (model/filter-xf (repo/model) {:namespace-prefix "mybank.compliance"}) (repo/nodes))
  
  ;
  )

(comment ; model navigation on banking model
  (model/descendant-nodes (repo/model)
                          (model/resolve-element (repo/model)
                                                 :banking/internet-banking-system))

  ;
  (model/referred-nodes (repo/model) :banking/api-application)
  (model/referring-nodes (repo/model) :banking/api-application)
  (model/referred-relations (repo/model) :banking/api-application)
  (model/referring-relations (repo/model) :banking/api-application)

  ; 
  (model/referred-nodes (repo/model) :banking/api-application {:el :request})
  (model/referring-nodes (repo/model) :banking/api-application {:el :request})
  ;(model/descendants (repo/model) :banking/internet-banking-system)
  ;(model/ancestors (repo/model) :banking/internet-banking-system)
  ;(model/sync-dependents (repo/model) :banking/api-application)
  ;(model/sync-dependencies (repo/model) :banking/api-application)
  ; 
  )

(comment ; model navigation on overarch model
  (model/children (repo/model)
                  (model/resolve-element (repo/model)
                                         :overarch.data-model/technical-element))
  ; type hierarchy of :code-model-node (upwards)
  (model/transitive-search (repo/model)
                           {:referred-node-selection {:els #{:inheritance :implementation}}}
                           :overarch.data-model/code-model-node)
  ; type hierarchy of :code-model-node (downwards)
  (model/transitive-search (repo/model)
                           {:referring-node-selection {:els #{:inheritance :implementation}}}
                           :overarch.data-model/code-model-node)

  (model/t-descendants (repo/model)
                       (model/resolve-element (repo/model)
                                              :banking/internet-banking-system))
  (model/t-ancestors (repo/model)
                       (model/resolve-element (repo/model)
                                              :banking/api-application))
  ;
  )

(comment ; view functions
  (def view (model/resolve-element (repo/model)
                                   :test.collapsed/container-view))
  (def view (model/resolve-element (repo/model)
                                   :mybank.compliance/container-view))
  (def view (model/resolve-element (repo/model)
                                   :mybank.core-banking/container-view))
  (def view (model/resolve-element (repo/model)
                                   :mybank.it-management/deployment-view))
  (def view (model/resolve-element (repo/model)
                                   :banking/organization-structure-view))
  (def view (model/resolve-element (repo/model)
                                   :views/comp1))
  (def view (model/resolve-element (repo/model)
                                   :banking.deployment/deployment-architecture-view))

  (keys (repo/model))
  (:id->element (repo/model))

  view
  (puml/sprites-for-view (repo/model) view)

  (def referenced
    (view/referenced-elements (repo/model) view))
  referenced
  (def selected
    (view/selected-elements (repo/model) view))
  selected
  (def specified
    (view/specified-elements (repo/model) view))
  specified
  (def included
    (view/included-elements (repo/model) view specified))
  included
  (def in-view
    (view/view-elements (repo/model) view))
  in-view
  (def roots
    (view/root-elements (repo/model) in-view))
  roots
  (def rendered
    (view/elements-to-render (repo/model) view))
  rendered
  (def rendered-new
    (view/rendered-elements (repo/model) view))
  rendered-new

  (require '[org.soulspace.overarch.adapter.render.plantuml :as plantuml])  
  (plantuml/plantuml-view? view)

  (render/render-view (repo/model)
                      :plantuml
                      (merge {:render-dir "exports"
                              :render-format :plantuml}
                             default-options)
                      view)

  (count referenced)
  (count selected)
  (count specified)
  (count included)
  (count in-view)
  (count roots)
  (count rendered)
  (count rendered-new)

  (model/children (repo/model) :sys-1)
  (model/descendant-nodes (repo/model) :sys-1)
  (puml/sprites-for-view (repo/model) view)
  (view/elements-to-render (repo/model) (repo/view-by-id :views/comp1))
  (view/rendered-elements (repo/model) (repo/view-by-id :views/comp1))
  ;
  )

(comment ; view functions for overarch data-model
  (fns/data-tapper "referenced"
                 (view/referenced-elements (repo/model)
                                           (model/resolve-element (repo/model)
                                                                  :overarch.data-model/data-model)))

  (fns/data-tapper "selected"
                   (view/selected-elements (repo/model)
                                           (model/resolve-element (repo/model)
                                                                  :overarch.data-model/data-model)))

  (fns/data-tapper "specified"
                   (view/specified-elements (repo/model)
                                            (model/resolve-element (repo/model)
                                                                   :overarch.data-model/data-model)))

  (fns/data-tapper "included"
                   (view/included-elements (repo/model)
                                           (model/resolve-element (repo/model)
                                                                  :overarch.data-model/data-model)
                                           specified))

  (fns/data-tapper "in-view"
                   (view/view-elements (repo/model)
                                       (model/resolve-element (repo/model)
                                                              :overarch.data-model/data-model)))

  (fns/data-tapper "rendered"
                   (view/elements-to-render (repo/model)
                                            (model/resolve-element (repo/model)
                                                                   :overarch.data-model/data-model)))

  ;
  )

(comment ; render functions
  (model/children (repo/model)
                  (model/resolve-element (repo/model)
                                         :banking/internet-banking-system))
  (c4/render-c4-element (repo/model)
                        (model/resolve-element (repo/model)
                                               :banking/container-view)
                        0
                        (model/resolve-element (repo/model)
                                               :banking/internet-banking-system))
  
  (c4/render-c4-element (repo/model)
                        (model/resolve-element (repo/model)
                                               :banking/container-view)
                        0
                        (model/resolve-element (repo/model)
                                               :banking/big-bank-plc-datacenter))

  ;
  )

(comment ; CLI calls
  (-main "--debug")
  (-main "--debug" "--render-format" "plantuml")
  (-main "--debug" "--render-format" "plantuml")
  (-main "--debug" "--render-format" "markdown")
  (-main "--debug" "--render-format" "graphviz")
  (-main "--debug" "--render-format" "all")
  (-main "--debug" "--render-format" "all" "--no-render-format-subdirs")
  (-main "--debug" "--export-format" "json")
  (-main "--model-dir" "models/banking" "--export-format" "structurizr")
  (-main "--model-info")
  (-main "--no-model-warnings")
  (-main "--plantuml-list-sprites")
  (-main "--help") ; ends REPL session

  ; 
  (-main "--debug" "--no-render-format-subdirs" "--render-format" "plantuml" "--generation-config" "templates/gencfg.edn")

  ; overarch development
  (-main "--debug" "--generation-config" "dev/model-gencfg.edn" "-T" "dev/templates")
  (-main "--debug" "--generation-config" "dev/report-gencfg.edn")
  (-main "--debug" "--generation-config" "dev/test-gencfg.edn")
  ;
  )
