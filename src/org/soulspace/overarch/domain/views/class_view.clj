(ns org.soulspace.overarch.domain.views.class-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :class-view
  [view e]
  (contains? view/class-view-element-types (:el e)))

(defmethod view/include-content? :class-view
  [view e]
  (contains? view/class-view-element-types (:el e)))

(defmethod view/render-relation-node? :class-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :class-view
  [view e]
  e)
