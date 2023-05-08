(ns org.soulspace.overarch.diagram
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.overarch.core :as arch]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]))

;;;;
;;;; PlantUML rendering
;;;;

(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)
      (derive :person-ext          :person)
      (derive :system-db           :system)
      (derive :system-queue        :system)
      (derive :system-ext          :system)
      (derive :system-db-ext       :system)
      (derive :system-queue-ext    :system)
      (derive :container-db        :container)
      (derive :container-queue     :container)
      (derive :container-ext       :container)
      (derive :container-db-ext    :container)
      (derive :container-queue-ext :container)
      (derive :component-db        :component)
      (derive :component-queue     :component)
      (derive :component-ext       :component)
      (derive :component-db-ext    :component)
      (derive :component-queue-ext :component)
      (derive :person              :desc)
      (derive :system              :desc)
      (derive :container           :tech)
      (derive :component           :tech)))

(def diagram-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-diagram          arch/context-level?
   :container-diagram        arch/container-level?
   :component-diagram        arch/component-level?
   :code-diagram             arch/code-level?
   :system-landscape-diagram arch/system-landscape-level?
   :dynamic-diagram          arch/dynamic-level?
   :deployment-diagram       arch/deployment-level?})

(def element->boundary
  "Maps model types to boundary types depending on the diagram type."
  {[:container-diagram :system]          :system-boundary
   [:component-diagram :system]          :system-boundary
   [:component-diagram :system-db]       :system-boundary
   [:component-diagram :system-queue]    :system-boundary
   [:component-diagram :container]       :container-boundary
   [:component-diagram :container-db]    :container-boundary
   [:component-diagram :container-queue] :container-boundary})

(def element->methods
  "Map from element type to PlantUML method."
  {:person              "Person"
   :person-ext          "Person"
   :system              "System"
   :system-ext          "System_Ext"
   :system-db           "SystemDb"
   :system-db-ext       "SystemDb_Ext"
   :system-queue        "SystemQueue"
   :system-queue-ext    "SystemQueue_Ext"
   :boundary            "Boundary"
   :enterprise-boundary "Enterprise_Boundary"
   :system-boundary     "System_Boundary"
   :container           "Container"
   :container-ext       "Container_Ext"
   :container-db        "ContainerDb"
   :container-db-ext    "ContainerDb_Ext"
   :container-queue     "ContainerQueue"
   :container-queue-ext "ContainerQueue_Ext"
   :container-boundary  "Container_Boundary"
   :component           "Component"
   :component-ext       "Component_Ext"
   :component-db        "ComponentDb"
   :component-db-ext    "ComponentDb_Ext"
   :component-queue     "ComponentQueue"
   :component-queue-ext "ComponentQueue_Ext"
   :node                "Node"
   :rel                 "Rel"})

(def layouts
  "Maps layout keys to PlantUML."
  {:landscape "LAYOUT_LANDSCAPE()"
   :left-right "LAYOUT_LEFT_RIGHT()"
   :top-down "LAYOUT_TOP_DOWN()"})

(def linetypes
  "Maps linetype keys to PlantUML."
  {:orthogonal "skinparam linetype ortho"
   :polygonal  "skinparam linetype polyline"})

(def directions
  "Maps direction keys to PlantUML Rel suffixes."
  {:down  "_Down"
   :left  "_Left"
   :right "_Right"
   :up    "_Up"})

(def styles
  {:element  ""
   :rel      ""
   :boundary ""})

;;;
;;; Context based content filtering
;;;

(defn render-predicate
  "Returns true if the element is should be rendered for this diagram type.
   Checks both sides of a relation."
  [diagram-type]
  (let [element-predicate (diagram-type->element-predicate diagram-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (arch/get-model-element (:from e)))
               (element-predicate (arch/get-model-element (:to e))))
          (element-predicate e)))))
  
(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this diagram type, false otherwise."
  [diagram-type e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary [diagram-type (:el e)])))

;;;
;;; Rendering functions
;;;

(defn alias-name
  "Returns a valid PlantUML alias for the keyword."
  [kw]
  (symbol (str (sstr/hyphen-to-camel-case (namespace kw)) "_"
               (sstr/hyphen-to-camel-case (name kw)))))

(defn element-to-render
  "Returns the model element to be rendered in the context of the diagram."
  [diagram-type e]
  (let [boundary (as-boundary? diagram-type e)]
    (if boundary
    ; e has a boundary type and has children, render as boundary
      (assoc e :el boundary)
    ; render e as normal model element
      e)))

