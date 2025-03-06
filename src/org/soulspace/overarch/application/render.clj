(ns org.soulspace.overarch.application.render
  "Contains dispatch functions for the rendering of the views."
  ; require views here to register multimethods
  (:require [clojure.string :as str]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.views.code-view :as code-view]
            [org.soulspace.overarch.domain.views.component-view :as component-view]
            [org.soulspace.overarch.domain.views.concept-view :as concept-view]
            [org.soulspace.overarch.domain.views.container-view :as container-view]
            [org.soulspace.overarch.domain.views.context-view :as context-view]
            [org.soulspace.overarch.domain.views.deployment-view :as deployment-view]
            [org.soulspace.overarch.domain.views.deployment-structure-view :as deployment-structure-view]
            [org.soulspace.overarch.domain.views.dynamic-view :as dynamic-view]
            [org.soulspace.overarch.domain.views.glossary-view :as glossary-view]
            [org.soulspace.overarch.domain.views.model-view :as model-view]
            [org.soulspace.overarch.domain.views.organization-structure-view :as organization-structure-view]
            [org.soulspace.overarch.domain.views.process-view :as process-view]
            [org.soulspace.overarch.domain.views.state-machine-view :as state-machine-view]
            [org.soulspace.overarch.domain.views.system-landscape-view :as system-landscape-view]
            [org.soulspace.overarch.domain.views.system-structure-view :as system-structure-view]
            [org.soulspace.overarch.domain.views.use-case-view :as use-case-view]
            ))

;;;
;;; General rendering functions
;;;
(defn indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

;;;
;;; Render multimethods 
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

(def render-formats
  "Contains the supported render formats."
  #{:graphviz :markdown :plantuml})

(defn render
  "Renders all relevant views of the `model` in the given `format`.
   Views marked as external (e.g. by setting the namespace scope in the `options`) are not rendered."
  [model format options]
  
  (when (:debug options)
    (when-let [excluded-views (map :id (filter el/external? (model/views model)))]
      (println "Excluding external views from rendering" excluded-views)))

  (if (= :all format)
    (doseq [current-format render-formats]
      (when (:debug options)
        (println "Rendering " current-format))
      (render model current-format options))
    (doseq [view (remove el/external? (model/views model))]
      (try
        (render-view model format options view)
        (catch Exception e
          (println "Error rendering view" (:id view) "in format" format ".")
          (println (ex-message e))
          (when (:debug options)
            (.printStacktrace e)))))))
