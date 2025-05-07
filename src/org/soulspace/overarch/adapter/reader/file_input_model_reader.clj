(ns org.soulspace.overarch.adapter.reader.file-input-model-reader
  (:require [clojure.edn :as edn]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.util.io :as io]
            [org.soulspace.overarch.adapter.reader.model-reader :as mr]))

;; TODO different file model repositories?
;; TODO transform input model on load? could be different for different input formats

(defn read-model-file
  "Reads a model `file`."
  [^java.io.File file]
  (try
    (->> file
         (slurp)
         (edn/read-string)
         (spec/check-input-model file))
    (catch Exception e
      (throw (ex-info (str "Error reading model file: " (.getPath file)) {} e)))))

(defn read-model
  "Reads a model by reading all files in `dir`."
  [dir]
  (->> (io/all-files-by-extension dir "edn")
       (mapcat read-model-file)))

(defmethod mr/read-models :file
  [options path]
  (->> path
       (io/split-path)
       (mapcat read-model)))



