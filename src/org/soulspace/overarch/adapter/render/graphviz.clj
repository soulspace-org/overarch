;;;;
;;;; GraphViz rendering and export
;;;;
(ns org.soulspace.overarch.adapter.render.graphviz
  "Functions to export views to GraphViz."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.domain.model :as model]))

;;;
;;; Rendering
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

(defmulti render-element
  "Renders an element `e` in the `view` with markdown according to the given `options`."
  (fn [model e _ _] (:el e))
  :hierarchy #'el/element-hierarchy)

(defmethod render-element :model-node
  [model e indent view]
  [(str (alias-name (:id e)) "[label=\"" (:name e) "\", style=\"filled\", fillcolor=\"#dddddd\"];")])

(defmethod render-element :model-relation
  [model e indent view]
  [(str (alias-name (:from e)) " -> " (alias-name (:to e))
        " [label=\"" (:name e) "\"];")])

(defmethod render-element :model-element
  [model e indent view]
  (println "unhandled element of type "
           (:el e) "with id" (:id e)
           "in graphviz rendering of view " (:id view)))

(defn render-layout
  "Renders the layout options for the `view`."
  [view]
  (let [spec (:spec view)
        graphviz (:graphviz spec)]
    (flatten [(when (= :left-right (:layout spec))
                "rankdir=\"LR\"")
              (when (:engine graphviz)
                (str "layout=\"" (name (:engine graphviz)) "\""))])))

(defn render-graphviz-view
  "Renders the `view` with graphviz according to the given `options`."
  [model options view]
  (let [elements (concat (sort-by :id (view/rendered-nodes model view))
                         (sort-by :id (view/rendered-relations model view)))]
    (flatten [(str "digraph \"" (:title view) "\" {")
              "labelloc= \"t\""
              (str "label=\"" (:title view) "\"")
              (render-layout view)
              (map #(render-element model % 0 view) elements)
              "}"])))

;;;
;;; Graphviz Rendering dispatch
;;;
(def graphviz-views
  "Contains the views rendered with graphviz."
  #{:concept-view})

(defn graphviz-view?
  "Returns true, if the view is to be rendered with graphviz."
  [view]
  (contains? graphviz-views (:el view)))

(defmethod rndr/render-file :graphviz
  [model format options view]
  (let [dir-name (str (:render-dir options) "/graphviz/"
                      (str/replace (namespace (:id view)) "." "/"))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".dot"))))

(defmethod rndr/render-view :graphviz
  [model format options view]
  (let [result (render-graphviz-view model options view)]
  (with-open [wrt (io/writer (rndr/render-file model format options view))]
    (binding [*out* wrt]
      (println (str/join "\n" result))))))

(defmethod rndr/render :graphviz
  [m format options]
  (doseq [view (model/views m)]
    (when (graphviz-view? view)
      (rndr/render-view m format options view))))
