(ns lib.plantuml.uml
  "Functions for templates generating PlantUML UML files."
  (:require [clojure.string :as str]
            [lib.plantuml :as puml]
            [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]))

;;;
;;; Maps
;;;
(def directions
  "Maps direction keys to PlantUML UML directions."
  {:down  "down"
   :left  "left"
   :right "right"
   :up    "up"})

(def visibility
  "Maps visibility keys to PlantUML UML visibility markers."
  {:private   "-"
   :protected "#"
   :package   "~"
   :public    "+"})

(def cardinality
  "Maps cardinality keys to PlantUML UML cardinalities."
  {:zero-to-one  "0..1"
   :zero-to-many "0..n"
   :one          "1"
   :one-to-many  "1..n"})

(def hides
  "Maps view-types to PlantUML UML hide directives for more compact layouts."
  {:state-view "hide empty description"
   :code-view  "hide empty members"})

(def use-case-level->color
  "Maps the use case level to a color."
  {:summary     "#FFFFFF"
   :user-goal   "#BBBBFF"
   :subfunction "#8888DD"})

;;;
;;; Render helpers
;;;
(defn render-name
  "Renders the name of the element `e`. If `e` has a :link entry, a link is rendered."
  [e]
  (if-let [link (:link e)]
    (if (keyword? link)
      (str "[[" (link e) " " (m/element-name e) "]]")
      (str "[[" link " " (m/element-name e) "]]"))
    (m/element-name e)))

(defn render-type
  "Renders the type of the element `e`. If the type is a keyword, it is resolved as element in the `model`."
  [model e]
  (if-let [type (:type e)]
    (if (keyword? type)
      (m/element-name (m/resolve-element model type))
      type)
    ; TODO default type?
    ""))

;; TODO use verbatim cardinality, if it's not a keyword/can't be looked up
(defn render-end
  "Renders the name and cardinality of one side of a relation.
   The `name-key` and `card-key` select the entries of the relation `e`."
  [e name-key card-key]
  (cond
    (and (name-key e) (card-key e))
    (str " \"" (name-key e) "\\n" (cardinality (card-key e)) "\" ")

    (name-key e)
    (str " \"" (name-key e) "\" ")

    (card-key e)
    (str " \"" (cardinality (card-key e)) "\" ")))

(defn render-from
  "Renders the referring side of the relation."
  [e]
  (render-end e :from-name :from-card))

(defn render-to
  "Renders the referred side of the relation."
  [e]
  (render-end e :to-name :to-card))

(defn render-stereotype
  "Renders the stereotype of the element `e`."
  [e]
  (when (:stereotype e)
    (str " <<" (:stereotype e) ">>")))

(defn render-line
  "Renders the `parts` as an indented line, skipping nil parts."
  [indent & parts]
  (str (puml/indent indent) (apply str parts)))

;;;
;;; Element rendering
;;;
(defmulti render-uml-element
  "Renders a UML element in PlantUML as a sequence of lines.

   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ _ e] (:el e))
  :hierarchy #'m/element-hierarchy)

(defn render-container
  "Renders the element `e` as a container with the `header` and the children to render.
   The children are ordered by the `sorter` function. A collapsed or empty element
   is rendered with empty braces."
  ([model view indent header e]
   (render-container model view indent header e identity))
  ([model view indent header e sorter]
   (let [children (m/children model e)
         content (seq (v/elements-to-render model view children))]
     (if (and content (not (m/collapsed? e)))
       (flatten [(render-line indent header " {")
                 (map #(render-uml-element model view (+ indent 2) %) (sorter content))
                 (render-line indent "}")])
       [(render-line indent header " {")
        (render-line indent "}")]))))

(defn render-classifier-header
  "Renders the header of a classifier of the given `kind` for element `e`."
  [kind e]
  (str kind " \"" (render-name e) "\" as " (puml/alias-name (:id e))
       (render-stereotype e)))

;; Boundaries
(defmethod render-uml-element :context-boundary
  [model view indent e]
  (let [children (m/children model e)]
    (when-let [content (v/elements-to-render model view children)]
      (flatten [(render-line indent "rectangle \"" (render-name e) "\" {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (render-line indent "}")]))))

;; Use cases
(defmethod render-uml-element :use-case
  [_ _ indent e]
  [(render-line indent
                "usecase \"" (render-name e) "\" as (" (puml/alias-name (:id e)) ")"
                (when (:level e)
                  (str " " (use-case-level->color (:level e)))))])

(defmethod render-uml-element :actor-node
  [_ _ indent e]
  [(render-line indent
                "actor \"" (render-name e) "\" as " (puml/alias-name (:id e)))])

(defmethod render-uml-element :uses
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " -"
                (when (:direction e)
                  (directions (:direction e)))
                "-> " (puml/alias-name (:to e)))])

(defmethod render-uml-element :include
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " ."
                (directions (:direction e :down))
                ".> " (puml/alias-name (:to e)) " : include")])

(defmethod render-uml-element :extends
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " ."
                (directions (:direction e :up))
                ".> " (puml/alias-name (:to e)) " : extends")])

