(ns org.soulspace.overarch.adapter.render.plantuml.uml
  "Functions to render PlantUML diagrams for UML views."
  (:require [clojure.string :as str]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as render]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]
            [org.soulspace.overarch.domain.model :as model]))

(def uml-directions
  "Maps direction keys to PlantUML UML directions."
  {:down  "down"
   :left  "left"
   :right "right"
   :up    "up"})

(def uml-visibility
  "Maps visibility keys to PlantUML UML directions."
  {:private         "-"
   :protected       "#"
   :package         "~"
   :public          "+"})

(def uml-cardinality
  "Maps cardinality keys to PlantUML UML cardinalities."
  {:zero-to-one  "0..1"
   :zero-to-many "0..n"
   :one          "1"
   :one-to-many  "1..n"})

(def uml-layouts
  "Maps layout keys to PlantUML UML directives."
  {:top-down   "top to bottom direction"
   :left-right "left to right direction"})

(def uml-hides
  "Maps view-types to PlantUML UML hide directives for more compact layouts."
  {:state-view "hide empty description"
   :code-view "hide empty members"})

(def use-case-level->color
  "Maps the use case level to a color."
  {:summary "#FFFFFF"
   :user-goal "#BBBBFF"
   :subfunction "#8888DD"})

;;;
;;; Rendering
;;;
(defn render-name
  "Renders the name of the element `e`. If `e` has a :link entry, a link is rendered."
  [e]
  (if-let [link (:link e)]
    (if (keyword? link)
      (str "[[" (link e) " " (el/element-name e) "]]")
      (str "[[" link " " (el/element-name e) "]]"))
    (el/element-name e)))

(defmulti render-uml-element
  "Renders a UML element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ _ e] (:el e))
  :hierarchy #'el/element-hierarchy)

(defmethod render-uml-element :context-boundary
  [model view indent e]
  (let [children (model/children model e)]
    (when-let [content (view/elements-to-render model view children)]
      (flatten [(str (render/indent indent)
                     "rectangle \"" (render-name e) "\" {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                "}"]))))

(defmethod render-uml-element :use-case
  [_ _ indent e]
  [(str (render/indent indent)
        "usecase \"" (render-name e) "\" as ("
        (puml/alias-name (:id e)) ")"
        (when (:level e)
          (str " " (use-case-level->color (:level e)))))])

(defmethod render-uml-element :actor-node
  [_ _ indent e]
  [(str (render/indent indent)
        "actor \"" (render-name e) "\" as " (puml/alias-name (:id e)))])

(defmethod render-uml-element :uses
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :include
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " ."
        (if (:direction e)
          (uml-directions (:direction e))
          (uml-directions :down))
        ".> "
        (puml/alias-name (:to e)) " : include")])

(defmethod render-uml-element :extends
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " ."
        (if (:direction e)
          (uml-directions (:direction e))
          (uml-directions :up))
        ".> "
        (puml/alias-name (:to e)) " : extends")])

(defmethod render-uml-element :generalizes
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " -"
        (if (:direction e)
          (uml-directions (:direction e))
          (uml-directions :right))
        "-|> "
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :package
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e))) 
      (flatten [(str (render/indent indent)
                     "package \"" (render-name e)
                     "\" as " (puml/alias-name (:id e)) " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            "package \"" (render-name e)
            "\" as " (puml/alias-name (:id e)) " {}")])))

(defmethod render-uml-element :namespace
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(str (render/indent indent)
                     "namespace \"" (render-name e)
                     "\" as " (puml/alias-name (:id e)) " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            "namespace \"" (render-name e)
            "\" as " (puml/alias-name (:id e)) " {}")])))

(defmethod render-uml-element :interface
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(str (render/indent indent)
                     "interface \"" (render-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element model view (+ indent 2) %)  (sort-by :name content))
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            "interface \"" (render-name e)
            "\" as " (puml/alias-name (:id e))
            (when (:stereotype e)
              (str " <<" (:stereotype e) ">>"))
            " {}")])))

(defmethod render-uml-element :protocol
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(str (render/indent indent)
                     "protocol \"" (render-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            "protocol \"" (render-name e)
            "\" as " (puml/alias-name (:id e))
            (when (:stereotype e)
              (str " <<" (:stereotype e) ">>"))
            " {}")])))

(defmethod render-uml-element :enum
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(str (render/indent indent)
                     "enum \"" (render-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            "enum \"" (render-name e)
            "\" as " (puml/alias-name (:id e))
            (when (:stereotype e)
              (str " <<" (:stereotype e) ">>"))
            " {}")])))

(defmethod render-uml-element :enum-value
  [_ _ indent e]
  [(str (render/indent indent)
        (render-name e)
        (when (:value e)
          (str " = " (:value e)))
        )])

(defmethod render-uml-element :class
  [model view indent e]
  (let [children (model/children model e)
        content (seq (view/elements-to-render model view children))]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(str (render/indent indent)
                     (when (:abstract e) "abstract ")
                     "class \"" (render-name e)
                     (when (:generic e)
                       (str "<" (:generic e) ">"))
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element model view (+ indent 2) %) (sort-by :name content))
                (str (render/indent indent) "}")])
      [(str (render/indent indent)
            (when (:abstract e) "abstract ")
            "class \"" (render-name e)
            (when (:generic e)
              (str "<" (:generic e) ">"))
            "\" as " (puml/alias-name (:id e))
            (when (:stereotype e)
              (str " <<" (:stereotype e) ">>"))
            " {}")])))

(defmethod render-uml-element :field
  [_ _ indent e]
  [(str (render/indent indent)
        (when (= :classifier (:scope e))
          "{classifier} ")
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (render-name e)
        (when (:type e)
          (str " : " (:type e)))
        (when (:optional e)
          (str " [" (uml-cardinality :zero-to-one) "]"))
        (when (:collection e)
          (str " [" (uml-cardinality :zero-to-many) "]"))
        )])

