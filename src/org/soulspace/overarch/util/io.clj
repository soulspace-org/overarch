(ns org.soulspace.overarch.util.io
  "Contains I/O related functions."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [charred.api :as json]))

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

