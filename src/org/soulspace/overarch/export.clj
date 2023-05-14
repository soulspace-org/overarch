(ns org.soulspace.overarch.export
  "Contains general functions for the export of data."
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
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

; general
(defmulti export-diagram
  "Exports the diagram in the given format."
  export-format)

; general
(defn export-diagrams
  "Export all diagrams in the given format."
  [format]
  (doseq [diagram (core/get-diagrams)]
    (export-diagram format diagram)))
