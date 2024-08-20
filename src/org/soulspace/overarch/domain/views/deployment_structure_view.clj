(ns org.soulspace.overarch.domain.views.deployment-structure-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-element? :deployment-structure-view
  [model view e]
  (contains? el/deployment-structure-view-element-types (:el e)))

(defmethod view/include-content? :deployment-structure-view
  [model view e]
  (contains? el/deployment-structure-view-element-types (:el e)))

(defmethod view/element-to-render :deployment-structure-view
  [model view e]
  e)
