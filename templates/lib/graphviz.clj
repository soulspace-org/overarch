(ns lib.graphviz
  "Functions for templates generating Graphviz files."
  (:require [clojure.string :as str]
            [org.soulspace.clj.string :as sstr]))

;;;
;;; Graphviz Render functions
;;;
(defn alias-name
  "Returns a valid alias for the namespaced keyword `kw`."
  [kw]
  (symbol (str (str/replace (sstr/hyphen-to-camel-case (namespace kw)) \. \_) "_"
               (sstr/hyphen-to-camel-case (name kw)))))
(defn short-name
  "Returns a valid alias for the name part of the keyword `kw`."
  [kw]
  (sstr/hyphen-to-camel-case (name kw)))

(defn id
  "Returns an id for graphviz dot for the element `e`"
  [e]
  (-> (str (namespace e) "__" (name e))
      (str/replace  "-" "_")
      (str/replace  "." "_")))