(defn elements-to-render
  "Returns the list of elements to render from the diagram
   or the given collection of elements, depending on the type
   of the diagram."
  ([diagram]
   (elements-to-render diagram (:ct diagram)))
  ([diagram coll]
   (let [diagram-type (:el diagram)]
     (->> coll
          (map arch/resolve-ref)
          (filter (render-predicate diagram-type))
          (map #(element-to-render diagram-type %))))))

(defn relation-to-render
  "Returns the relation to be rendered in the context of the diagram."
  [diagram rel]
  ; TODO promote reations to higher levels?
  )

(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

(defn render-boundary
  "Renders a PlantUML call for boundary e."
  [e]
  (str (element->methods (:el e)) "("
       (alias-name (:id e)) ", \""
       (:name e) "\""
       ")"))

(defn render-tech
  "Renders a PlantUML call for tech element e."
  [e]
  (str (element->methods (:el e)) "("
       (alias-name (:id e)) ", \""
       (:name e) "\""
       (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
       (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
       ")"))

(defn render-desc
  "Renders a PlantUML call for describeable element e."
  [e]
  (str (element->methods (:el e)) "("
       (alias-name (:id e)) ", \""
       (:name e) "\""
       (when (:type e) (str ", $type=\"" (:type e) "\""))
       (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
       ")"))

(defn render-node
  "Renders a PlantUML call for describeable element e."
  [e]
  (str (element->methods (:el e)) "("
       (alias-name (:id e)) ", \""
       (:name e) "\""
       (when (:type e) (str ", $type=\"" (:type e) "\""))
       (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
       ")"))

(defn render-rel
  "Renders a PlantUML call for relation e."
  [e]
  (str (element->methods (:el e))
       (when (:direction e) (directions (:direction e))) "("
       (alias-name (:from e)) ", "
       (alias-name (:to e)) ", \""
       (:name e) "\""
       (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
       (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
       ")"))

(defmulti render-element
  (fn [diagram indent e] (:el e))
  :hierarchy #'element-hierarchy)

(defmethod render-element :boundary
  [diagram indent e]
    (if (seq (:ct e))
      (let [children (elements-to-render diagram (:ct e))]
        (flatten [(str (render-indent indent)
                       (render-boundary e) " {")
                  (map #(render-element diagram (+ indent 2) %)
                       children)
                  (str (render-indent indent) "}")]))
      [(str (render-indent indent) (render-boundary e))]))

(defmethod render-element :rel
  [diagram indent e]
  [(str (render-indent indent) (render-rel e))])

(defmethod render-element :desc
  [diagram indent e]
  [(str (render-indent indent) (render-desc e))])

(defmethod render-element :tech
  [diagram indent e]
  [(str (render-indent indent) (render-tech e))])

(defmethod render-element :node
  [diagram indent e]
    (if (seq (:ct e))
      (let [children (elements-to-render diagram (:ct e))]
        (flatten [(str (render-indent indent)
                       (render-node e) " {")
                  (map #(render-element diagram (+ indent 2) %)
                       children)
                  (str (render-indent indent) "}")]))
      [(str (render-indent indent) (render-node e))]))

(defn render-imports
  "Renders the imports for the diagram."
  [diagram]
  (cond
    (= :context-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml"
    (= :container-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml"
    (= :component-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml"
    (= :dynamic-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Dynamic.puml"
    (= :deployment-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml"))

(defn render-style
  "Renders a styles for the diagram."
  [diagram style]
  ; TODO
  )

(defn render-layout
  "Renders the layout for the diagram."
  [diagram]
  (let [spec (:spec diagram)]
    (flatten [(when (:styles spec)
                (into [] (map #(render-style diagram %)) (:styles spec)))
              (when (:legend spec)
                "LAYOUT_WITH_LEGEND()")
              (when (:sketch spec)
                "LAYOUT_AS_SKETCH()")
              (when (:layout spec)
                (layouts (:layout spec)))
              (when (:linetype spec)
                (linetypes (:linetype spec)))])))

(defn render-title
  "Renders the title of the diagram."
  [diagram]
  (when (:title diagram) (str "title " (:title diagram))))

(defn render-diagram
  "Renders the PlantUML code for the diagram."
  [diagram]
  (let [children (elements-to-render diagram)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (alias-name (:id diagram)))
              (render-imports diagram)
              (render-layout diagram)
              (render-title diagram)
              (map #(render-element diagram 0 %) children)
              "@enduml"])))

;;;
;;; PlantUML file export
;;;

(defmethod arch/export-file :plantuml
  [format diagram]
  (let [dir-name (str "exports/plantuml/" (namespace (:id diagram)))
        dir (io/as-file dir-name)]
    (file/create-dir dir)
    (io/as-file (str dir-name "/"
                     (name (:id diagram)) ".puml"))))

(defmulti export-diagram
  "Exports the diagram in the given format."
  arch/export-format)

(defmethod export-diagram :plantuml
  [format diagram]
  (with-open [wrt (io/writer (arch/export-file format diagram))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-diagram diagram))))))

(defn export-diagrams 
  "Export all diagrams in the given format."
  [format]
  (doseq [diagram (arch/get-diagrams)]
    (println (:id diagram))
    (export-diagram format diagram)))

(comment
  (render-diagram (first (arch/get-diagrams)))
  (println (str/join "\n" (render-diagram (first (arch/get-diagrams)))))
  (println (str/join "\n" (render-diagram (arch/get-diagram :pulsar/container-view))))
  (export-diagram :plantuml (arch/get-diagram :pulsar/container-view))
  (export-diagrams :plantuml)
  )