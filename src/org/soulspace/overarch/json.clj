(ns org.soulspace.overarch.json
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [charred.api :as json]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.export :as exp]
            [org.soulspace.overarch.io :as oio]))

(defn export-json
  "Exports the data files in the model directory, as specified by `options`, to JSON."
  [options]
  (doseq [file (file/all-files-by-extension "edn" (:model-dir options))]
    (let [out-dir (str (:export-dir options)"/"
                       (file/parent-path file) "/")
          out-file (str out-dir (file/base-name file) ".json")]
      (->> file
           (oio/load-edn)
           (oio/write-json out-file)))))

(defmethod exp/export :json
  [options]
  (export-json options))

(comment
  (export-json "models"))