(defmethod render-uml-element :generalizes
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " -"
                (directions (:direction e :right))
                "-|> " (puml/alias-name (:to e)))])

;; Code containers and classifiers
(defmethod render-uml-element :package
  [model view indent e]
  (render-container model view indent
                    (str "package \"" (render-name e) "\" as " (puml/alias-name (:id e)))
                    e))

(defmethod render-uml-element :namespace
  [model view indent e]
  (render-container model view indent
                    (str "namespace \"" (render-name e) "\" as " (puml/alias-name (:id e)))
                    e))

(defmethod render-uml-element :interface
  [model view indent e]
  (render-container model view indent
                    (render-classifier-header "interface" e)
                    e (partial sort-by :name)))

(defmethod render-uml-element :protocol
  [model view indent e]
  (render-container model view indent
                    (render-classifier-header "protocol" e)
                    e))

(defmethod render-uml-element :enum
  [model view indent e]
  (render-container model view indent
                    (render-classifier-header "enum" e)
                    e))

(defmethod render-uml-element :enum-value
  [_ _ indent e]
  [(render-line indent
                (render-name e)
                (when (:value e)
                  (str " = " (:value e))))])

(defmethod render-uml-element :schema
  [model view indent e]
  (render-container model view indent
                    (str "class \"" (render-name e) "\" as " (puml/alias-name (:id e))
                         " <<schema>>")
                    e (partial sort-by :name)))

(defmethod render-uml-element :class
  [model view indent e]
  (render-container model view indent
                    (str (when (:abstract e) "abstract ")
                         "class \"" (render-name e)
                         (when (:generic e)
                           (str "<" (:generic e) ">"))
                         "\" as " (puml/alias-name (:id e))
                         (render-stereotype e))
                    e (partial sort-by :name)))

;; Class members
(defmethod render-uml-element :field
  [model _ indent e]
  [(render-line indent
                (when (= :classifier (:scope e))
                  "{classifier} ")
                (when (:visibility e)
                  (visibility (:visibility e)))
                (render-name e)
                (when (:type e)
                  (str " : " (render-type model e)))
                (when (:optional e)
                  (str " [" (cardinality :zero-to-one) "]"))
                (when (:collection e)
                  (str " [" (cardinality :zero-to-many) "]")))])

(defmethod render-uml-element :parameter
  [model _ _ e]
  [(str (render-name e)
        (when (:type e)
          (str " : " (render-type model e))))])

(defmethod render-uml-element :method
  [model view indent e]
  (let [children (m/children model e)
        parameters (mapcat #(render-uml-element model view 0 %)
                           (v/elements-to-render model view children))]
    [(render-line indent
                  (when (= :classifier (:scope e))
                    "{classifier} ")
                  (when (:abstract e)
                    "{abstract} ")
                  (when (:visibility e)
                    (visibility (:visibility e)))
                  (render-name e) "(" (str/join ", " parameters) ")"
                  (when (:type e)
                    (str " : " (render-type model e))))]))

(defmethod render-uml-element :function
  [_ _ indent e]
  [(render-line indent
                (when (:visibility e)
                  (visibility (:visibility e)))
                (render-name e) "()"
                (when (:type e)
                  (str " : " (:type e))))])

;; Code relations
(defn render-association-like
  "Renders a relation `e` with the `arrow` start and the optional direction."
  [indent arrow e]
  [(render-line indent
                (puml/alias-name (:from e))
                (render-from e)
                " " arrow "-"
                (when (:direction e)
                  (directions (:direction e)))
                "-> "
                (render-to e)
                (puml/alias-name (:to e)))])

