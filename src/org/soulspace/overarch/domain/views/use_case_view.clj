(ns org.soulspace.overarch.domain.views.use-case-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :use-case-view
  [view e]
  (contains? el/use-case-view-element-types (:el e)))

(defmethod view/include-content? :use-case-view
  [view e]
  (contains? el/use-case-view-element-types (:el e)))

(defmethod view/render-relation-node? :use-case-view
  [view e]
  (view/render-model-element? view e))

(defmethod view/element-to-render :use-case-view
  [view e]
  e)
