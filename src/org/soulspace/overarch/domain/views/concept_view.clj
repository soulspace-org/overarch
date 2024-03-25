(ns org.soulspace.overarch.domain.views.concept-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :concept-view
  [model view e]
  (contains? el/concept-view-element-types (:el e)))

(defmethod view/include-content? :concept-view
  [model view e]
  false)

(defmethod view/element-to-render :concept-view
  [model view e]
  e)
