(ns org.soulspace.overarch.domain.views.deployment-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :deployment-view
  [view e]
  (contains? model/deployment-types (:el e)))

(defmethod view/include-content? :deployment-view
  [view e]
  (contains? model/deployment-types (:el e)))

(defmethod view/element-to-render :deployment-view
  [view e]
  e)