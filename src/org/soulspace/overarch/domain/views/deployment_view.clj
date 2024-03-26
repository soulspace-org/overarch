(ns org.soulspace.overarch.domain.views.deployment-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :deployment-view
  [model view e]
  (contains? el/deployment-view-element-types (:el e)))

(defmethod view/include-content? :deployment-view
  [model view e]
  (contains? el/deployment-view-element-types (:el e)))

(defmethod view/element-to-render :deployment-view
  [model view e]
  e)
