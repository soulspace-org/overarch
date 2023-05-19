(ns org.soulspace.overarch.structurizr
  "Functions for the export to structurizr."
  (:require [clojure.string :as str]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.diagram :as dia]
            [org.soulspace.clj.string :as sstr]))

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

(def element-hierarchy
  "Hierarchy for rendering elements."
  (-> (make-hierarchy)
      (derive :person :model-element)
      (derive :system :model-element)
      (derive :container :model-element)
      (derive :component :model-element)
      (derive :node :model-element)
      (derive :enterprise-boundary :model-element)
      ))

(defn alias-name
  "Returns the alias name for the element."
  [id]
  (sstr/hyphen-to-camel-case (name id)))

(defmulti render-element
  "Renders a structurizr model element."
  (fn [indent e] (:el e))
  :hierarchy #'element-hierarchy)

(defmethod render-element :rel
  [indent e]
  [(str (alias-name (:from e)) " -> " (alias-name (:to e))
        " \"" (:name e) "\""
        (when (:tech e) (str " \"" (:tech e) "\"")))])

(defmethod render-element :model-element
  [indent e]
  (if (:ct e)
    (let [children (map core/resolve-ref (:ct e))]
      [(str (dia/render-indent indent) (alias-name (:id e)) " = "
            (element-type->structurizr (:el e))
            " \"" (dia/element-name e) "\" {")
       (map (partial render-element (+ indent 2)) children)
       (str (dia/render-indent indent) "}")])
    [(str (dia/render-indent indent) (alias-name (:id e)) " = "
          (element-type->structurizr (:el e))
          " \"" (dia/element-name e) "\"")]))

(defn render-model
  "Renders the structurizr model."
  [elements]
  (flatten [(str (dia/render-indent 2) "model {")
            (map (partial render-element 4)  elements)
            (str (dia/render-indent 2) "}")])
  )

; TODO render rels only
(defn render-view-element
  "Renders an element of a view."
  [indent e]
  [(str (dia/render-indent indent) (:id e))]
  )

(defn render-view
  "Renders a structurizr view."
  [view]
  (flatten [(str (dia/render-indent 4)
                 (diagram-type->structurizr (:el view))
                 " \"" (sstr/first-upper-case
                        (sstr/hyphen-to-camel-case
                         (diagram-type->structurizr (:el view)))) "\" {\n")
            (if (:ct view)
              (let [children (dia/elements-to-render view)]
                (map (partial render-view-element 6) children))
              (str (dia/render-indent 6) "include *\n"))
            (when (:title view)
              (str (dia/render-indent 6) "description \"" (:title view) "\"\n"))
            (str (dia/render-indent 4) "}")]))

(defn render-views
  "Renders the structurizr views."
  [views]
  (flatten [(str (dia/render-indent 2) "views {")
            (map render-view views)
            (str (dia/render-indent 4) "theme default")
            (str (dia/render-indent 2) "}")])
  )

(defn render-workspace
  "Renders a structurizr workspace."
  ([]
   [(str "workspace {")
    (render-model (core/get-model-elements))
    (render-views (core/get-diagrams))
    "}"])
  ([m]
   [(str "workspace {")
    (render-model (core/get-model-elements))
    (render-views (core/get-diagrams))
    "}"]))

(comment
  (println (str/join "\n" (flatten (render-workspace))))
  )