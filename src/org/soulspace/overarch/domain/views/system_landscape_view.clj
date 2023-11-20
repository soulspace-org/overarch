(ns org.soulspace.overarch.domain.views.system-landscape-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-node? :system-landscape-view
  [view e]
  (contains? model/context-types (:el e)))

(defmethod view/include-content? :system-landscape-view
  [view e]
  (contains? model/context-types (:el e)))

(defmethod view/render-relation-node? :system-landscape-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :system-landscape-view
  [view e]
  e)
