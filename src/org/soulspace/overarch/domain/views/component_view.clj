(ns org.soulspace.overarch.domain.views.component-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :component-view
  [view e]
  (contains? model/component-types (:el e)))

(defmethod view/render-content? :component-view
  [view e]
  (contains? model/component-types (:el e)))

