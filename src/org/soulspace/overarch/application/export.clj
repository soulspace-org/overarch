(ns org.soulspace.overarch.application.export
  "Contains general functions for the export of the model data." 
  (:require [clojure.string :as str]))

;;;
;;; Export functions
;;;

(defn indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))


;;;
;;; Export multimethods 
;;;

(def export-formats
  "Contains the supported render formats."
  #{:json :structurizr})

(defn export-format
  "Returns the export format for the data."
  ([m format options]
   format)
  ([m format options & args]
   format))

(defmulti export-file
  "Returns the export file for the given format."
  export-format)

(defmulti export
  "Exports the data in the given format."
  export-format)

(defmethod export :all
  [model format options]
  (doseq [current-format export-formats]
    (when (:debug options)
      (println "Exporting " current-format))
    (export model current-format options)))

