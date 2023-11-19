(ns org.soulspace.overarch.domain.views.use-case-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :use-case-view
  [view e]
  (contains? model/use-case-types (:el e)))

(defmethod view/include-content? :context-view
  [view e]
  (contains? model/use-case-types (:el e)))

