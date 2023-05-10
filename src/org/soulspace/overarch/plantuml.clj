(ns org.soulspace.overarch.plantuml
  "Functionality to export views to plantuml."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.diagram :as dia]
            [org.soulspace.overarch.export :as exp]))

;;;;
;;;; PlantUML rendering
;;;;

;;;
;;; PlantUML mappings#
;;;

; plantuml
(def element->methods
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

; plantuml
(def subtype->suffix
  "Maps the subtype of an element to the PlantUML suffix."
  {:database "Db"
   :queue "Queue"})

; plantuml
(def layouts
  "Maps layout keys to PlantUML."
  {:landscape "LAYOUT_LANDSCAPE()"
   :left-right "LAYOUT_LEFT_RIGHT()"
   :top-down "LAYOUT_TOP_DOWN()"})

; plantuml
(def linetypes
  "Maps linetype keys to PlantUML."
  {:orthogonal "skinparam linetype ortho"
   :polygonal  "skinparam linetype polyline"})

; plantuml
(def directions
  "Maps direction keys to PlantUML Rel suffixes."
  {:down  "_Down"
   :left  "_Left"
   :right "_Right"
   :up    "_Up"})

; plantuml
(def styles
  {:element  "AddElementTag"
   :rel      "AddRelTag"
   :boundary ""})

(defmulti render-element
  (fn [diagram indent e] (:el e))
  :hierarchy #'dia/element-hierarchy)


(defmethod render-element :boundary
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (dia/elements-to-render diagram (:ct e))]
      (flatten [(str (dia/render-indent indent) 
                     (element->methods (:el e)) "("
                     (dia/alias-name (:id e)) ", \"" 
                     (:name e) "\""
                     ") {")
                (map #(render-element diagram (+ indent 2) %)
                     children)
                (str (dia/render-indent indent) "}")]))
    [(str (dia/render-indent indent) 
                     (element->methods (:el e)) "("
                     (dia/alias-name (:id e)) ", \"" 
                     (:name e) "\""
                     ")")]))

(defmethod render-element :person
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->methods (:el e))
        (when (:external e) "_Ext") "("
        (dia/alias-name (:id e)) ", \""
        (:name e) "\""
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        ")")])

(defmethod render-element :system
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->methods (:el e))
        (when (:external e) "_Ext") "("
        (dia/alias-name (:id e)) ", \""
        (:name e) "\""
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        ")")])

(defmethod render-element :container
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->methods (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (dia/alias-name (:id e)) ", \""
        (:name e) "\""
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        ")")])

(defmethod render-element :component
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->methods (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
        (when (:external e) "_Ext") "("
        (dia/alias-name (:id e)) ", \""
        (:name e) "\""
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        ")")])

(defmethod render-element :node
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (dia/elements-to-render diagram (:ct e))]
      (flatten [(str (dia/render-indent indent)
                     (element->methods (:el e)) "("
                     (dia/alias-name (:id e)) ", \""
                     (:name e) "\""
                     (when (:type e) (str ", $type=\"" (:type e) "\""))
                     (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
                     ") {")
                (map #(render-element diagram (+ indent 2) %)
                     children)
                (str (dia/render-indent indent) "}")]))
    [(str (dia/render-indent indent)
          (element->methods (:el e)) "("
          (dia/alias-name (:id e)) ", \""
          (:name e) "\""
          (when (:type e) (str ", $type=\"" (:type e) "\""))
          (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
          ")")]))

(defmethod render-element :rel
  [diagram indent e]
  [(str (dia/render-indent indent)
        (element->methods (:el e))
        (when (:direction e) (directions (:direction e))) "("
        (dia/alias-name (:from e)) ", "
        (dia/alias-name (:to e)) ", \""
        (:name e) "\""
        (when (:tech e) (str ", $techn=\"" (:tech e) "\""))
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        ")")])

; plantuml
(defn render-imports
  "Renders the imports for the diagram."
  [diagram]
  (comment
    ; include icon/sprite sets, if icons are used, e.g.
    "!define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"
    "!define FONTAWESOME https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"
    "!include DEVICONS/angular.puml
     !include DEVICONS/java.puml
     !include DEVICONS/msql_server.puml
     !include FONTAWESOME/users.puml
     "
    )
  (cond
    (= :context-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml"
    (= :system-landscape-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml"
    (= :container-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml"
    (= :component-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml"
    (= :dynamic-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Dynamic.puml"
    (= :deployment-diagram (:el diagram))
    "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml")
  )

; plantuml
(defn render-style
  "Renders a styles for the diagram."
  [diagram style]
  ; TODO
  )

; plantuml
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

; plantuml
(defn render-legend
  "Renders the legend for the diagram."
  [diagram]
  (let [spec (:spec diagram)]
    [(when (:legend spec)
       "SHOW_LEGEND()")]))

; plantuml
(defn render-title
  "Renders the title of the diagram."
  [diagram]
  (when (:title diagram) (str "title " (:title diagram))))

; plantuml
(defmethod dia/render-diagram :plantuml
  [format diagram]
  (let [children (dia/elements-to-render diagram)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (dia/alias-name (:id diagram)))
              (render-imports diagram)
              (render-layout diagram)
              (render-title diagram)
              (map #(render-element diagram 0 %) children)
              (render-legend diagram)
              "@enduml"])))

;;;
;;; PlantUML file export
;;;

; plantuml
(defmethod exp/export-file :plantuml
  [format diagram]
  (let [dir-name (str "exports/plantuml/" (namespace (:id diagram)))
        dir (io/as-file dir-name)]
    (file/create-dir dir)
    (io/as-file (str dir-name "/"
                     (name (:id diagram)) ".puml"))))

; plantuml
(defmethod exp/export-diagram :plantuml
  [format diagram]
  (with-open [wrt (io/writer (exp/export-file format diagram))]
    (binding [*out* wrt]
      (println (str/join "\n" (dia/render-diagram format diagram))))))
