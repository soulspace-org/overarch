(ns org.soulspace.overarch.domain.views.class-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :class-view
  [model view e]
  (contains? el/class-view-element-types (:el e)))

(defmethod view/include-content? :class-view
  [model view e]
  (contains? el/class-view-element-types (:el e)))

(defmethod view/element-to-render :class-view
  [model view e]
  e)
