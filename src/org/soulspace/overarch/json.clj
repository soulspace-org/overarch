(ns org.soulspace.overarch.json
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [charred.api :as json]
            [org.soulspace.clj.java.file :as file]))

(defn write-json
  "Writes the data as JSON to out-file."
  [out-file data]
  (with-open [wrt (io/writer out-file)]
    (json/write-json wrt data)))

(defn export-json
  "Exports the data files in the given directory to JSON"
  [dir]
  (doseq [file (file/all-files-by-extension "edn" dir)]
    (let [out-dir (str "exports/"
                       (file/parent-path file) "/")
          out-file (str out-dir (file/base-name file) ".json")]
;      (println "In: " (file/path file))
;      (println "Out:" out-file)
      (file/create-dir out-dir)
      (->> file
           (slurp)
           (edn/read-string)
           (write-json out-file)))))

(comment
  (export-json "models"))