(ns org.soulspace.overarch.domain.views.dynamic-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :dynamic-view
  [view e]
  (contains? model/dynamic-types (:el e)))

(defmethod view/render-content? :dynamic-view
  [view e]
  (contains? model/dynamic-types (:el e)))

