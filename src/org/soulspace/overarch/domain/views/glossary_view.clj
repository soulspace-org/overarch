(ns org.soulspace.overarch.domain.views.glossary-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-node? :glossary-view
  [view e]
  (contains? model/glossary-types (:el e)))

(defmethod view/include-content? :glossary-view
  [view e]
  (contains? model/glossary-types (:el e)))

(defmethod view/element-to-render :glossary-view
  [view e]
  e)