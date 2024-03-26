(ns org.soulspace.overarch.domain.views.dynamic-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :dynamic-view
  [model view e]
  (contains? el/dynamic-view-element-types (:el e)))

(defmethod view/include-content? :dynamic-view
  [model view e]
  (contains? el/dynamic-view-element-types (:el e)))

(defmethod view/element-to-render :dynamic-view
  [model view e]
  e)
