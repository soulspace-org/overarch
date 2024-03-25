(ns org.soulspace.overarch.domain.views.component-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {:system    :system-boundary
   :container :container-boundary})

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

(defmethod view/render-model-element? :component-view
  [model view e]
  (contains? el/component-view-element-types (:el e)))

(defmethod view/include-content? :component-view
  [model view e]
  (and (contains? el/component-view-element-types (:el e))
       (el/boundary? e)))

(defmethod view/render-relation-node? :component-view
  [model view e]
  (and (view/render-model-element? model view e)
       ; exclude system and container boundaries
       (not (as-boundary? e))))

(defmethod view/element-to-render :component-view
  [model view e]
  (if (as-boundary? e)
      ; e has a boundary type and has children, render as boundary
    (assoc e :el (keyword (str (name (:el e)) "-boundary")))
      ; render e as normal model element
    e))

(comment
;
  )