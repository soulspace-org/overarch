(ns org.soulspace.overarch.adapter.exports.edn 
  (:require [clojure.java.io :as io]
            [org.soulspace.overarch.application.export :as exp]))

;;;;
;;;; export model into edn files structured according to the types and namespaces of the elements
;;;;

(defn elements-by-namespace
  ""
  [model]
  )


(defn export-model
  "Exports the model as EDN files."
  [m])

(defmethod exp/export-file :edn
  [m format options ]
  (let [dir-name (str (:export-dir options) "/edn/")
        file (namespace (:id (first (model/get-model-elements m))))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/" file ".edn"))))

(defmethod exp/export :edn
  [m format options]
  ; FIXME files per namespace
  (with-open [wrt (io/writer (exp/export-file m format options))]
    (binding [*out* wrt]
      (println (str/join "\n" (doall (export-model m)))))))

