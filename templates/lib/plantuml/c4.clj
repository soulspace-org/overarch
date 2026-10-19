(ns lib.plantuml.c4
  "Functions for templates generating PlantUML C4 files."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [lib.text :as t]
            [lib.plantuml :as puml]
            [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]))

;;;
;;; Maps
;;;
(def element->method
  "Map from element type to PlantUML C4 method."
  {:person              "Person"
   :system              "System"
   :boundary            "Boundary"
   :enterprise-boundary "Enterprise_Boundary"
   :system-boundary     "System_Boundary"
   :container           "Container"
   :container-instance  "Container"
   :container-boundary  "Container_Boundary"
   :context-boundary    "Boundary"
   :component           "Component"
   :artifact            "Container"
   :node                "Node"
   :send                "Rel"
   :request             "Rel"
   :response            "Rel"
   :publish             "Rel"
   :subscribe           "Rel"
   :dataflow            "Rel"
   :link                "Rel"
   :step                "Rel"
   :rel                 "Rel"})

(def view-type->import
  "Map from diagram type to PlantUML C4 import."
  {:system-landscape-view        "C4_Context.puml"
   :context-view                 "C4_Context.puml"
   :container-view               "C4_Container.puml"
   :component-view               "C4_Component.puml"
   :dynamic-view                 "C4_Dynamic.puml"
   :deployment-view              "C4_Deployment.puml"
   :deployment-architecture-view "C4_Deployment.puml"})

(def subtype->suffix
  "Maps the subtype of an element to the PlantUML C4 suffix."
  {:database "Db"
   :queue    "Queue"})

(def layouts
  "Maps layout keys to PlantUML C4."
  {:landscape  "LAYOUT_LANDSCAPE()"
   :left-right "LAYOUT_LEFT_RIGHT()"
   :top-down   "LAYOUT_TOP_DOWN()"})

(def directions
  "Maps direction keys to PlantUML C4 Rel suffixes."
  {:down  "_D"
   :left  "_L"
   :right "_R"
   :up    "_U"})

(def style->method
  "Maps the style element keys to the PlantUML C4 method."
  {:element  "AddElementTag"
   :rel      "AddRelTag"
   :boundary "AddBoundaryTag"})

(def line-style->method
  "Maps the line style keys to the PlantUML C4 method."
  {:bold   "BoldLine()"
   :dashed "DashedLine()"
   :dotted "DottedLine()"})

;;;
;;; Render helpers
;;;
(defn render-link
  "Renders a link for the element `e`."
  [e]
  (when-let [link (:link e)]
    (if (keyword? link)
      (str ", $link=\"" (link e) "\"")
      (str ", $link=\"" link "\""))))

(defn sprite
  "Returns the sprite for the element `e`."
  [e]
  (if (:sprite e)
    (v/tech-sprite (:sprite e))
    (some v/tech-sprite (:tech e))))

(defn render-descr
  "Renders the description argument for the element `e`."
  [e]
  (when (:desc e)
    (str ", $descr=\"" (t/single-line (:desc e)) "\"")))

(defn render-tech
  "Renders the technology argument with the argument name `arg` for the element `e`."
  [arg e]
  (when (seq (:tech e))
    (str ", $" arg "=\"" (str/join ", " (:tech e)) "\"")))

(defn render-sprite
  "Renders the sprite argument for the element `e`. The optional `suffix` is appended to the sprite name."
  ([e]
   (render-sprite e ""))
  ([e suffix]
   (when-let [sprite (sprite e)]
     (str ", $sprite=\"" (:name sprite) suffix "\""))))

(defn render-tags
  "Renders the tags argument with the argument name `arg` for the element `e`."
  ([e]
   (render-tags "tags" e))
  ([arg e]
   (when (:style e)
     (str ", $" arg "=\"" (puml/short-name (:style e)) "\""))))

(defn render-method
  "Renders the C4 method for the element `e`, with the suffixes for the subtype and external elements, if enabled."
  [e {:keys [subtype external]}]
  (str (element->method (:el e))
       (when (and subtype (:subtype e)) (subtype->suffix (:subtype e)))
       (when (and external (:external e)) "_Ext")))

(defn render-call
  "Renders the C4 call of `method` for the element `e` with the `args` as indented line.
   Nil args are skipped."
  [indent method e & args]
  (str (puml/indent indent)
       method "("
       (puml/alias-name (:id e)) ", \""
       (m/element-name e) "\""
       (apply str args)
       ")"))

