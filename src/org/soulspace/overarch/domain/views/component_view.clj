(ns org.soulspace.overarch.domain.views.component-view
  (:require [org.soulspace.overarch.domain.element :as e]
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
   (e/internal? e)))

(defmethod view/render-model-node? :component-view
  [view e]
  (contains? e/component-view-types (:el e)))

(defmethod view/include-content? :component-view
  [view e]
  (and (contains? e/component-view-types (:el e))
       (e/boundary? e)))

(defmethod view/render-relation-node? :component-view
  [view e]
  (and (view/render-model-node? view e)
       ; exclude system and container boundaries
       (not (as-boundary? e))))

(defmethod view/element-to-render :component-view
  [view e]
  (if (as-boundary? e)
      ; e has a boundary type and has children, render as boundary
    (assoc e :el (keyword (str (name (:el e)) "-boundary")))
      ; render e as normal model element
    e))

(comment
;
  )