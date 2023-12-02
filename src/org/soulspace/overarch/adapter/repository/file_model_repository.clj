(ns org.soulspace.overarch.adapter.repository.file-model-repository
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as mr]
            [clojure.string :as str]))

(defn windows-path?
  "Returns true, if the given `path` contains semicolons, which are the path separators on a windows system."
  [path]
  (str/index-of path \;))

(defn split-path
  "Splits a path into a sequence of file entries."
  [path]
  (if (windows-path? path)
    (str/split path #";")
    (str/split path #":")))

(defn load-model
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))

(s/fdef read-model
  :args [string?]
  :ret :overarch/ct)
(defmethod mr/read-model :filesystem
  [rtype dir]
  (load-model dir))

(s/fdef read-models
  :args [string?]
  :ret :overarch/ct)
(defmethod mr/read-models :filesystem
  [rtype path]
  (->> path
       (split-path)
       (mapcat load-model)))

(comment
  (java.io.File/separator)
  (file/split-path "/p1/models:/p2/models")
  (file/split-path "/p1/models:/p2/models")
  (file/normalize-path "/p1/models:/p2/models")
  (file/normalize-path "\\C:\\p1\\models;\\C:\\p2\\models")
  (-> "\\C:\\p1\\models;\\C:\\p2\\models"
      (file/normalize-path)
      (file/split-path))

  (str/split "\\C:\\p1\\models;\\C:\\p2\\models" #";")
  (windows-path? "/p1/models:/p2/models")
  (windows-path? "\\C:\\p1\\models;\\C:\\p2\\models")
  (split-path "/p1/models:/p2/models")
  (split-path "\\C:\\p1\\models;\\C:\\p2\\models")
  ;

  )