(ns org.soulspace.overarch.domain.views.system-landscape-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.model :as model]))

(defmethod view/render-element? :ystem-landscape-view
  [view e]
  (contains? model/context-types (:el e)))

(defmethod view/include-content? :ystem-landscape-view
  [view e]
  (contains? model/context-types (:el e)))

