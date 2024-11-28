(ns org.soulspace.overarch.domain.views.component-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {:system    :system-boundary
   :container :container-boundary})

(defn as-boundary?
  "Returns the boundary element, if the element `e` should be rendered
   as a boundary for this view type, false otherwise."
  [model view e]
  (when (seq e)
    (or (el/boundary? e) ; regular boundary
        (and
         (seq (model/children model e))        ; has children 
         (element->boundary (:el e))           ; has a boundary mapping for this diagram-type
         (or (el/internal? e)                  ; is internal
             (view/expand-external-spec view)) ; should be expanded
         ))))

(defn render-model-node?
  "Returns true if the `model` node `e` is rendered in the component `view`."
  [model view e]
  (let [p (model/parent model e)]
    (and (contains? el/component-view-element-types (:el e))
         (or (not p) ; has no parent
             (as-boundary? model view p) ; parent is rendered as boundary
             ))))

(defn render-model-relation?
  "Returns true if the `model` relation `e` is rendered in the component `view`."
  [model view e]
  (let [from (model/model-element model (:from e))
        to (model/model-element model (:to e))]
    (and (contains? el/component-view-element-types (:el e))
         (render-model-node? model view from)
         (render-model-node? model view to)
         ; exclude system boundaries
         (and (not (as-boundary? model view from))
              (not (as-boundary? model view to))))))

(defmethod view/render-model-element? :component-view
  [model view e]
  (if (el/model-relation? e)
   (render-model-relation? model view e)
   (render-model-node? model view e)))

(defmethod view/include-content? :component-view
  [model view e]
  (and (contains? el/component-view-element-types (:el e))
       (el/boundary? e)))

(defmethod view/element-to-render :component-view
  [model view e]
  (if (and (as-boundary? model view e) (element->boundary (:el e)))
    ; e should be rendered as a boundary
    (assoc e :el (element->boundary (:el e)))
    ; render e as normal model element
    e))
