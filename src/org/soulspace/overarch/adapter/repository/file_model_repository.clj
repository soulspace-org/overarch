(ns org.soulspace.overarch.adapter.repository.file-model-repository
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as mr]
            [clojure.string :as str]))

(defn windows-path?
  "Returns true, if the path is of a windows system."
  [path]
  (or (str/index-of path \;) (re-matches #"[A-Za-z]:.*" path)))

(defn split-path
  "Splits a path into a sequence of file entries."
  [path]
  (if (windows-path? path)
    (str/split path #";")
    (str/split path #":")))

(defn read-model
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))

(s/fdef read-models
  :args [string?]
  :ret :overarch/ct)
(defmethod mr/read-models :file
  [rtype path]
  (->> path
       (split-path)
       (mapcat read-model)))

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
  (re-matches #"[A-Za-z]:.*" "C:\\path")
  (windows-path? "/p1/models:/p2/models")
  (windows-path? "\\C:\\p1\\models;\\C:\\p2\\models")
  (windows-path? "C:\\p1\\models")
  (windows-path? "C:/p1/models")
  (split-path "/p1/models")
  (split-path "/p1/models:/p2/models")
  (split-path "\\C:\\p1\\models;\\C:\\p2\\models")
  ;

  )