(defmethod render-uml-element :composition
  [_ _ indent e]
  (render-association-like indent "*" e))

(defmethod render-uml-element :aggregation
  [_ _ indent e]
  (render-association-like indent "o" e))

(defmethod render-uml-element :association
  [_ _ indent e]
  (render-association-like indent "" e))

(defmethod render-uml-element :inheritance
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:to e)) " <|-"
                (when (:direction e)
                  (directions (:direction e)))
                "- " (puml/alias-name (:from e)))])

(defmethod render-uml-element :implementation
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:to e)) " <|."
                (when (:direction e)
                  (directions (:direction e)))
                ". " (puml/alias-name (:from e)))])

(defmethod render-uml-element :dependency
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " ."
                (when (:direction e)
                  (directions (:direction e)))
                ".> " (puml/alias-name (:to e)))])

;; State machines
(defmethod render-uml-element :state-machine
  [model view indent e]
  (let [children (m/children model e)]
    (when-let [content (v/elements-to-render model view children)]
      (flatten (map #(render-uml-element model view (+ indent 2) %) content)))))

(defmethod render-uml-element :state
  [model view indent e]
  (let [children (m/children model e)
        content (v/elements-to-render model view children)
        header (str "state \"" (render-name e) "\" as " (puml/alias-name (:id e)))]
    (if (and (seq content) (not (m/collapsed? e)))
      (flatten [(render-line indent header " {")
                (map #(render-uml-element model view (+ indent 2) %) content)
                (render-line indent "}")])
      [(render-line indent header)])))

(defn render-pseudo-state
  "Renders a pseudo state with the given `stereotype`."
  [indent stereotype e]
  [(render-line indent "state " (puml/alias-name (:id e)) " <<" stereotype ">>")])

(defmethod render-uml-element :start-state
  [_ _ indent e]
  (render-pseudo-state indent "start" e))

(defmethod render-uml-element :end-state
  [_ _ indent e]
  (render-pseudo-state indent "end" e))

(defmethod render-uml-element :fork
  [_ _ indent e]
  (render-pseudo-state indent "fork" e))

(defmethod render-uml-element :join
  [_ _ indent e]
  (render-pseudo-state indent "join" e))

(defmethod render-uml-element :choice
  [_ _ indent e]
  (render-pseudo-state indent "choice" e))

(defmethod render-uml-element :transition
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e)) " -"
                (when (:direction e)
                  (directions (:direction e)))
                "-> " (puml/alias-name (:to e))
                (when (:name e)
                  (str " : " (m/element-name e))))])

;; Fallbacks
; TODO won't be rendered because of view categories, add extra constraint relation type for views
(defmethod render-uml-element :model-relation
  [_ _ indent e]
  [(render-line indent
                (puml/alias-name (:from e))
                (if (:constraint e)
                  (if (= :right (:direction e))
                    "-[hidden]>"
                    "-[hidden]->")
                  (str " -"
                       (when (:direction e)
                         (directions (:direction e)))
                       "-> "))
                (puml/alias-name (:to e))
                (when (:name e)
                  (str " : " (m/element-name e))))])

(defmethod render-uml-element :model-element
  [_ view indent e]
  [(render-line indent
                "' unhandled element of type " (:el e) " with id " (:id e)
                " in PlantUML UML rendering of view " (:id view))])

;;;
;;; Diagram
;;;
(defn render-uml-layout
  "Renders the layout directives for the UML diagram of the `view` as a vector of lines."
  [view]
  (into (if-let [hide (and (:compact view) (hides (:el view)))]
          [hide]
          [])
        (puml/render-layout view)))

(defn render-uml-view
  "Renders the UML `view` of the `model` as a vector of PlantUML lines."
  [model view]
  (let [elements (v/view-elements model view)
        nodes (v/root-elements model (filter m/model-node? elements))
        relations (filter m/model-relation? elements)
        rendered (v/elements-to-render model view (concat nodes relations))]
    (->> (concat [(str "@startuml " (name (:id view)))]
                 (render-uml-layout view)
                 [(puml/render-title view)
                  (puml/render-skinparams view)]
                 (mapcat #(render-uml-element model view 0 %) rendered)
                 ["@enduml"])
         (remove nil?)
         (vec))))
