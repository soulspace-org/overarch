(ns org.soulspace.overarch.plantuml
  "Functions to export views to plantuml."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.diagram :as dia]
            [org.soulspace.overarch.export :as exp]))

;;;;
;;;; PlantUML rendering
;;;;

;;;
;;; PlantUML mappings
;;;

; TODO
(def icon-library->import
  "Icon Libraries"
  {:azure       ""
   :aws         ""
   :devicons    ""
   :fontawesome ""})

(def element->method
  "Map from element type to PlantUML method."
  {:person              "Person"
   :system              "System"
   :boundary            "Boundary"
   :enterprise-boundary "Enterprise_Boundary"
   :system-boundary     "System_Boundary"
   :container           "Container"
   :container-boundary  "Container_Boundary"
   :component           "Component"
   :node                "Node"
   :rel                 "Rel"})

(def diagram-type->import
  "Map from diagram type to PlantUML import."
  {:system-landscape-diagram "C4_Context.puml"
   :context-diagram          "C4_Context.puml"
   :container-diagram        "C4_Container.puml"
   :component-diagram        "C4_Component.puml"
   :dynamic-diagram          "C4_Dynamic.puml"
   :deployment-diagram       "C4_Deployment.puml"})

(def subtype->suffix
  "Maps the subtype of an element to the PlantUML suffix."
  {:database "Db"
   :queue "Queue"})

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

(def style->method
  {:element  "AddElementTag"
   :rel      "AddRelTag"
   :boundary "AddBoundaryTag"})

(def line-style->method
  {:bold   "BoldLine()"
   :dashed "DashedLine()"
   :dotted "DottedLine()"})

;;;
;;; Rendering
;;;

(defn alias-name
  "Returns a valid PlantUML alias for the namespaced keyword."
  [kw]
  (symbol (str (sstr/hyphen-to-camel-case (namespace kw)) "_"
               (sstr/hyphen-to-camel-case (name kw)))))
(defn short-name
  "Returns a valid PlantUML alias for the name part of the keyword."
  [kw]
  (sstr/hyphen-to-camel-case (name kw)))

