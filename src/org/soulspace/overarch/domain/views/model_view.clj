(ns org.soulspace.overarch.domain.views.model-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-element? :model-view
  [model view e]
  true)

(defmethod view/include-content? :model-view
  [model view e]
  (contains? el/model-view-element-types (:el e)))

(defmethod view/element-to-render :model-view
  [model view e]
  e)
