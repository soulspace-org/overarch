(ns org.soulspace.overarch.domain.views.class-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-node? :class-view
  [view e]
  (contains? model/class-types (:el e)))

(defmethod view/include-content? :class-view
  [view e]
  (contains? model/class-types (:el e)))

(defmethod view/element-to-render :class-view
  [view e]
  e)
