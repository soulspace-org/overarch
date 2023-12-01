(ns org.soulspace.overarch.adapter.repository.file-model-repository
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as mr]))

(s/fdef read-model
  :args [string?]
  :ret :overarch/ct)
(defmethod mr/read-model :filesystem
  [r-type dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))