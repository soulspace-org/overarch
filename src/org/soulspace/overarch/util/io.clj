(ns org.soulspace.overarch.util.io
  "Contains I/O related functions."
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [charred.api :as json]
            [org.soulspace.clj.java.file :as file]))

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

(defn all-files-by-extension
  "Returns all files in `dir` with the given extension `ext`."
  [dir ext]
  (file/all-files-by-extension ext dir))

(defn load-edn-from-resource
  "Returns the data from the resource named `filename`."
  [filename]
  (-> filename
      (io/resource)
      (slurp)
      (edn/read-string)))

(defn load-edn
  "Returns the data from the file named `filename`."
  [filename]
  (-> filename
      (slurp)
      (edn/read-string)))

(defn write-json
  "Writes the data as JSON to `filename`."
  [filename data]
  (io/make-parents filename)
  (with-open [wrt (io/writer filename)]
    (json/write-json wrt data)))

(comment ;
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

