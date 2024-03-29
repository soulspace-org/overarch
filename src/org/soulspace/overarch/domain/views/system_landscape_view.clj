(ns org.soulspace.overarch.domain.views.system-landscape-view
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]))

(defmethod view/render-model-element? :system-landscape-view
  [model view e]
  (contains? el/system-landscape-view-element-types (:el e)))

(defmethod view/include-content? :system-landscape-view
  [model view e]
  (contains? el/system-landscape-view-element-types (:el e)))

(defmethod view/element-to-render :system-landscape-view
  [model view e]
  e)
