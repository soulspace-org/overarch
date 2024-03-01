(ns org.soulspace.overarch.domain.views.deployment-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :deployment-view
  [view e]
  (contains? el/deployment-view-element-types (:el e)))

(defmethod view/include-content? :deployment-view
  [view e]
  (contains? el/deployment-view-element-types (:el e)))

(defmethod view/render-relation-node? :deployment-view
  [view e]
  (view/render-model-element? view e))

(defmethod view/element-to-render :deployment-view
  [view e]
  e)
