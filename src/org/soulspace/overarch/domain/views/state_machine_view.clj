(ns org.soulspace.overarch.domain.views.state-machine-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :state-machine-view
  [view e]
  (contains? model/state-machine-types (:el e)))

(defmethod view/include-content? :state-machine-view
  [view e]
  (contains? model/state-machine-types (:el e)))

