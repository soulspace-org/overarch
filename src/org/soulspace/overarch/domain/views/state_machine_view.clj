(ns org.soulspace.overarch.domain.views.state-machine-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(def element->boundary
  "Maps model types to boundary type."
  {})

(defn as-boundary?
  "Returns the boundary element, if the element `e` should be rendered
   as a boundary for this view type, false otherwise."
  [model view e]
  (when (seq e)
    (or (el/boundary? e) ; regular boundary
        (and
         (seq (model/children model e)) ; has children 
         (element->boundary (:el e)) ; has a boundary mapping for this diagram-type
         (el/internal? e)
         (not (el/collapsed? e))))))

(defn render-model-node?
  "Returns true if the `model` node `e` is rendered in the state-machine `view`."
  [model view e]
  (let [p (model/parent model e)]
    (contains? el/state-machine-view-element-types (:el e))))

(defn render-model-relation?
  "Returns true if the `model` relation `e` is rendered in the state-machine `view`."
  [model view e]
  (let [from (model/model-element model (:from e))
        to (model/model-element model (:to e))]
    (and (contains? el/state-machine-view-element-types (:el e))
         (render-model-node? model view from)
         (render-model-node? model view to))))

(defmethod view/render-model-element? :state-machine-view
  [model view e]
  (if (el/model-relation? e)
    (render-model-relation? model view e)
    (render-model-node? model view e)))

(defmethod view/include-content? :state-machine-view
  [model view e]
  (contains? el/state-machine-view-element-types (:el e)))

(defmethod view/element-to-render :state-machine-view
  [model view e]
  e)
