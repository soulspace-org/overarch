(ns org.soulspace.overarch.adapter.repository.file-model-repository
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as mr]
            [org.soulspace.overarch.domain.spec :as spec]))

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

(defn read-model-file
  "Reads a model `file`."
  [^java.io.File file]
  (-> file
      (slurp)
      (edn/read-string)
      (spec/check-input-model file)))

(defn read-model
  "Reads a model by reading all files in `dir`."
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (mapcat read-model-file)))

(defmethod mr/read-models :file
  [rtype path]
  (->> path
       (split-path)
       (mapcat read-model)))

(comment
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