(defmulti render-element
  "Renders an element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [diagram indent e] (:el e))
  :hierarchy #'dia/element-hierarchy)

(defmethod render-element :boundary
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (dia/elements-to-render diagram (:ct e))]
      (flatten [(str (dia/render-indent indent)
                     (element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (dia/element-name e) "\""
                     (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-element diagram (+ indent 2) %)
                     children)
                (str (dia/render-indent indent) "}")]))
    [(str (dia/render-indent indent)
          (element->method (:el e)) "("
          (alias-name (:id e)) ", \""
          (dia/element-name e) "\""
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-element :person
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->method (:el e))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (dia/element-name e) "\""
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-element :system
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->method (:el e))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (dia/element-name e) "\""
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-element :container
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->method (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (dia/element-name e) "\""
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-element :component
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->method (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (dia/element-name e) "\""
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-element :node
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (dia/elements-to-render diagram (:ct e))]
      (flatten [(str (dia/render-indent indent)
                     (element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (dia/element-name e) "\""
                     (when (:type e) (str ", $type=\"" (:type e) "\""))
                     (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
                     (when (:style e) (str ", $tag=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-element diagram (+ indent 2) %)
                     children)
                (str (dia/render-indent indent) "}")]))
    [(str (dia/render-indent indent)
          (element->method (:el e)) "("
          (alias-name (:id e)) ", \""
          (dia/element-name e) "\""
          (when (:type e) (str ", $type=\"" (:type e) "\""))
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-element :rel
  [diagram indent e]
  (if (:constraint e) ; TODO :hidden or :constraint
    [(str (dia/render-indent indent) "Lay"
          (when (:direction e) (directions (:direction e))) "("
          (alias-name (:from e)) ", "
          (alias-name (:to e)) ", \""
          ")")]
    [(str (dia/render-indent indent)
          (element->method (:el e))
          (when (:direction e) (directions (:direction e))) "("
          (alias-name (:from e)) ", "
          (alias-name (:to e)) ", \""
          (:name e) "\""
          (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(comment
  ; include icon/sprite sets, if icons are used, e.g. 
  "!define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"
  "!define FONTAWESOME https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"
  "!include DEVICONS/angular.puml
   !include DEVICONS/java.puml
   !include DEVICONS/msql_server.puml
   !include FONTAWESOME/users.puml
   ")

(defn render-imports
  "Renders the imports for the diagram."
  [diagram]
  (if (get-in diagram [:spec :plantuml :local-imports])
    (str "!include <C4/"
         (diagram-type->import (:el diagram)) ">")
    (str "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/"
         (diagram-type->import (:el diagram)))))

(def styles-hierarchy
  "Hierarchy for style methods."
  (-> (make-hierarchy)
      (derive :person :type)
      (derive :system :type)
      (derive :container :type)
      (derive :component :type)
      (derive :node :type)))

(defmulti render-style
  "Renders a styles for the diagram."
  (fn [diagram style] (:el style)) :hierarchy #'styles-hierarchy)

; AddElementTag (tagStereo, ?bgColor, ?fontColor, ?borderColor, ?shadowing, ?shape, ?sprite, ?techn, ?legendText, ?legendSprite)
(defmethod render-style :element
  [diagram style]
  (let [el (:el style)]
    (str (style->method (:el style)) "("
         (short-name (:id style))
       ; (alias-name (:id style))
         (when (:bg-color style) (str ", $bgColor=\"" (:bg-color style) "\""))
         (when (:text-color style) (str ", $fontColor=\"" (:text-color style) "\""))
         (when (:border-color style) (str ", $borderColor=\"" (:border-color style) "\""))
         (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
         (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
         ")")))

; AddRelTag (tagStereo, ?textColor, ?lineColor, ?lineStyle, ?sprite, ?techn, ?legendText, ?legendSprite, ?lineThickness)
(defmethod render-style :rel
  [diagram style]
  (let [el (:el style)]
    (str (style->method (:el style)) "("
         (short-name (:id style))
       ; (alias-name (:id style))
         (when (:text-color style) (str ", $textColor=\"" (:text-color style) "\""))
         (when (:line-color style) (str ", $lineColor=\"" (:line-color style) "\""))
         (when (:line-style style) (str ", $lineStyle=\"" (line-style->method (:line-style style)) "\""))
         (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
         (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
         ")")))

(defn render-layout
  "Renders the layout for the diagram."
  [diagram]
  (let [spec (:spec diagram)]
    (flatten [(when (:styles spec)
                (into [] (map #(render-style diagram %)) (:styles spec)))
              (when (:sketch spec)
                "LAYOUT_AS_SKETCH()")
              (when (:layout spec)
                (layouts (:layout spec)))
              (when (:linetype spec)
                (linetypes (:linetype spec)))])))

(defn render-legend
  "Renders the legend for the diagram."
  [diagram]
  (let [spec (:spec diagram)]
    [(when (:legend spec)
       "SHOW_LEGEND()")]))

(defn render-title
  "Renders the title of the diagram."
  [diagram]
  (when (:title diagram) (str "title " (:title diagram))))

(defn render-diagram
  [options diagram]
  (let [children (dia/elements-to-render diagram)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (alias-name (:id diagram)))
              (render-imports diagram)
              (render-layout diagram)
              (render-title diagram)
              (map #(render-element diagram 0 %) children)
              (render-legend diagram)
              "@enduml"])))

;;;
;;; PlantUML file export
;;;

(defmethod exp/export-file :plantuml
  [options diagram]
  (let [dir-name (str (:export-dir options) "/plantuml/" (namespace (:id diagram)))
        dir (io/as-file dir-name)]
    (file/create-dir dir)
    (io/as-file (str dir-name "/"
                     (name (:id diagram)) ".puml"))))

; general
(defmulti export-diagram
  "Exports the diagram in the given format."
  exp/export-format)

(defmethod export-diagram :plantuml
  [options diagram]
  (with-open [wrt (io/writer (exp/export-file options diagram))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-diagram options diagram))))))

(defmethod exp/export :plantuml
  [options]
  (doseq [diagram (core/get-diagrams)]
    (export-diagram options diagram)))

