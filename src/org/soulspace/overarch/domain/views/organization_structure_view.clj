(ns org.soulspace.overarch.domain.views.organization-structure-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-model-element? :organization-structure-view
  [model view e]
  (contains? el/system-structure-view-element-types (:el e)))

(defmethod view/include-content? :organization-structure-view
  [model view e]
  (contains? el/system-structure-view-element-types (:el e)))

(defmethod view/element-to-render :organization-structure-view
  [model view e]
  e)
