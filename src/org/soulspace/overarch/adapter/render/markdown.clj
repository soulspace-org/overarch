;;;;
;;;; Markdown rendering and export
;;;;
(ns org.soulspace.overarch.adapter.render.markdown
  "Functions to export views to markdown."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.cmp.md.markdown-dsl :as md]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as rndr]))

;;;
;;; Rendering
;;;
(defmulti render-element
  "Renders an `element` in the `view` with markdown according to the given `options`."
  (fn [model e _ _] (:el e))
  :hierarchy #'view/element-hierarchy)

(defmethod render-element :concept
  [model e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))])

(defmethod render-element :person
  [model e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))])

(defmethod render-element :technical-architecture-node
  [model e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))
   ; TODO model
   ;(when ((:id e) (:referred-id->relations model))
   ;  (md/h3 "Referres to "))
   ])

(defmethod render-element :boundary
  [model e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))
   ])


(defmethod render-element :relation
  [model e options view]
  "")

(defn render-markdown-view
  "Renders the `view` with markdown according to the given `options`."
  [model options view]
  (let [children (sort-by :name (view/elements-in-view model view))]
    (flatten [(md/h1 (:title view))
              (map #(render-element model % options view) children)])))

;;;
;;; Markdown Rendering dispatch
;;;
(def markdown-views
  "Contains the views rendered with markdown."
  #{:concept-view :glossary-view
    :context-view :container-view :component-view :system-landscape-view})

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
  (with-open [wrt (io/writer (rndr/render-file model format options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-markdown-view model options view))))))

(defmethod rndr/render :markdown
  [model format options]
  (doseq [view (view/get-views model)]
    (when (markdown-view? view)
      (rndr/render-view model format options view))))
