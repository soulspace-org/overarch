(ns org.soulspace.overarch.export
  "Contains general functions for the export of data.")

;;;
;;; Export multimethods 
;;;

(defn export-format
  "Returns the export format for the data."
  ([options]
   (:format options))
  ([options _]
   (:format options)))

(defmulti export-file
  "Returns the export directory for the format."
  export-format)

(defmulti export-view
  "Exports the view in the given format."
  export-format)

(defmulti export
  "Exports the data in the given format."
  export-format)

