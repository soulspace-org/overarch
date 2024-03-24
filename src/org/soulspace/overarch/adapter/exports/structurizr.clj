;;;;
;;;; Structurizr export (architecture model and views only)
;;;;
(ns org.soulspace.overarch.adapter.exports.structurizr
  "Functions for the export to structurizr."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.export :as export]))

(def element-type->structurizr
  "Maps the element to a structurizr type."
  {:person "person"
   :system "softwaresystem"
   :container "container"
   :component "component"
   :node "deploymentNode"})

(def view-type->structurizr
  "Maps the view type"
  {:system-landscape-view "systemlandscape"
   :context-view "systemcontext"
   :container-view "container"
   :component-view "component"
   :deployment-view "deployment"
   :dynamic-view "dynamic"})

(def element-hierarchy
  "Hierarchy for rendering elements."
  (-> (make-hierarchy)
      (derive :person :model-element)
      (derive :system :model-element)
      (derive :container :model-element)
      (derive :component :model-element)
      (derive :node :model-element)
      (derive :enterprise-boundary :model-element)
      (derive :context-boundary :model-element)))

(def structurizr-elements
  "Contains the model element types exported to structurizr."
  (set/union el/component-view-element-types
             el/deployment-view-element-types))

(defn structurizr-element?
  "Returns true, if the element `e` is to be exported to structurizr."
  [e]
  (contains? structurizr-elements (:el e)))

(def structurizr-views
  "Contains the views types exported to structurizr."
  #{:system-landscape-view :context-view :container-view :component-view
    :deployment-view :dynamic-view})

(defn structurizr-view?
  "Returns true, if the `view` is to be exported to structurizr."
  [view]
  (contains? structurizr-views (:el view)))

(defn alias-name
  "Returns the alias name for the element `id` ."
  [id]
  (sstr/hyphen-to-camel-case (name id)))

(defmulti render-element
  "Renders a structurizr model element."
  (fn [_ _ e] (:el e)) :hierarchy #'element-hierarchy)

(defmethod render-element :rel
  [m indent e]
  [(str (export/indent indent)
        (alias-name (:from e)) " -> " (alias-name (:to e))
        " \"" (:name e) "\""
        (when (:tech e) (str " \"" (:tech e) "\"")))])

(defmethod render-element :model-element
  [model indent e]
  (if (:ct e)
    (let [children (map (partial model/resolve-element model) (:ct e))]
      [(str (export/indent indent) (alias-name (:id e)) " = "
            (element-type->structurizr (:el e))
            " \"" (el/element-name e) "\" {")
       (map (partial render-element model (+ indent 2)) children)
       (str (export/indent indent) "}")])
    [(str (export/indent indent) (alias-name (:id e)) " = "
          (element-type->structurizr (:el e))
          " \"" (el/element-name e) "\"")]))

(defn render-model
  "Renders the structurizr model."
  [model elements]
  (flatten [(str (export/indent 2) "model {")
            (map (partial render-element model 4) 
                 (filter structurizr-element? elements))
            (str (export/indent 2) "}")])
  )

(defn render-view
  "Renders a structurizr view."
  [model view]
  (flatten [(str (export/indent 4)
                 (view-type->structurizr (:el view))
                 " \"" (sstr/first-upper-case
                        (sstr/hyphen-to-camel-case
                         (view-type->structurizr (:el view)))) "\" {\n")
            (if (:ct view)
              (let [children (filter el/model-relation?
                                     (view/elements-to-render model view))]
                (map (partial render-element model 6) children))
              (str (export/indent 6) "include *\n"))
            (when (:title view)
              (str (export/indent 6) "description \"" (:title view) "\"\n"))
            (str (export/indent 4) "}")]))

(defn render-views
  "Renders the structurizr views."
  [model views]
  (flatten [(str (export/indent 2) "views {")
            (map (partial render-view model) (filter structurizr-view? views))
            (str (export/indent 4) "theme default")
            (str (export/indent 2) "}")]))

(defn render-workspace
  "Renders a structurizr workspace."
  [model]
  (flatten [(str "workspace {")
            (render-model model (model/model-elements model))
            (render-views model (view/views model))
            "}"]))

(defmethod export/export-file :structurizr
  [model format options]
  (let [dir-name (str (:export-dir options) "/structurizr/")
        workspace (namespace (:id (first (model/model-elements model))))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/" workspace ".dsl"))))

(defmethod export/export :structurizr
  [model format options]
  (with-open [wrt (io/writer (export/export-file model format options))]
    (binding [*out* wrt]
      (println (str/join "\n" (doall (render-workspace model)))))))
