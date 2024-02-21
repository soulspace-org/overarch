(ns org.soulspace.overarch.domain.views.glossary-view
  (:require [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-node? :glossary-view
  [view e]
  (contains? e/glossary-view-types (:el e)))

(defmethod view/include-content? :glossary-view
  [view e]
  (contains? e/glossary-view-types (:el e)))

(defmethod view/render-relation-node? :glossary-view
  [view e]
  (view/render-model-node? view e))

(defmethod view/element-to-render :glossary-view
  [view e]
  e)
