;;;;
;;;; PlantUML rendering and export
;;;;
(ns org.soulspace.overarch.adapter.render.plantuml
  "Functions to export views to PlantUML."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.util.io :as oio]))

;;;
;;; PlantUML mappings
;;;

; FIXME remote urls
(def sprite-libraries
  "Definition of sprite libraries."
  {:azure          {:name "azure"
                    :local-prefix "azure"
                    :local-imports ["AzureCommon"
                                    "AzureC4Integration"]
                    :remote-prefix "AZURE"
                    :remote-url ""
                    :remote-imports ["AzureCommon"
                                     "AzureC4Integration"]}
   :awslib         {:name "awslib"
                    :local-prefix "awslib14"
                    :local-imports ["AWSCommon"
                                    "AWSC4Integration"]
                    :remote-prefix "AWS"
                    :remote-url ""
                    :remote-imports ["AWSCommon"
                                     "AWSC4Integration"]}
   :cloudinsight   {:name "cloudinsight"
                    :local-prefix "cloudinsight"
                    :local-imports []
                    :remote-prefix "CLOUDINSIGHT"
                    :remote-url ""}
   :cloudogu       {:name "cloudogu"
                    :local-prefix "cloudogu"
                    :local-imports ["common"]
                    :remote-prefix "CLOUDOGU"
                    :remote-url ""}
   :logos          {:name "logos"
                    :local-prefix "logos"}
   :devicons       {:name "devicons"
                    :local-prefix "tupadr3"
                    :local-imports ["common"]
                    :remote-prefix "DEVICONS"
                    :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"}
   :devicons2      {:name "devicons2"
                    :local-prefix "tupadr3"
                    :local-imports ["common"]
                    :remote-prefix "DEVICONS2"
                    :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons2"}
   :font-awesome-5 {:name "font-awesome-5"
                    :local-prefix "tupadr3"
                    :local-imports ["common"]
                    :remote-prefix "FONTAWESOME"
                    :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"}
   :material       {:name "material"
                    :local-prefix "material"
                    :local-imports ["common"]}})

(def view-hierarchy
  "Hierarchy for views"
  (-> (make-hierarchy)
      (derive :system-landscape-view :c4-view)
      (derive :context-view          :c4-view)
      (derive :container-view        :c4-view)
      (derive :component-view        :c4-view)
      (derive :deployment-view       :c4-view)
      (derive :dynamic-view          :c4-view)
      (derive :use-case-view         :uml-view)
      (derive :state-machine-view    :uml-view)
      (derive :class-view            :uml-view)))

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

(def linetypes
  "Maps linetype keys to PlantUML C4."
  {:orthogonal "skinparam linetype ortho"
   :polygonal  "skinparam linetype polyline"})

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
;;; Tech to Sprite mapping
;;;

(def sprite-resources
  ["cloudogu" "cloudinsight" "logos" "devicons" "devicons2"
   "font-awesome-5" "azure" "awslib14"])

(defn load-sprite-mappings-from-dir
  "Loads the mappings from the directory `dir` and returns the merged map."
  [dir]
  (->> (file/all-files-by-extension ".edn" dir)
       (map oio/load-edn)
       (reduce merge)))

(defn load-sprite-mappings-from-resource
  "Loads the list of `resources` and returns the merged map."
  [resources]
  (->> resources
       (map #(str "plantuml/" % ".edn"))
       (map oio/load-edn-from-resource)
       (reduce merge)))

; use (io/resource ) or load sprite mapping from options config dir 
(defn load-sprite-mappings
  "Loads the mappings from tech to sprite."
  [options]
  (if (:config-dir options)
    (load-sprite-mappings-from-dir (str (:config-dir options) "/plantuml"))
    (oio/load-edn-from-resource sprite-resources)))

(def tech->sprite (load-sprite-mappings-from-resource sprite-resources))

(defn sprite-path
  "Returns a path for the given `sprite`."
  [sprite]
  (str (:prefix sprite) "/"
       (if (empty? (:path sprite))
         ""
         (str (:path sprite) "/"))
       (:name sprite)))

(defn sorted-sprite-mappings
  "Returns a list of sprite mappings sorted by the key."
  [m]
  (->> m
       (keys)
       (sort)
       (map (fn [key] (merge {:key key} (m key))))))

(defn sprite?
  "Returns true if the icon-map contains an icon for the given technology."
  [tech]
  (tech->sprite tech))

(comment
  (load-sprite-mappings-from-resource ["azure" "awslib14"])
  (count (sorted-sprite-mappings tech->sprite)))

;;;
;;; Rendering
;;;

(defn renderer
  "Returns the renderer for the diagram"
  [_ _ diagram]
  (:el diagram))

(defmulti render-plantuml-view
  "Renders the diagram with PlantUML."
  renderer
  :hierarchy #'view-hierarchy)

;;
;; Elements
;; 
(defn alias-name
  "Returns a valid PlantUML alias for the namespaced keyword `kw`."
  [kw]
  (symbol (str (str/replace (sstr/hyphen-to-camel-case (namespace kw)) \. \_) "_"
               (sstr/hyphen-to-camel-case (name kw)))))
(defn short-name
  "Returns a valid PlantUML alias for the name part of the keyword `kw`."
  [kw]
  (sstr/hyphen-to-camel-case (name kw)))

(defmulti render-c4-element
  "Renders a C4 element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ e] (:el e))
  :hierarchy #'view/element-hierarchy)

(defmulti render-uml-element
  "Renders a UML element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ e] (:el e))
  :hierarchy #'view/element-hierarchy)

(defmethod render-c4-element :boundary
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (c4-element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (view/element-name e) "\""
                     (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-c4-element view (+ indent 2) %)
                     children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (c4-element->method (:el e)) "("
          (alias-name (:id e)) ", \""
          (view/element-name e) "\""
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-c4-element :person
  [_ indent e]
  [(str (view/render-indent indent)
        (c4-element->method (:el e))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (view/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-c4-element :system
  [_ indent e]
  [(str (view/render-indent indent)
        (c4-element->method (:el e))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (view/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:tech e) (str ", $type=\"" (:tech e) "\""))
        (if (:sprite e)
          (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
          (when (sprite? (:tech e))
            (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-c4-element :container
  [_ indent e]
  [(str (view/render-indent indent)
        (c4-element->method (:el e))
        (when (:subtype e) (c4-subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (view/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (if (:sprite e)
          (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
          (when (sprite? (:tech e))
            (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-c4-element :component
  [_ indent e]
  [(str (view/render-indent indent)
        (c4-element->method (:el e))
        (when (:subtype e) (c4-subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (view/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (if (:sprite e)
          (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
          (when (sprite? (:tech e))
            (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-c4-element :node
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (c4-element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (view/element-name e) "\""
                     (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
                     (when (:tech e) (str ", $type=\"" (:tech e) "\""))
                     (if (:sprite e)
                       (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
                       (when (sprite? (:tech e))
                         (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
                     (when (:style e) (str ", $tag=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-c4-element view (+ indent 2) %)
                     children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (c4-element->method (:el e)) "("
          (alias-name (:id e)) ", \""
          (view/element-name e) "\""
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:tech e) (str ", $type=\"" (:tech e) "\""))
          (if (:sprite e)
            (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
            (when (sprite? (:tech e))
              (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-c4-element :rel
  [_ indent e]
  (if (:constraint e) ; TODO :hidden or :constraint
    [(str (view/render-indent indent) "Lay"
          (when (:direction e) (c4-directions (:direction e))) "("
          (alias-name (:from e)) ", "
          (alias-name (:to e))
          ")")]
    [(str (view/render-indent indent)
          (c4-element->method (:el e))
          (when (:direction e) (c4-directions (:direction e))) "("
          (if (:reverse e)
            (str (alias-name (:to e)) ", "
                 (alias-name (:from e)) ", \"")
            (str (alias-name (:from e)) ", "
                 (alias-name (:to e)) ", \""))
          (:name e) "\""
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
          (if (:sprite e)
            (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) ",scale=0.5\"")
            (when (sprite? (:tech e))
              (str ", $sprite=\"" (:name (tech->sprite (:tech e))) ",scale=0.5\"")))
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-uml-element :context-boundary
  [view indent e]
  (when (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "rectangle \"" (view/element-name e) "\" {")
                (map #(render-uml-element view (+ indent 2) %) children)
                "}"]))))

(defmethod render-uml-element :use-case
  [_ indent e]
  [(str (view/render-indent indent)
        "usecase \"" (view/element-name e) "\" as ("
        (alias-name (:id e)) ")"
        (when (:level e)
          (str " " (use-case-level->color (:level e)))))])

(defmethod render-uml-element :actor
  [_ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (alias-name (:id e)))])

(defmethod render-uml-element :person
  [_ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (alias-name (:id e)))])

(defmethod render-uml-element :system
  [_ indent e]
  [(str (view/render-indent indent)
        "actor \"" (view/element-name e) "\" as " (alias-name (:id e)))])

(defmethod render-uml-element :uses
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (alias-name (:to e)))])

(defmethod render-uml-element :include
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (alias-name (:to e)) " : include")])

(defmethod render-uml-element :extends
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (alias-name (:to e)) " : extends")])

(defmethod render-uml-element :generalizes
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-|> "
        (alias-name (:to e)))])

(defmethod render-uml-element :package
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "package \"" (view/element-name e)
                     "\" as " (alias-name (:id e)) " {")
                (map #(render-uml-element view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "package \"" (view/element-name e)
          "\" as " (alias-name (:id e)) " {}")]))

(defmethod render-uml-element :namespace
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "namespace \"" (view/element-name e)
                     "\" as " (alias-name (:id e)) " {")
                (map #(render-uml-element view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "namespace \"" (view/element-name e)
          "\" as " (alias-name (:id e)) " {}")]))

(defmethod render-uml-element :interface
  [view indent e]
  (if (seq (:ct e))
   (let [children (view/elements-to-render view (:ct e))]
     (flatten [(str (view/render-indent indent)
                    "interface \"" (view/element-name e)
                    "\" as " (alias-name (:id e))
                    (when (:stereotype e)
                      (str " <<" (:stereotype e) ">>"))
                    " {")
               (map #(render-uml-element view (+ indent 2) %) children)
               (str (view/render-indent indent) "}")]))
   [(str (view/render-indent indent)
         "interface \"" (view/element-name e)
         "\" as " (alias-name (:id e))
         (when (:stereotype e)
           (str " <<" (:stereotype e) ">>"))
         " {}")]))

(defmethod render-uml-element :protocol
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "protocol \"" (view/element-name e)
                     "\" as " (alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "protocol \"" (view/element-name e)
          "\" as " (alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod render-uml-element :enum
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     "enum \"" (view/element-name e)
                     "\" as " (alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
                (map #(render-uml-element view (+ indent 2) %) children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          "enum \"" (view/element-name e)
          "\" as " (alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod render-uml-element :class
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (when (:abstract e) "abstract ")
                     "class \"" (view/element-name e)
                     (when (:generic e)
                       (str "<" (:generic e) ">"))
                     "\" as " (alias-name (:id e))
                     (when (:stereotype e)
                       (str " <<" (:stereotype e) ">>"))
                     " {")
               (map #(render-uml-element view (+ indent 2) %) children)
               (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (when (:abstract e) "abstract ")
          "class \"" (view/element-name e)
          (when (:generic e)
            (str "<" (:generic e) ">"))
          "\" as " (alias-name (:id e))
          (when (:stereotype e)
            (str " <<" (:stereotype e) ">>"))
          " {}")]))

(defmethod render-uml-element :field
  [view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e)
        (when (:type e)
          (str " : " (:type e)))
        )])

(defmethod render-uml-element :method
  [view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e) "()"
        (when (:type e)
          (str " : " (:type e)))
        )])

(defmethod render-uml-element :function
  [view indent e]
  [(str (view/render-indent indent)
        (when (:visibility e)
          (uml-visibility (:visibility e)))
        (view/element-name e) "()"
        (when (:type e)
          (str " : " (:type e))))])

(defmethod render-uml-element :composition
  [_ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
        (alias-name (:from e))
        " *--> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (alias-name (:to e)))])

(defmethod render-uml-element :aggregation
  [_ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
        (alias-name (:from e))
        (when (:from-card e)
          (str " \"" (uml-cardinality (:from-card e)) "\" "))
        " o-"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (alias-name (:to e)))])

(defmethod render-uml-element :association
  [_ indent e]
  ; TODO render roles
  [(str (view/render-indent indent)
        (alias-name (:from e))
        " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (when (:to-card e)
          (str " \"" (uml-cardinality (:to-card e)) "\" "))
        (alias-name (:to e)))])

(defmethod render-uml-element :inheritance
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:to e)) " <|-"
        (when (:direction e)
          (uml-directions (:direction e)))
        "- "
        (alias-name (:from e)))])

(defmethod render-uml-element :implementation
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:to e)) " <|."
        (when (:direction e)
          (uml-directions (:direction e)))
        ". "
        (alias-name (:from e)))])

(defmethod render-uml-element :dependency
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " ."
        (when (:direction e)
          (uml-directions (:direction e)))
        ".> "
        (alias-name (:to e)))])

(defmethod render-uml-element :state-machine
  [view indent e]
  (when (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(map #(render-uml-element view (+ indent 2) %) children)]))))

(defmethod render-uml-element :state
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(view/render-indent indent)
                (str "state \"" (view/element-name e) "\" as "
                     (alias-name (:id e)) " {")
                (map #(render-uml-element view (+ indent 2) %) children)
                "}"]))
    [(str (view/render-indent indent)
          "state \"" (view/element-name e) "\" as " (alias-name (:id e)))]))

(defmethod render-uml-element :start-state
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<start>>")])

(defmethod render-uml-element :end-state
  [_ indent e]
  [(str (view/render-indent indent)
        "state " (alias-name (:id e)) " <<end>>")])

(defmethod render-uml-element :fork
  [_ indent e]
  [(str (view/render-indent indent)
        "state " (alias-name (:id e)) " <<fork>>")])

(defmethod render-uml-element :join
  [_ indent e]
  [(str (view/render-indent indent)
        "state " (alias-name (:id e)) " <<join>>")])

(defmethod render-uml-element :choice
  [_ indent e]
  [(str (view/render-indent indent)
        "state " (alias-name (:id e)) " <<choice>>")])

(defmethod render-uml-element :transition
  [_ indent e]
  [(str (view/render-indent indent)
        (alias-name (:from e)) " -"
        (when (:direction e)
          (uml-directions (:direction e)))
        "-> "
        (alias-name (:to e)) " : " (view/element-name e))])

;;;
;;; Imports
;;;

;;
;; Sprite Imports
;;
(defn collect-sprites
  "Returns the set of sprites for the elements of the coll."
  ([coll]
   (collect-sprites #{} coll))
  ([sprites coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (:sprite e)
         (recur (collect-sprites (set/union sprites #{(:sprite e)}) (:ct e)) (rest coll))
         (recur (collect-sprites sprites (:ct e)) (rest coll))))
     sprites)))

(defn collect-all-sprites
  "Collects all sprites for the collection of elements."
  [coll]
  (filter sprite? (set/union (view/collect-technologies coll) (collect-sprites coll))))

(defn sprites-for-diagram
  "Collects the sprites for the"
  [m view]
  (->> view
       (view/elements-to-render m)
       (collect-all-sprites)
       (map #(tech->sprite %))))

(defn local-import
  "Renders a local import."
  ([path]
   (str "!include <" path ">"))
  ([prefix path]
   (str "!include <" prefix "/" path ">")))

(defn remote-import
  "Renders a remote import."
  ([url]
   (str "!includeurl " url))
  ([prefix path]
   (str "!includeurl " (str/upper-case prefix) "/" path)))

(defn render-sprite-import
  "Renders the import for an sprite."
  [view sprite]
  (if (get-in view [:spec :plantuml :remote-imports])
    (remote-import (sprite-path sprite))
    (local-import (sprite-path sprite))))

(defn render-spritelib-import
  "Renders the imports for an sprite library."
  [view sprite-lib]
  (if (get-in view [:spec :plantuml :remote-imports])
    [(str "!define " (:remote-prefix sprite-lib) (:remote-url sprite-lib))
     (map (partial remote-import (:remote-prefix sprite-lib))
          (:remote-imports (sprite-libraries sprite-lib)))]
    [(map (partial local-import (:local-prefix (sprite-libraries sprite-lib)))
          (:local-imports (sprite-libraries sprite-lib)))]))

(defn render-sprite-imports
  "Renders the imports for icon/sprite libraries."
  [m view]
  (let [icon-libs (get-in view [:spec :plantuml :sprite-libs])
        icons (sprites-for-diagram m view)]
    [(map (partial render-spritelib-import view) icon-libs)
     (map (partial render-sprite-import view) icons)]))

(comment
  (tech->sprite "Angular"))

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
         (short-name (:id style))
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
         (short-name (:id style))
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
  [view]
  (let [spec (:spec view)]
    (flatten [(when (:styles spec)
                (into [] (map #(render-c4-style view %)) (:styles spec)))
              (when (:sketch spec)
                "LAYOUT_AS_SKETCH()")
              (when (:layout spec)
                (c4-layouts (:layout spec)))
              (when (:linetype spec)
                (linetypes (:linetype spec)))])))

(defn render-c4-legend
  "Renders the legend for the diagram."
  [view]
  (let [spec (:spec view)]
    [(when-not (:no-legend spec)
       "SHOW_LEGEND()")]))

(defn render-title
  "Renders the title of the diagram."
  [view]
  (when (:title view) (str "title " (:title view))))

(defmethod render-plantuml-view :c4-view
  [m options view]
  (let [children (view/elements-to-render m view)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (alias-name (:id view)))
              (render-c4-imports view)
              (render-sprite-imports m view)
              (render-c4-layout view)
              (render-title view)
              (map #(render-c4-element view 0 %) children)
              (render-c4-legend view)
              "@enduml"])))


(defn render-uml-layout
  "Renders the layout for the UML diagram."
  [view]
  (let [spec (:spec view)]
    (flatten [;(when (:styles spec)
              ;  (into [] (map #(render-uml-style view %)) (:styles spec)))
              (when (:sketch spec)
                "skinparam handwritten true")
              (when (:compact spec)
                (uml-hides (:el view)))
              (when (:layout spec)
                (uml-layouts (:layout spec)))
              (when (:linetype spec)
                (linetypes (:linetype spec)))])))


(defmethod render-plantuml-view :uml-view
  [m options view]
  (let [children (view/elements-to-render m view)]
    (flatten [(str "@startuml " (alias-name (:id view)))
              (render-uml-layout view)
              (render-title view)
              (map #(render-uml-element view 0 %) children)
              "@enduml"])))

;;;
;;; PlantUML Rendering dispatch
;;;
(def plantuml-views
  "Contains the views to be rendered with plantuml."
  #{:system-landscape-view :context-view :container-view :component-view
    :deployment-view :dynamic-view :class-view :use-case-view
    :state-machine-view})

(defn plantuml-view?
  "Returns true, if the view is to be rendered with plantuml."
  [view]
  (contains? plantuml-views (:el view)))

(defmethod rndr/render-file :plantuml
  [m format options view]
  (let [dir-name (str (:render-dir options) "/plantuml/" (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".puml"))))

(defmethod rndr/render-view :plantuml
  [m format options view]
  (with-open [wrt (io/writer (rndr/render-file m format options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-plantuml-view m options view))))))

(defmethod rndr/render :plantuml
  [m format options]
  (doseq [view (view/get-views m)]
    (when (plantuml-view? view)
      (rndr/render-view m format options
                        (assoc view :ct (view/specified-elements m view)) ; TODO do preprocessing once in build phase?
                        ))))