(defmethod render-uml-element :parameter
  [_ _ _ e]
  [(str (render-name e)
        (when (:type e)
          (str " : " (:type e))))])

(defmethod render-uml-element :method
  [model view indent e]
  [(str (render/indent indent)
        (when (= :classifier (:scope e))
          "{classifier} ")
        (when (:abstract e)
          "{abstract} ")
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (render-name e) "("
        (let [children (model/children model e)]
          (str/join ", " (map (partial render-uml-element model view 0)
                              (view/elements-to-render model view children))))
        ")"
        (when (:type e)
          (str " : " (:type e))))])

(defmethod render-uml-element :function
  [_ _ indent e]
  [(str (render/indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (render-name e) "()"
        (when (:type e)
          (str " : " (:type e))))])

(defmethod render-uml-element :composition
  [_ _ indent e]
  ; TODO render roles
  [(str (render/indent indent)
        (puml/alias-name (:from e))
        " *--> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :aggregation
  [_ _ indent e]
  ; TODO render roles
  [(str (render/indent indent)
        (puml/alias-name (:from e))
        (when (:from-card e)
          (str " \"" (uml-cardinality (:from-card e)) "\" "))
        " o-"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :association
  [_ _ indent e]
  ; TODO render roles
  [(str (render/indent indent)
        (puml/alias-name (:from e))
        " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :inheritance
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:to e)) " <|-"
        (when (:direction e)
          (uml-directions (:direction e)))
        "- "
        (puml/alias-name (:from e)))])

(defmethod render-uml-element :implementation
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:to e)) " <|."
        (when (:direction e)
          (uml-directions (:direction e)))
        ". "
        (puml/alias-name (:from e)))])

(defmethod render-uml-element :dependency
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (puml/alias-name (:to e)))])

(defmethod render-uml-element :state-machine
  [model view indent e]
  (let [children (model/children model e)]
    (when-let [content (view/elements-to-render model view children)]
      (flatten [(map #(render-uml-element model view (+ indent 2) %) content)]))))

(defmethod render-uml-element :state
  [model view indent e]
  (let [children (model/children model e)
        content (view/elements-to-render model view children)]
    (if (and (seq content) (not (el/collapsed? e)))
      (flatten [(render/indent indent)
                (str "state \"" (render-name e) "\" as "
                     (puml/alias-name (:id e)) " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                "}"])
      [(str (render/indent indent)
            "state \"" (render-name e) "\" as " (puml/alias-name (:id e)))])))

(defmethod render-uml-element :start-state
  [_ _ indent e]
  [(str (render/indent indent)
        "state " (puml/alias-name (:id e)) " <<start>>")])

(defmethod render-uml-element :end-state
  [_ _ indent e]
  [(str (render/indent indent)
        "state " (puml/alias-name (:id e)) " <<end>>")])

(defmethod render-uml-element :fork
  [_ _ indent e]
  [(str (render/indent indent)
        "state " (puml/alias-name (:id e)) " <<fork>>")])

(defmethod render-uml-element :join
  [_ _ indent e]
  [(str (render/indent indent)
        "state " (puml/alias-name (:id e)) " <<join>>")])

(defmethod render-uml-element :choice
  [_ _ indent e]
  [(str (render/indent indent)
        "state " (puml/alias-name (:id e)) " <<choice>>")])

(defmethod render-uml-element :transition
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (puml/alias-name (:to e))
        (when (:name e)
          (str " : " (el/element-name e))))])

; TODO won't be rendered because of view categories, add extra constraint relation type for views
(defmethod render-uml-element :model-relation
  [_ _ indent e]
  [(str (render/indent indent)
        (puml/alias-name (:from e))
        (if (:constraint e)
          (if (= :right (:direction e))
            "-[hidden]>"
            "-[hidden]->")
          (str " -"
               (when (:direction e)
                 (uml-directions (:direction e)))
               "-> "))
        (puml/alias-name (:to e))
        (when (:name e)
          (str " : " (el/element-name e))))])

(defmethod render-uml-element :model-element
  [_ view indent e]
  (println "unhandled element of type "
           (:el e) "with id" (:id e)
           "in PlantUML UML rendering of view " (:id view)))

;;;
;;; Diagram Layout
;;;
(defn render-uml-layout
  "Renders the layout for the UML diagram."
  [view]
  (let [plantuml-spec (view/plantuml-spec view)]
    (flatten [;(when (:styles spec)
              ;  (into [] (map #(render-uml-style view %)) (:styles spec)))
              (when (view/sketch-spec view)
                "skinparam handwritten true")
              (when (:compact view)
                (uml-hides (:el view)))
              (when (view/layout-spec view)
                (uml-layouts (view/layout-spec view)))
              (when (view/linetype-spec view)
                (puml/linetypes (view/linetype-spec view)))
              (when (:node-separation plantuml-spec)
                (str "skinparam nodesep " (:node-separation plantuml-spec)))
              (when (:rank-separation plantuml-spec)
                (str "skinparam ranksep " (:rank-separation plantuml-spec)))])))

(defmethod puml/render-plantuml-view :uml-view
  [model options view]
  (let [elements (view/view-elements model view)
        nodes (view/root-elements model (filter el/model-node? elements))
        relations (filter el/model-relation? elements)
        rendered (view/elements-to-render model view (concat nodes relations))]
    (flatten [(str "@startuml " (name (:id view)))
              (render-uml-layout view)
              (puml/render-title view)
              (puml/render-skinparams view)
              (map #(render-uml-element model view 0 %) rendered)
              "@enduml"])))
