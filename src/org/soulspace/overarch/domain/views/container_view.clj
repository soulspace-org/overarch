(ns org.soulspace.overarch.domain.views.container-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))


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
   (not (:external e))))

(defmethod view/render-element? :container-view
  [view e]
  (contains? model/container-types (:el e)))

(defmethod view/include-content? :container-view
  [view e]
  (contains? model/container-types (:el e)))

(defmethod view/element-to-render :component-view
  [view e]
  (if (as-boundary? e)
      ; e has a boundary type and has children, render as boundary
    (assoc e :el (keyword (str (name (:el e)) "-boundary")))
      ; render e as normal model element
    e))
