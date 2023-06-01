(ns org.soulspace.overarch.cli
  "Functions for the command line interface of overarch."
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.export :as exp]
            ; must be loaded, for registering of the multimethods
            ; require dynamically?
            [org.soulspace.overarch.json :as json]
            [org.soulspace.overarch.plantuml.core :as puml]
            [hawk.core :as hawk])
  (:gen-class))

;;;
;;; CLI definition
;;;

(def appname "overarch")
(def description "Overarch CLI Exporter
                  Reads your model and view specifications and exports them
                  into the specified format.")

(def cli-opts
  [["-m" "--model-dir DIRNAME" "Model directory" :default "models"]
   ["-e" "--export-dir DIRNAME" "Export directory" :default "export"]
   ["-w" "--watch" "Watch model dir for changes and trigger export" :default false]
   ["-f" "--format FORMAT" "Export format (json, plantuml, structurizr)" :default :plantuml :parse-fn keyword]
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

;;;
;;; Handler logic
;;;

(defn update-and-export!
  "Read models and export the data according to the given `options`."
  [options]
  (core/update-state! (:model-dir options))
  (exp/export options))

(defn handle
  "Handle the `options` and generate the requested outputs."
  [options]
  (update-and-export! options)
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
  (-main "--debug" "--format" "json")
  (-main "--debug")
  (-main "--help")
  )
