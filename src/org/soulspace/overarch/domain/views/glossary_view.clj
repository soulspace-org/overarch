(ns org.soulspace.overarch.domain.views.glossary-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :glossary-view
  [model view e]
  (contains? el/glossary-view-element-types (:el e)))

(defmethod view/include-content? :glossary-view
  [model view e]
  false)

(defmethod view/element-to-render :glossary-view
  [model view e]
  e)
