(ns org.soulspace.overarch.domain.views.context-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))



(defmethod view/render-element? :context-view
  [view e]
  (contains? model/context-types (:el e)))

(defmethod view/include-content? :context-view
  [view e]
  (contains? model/context-types (:el e)))

