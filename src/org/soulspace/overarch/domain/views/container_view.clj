(ns org.soulspace.overarch.domain.views.container-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))


(def element->boundary
  "Maps model types to boundary type."
  {:system :system-boundary})

(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this view type, false otherwise."
  [e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary (:el e))
   (el/internal? e)))

(defn render-model-node?
  "Returns true if the node `e` is rendered in the container view."
  [model view e]
  (and (contains? el/container-view-element-types (:el e))
       (el/internal? (model/parent model e))))

(defn render-model-relation?
  "Returns true if the relation `e` is rendered in the container view."
  [model view e]
  (let [from (model/model-element model (:from e))
        to (model/model-element model (:to e))]
    (and (contains? el/container-view-element-types (:el e))
         (render-model-node? model view from)
         (render-model-node? model view to)
         ; exclude system boundaries
         (or (not (as-boundary? from))
             (not (as-boundary? to))))))

(defmethod view/render-model-element? :container-view
  [model view e]
  (if (el/model-relation? e)
    (render-model-relation? model view e)
    (render-model-node? model view e)))

(defmethod view/include-content? :container-view
  [model view e]
  (and (contains? el/container-view-element-types (:el e))
       (el/boundary? e)))

(defmethod view/render-relation-node? :container-view
  [model view e]
  (and (view/render-model-element? model view e)
       ; exclude system and container boundaries
      (not (as-boundary? e))))

(defmethod view/element-to-render :container-view
  [view e]
  (if (as-boundary? e)
    ; e has a boundary type and has children, render as boundary
    (assoc e :el (keyword (str (name (:el e)) "-boundary")))
    ; render e as normal model element
    e))
