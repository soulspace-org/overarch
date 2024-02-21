(ns org.soulspace.overarch.domain.views.deployment-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :deployment-view
  [view e]
  (contains? e/deployment-view-types (:el e)))

(defmethod view/include-content? :deployment-view
  [view e]
  (contains? e/deployment-view-types (:el e)))

(defmethod view/render-relation-node? :deployment-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :deployment-view
  [view e]
  e)
