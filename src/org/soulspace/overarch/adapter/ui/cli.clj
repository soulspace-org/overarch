(ns org.soulspace.overarch.adapter.ui.cli
  "Functions for the command line interface of overarch."
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.edn :as edn]
            [clojure.tools.cli :as cli]
            [nextjournal.beholder :as beholder]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.analytics :as al]
            [org.soulspace.overarch.application.model-repository :as repo]
            [org.soulspace.overarch.application.export :as exp]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.application.template :as tmpl]
            ; require adapters here to register multimethods
            ; require dynamically?
            [org.soulspace.overarch.adapter.exports.json :as json]
            [org.soulspace.overarch.adapter.exports.edn :as eex]
            [org.soulspace.overarch.adapter.exports.structurizr :as structurizr]
            [org.soulspace.overarch.adapter.render.graphviz :as graphviz]
            [org.soulspace.overarch.adapter.render.markdown :as markdown]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]
            [org.soulspace.overarch.adapter.render.plantuml.c4 :as c4]
            [org.soulspace.overarch.adapter.render.plantuml.uml :as uml]
            [org.soulspace.overarch.adapter.repository.file-model-repository :as frepo]
            [org.soulspace.overarch.adapter.template.comb :as comb]
            [clojure.set :as set]
            [org.soulspace.overarch.domain.spec :as spec])
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
;   [nil  "--scope NAMESPACE" "Sets the internal scope by namespace"]
   [nil  "--[no-]model-warnings" "Returns warnings for the loaded model" :default true]
   [nil  "--[no-]model-info" "Returns infos for the loaded model" :default false]
   [nil  "--plantuml-list-sprites" "Lists the loaded PlantUML sprites" :default false]
;  [nil  "--plantuml-find-sprite" "Searches the loaded PlantUML sprites for the given name"]
   ["-h" "--help" "Print help"]
   [nil  "--debug" "Print debug messages" :default false]])

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
(defmethod exp/export :all
  [model format options]
  (doseq [current-format exp/export-formats]
    (when (:debug options)
      (println "Exporting " current-format))
    (exp/export model current-format options)))

(defn model-warnings
  "Reports warnings about the model and views."
  [model options]
  {:unresolved-refs-in-views (al/check-views model)
   :unresolved-refs-in-relations (al/check-relations model)})
   :unnamespaced (al/unnamespaced-elements (repo/model-elements))
   :unidentifiable (al/unidentifiable-elements (repo/model-elements))
   :unnamed (al/unnamed-elements (repo/model-elements))
   :unrelated (al/unrelated-nodes (repo/model-elements))
   
(defn model-info
  "Reports information about the model and views."
  [model options]
  {:nodes-count                 (count (repo/nodes))
   :nodes-by-type-count         (al/count-nodes-per-type (repo/nodes))
   :relations-count             (count (repo/relations))
   :relations-by-type-count     (al/count-relations-per-type (repo/relations))
   :views-count                 (count (repo/views))
   :views-by-type-count         (al/count-views-per-type (repo/views))
   :elements-by-namespace-count (al/count-elements-per-namespace (repo/model-elements))
   :external-count              (al/count-external (repo/model-elements))
   :synthetic-count             (al/count-synthetic (repo/model-elements))})
   
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
    (into #{} (model/filter-xf @repo/state criteria)
          (set/union (repo/nodes) (repo/relations)))))

