(ns lib.plantuml
  "Functions for templates generating PlantUML files."
  (:require [clojure.string :as str]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.overarch.adapter.template.view-api :as view]))

;;;
;;; PlantUML Aliases
;;;
(defn alias-name
  "Returns a valid PlantUML alias for the namespaced keyword `kw`."
  [kw]
  (symbol (str (str/replace (sstr/hyphen-to-camel-case (namespace kw)) \. \_) "_"
               (sstr/hyphen-to-camel-case (name kw)))))
(defn short-name
  "Returns a valid PlantUML alias for the name part of the keyword `kw`."
  [kw]
  (sstr/hyphen-to-camel-case (name kw)))

;;;
;;; Diagram Layout
;;;

(defn render-title
  "Renders the title of the diagram."
  [view]
  (when-let [title (view/title view)]
    (str "title " title)))

(defn render-skinparam
  "Renders a skinparam for the plantuml diagram."
  [[k v]]
  (str "skinparam " k " " v))

(defn render-skinparams
  "Renders skinparams for the plantuml diagram."
  [view]
  (when-let [skinparams (get (view/plantuml-spec view) :skinparams)]
    (->> skinparams
         (map render-skinparam)
         (str/join "\n"))))
