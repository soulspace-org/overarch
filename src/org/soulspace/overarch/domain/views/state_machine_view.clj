(ns org.soulspace.overarch.domain.views.state-machine-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :state-machine-view
  [view e]
  (contains? e/state-machine-types (:el e)))

(defmethod view/include-content? :state-machine-view
  [view e]
  (contains? e/state-machine-types (:el e)))

(defmethod view/render-relation-node? :state-machine-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :state-machine-view
  [view e]
  e)
