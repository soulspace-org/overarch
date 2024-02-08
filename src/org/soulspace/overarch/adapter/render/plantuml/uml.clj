(ns org.soulspace.overarch.adapter.render.plantuml.uml
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]))

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
   :class-view "hide empty members"})

(def use-case-level->color
  "Maps the use case level to a color."
  {:summary "#FFFFFF"
   :user-goal "#BBBBFF"
   :subfunction "#8888DD"})

;;;
;;; Rendering
;;;

(defmethod puml/render-uml-element :context-boundary
  [model view indent e]
  (when (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "rectangle \"" (view/element-name e) "\" {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                "}"]))))

(defmethod puml/render-uml-element :use-case
  [_ _ indent e]
  [(str (view/render-indent indent)
        "usecase \"" (view/element-name e) "\" as ("
        (puml/alias-name (:id e)) ")"
        (when (:level e)
          (str " " (use-case-level->color (:level e)))))])

(defmethod puml/render-uml-element :actor
  [_ _ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (puml/alias-name (:id e)))])

(defmethod puml/render-uml-element :person
  [_ _ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (puml/alias-name (:id e)))])

(defmethod puml/render-uml-element :system
  [_ _ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (puml/alias-name (:id e)))])

(defmethod puml/render-uml-element :uses
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (puml/alias-name (:to e)))])

(defmethod puml/render-uml-element :include
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (puml/alias-name (:to e)) " : include")])

(defmethod puml/render-uml-element :extends
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (puml/alias-name (:to e)) " : extends")])

(defmethod puml/render-uml-element :generalizes
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-|> "
        (puml/alias-name (:to e)))])

(defmethod puml/render-uml-element :package
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "package \"" (view/element-name e)
                     "\" as " (puml/alias-name (:id e)) " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "package \"" (view/element-name e)
          "\" as " (puml/alias-name (:id e)) " {}")]))

(defmethod puml/render-uml-element :namespace
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "namespace \"" (view/element-name e)
                     "\" as " (puml/alias-name (:id e)) " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "namespace \"" (view/element-name e)
          "\" as " (puml/alias-name (:id e)) " {}")]))

(defmethod puml/render-uml-element :interface
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "interface \"" (view/element-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "interface \"" (view/element-name e)
          "\" as " (puml/alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod puml/render-uml-element :protocol
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "protocol \"" (view/element-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "protocol \"" (view/element-name e)
          "\" as " (puml/alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod puml/render-uml-element :enum
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "enum \"" (view/element-name e)
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "enum \"" (view/element-name e)
          "\" as " (puml/alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod puml/render-uml-element :class
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (when (:abstract e) "abstract ")
                     "class \"" (view/element-name e)
                     (when (:generic e)
                       (str "<" (:generic e) ">"))
                     "\" as " (puml/alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (when (:abstract e) "abstract ")
          "class \"" (view/element-name e)
          (when (:generic e)
            (str "<" (:generic e) ">"))
          "\" as " (puml/alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod puml/render-uml-element :field
  [model view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e)
        (when (:type e)
          (str " : " (:type e))))])

(defmethod puml/render-uml-element :method
  [model view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e) "()"
        (when (:type e)
          (str " : " (:type e))))])

(defmethod puml/render-uml-element :function
  [model view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e) "()"
        (when (:type e)
          (str " : " (:type e))))])

(defmethod puml/render-uml-element :composition
  [_ _ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
        (puml/alias-name (:from e))
        " *--> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (puml/alias-name (:to e)))])

(defmethod puml/render-uml-element :aggregation
  [_ _ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
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

(defmethod puml/render-uml-element :association
  [_ _ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
        (puml/alias-name (:from e))
        " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (puml/alias-name (:to e)))])

(defmethod puml/render-uml-element :inheritance
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:to e)) " <|-"
        (when (:direction e)
          (uml-directions (:direction e)))
        "- "
        (puml/alias-name (:from e)))])

(defmethod puml/render-uml-element :implementation
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:to e)) " <|."
        (when (:direction e)
          (uml-directions (:direction e)))
        ". "
        (puml/alias-name (:from e)))])

(defmethod puml/render-uml-element :dependency
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (puml/alias-name (:to e)))])

(defmethod puml/render-uml-element :state-machine
  [model view indent e]
  (when (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(map #(puml/render-uml-element model view (+ indent 2) %) children)]))))

(defmethod puml/render-uml-element :state
  [model view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render model view (:ct e))]
      (flatten [(view/render-indent indent)
                (str "state \"" (view/element-name e) "\" as "
                     (puml/alias-name (:id e)) " {")
                (map #(puml/render-uml-element model view (+ indent 2) %) children)
                "}"]))
    [(str (view/render-indent indent)
          "state \"" (view/element-name e) "\" as " (puml/alias-name (:id e)))]))

(defmethod puml/render-uml-element :start-state
  [_ _ indent e]
  [(str (view/render-indent indent)
        "state " (puml/alias-name (:id e)) " <<start>>")])

(defmethod puml/render-uml-element :end-state
  [_ _ indent e]
  [(str (view/render-indent indent)
        "state " (puml/alias-name (:id e)) " <<end>>")])

(defmethod puml/render-uml-element :fork
  [_ _ indent e]
  [(str (view/render-indent indent)
        "state " (puml/alias-name (:id e)) " <<fork>>")])

(defmethod puml/render-uml-element :join
  [_ _ indent e]
  [(str (view/render-indent indent)
        "state " (puml/alias-name (:id e)) " <<join>>")])

(defmethod puml/render-uml-element :choice
  [_ _ indent e]
  [(str (view/render-indent indent)
        "state " (puml/alias-name (:id e)) " <<choice>>")])

(defmethod puml/render-uml-element :transition
  [_ _ indent e]
  [(str (view/render-indent indent)
        (puml/alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (puml/alias-name (:to e)) " : " (view/element-name e))])


;;;
;;; Diagram Layout
;;;

(defn render-uml-layout
  "Renders the layout for the UML diagram."
  [view]
  (let [spec (:spec view)
        plantuml-spec (:plantuml spec)]
    (flatten [;(when (:styles spec)
              ;  (into [] (map #(render-uml-style view %)) (:styles spec)))
              (when (:sketch spec)
                "skinparam handwritten true")
              (when (:compact spec)
                (uml-hides (:el view)))
              (when (:layout spec)
                (uml-layouts (:layout spec)))
              (when (:linetype spec)
                (puml/linetypes (:linetype spec)))
              (when (:node-separation plantuml-spec)
                (str "skinparam nodesep " (:node-separation plantuml-spec)))
              (when (:rank-separation plantuml-spec)
                (str "skinparam ranksep " (:rank-separation plantuml-spec)))])))

(defmethod puml/render-plantuml-view :uml-view
  [model options view]
  (let [children (view/elements-to-render model view)]
    (flatten [(str "@startuml " (puml/alias-name (:id view)))
              (render-uml-layout view)
              (puml/render-title view)
              (map #(puml/render-uml-element model view 0 %) children)
              "@enduml"])))

