(ns org.soulspace.overarch.structurizr
  "Functions for the export to structurizr."
  (:require [clojure.string :as str]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.diagram :as dia]))

(def element-type->structurizr
  "Maps the element to a structurizr type."
  {:person "person"
   :system "softwaresystem"
   :container "container"
   :component "component"
   :node "deploymentNode"})

(def diagram-type->structurizr
  "Maps the diagram type"
  {:system-landscape-diagram "systemlandscape"
   :context-diagram "systemcontext"
   :container-diagram "container"
   :component-diagram "component"
   :deployment-diagram "deployment"
   :dynamic-diagram "dynamic"})

(defn alias-name
  "Returns the alias name for the element."
  [id]
  (name id))


(defn render-relation
  "Renders a structurizr relation."
  ([e]
   [(str (alias-name (:from e)) " -> " (alias-name (:to e))
         " \"" (:name e) "\""
         (when (:tech e) (str " \"" (:tech e) "\"")))]))

(defn render-element
  "Renders a structurizr model element."
  ([e]
   (render-element 4 e))
  ([indent e]
   (if (:ct e)
     [(str (dia/render-indent indent) (alias-name e) " = "
           (element-type->structurizr (:el e)) " "
           (:name e) "{")
      (map (partial render-element (+ indent 2)) (:ct e))
      (str (dia/render-indent indent) "}")]
     [(str (dia/render-indent indent) (alias-name e) " = "
           (element-type->structurizr (:el e)) " "
           (:name e))])))

(defn render-model
  "Renders the structurizr model."
  [elements]
  [(str (dia/render-indent 2) "model {")
   (map render-element elements)
   (str (dia/render-indent 2) "}")]
  )

(defn render-view
  "Renders a structurizr view."
  [view]
  [(str (dia/render-indent 4) "{")
   
   (str (dia/render-indent 4) "}")]
  )

(defn render-views
  "Renders the structurizr views."
  [views]
  [(str (dia/render-indent 2) "views {")
   (map render-view views)
   (str (dia/render-indent 4) "theme default")
   (str (dia/render-indent 2) "}")]
  )

(defn render-workspace
  "Renders a structurizr workspace."
  []
  [(str "workspace {")
   (render-model (core/get-model-elements))
   (render-views (core/get-diagrams))
   "}"])
