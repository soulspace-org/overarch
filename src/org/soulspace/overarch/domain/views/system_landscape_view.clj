(ns org.soulspace.overarch.domain.views.system-landscape-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :system-landscape-view
  [view e]
  (contains? view/system-landscape-view-element-types (:el e)))

(defmethod view/include-content? :system-landscape-view
  [view e]
  (contains? view/system-landscape-view-element-types (:el e)))

(defmethod view/render-relation-node? :system-landscape-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :system-landscape-view
  [view e]
  e)
