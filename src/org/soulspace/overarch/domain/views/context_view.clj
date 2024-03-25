(ns org.soulspace.overarch.domain.views.context-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))


(defmethod view/render-model-element? :context-view
  [model view e]
  (contains? el/context-view-element-types (:el e)))

(defmethod view/include-content? :context-view
  [model view e]
  (and (contains? el/context-view-element-types (:el e))
       (el/boundary? e)))

(defmethod view/render-relation-node? :context-view
  [model view e]
  (view/render-model-element? view e))

(defmethod view/element-to-render :context-view
  [model view e]
  e)
