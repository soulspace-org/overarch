;;;;
;;;; Functions for a structured EDN export
;;;;

(ns org.soulspace.overarch.adapter.exports.edn 
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.application.export :as exp]
            [org.soulspace.overarch.domain.element :as el]))

;;
;; export model into edn files structured according to the types and namespaces of the elements
;;

(defn elements-by-namespace
  "Returns the elements of the `coll` grouped by namespace."
  [coll]
  (group-by el/element-namespace coll))

(defn edn-filename
  "Returns the filename for the `namespace` and the `kind` of data."
  ([options namespace kind]
   (let [dir-name (str (:export-dir options) "/edn/"
                       (str/replace namespace "." "/") "/")]
     (file/create-dir (io/as-file dir-name))
     (io/as-file (str dir-name "/" kind ".edn")))))

(defn write-edn
  "Write the elements of the `coll` to an EDN file."
  [options namespace kind coll]
  (with-open [wrt (io/writer (edn-filename options namespace kind))]
   (binding [*out* wrt]
     (println (str/join "\n" (doall coll))))))

(defmethod exp/export :edn
  [model format options]
  ; TODO implement
  )
