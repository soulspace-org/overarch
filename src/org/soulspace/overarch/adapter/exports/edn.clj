;;;;
;;;; Functions for a structured EDN export
;;;;

(ns org.soulspace.overarch.adapter.exports.edn 
  "Export themodel into edn files structured according to the types and namespaces
   of the elements."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.application.export :as exp]
            [org.soulspace.overarch.domain.element :as el]))

;;
;; export model into edn files structured according to the types and namespaces of the elements
;;
(def model-element-order
  "The order of the model elements in a namespace."
  [:concept-model-element :use-case-model-element
   :architecture-model-element :state-machine-model-element
   :code-model-element :deployment-model-element])

(def view-order
  "The order of the views in a namespace."
  [:concept-view :use-case-view :context-view :container-view :component-view :system-landscape-view :dynamic-view
   :state-machine-view :code-view :deployment-view]
  )

(defn file-comment
  "File comment."
  ([s]
   (file-comment s 0))
  ([s indent]
   (str ";;;;\n"
        ";;;; " s "\n"
        ";;;;")))

(defn model-comment
  "Model comment."
  ([s]
   (model-comment s 0))
  ([s indent]
   (str ";;;\n"
        ";;; " s "\n"
        ";;;")))

(defn elements-comment
  "Elements comment."
  ([s]
   (elements-comment s 0))
  ([s indent]
   (str ";;\n"
        ";; " s "\n"
        ";;")))

(defn element-comment
  "Element comment."
  ([s]
   (element-comment s 0))
  ([s indent]
   (str "; " s)))

(defn export-namespace
  "Exports the namespace `nspace`."
  [nspace coll]
  [(file-comment (str "Model for Namespace " nspace))
   "#{"
   " }"]
  )

(defn edn-filename
  "Returns the filename for the `namespace` and the `kind` of data."
  ([options nspace kind]
   (let [dir-name (str (:export-dir options) "/edn/"
                       (str/replace nspace "." "/") "/")]
     (file/create-dir (io/as-file dir-name))
     (io/as-file (str dir-name "/" kind ".edn")))))

(defn write-edn
  "Write the elements of the `coll` to an EDN file."
  [options nspace kind coll]
  (let [result (export-namespace nspace coll)]
    (with-open [wrt (io/writer (edn-filename options nspace kind))]
      (binding [*out* wrt]
        (println (str/join "\n" (doall result)))))))

(defmethod exp/export :edn
  [model format options]
  (let [elements-by-namespace (el/elements-by-namespace (model/input-elements model))]
    (doseq [[k v] elements-by-namespace]
      ; TODO implement
      )))
