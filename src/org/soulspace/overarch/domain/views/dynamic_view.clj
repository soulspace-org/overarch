(ns org.soulspace.overarch.domain.views.dynamic-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :dynamic-view
  [view e]
  (contains? e/dynamic-types (:el e)))

(defmethod view/include-content? :dynamic-view
  [view e]
  (contains? e/dynamic-types (:el e)))

(defmethod view/render-relation-node? :dynamic-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :dynamic-view
  [view e]
  e)
