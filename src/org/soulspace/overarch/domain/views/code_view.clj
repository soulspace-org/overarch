(ns org.soulspace.overarch.domain.views.code-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(def element->boundary
  "Maps model types to boundary type."
  {})

(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this view type, false otherwise."
  [e]
  (when (seq e)
    (or (el/boundary? e) ; regular boundary
        (and
         (seq (:ct e)) ; has children 
         (element->boundary (:el e)) ; has a boundary mapping for this diagram-type
         (el/internal? e)))))

(defn render-model-node?
  "Returns true if the `model` node `e` is rendered in the code `view`."
  [model view e]
  (let [p (model/parent model e)]
    (contains? el/code-view-element-types (:el e))))

(defn render-model-relation?
  "Returns true if the `model` relation `e` is rendered in the code `view`."
  [model view e]
  (let [from (model/model-element model (:from e))
        to (model/model-element model (:to e))]
    (and (contains? el/code-view-element-types (:el e))
         (render-model-node? model view from)
         (render-model-node? model view to))))

(defmethod view/render-model-element? :code-view
  [model view e]
  (if (el/model-relation? e)
    (render-model-relation? model view e)
    (render-model-node? model view e)))

(defmethod view/include-content? :code-view
  [model view e]
  (contains? el/code-view-element-types (:el e)))

(defmethod view/element-to-render :code-view
  [model view e]
  e)
