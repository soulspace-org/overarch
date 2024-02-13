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
(def element-hierarchy
  "Hierarchy for elements to render."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :architecture-model-element)
      (derive :context-boundary    :architecture-model-element)
      (derive :system-boundary     :architecture-model-element)
      (derive :container-boundary  :architecture-model-element)
      (derive :system              :architecture-model-element)
      (derive :container           :architecture-model-element)
      (derive :component           :architecture-model-element)
      ))

; TODO add model parameter to resolve referrers and referred
(defmulti render-element
  "Renders an `element` in the `view` with markdown according to the given `options`."
  (fn [e _ _] (:el e))
  :hierarchy #'element-hierarchy)

(defmethod render-element :concept
  [e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))])

(defmethod render-element :person
  [e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))])

(defmethod render-element :architecture-model-element
  [e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))
   ; TODO model
   ;(when ()
   ;  (md/h3 "Relates to "))
   ])

(defmethod render-element :rel
  [e options view]
  "")

(defn render-markdown-view
  "Renders the `view` with markdown according to the given `options`."
  [m options view]
  (let [children (sort-by :name (view/elements-in-view m view))]
    (flatten [(md/h1 (:title view))
              (map #(render-element % options view) children)])))

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
  [m format options view]
  (let [dir-name (str (:render-dir options) "/markdown/"
                      (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".md"))))

(defmethod rndr/render-view :markdown
  [m format options view]
  (with-open [wrt (io/writer (rndr/render-file m format options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-markdown-view m options view))))))

(defmethod rndr/render :markdown
  [m format options]
  (doseq [view (view/get-views m)]
    (when (markdown-view? view)
      (rndr/render-view m format options view))))
