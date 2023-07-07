(ns org.soulspace.overarch.exports.structurizr
  "Functions for the export to structurizr."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.view :as view]
            [org.soulspace.overarch.exports.core :as exp]
            [org.soulspace.clj.string :as sstr]))

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

(defn alias-name
  "Returns the alias name for the element."
  [id]
  (sstr/hyphen-to-camel-case (name id)))

(defmulti render-element
  "Renders a structurizr model element."
  (fn [indent e] (:el e)) :hierarchy #'element-hierarchy)

(defmethod render-element :rel
  [indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " -> " (alias-name (:to e))
        " \"" (:name e) "\""
        (when (:tech e) (str " \"" (:tech e) "\"")))])

(defmethod render-element :model-element
  [indent e]
  (if (:ct e)
    (let [children (map core/resolve-ref (:ct e))]
      [(str (view/render-indent indent) (alias-name (:id e)) " = "
            (element-type->structurizr (:el e))
            " \"" (view/element-name e) "\" {")
       (map (partial render-element (+ indent 2)) children)
       (str (view/render-indent indent) "}")])
    [(str (view/render-indent indent) (alias-name (:id e)) " = "
          (element-type->structurizr (:el e))
          " \"" (view/element-name e) "\"")]))

(defn render-model
  "Renders the structurizr model."
  [elements]
  (flatten [(str (view/render-indent 2) "model {")
            (map (partial render-element 4)  elements)
            (str (view/render-indent 2) "}")])
  )

(defn render-view
  "Renders a structurizr view."
  [view]
  (flatten [(str (view/render-indent 4)
                 (view-type->structurizr (:el view))
                 " \"" (sstr/first-upper-case
                        (sstr/hyphen-to-camel-case
                         (view-type->structurizr (:el view)))) "\" {\n")
            (if (:ct view)
              (let [children (filter core/relation?
                                     (view/elements-to-render view))]
                (map (partial render-element 6) children))
              (str (view/render-indent 6) "include *\n"))
            (when (:title view)
              (str (view/render-indent 6) "description \"" (:title view) "\"\n"))
            (str (view/render-indent 4) "}")]))

(defn render-views
  "Renders the structurizr views."
  [views]
  (flatten [(str (view/render-indent 2) "views {")
            (map render-view views)
            (str (view/render-indent 4) "theme default")
            (str (view/render-indent 2) "}")])
  )

(defn render-workspace
  "Renders a structurizr workspace."
  ([]
   (flatten [(str "workspace {")
    (render-model (core/get-model-elements))
    (render-views (core/get-views))
    "}"]))
  ([m]
   (flatten [(str "workspace {")
    (render-model (core/get-model-elements m))
    (render-views (core/get-views m))
    "}"])))

(defmethod exp/export-file :structurizr
  [options]
  (let [dir-name (str (:export-dir options) "/structurizr/")
        workspace (namespace (:id (first (core/get-model-elements))))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/" workspace ".dsl"))))

(defmethod exp/export :structurizr
  [options]
  (with-open [wrt (io/writer (exp/export-file options))]
    (binding [*out* wrt]
      (println (str/join "\n" (doall (render-workspace)))))))

(comment
  (println (str/join "\n" (flatten (render-workspace))))
  )
