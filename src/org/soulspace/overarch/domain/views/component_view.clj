(ns org.soulspace.overarch.domain.views.component-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {:system    :system-boundary
   :container :container-boundary})

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
  "Returns true if the node `e` is rendered in the component view."
  [model view e]
  (let [p (model/parent model e)]
    (and (contains? el/component-view-element-types (:el e))
         (or (not p) ; has no parent
             (as-boundary? p) ; parent is rendered as boundary
             ))))

(defn render-model-relation?
  "Returns true if the relation `e` is rendered in the container view."
  [model view e]
  (let [from (model/model-element model (:from e))
        to (model/model-element model (:to e))]
    (and (contains? el/component-view-element-types (:el e))
         (render-model-node? model view from)
         (render-model-node? model view to)
         ; exclude system boundaries
         (or (not (as-boundary? from))
             (not (as-boundary? to))))))

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
  (if (and (as-boundary? e) (element->boundary (:el e)))
    ; e should be rendered as a boundary
    (assoc e :el (element->boundary (:el e)))
    ; render e as normal model element
    e))
