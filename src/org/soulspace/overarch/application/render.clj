(ns org.soulspace.overarch.application.render
  "Contains dispatch functions for the rendering of the views."
  ; require views here to register multimethods
  (:require [org.soulspace.overarch.domain.views.class-view :as class-view]
            [org.soulspace.overarch.domain.views.component-view :as component-view]
            [org.soulspace.overarch.domain.views.concept-view :as concept-view]
            [org.soulspace.overarch.domain.views.container-view :as container-view]
            [org.soulspace.overarch.domain.views.context-view :as context-view]
            [org.soulspace.overarch.domain.views.deployment-view :as deployment-view]
            [org.soulspace.overarch.domain.views.dynamic-view :as dynamic-view]
            [org.soulspace.overarch.domain.views.glossary-view :as glossary-view]
            [org.soulspace.overarch.domain.views.state-machine-view :as state-machine-view]
            [org.soulspace.overarch.domain.views.system-landscape-view :as system-landscape-view]
            [org.soulspace.overarch.domain.views.use-case-view :as use-case-view]
            ))

;;;
;;; Export multimethods 
;;;  

(defn render-format
  "Returns the render format for the multimethod invocation."
  ([m format]
   format)
  ([m format options]
   format)
  ([m format options view]
   format))

(defmulti render-file
  "Returns the render file for the given format."
  render-format)

(defmulti render-view
  "Renders the view in the given format." 
  render-format)

(defmulti render
  "Renders all relevant views in the given format."
  render-format)
