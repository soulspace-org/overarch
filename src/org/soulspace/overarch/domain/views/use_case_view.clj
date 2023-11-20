(ns org.soulspace.overarch.domain.views.use-case-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-node? :use-case-view
  [view e]
  (contains? model/use-case-types (:el e)))

(defmethod view/include-content? :use-case-view
  [view e]
  (contains? model/use-case-types (:el e)))

(defmethod view/render-relation-node? :use-case-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :use-case-view
  [view e]
  e)
