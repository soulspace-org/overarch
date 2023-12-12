(ns org.soulspace.overarch.domain.views.context-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))


(defmethod view/render-model-node? :context-view
  [view e]
  (contains? e/context-types (:el e)))

(defmethod view/include-content? :context-view
  [view e]
  (and (contains? e/context-types (:el e))
       (e/boundary? e)))

(defmethod view/render-relation-node? :context-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :context-view
  [view e]
  e)