(defn select-references
  "Returns references to the model elements selected by criteria specified in the `options`."
  [options]
  (when-let [criteria (spec/check-selection-criteria (:select-references options))]
    (into []
          (comp (model/filter-xf @repo/state criteria)
                (map el/element->ref))
          (repo/model-elements))))

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
  (let [model (repo/update-state! options)]
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
  (let [{:keys [options exit-message success]} (validate-args args cli-opts)]
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
  (update-and-dispatch! {:model-dir "models"
                         :export-dir "export"
                         :render-dir "export"
                         :render-format :plantuml
                         :debug true})
  
  (model-info (repo/update-state! {:model-dir "models/banking:models/overarch"})
              {:model-info true})
  (repo/update-state! {:model-dir "models/banking"})
  )

(comment ; model analytics 
  (al/count-elements-per-namespace (concat (repo/nodes) (repo/relations)))
  (al/count-elements-per-type (concat (repo/nodes) (repo/relations)))
  (al/count-nodes-per-type (repo/nodes))
  (al/count-relations-per-type (repo/relations))
  (al/count-views-per-type (repo/views))

  (al/count-external (concat (repo/nodes) (repo/relations)))
  (al/count-synthetic (concat (repo/nodes) (repo/relations)))

  (al/all-keys (repo/nodes))
  (al/all-keys (repo/relations))
  (al/all-values-for-key :el (repo/nodes))
  (al/all-values-for-key :subtype (repo/nodes))
  (al/all-values-for-key :tech (repo/nodes))

  (al/unidentifiable-elements (concat (repo/nodes) (repo/relations)))
  (al/unnamespaced-elements (concat (repo/nodes) (repo/relations)))
  (al/unrelated-nodes (repo/model))
  (al/check-relations (repo/model))
  (al/check-views (repo/model))
  (al/unresolved-refs  (repo/model) (model/resolve-element @repo/state :test/missing-elements))
  (al/unmatched-relation-namespaces (repo/relations))

  (el/elements-by-namespace (repo/nodes))
  (el/elements-by-namespace (repo/relations))
  (el/elements-by-namespace (repo/views))

  ;
  )

(comment ; model navigation 
  (model/descendant-nodes (repo/model)
                          (model/resolve-element (repo/model)
                                                 :banking/internet-banking-system))
  ;
  )

(comment ; selections
  (into #{} (model/filter-xf (repo/model) {:namespace "ddd"}) (repo/nodes))
  (into #{} (model/filter-xf (repo/model) {:namespace "ddd"}) (repo/relations))
  (into #{} (model/filter-xf (repo/model) {:subtype :queue}) (repo/nodes))

  (into #{} (model/filter-xf (repo/model) {:namespace "banking"}) (repo/nodes))
  (into #{} (model/filter-xf (repo/model) {:namespace "banking"}) (repo/relations))

  (into #{}
        (model/filter-xf (repo/model) {:namespace "overarch.data-model"})
        (repo/model-elements))
  (->> (into #{}
             (model/filter-xf (repo/model) {:namespace "overarch.data-model"})
             (repo/model-elements))
       (model/collect-fields (repo/model)))
  ;
  )

(comment ; view functions
  (def referenced
    (view/referenced-elements (repo/model)
                              (model/resolve-element (repo/model)
                                                     :banking/container-view)))
  (def selected
    (view/selected-elements (repo/model)
                            (model/resolve-element (repo/model)
                                                   :banking/container-view)))
  (view/specified-elements (repo/model)
                           (model/resolve-element (repo/model)
                                                  :banking/container-view))
  (def specified
    (view/specified-elements (repo/model)
                             (model/resolve-element (repo/model)
                                                    :banking/container-view)))
  (def included
    (view/included-elements (repo/model)
                            (model/resolve-element (repo/model)
                                                   :banking/container-view)
                            specified))
  included
  (def in-view
    (view/view-elements (repo/model)
                        (model/resolve-element (repo/model)
                                               :banking/container-view)))
  (def rendered
    (view/elements-to-render (repo/model)
                             (model/resolve-element (repo/model)
                                                    :banking/container-view)))

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
  
  (model/deployed-on (repo/model)
                     (model/resolve-element (repo/model)
                                            :banking/big-bank-api-server-pod))
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
         (-main "--debug" "--render-format" "markdown")
         (-main "--debug" "--render-format" "graphviz")
         (-main "--debug" "--render-format" "all")
         (-main "--debug" "--render-format" "all"  "--no-format-subdirs")
         (-main "--debug" "--export-format" "json")
         (-main "--model-dir" "models/banking" "--export-format" "structurizr")
         (-main "--model-info")
         (-main "--no-model-warnings")
         (-main "--plantuml-list-sprites")
         (-main "--help") ; ends REPL session

         ; overarch development
         (-main "--debug" "--generation-config" "dev/model-gencfg.edn" "-T" "dev/templates")
         (-main "--debug" "--generation-config" "dev/report-gencfg.edn")
         (-main "--debug" "--generation-config" "dev/test-gencfg.edn")
  ;
  )
         
