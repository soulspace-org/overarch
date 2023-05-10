(ns org.soulspace.overarch.export
  "Contains general functions for the export of data."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [charred.api :as json]
            [org.soulspace.overarch.core :as core]))

;;;
;;; Exports
;;;

(defn export-format
  "Returns the export format for the data."
  ([format]
   format)
  ([format _]
   format))

(defmulti export-file
  "Returns the export directory for the diagram."
  export-format)

(defmulti export-model
  "Exports the diagram in the given format."
  export-format)

(defmethod export-file :json
  [format])

(defmethod export-model :json
  [format]
  (with-open [wrt (io/writer (export-file :json))]
    (json/write-json wrt (core/get-model-elements))))

; general
(defmulti export-diagram
  "Exports the diagram in the given format."
  export-format)

; general
(defn export-diagrams
  "Export all diagrams in the given format."
  [format]
  (doseq [diagram (core/get-diagrams)]
    (println (:id diagram))
    (export-diagram format diagram)))

