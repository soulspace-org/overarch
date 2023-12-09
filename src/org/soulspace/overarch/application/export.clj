(ns org.soulspace.overarch.application.export
  "Contains general functions for the export of the model data.")

;;;
;;; Export multimethods 
;;;

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

