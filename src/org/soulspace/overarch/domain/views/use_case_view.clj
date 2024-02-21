(ns org.soulspace.overarch.domain.views.use-case-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :use-case-view
  [view e]
  (contains? e/use-case-view-types (:el e)))

(defmethod view/include-content? :use-case-view
  [view e]
  (contains? e/use-case-view-types (:el e)))

(defmethod view/render-relation-node? :use-case-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :use-case-view
  [view e]
  e)
