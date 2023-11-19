(ns org.soulspace.overarch.domain.views.container-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :container-view
  [view e]
  (contains? model/container-types (:el e)))

(defmethod view/render-content? :container-view
  [view e]
  (contains? model/container-types (:el e)))

