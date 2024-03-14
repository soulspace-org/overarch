;;;;
;;;; Markdown rendering and export
;;;;
(ns org.soulspace.overarch.adapter.render.markdown
  "Functions to export views to markdown."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.cmp.md.markdown-dsl :as md]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.util.functions :as fn]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

;;;
;;; Predicates
;;;
(defn references?
  "Returns true, if relations should be rendered."
  [view]
  (fn/truthy? (get-in view [:spec :markdown :references] false)))

;;;
;;; Rendering
;;;

(defn render-type
  "Renders the type of the element `e` including the subtype, if defined."
  [e]
  (if (:subtype e)
    (str " (" (str/capitalize (name (:subtype e)))
         " " (str/capitalize (name (:el e))) ")")
    (str " (" (str/capitalize (name (:el e))) ")")))

(defmulti render-element
  "Renders an `element` in the `view` with markdown according to the given `options`."
  (fn [model e _ _] (:el e))
  :hierarchy #'el/element-hierarchy)

(defn render-reference
  "Renders the relation as reference."
  [model rel]
  (str " * " (model/from-name model rel) " " 
       (:name rel) " " 
       (model/to-name model rel)))

(defmethod render-element :boundary
  [model e options view]
  [(md/h2 (str (:name e) (render-type e)))
   (md/p (:desc e))])

(defmethod render-element :model-node
  [model e options view]
  [(md/h2 (str (:name e) (render-type e)))
   (md/p (:desc e))
   (when (references? view)
     (when-let [referrer ((:id e) (:referrer-id->relations model))]
       [(md/h3 "Refers to: ")
        (map (partial render-reference model) referrer)])
     (when-let [referred ((:id e) (:referred-id->relations model))]
       [(md/h3 "Referred from: ")
        (map (partial render-reference model) referred)]))
   "\n"])

(defmethod render-element :model-relation
  [model e options view]
  ; relations are not rendered directly in markdown
  "")

(defmethod render-element :model-element
  [model e options view]
  (println "unhandled element of type "
           (:el e) "with id" (:id e)
           "in markdown rendering of view " (:id view)))

(defn render-markdown-view
  "Renders the `view` with markdown according to the given `options`."
  [model options view]
  (let [elements (sort-by :id (view/rendered-model-nodes model view))]
    (flatten [(md/h1 (:title view))
              (map #(render-element model % options view) elements)])))

;;;
;;; Markdown Rendering dispatch
;;;
(def markdown-views
  "Contains the views rendered with markdown."
  #{:concept-view :glossary-view
    :context-view :container-view :component-view
    :system-landscape-view})

(defn markdown-view?
  "Returns true, if the view is to be rendered with markdown."
  [view]
  (contains? markdown-views (:el view)))

(defmethod rndr/render-file :markdown
  [model format options view]
  (let [dir-name (str (:render-dir options) "/markdown/"
                      (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".md"))))

(defmethod rndr/render-view :markdown
  [model format options view]
  (let [result (render-markdown-view model options view)]
    (with-open [wrt (io/writer (rndr/render-file model format options view))]
      (binding [*out* wrt]
        (println (str/join "\n" result))))))

(defmethod rndr/render :markdown
  [model format options]
  (doseq [view (view/get-views model)]
    (when (markdown-view? view)
      (rndr/render-view model format options view))))