;;;
;;; Element rendering
;;;
(defmulti render-c4-element
  "Renders a C4 element in PlantUML as a sequence of lines.

   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ _ e] (:el e))
  :hierarchy #'m/element-hierarchy)

(defmethod render-c4-element :boundary
  [model view indent e]
  (let [children (m/children model e)
        content (seq (v/elements-to-render model view children))
        call (render-call indent (element->method (:el e)) e (render-tags e))]
    (if content
      (flatten [(str call " {")
                (map #(render-c4-element model view (+ indent 2) %) content)
                (str (puml/indent indent) "}")])
      [call])))

(defmethod render-c4-element :person
  [_ _ indent e]
  [(render-call indent (render-method e {:external true}) e
                (render-descr e)
                (when (:type e) (str ", $type=\"" (:type e) "\""))
                (render-link e)
                (render-tags e))])

(defmethod render-c4-element :system
  [_ _ indent e]
  ; systems don't have a `techn` but a `type`
  [(render-call indent (render-method e {:subtype true :external true}) e
                (render-descr e)
                (render-tech "type" e)
                (render-link e)
                (render-sprite e)
                (render-tags e))])

(defn render-technical-node
  "Renders a technical element (container, component, artifact or deployable node)."
  [indent e]
  [(render-call indent (render-method e {:subtype true :external true}) e
                (render-descr e)
                (render-tech "techn" e)
                (render-link e)
                (render-sprite e)
                (render-tags e))])

(defmethod render-c4-element :technical-architecture-model-node
  [_ _ indent e]
  (render-technical-node indent e))

; Render artifacts as containers for now, as they are deployed like containers
(defmethod render-c4-element :artifact
  [_ _ indent e]
  (render-technical-node indent e))

; Render container instances and artifacts
(defmethod render-c4-element :deployable-node
  [_ _ indent e]
  (render-technical-node indent e))

(defmethod render-c4-element :node
  [model view indent e]
  (let [children (m/children model e)    ; selects the child nodes
        ; selects the deployed elements that are rendered in the view, e.g. containers and artifacts
        deployed (set/intersection (set (m/referring-nodes model e {:el :deployed-to}))
                                   (set (v/rendered-elements model view)))
        content (concat (v/elements-to-render model view children)
                        (v/elements-to-render model view deployed))]
    (if (and (seq content) (not (m/collapsed? e)))
      (flatten [(str (render-call indent (element->method (:el e)) e
                                  (render-descr e)
                                  (render-tech "type" e)
                                  (render-link e)
                                  (render-sprite e)
                                  (render-tags e))
                     " {")
                (map #(render-c4-element model view (+ indent 2) %) content)
                (str (puml/indent indent) "}")])
      [(render-call indent (element->method (:el e)) e
                    (render-descr e)
                    (render-tech "type" e)
                    (render-link e)
                    (render-sprite e)
                    (render-tags e))])))

(defmethod render-c4-element :deployed-to
  [_ _ _ _]
  ; don't render deployed on as relation as it is already rendered by :node
  nil)

;; Relations
(defn render-direction
  "Renders the direction suffix of the relation `e`.
   Uses reasonable defaults, if no direction is specified."
  [e]
  (if (:direction e)
    (directions (:direction e))
    (case (:el e)
      :publish (directions :down)
      :subscribe (directions :up)
      "")))

(defn render-rel
  "Renders the relation `e` as a C4 relation or as layout constraint."
  [view indent e]
  (if (:constraint e)
    [(str (puml/indent indent) "Lay"
          (when (:direction e) (directions (:direction e))) "("
          (puml/alias-name (:from e)) ", "
          (puml/alias-name (:to e))
          ")")]
    [(str (puml/indent indent)
          (element->method (:el e))
          (render-direction e)
          "("
          (when (and (:index e) (= :dynamic-view (:el view)))
            (str "$index=SetIndex(" (:index e) "), "))
          (if (:reverse e)
            (str (puml/alias-name (:to e)) ", "
                 (puml/alias-name (:from e)) ", \"")
            (str (puml/alias-name (:from e)) ", "
                 (puml/alias-name (:to e)) ", \""))
          (t/single-line (:name e)) "\""
          (render-descr e)
          (render-tech "techn" e)
          (render-sprite e ",scale=0.5")
          (render-tags e)
          ")")]))

(defmethod render-c4-element :link
  [_ view indent e]
  (render-rel view indent e))

(defmethod render-c4-element :architecture-model-relation
  [_ view indent e]
  (render-rel view indent e))

(defmethod render-c4-element :step
  [_ view indent e]
  (render-rel view indent e))

(defmethod render-c4-element :model-relation
  [_ view indent e]
  (render-rel view indent e))

(defmethod render-c4-element :model-element
  [_ view indent e]
  [(str (puml/indent indent)
        "' unhandled element of type " (:el e) " with id " (:id e)
        " in PlantUML C4 rendering of view " (:id view))])

;;;
;;; C4 Imports
;;;
(defn render-imports
  "Renders the imports for the diagram."
  [view]
  (if (get (v/plantuml-spec view) :remote-imports)
    (str "!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/"
         (view-type->import (:el view)))
    (str "!include <C4/"
         (view-type->import (:el view)) ">")))

;;;
;;; Diagram Styles
;;;
(defmulti render-c4-style
  "Renders a style for the diagram.

   Multifunction dispatching on the value of the :el key of the `style`."
  (fn [_ style] (:el style)))

; AddElementTag (tagStereo, ?bgColor, ?fontColor, ?borderColor, ?shadowing, ?shape, ?sprite, ?techn, ?legendText, ?legendSprite)
(defmethod render-c4-style :element
  [_ style]
  (str (style->method (:el style)) "("
       (puml/short-name (:id style))
       (when (:bg-color style) (str ", $bgColor=\"" (:bg-color style) "\""))
       (when (:text-color style) (str ", $fontColor=\"" (:text-color style) "\""))
       (when (:border-color style) (str ", $borderColor=\"" (:border-color style) "\""))
       (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
       (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
       ")"))

; AddRelTag (tagStereo, ?textColor, ?lineColor, ?lineStyle, ?sprite, ?techn, ?legendText, ?legendSprite, ?lineThickness)
(defmethod render-c4-style :rel
  [_ style]
  (str (style->method (:el style)) "("
       (puml/short-name (:id style))
       (when (:text-color style) (str ", $textColor=\"" (:text-color style) "\""))
       (when (:line-color style) (str ", $lineColor=\"" (:line-color style) "\""))
       (when (:line-style style) (str ", $lineStyle=\"" (line-style->method (:line-style style)) "\""))
       (when (:tech style) (str ", $techn=\"" (:tech style) "\""))
       (when (:legend-text style) (str ", $legendText=\"" (:legend-text style) "\""))
       ")"))

(defmethod render-c4-style :default
  [view style]
  (str "' unhandled style of type " (:el style) " with id " (:id style)
       " in PlantUML C4 rendering of view " (:id view)))

;;;
;;; Diagram Layout
;;;
(defn render-c4-layout
  "Renders the styles and layout directives for the C4 diagram as a vector of lines."
  [model view]
  (into (mapv #(render-c4-style view %) (v/styles-spec model view))
        (puml/render-layout view {:sketch "LAYOUT_AS_SKETCH()"
                                  :layouts layouts})))

(defn render-c4-legend
  "Renders the legend for the diagram as a vector of lines."
  [view]
  (if (v/legend-spec view)
    ["SHOW_LEGEND()"]
    []))

;;;
;;; Diagram
;;;
(defn deployment-view?
  "Returns true, if the `view` is a deployment view."
  [view]
  (contains? #{:deployment-view :deployment-architecture-view} (:el view)))

(defn render-roots
  "Returns the elements to start rendering the `view` with.
   Deployment views only render the deployment nodes as roots, because the deployed
   containers should only be rendered as children of the nodes, not on their own."
  [model view elements]
  (let [nodes (filter m/model-node? elements)
        relations (filter m/model-relation? elements)]
    (if (deployment-view? view)
      (concat (filter #(= :node (:el %)) (v/root-elements model nodes))
              (filter #(contains? #{:person :system :container} (:el %)) elements)
              relations)
      (concat (if (v/hierarchical-view? view)
                (v/root-elements model nodes)
                nodes)
              relations))))

(defn render-c4-view
  "Renders the C4 `view` of the `model` as a vector of PlantUML lines."
  [model view]
  (let [elements (v/view-elements model view)
        rendered (v/elements-to-render model view (render-roots model view elements))]
    (->> (concat [(str "@startuml " (name (:id view)))
                  (render-imports view)]
                 (puml/render-sprite-imports model view)
                 (render-c4-layout model view)
                 [(puml/render-skinparams view)
                  (puml/render-title view)]
                 (mapcat #(render-c4-element model view 0 %) rendered)
                 (render-c4-legend view)
                 ["@enduml"])
         (remove nil?)
         (vec))))
