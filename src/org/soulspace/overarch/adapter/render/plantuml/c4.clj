(ns org.soulspace.overarch.adapter.render.plantuml.c4
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as render]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]))

(def c4-element->method
  "Map from element type to PlantUML C4 method."
  {:person              "Person"
   :system              "System"
   :boundary            "Boundary"
   :enterprise-boundary "Enterprise_Boundary"
   :system-boundary     "System_Boundary"
   :container           "Container"
   :container-boundary  "Container_Boundary"
   :context-boundary    "Boundary"
   :component           "Component"
   :node                "Node"
   :send                "Rel"
   :request             "Rel"
   :response            "Rel"
   :publish             "Rel"
   :subscribe           "Rel"
   :dataflow            "Rel"
   :link                "Rel"
   :rel                 "Rel"})

(def c4-view-type->import
  "Map from diagram type to PlantUML C4 import."
  {:system-landscape-view "C4_Context.puml"
   :context-view          "C4_Context.puml"
   :container-view        "C4_Container.puml"
   :component-view        "C4_Component.puml"
   :dynamic-view          "C4_Dynamic.puml"
   :deployment-view       "C4_Deployment.puml"})

(def c4-subtype->suffix
  "Maps the subtype of an element to the PlantUML C4 suffix."
  {:database "Db"
   :queue "Queue"})

(def c4-layouts
  "Maps layout keys to PlantUML C4."
  {:landscape "LAYOUT_LANDSCAPE()"
   :left-right "LAYOUT_LEFT_RIGHT()"
   :top-down "LAYOUT_TOP_DOWN()"})

(def c4-directions
  "Maps direction keys to PlantUML C4 Rel suffixes."
  {:down  "_D"
   :left  "_L"
   :right "_R"
   :up    "_U"})

(def c4-style->method
  "Maps the style element keys to the PlantUML C4 method."
  {:element  "AddElementTag"
   :rel      "AddRelTag"
   :boundary "AddBoundaryTag"})

(def c4-line-style->method
  "Maps the line style keys to the PlantUML C4 method."
  {:bold   "BoldLine()"
   :dashed "DashedLine()"
   :dotted "DottedLine()"})

;;;
;;; Rendering
;;;

(defmethod puml/render-c4-element :boundary
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (render/indent indent)
                     (c4-element->method (:el e)) "("
                     (puml/alias-name (:id e)) ", \""
                     (el/element-name e) "\""
                     (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
                     ") {")
                (map #(puml/render-c4-element model view (+ indent 2) %)
                     children)
                (str (render/indent indent) "}")]))
    [(str (render/indent indent)
          (c4-element->method (:el e)) "("
          (puml/alias-name (:id e)) ", \""
          (el/element-name e) "\""
          (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
          ")")]))

(defmethod puml/render-c4-element :person
  [_ _ indent e]
  [(str (render/indent indent)
        (c4-element->method (:el e))
        (when (:external e) "_Ext") "("
        (puml/alias-name (:id e)) ", \""
        (el/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
        ")")])

(defmethod puml/render-c4-element :technical-architecture-node
  [_ _ indent e]
  [(str (render/indent indent)
        (c4-element->method (:el e))
        (when (:subtype e) (c4-subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (puml/alias-name (:id e)) ", \""
        (el/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (if (:sprite e)
          (str ", $sprite=\"" (:name (puml/tech->sprite (:sprite e))) "\"")
          (when (puml/sprite? (:tech e))
            (str ", $sprite=\"" (:name (puml/tech->sprite (:tech e))) "\"")))
        (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
        ")")])

(defmethod puml/render-c4-element :node
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (render/indent indent)
                     (c4-element->method (:el e)) "("
                     (puml/alias-name (:id e)) ", \""
                     (el/element-name e) "\""
                     (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
                     (when (:tech e) (str ", $type=\"" (:tech e) "\""))
                     (if (:sprite e)
                       (str ", $sprite=\"" (:name (puml/tech->sprite (:sprite e))) "\"")
                       (when (puml/sprite? (:tech e))
                         (str ", $sprite=\"" (:name (puml/tech->sprite (:tech e))) "\"")))
                     (when (:style e) (str ", $tag=\"" (puml/short-name (:style e)) "\""))
                     ") {")
                (map #(puml/render-c4-element model view (+ indent 2) %)
                     children)
                (str (render/indent indent) "}")]))
    [(str (render/indent indent)
          (c4-element->method (:el e)) "("
          (puml/alias-name (:id e)) ", \""
          (el/element-name e) "\""
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:tech e) (str ", $type=\"" (:tech e) "\""))
          (if (:sprite e)
            (str ", $sprite=\"" (:name (puml/tech->sprite (:sprite e))) "\"")
            (when (puml/sprite? (:tech e))
              (str ", $sprite=\"" (:name (puml/tech->sprite (:tech e))) "\"")))
          (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
          ")")]))

