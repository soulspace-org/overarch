(ns org.soulspace.overarch.cli
  "Functions for the command line interface of overarch."
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [hawk.core :as hawk]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.export :as exp]
            ; must be loaded here for registering of the multimethods
            ; require dynamically?
            [org.soulspace.overarch.exports.json :as json]
            [org.soulspace.overarch.exports.graphviz :as graphviz]
            [org.soulspace.overarch.exports.markdown :as markdown]
            [org.soulspace.overarch.exports.structurizr :as structurizr]
            [org.soulspace.overarch.exports.plantuml :as puml])
  (:gen-class))

;;;
;;; CLI definition
;;;

(def appname "overarch")
(def description
  "Overarch CLI Exporter
   Reads your model and view specifications and exports them
   into the specified format.")

(def cli-opts
  [["-m" "--model-dir DIRNAME" "Model directory" :default "models"]
   ["-e" "--export-dir DIRNAME" "Export directory" :default "export"]
   ["-w" "--watch" "Watch model dir for changes and trigger export" :default false]
   ["-f" "--format FORMAT" "Export format (json, markdown, plantuml, structurizr)" :default :plantuml :default-desc "plantuml" :parse-fn keyword]
   [nil  "--model-info" "Returns infos for the loaded model" :default false] 
   [nil  "--plantuml-list-sprites" "Lists the loaded PlantUML sprites" :default false]
;   [nil  "--plantuml-find-sprite" "Searches the loaded PlantUML sprites for the given name"]
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
              (str "Usage: java --jar " name ".jar [options].")
              ""
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
      {:exit-message (usage-msg appname description summary)})))

;;;
;;; Handler logic
;;;

(defn update-and-export!
  "Read models and export the data according to the given `options`."
  [options]
  (core/update-state! (:model-dir options))
  (exp/export options))

(defn model-info
  "Reports information about the model and views."
  [options]
  (let [elements (core/all-elements)
        element-count (count (remove core/relation?
                                     (filter core/model-element? elements)))
        unrelated-elements (core/unconnected-components)]
    {:element-count element-count
     :view-count (count (filter core/view? elements))
     :person-count (count (filter core/person? elements))
     :system-count (count (filter core/system? elements))
     :container-count (count (filter core/container? elements))
     :component-count (count (filter core/component? elements))
     :node-count (count (filter core/node? elements))
     :relation-count (count (filter core/relation? elements))
     :external-count (count (filter (comp core/model-element?
                                          core/external?) elements))
     :unrelated-elements unrelated-elements}))

(defn print-sprite-mappings
  "Prints the given list of the sprite mappings."
  ([]
   (print-sprite-mappings (puml/sorted-sprite-mappings puml/tech->sprite)))
  ([sprite-mappings]
   (doseq [sprite sprite-mappings]
     (println (str (:key sprite) ": " (puml/sprite-path sprite))))))

(defn handle
  "Handle the `options` and generate the requested outputs."
  [options]
  (core/update-state! (:model-dir options))
  (exp/export options)
  (when (:model-info options)
    (println (model-info options)))
  (when (:plantuml-list-sprites options)
    (print-sprite-mappings))
  (when (:watch options)
    ; TODO loop recur this update-and-export! as handler
    (hawk/watch! [{:paths [(:model-dir options)]
                   :handler (fn [ctx e]
;                              (println "event: " e)
;                              (println "context: " ctx)
                              (update-and-export! options))}])
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

(comment
  (update-and-export! {:model-dir "models"
                       :format :plantuml})
  (model-info {:model-info true})
  (print-sprite-mappings)
  (-main "--debug" "--format" "json")
  (-main "--debug" "--format" "markdown")
  (-main "--debug" "--format" "graphviz")
  (-main "--model-dir" "models/banking" "--format" "structurizr")
  (-main "--model-info")
  (-main "--debug")
  (-main "--help") ; ends REPL session
  (-main "--plantuml-list-sprites")
  )
