(ns org.soulspace.overarch.domain.views.system-structure-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-element? :system-structure-view
  [model view e]
  (contains? el/system-structure-view-element-types (:el e)))

(defmethod view/include-content? :system-structure-view
  [model view e]
  (contains? el/system-structure-view-element-types (:el e)))

(defmethod view/element-to-render :system-structure-view
  [model view e]
  e)