(defmethod puml/render-c4-element :relation
  [_ _ indent e]
  (if (:constraint e)
    [(str (render/indent indent) "Lay"
          (when (:direction e) (c4-directions (:direction e))) "("
          (puml/alias-name (:from e)) ", "
          (puml/alias-name (:to e))
          ")")]
    [(str (render/indent indent)
          (c4-element->method (:el e))
          (when (:direction e) (c4-directions (:direction e))) "("
          (if (:reverse e)
            (str (puml/alias-name (:to e)) ", "
                 (puml/alias-name (:from e)) ", \"")
            (str (puml/alias-name (:from e)) ", "
                 (puml/alias-name (:to e)) ", \""))
          (:name e) "\""
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
          (if (:sprite e)
            (str ", $sprite=\"" (:name (puml/tech->sprite (:sprite e))) ",scale=0.5\"")
            (when (puml/sprite? (:tech e))
              (str ", $sprite=\"" (:name (puml/tech->sprite (:tech e))) ",scale=0.5\"")))
          (when (:style e) (str ", $tags=\"" (puml/short-name (:style e)) "\""))
          ")")]))


;;
;; C4 Imports
;;

(defn render-c4-imports
  "Renders the imports for the diagram."
  [view]
  (if (get-in view [:spec :plantuml :remote-imports])
    (str "!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/"
         (c4-view-type->import (:el view)))
    (str "!include <C4/"
         (c4-view-type->import (:el view)) ">")))

;;;
;;; Diagram Styles
;;;

(def c4-styles-hierarchy
  "Hierarchy for style methods."
  (-> (make-hierarchy)
      (derive :person :type)
      (derive :system :type)
      (derive :container :type)
      (derive :component :type)
      (derive :node :type)))

(defmulti render-c4-style
  "Renders a styles for the diagram."
  (fn [view style] (:el style)) :hierarchy #'c4-styles-hierarchy)

; AddElementTag (tagStereo, ?bgColor, ?fontColor, ?borderColor, ?shadowing, ?shape, ?sprite, ?techn, ?legendText, ?legendSprite)
(defmethod render-c4-style :element
  [view style]
  (let [el (:el style)]
    (str (c4-style->method (:el style)) "("
         (puml/short-name (:id style))
       ; (alias-name (:id style))
         (when (:bg-color style) (str ", $bgColor=\"" (:bg-color style) "\""))
         (when (:text-color style) (str ", $fontColor=\"" (:text-color style) "\""))
         (when (:border-color style) (str ", $borderColor=\"" (:border-color style) "\""))
         (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
         (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
         ")")))

; AddRelTag (tagStereo, ?textColor, ?lineColor, ?lineStyle, ?sprite, ?techn, ?legendText, ?legendSprite, ?lineThickness)
(defmethod render-c4-style :rel
  [view style]
  (let [el (:el style)]
    (str (c4-style->method (:el style)) "("
         (puml/short-name (:id style))
       ; (alias-name (:id style))
         (when (:text-color style) (str ", $textColor=\"" (:text-color style) "\""))
         (when (:line-color style) (str ", $lineColor=\"" (:line-color style) "\""))
         (when (:line-style style) (str ", $lineStyle=\"" (c4-line-style->method (:line-style style)) "\""))
         (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
         (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
         ")")))

;;;
;;; Diagram Layout
;;;

(defn render-c4-layout
  "Renders the layout for the C4 diagram."
  [model view]
  (let [spec (:spec view)
        styles (view/styles-spec model view)
        plantuml-spec (:plantuml spec)] 
    (flatten [(when (seq styles)
                (into [] (map #(render-c4-style view %)) styles))
              (when (:sketch spec)
                "LAYOUT_AS_SKETCH()")
              (when (:layout spec)
                (c4-layouts (:layout spec)))
              (when (:linetype spec)
                (puml/linetypes (:linetype spec)))
              (when (:node-separation plantuml-spec)
                (str "skinparam nodesep " (:node-separation plantuml-spec)))
              (when (:rank-separation plantuml-spec)
                (str "skinparam ranksep " (:rank-separation plantuml-spec)))])))

(defn render-c4-legend
  "Renders the legend for the diagram."
  [view]
  (let [spec (:spec view)]
    [(when-not (:no-legend spec)
       "SHOW_LEGEND()")]))

(defmethod puml/render-plantuml-view :c4-view
  [model options view]
  (let [children (view/elements-to-render model view)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (puml/alias-name (:id view)))
              (render-c4-imports view)
              (puml/render-sprite-imports model view)
              (render-c4-layout model view)
              (puml/render-title view)
              (map #(puml/render-c4-element model view 0 %) children)
              (render-c4-legend view)
              "@enduml"])))


