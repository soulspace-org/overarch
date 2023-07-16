(ns org.soulspace.overarch.exports.plantuml
  "Functions to export views to PlantUML."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.view :as view]
            [org.soulspace.overarch.export :as exp]
            [org.soulspace.overarch.io :as oio]))

;;;;
;;;; PlantUML rendering
;;;;



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

(def element->method
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

(def view-type->import
  "Map from diagram type to PlantUML import."
  {:system-landscape-view "C4_Context.puml"
   :context-view          "C4_Context.puml"
   :container-view        "C4_Container.puml"
   :component-view        "C4_Component.puml"
   :dynamic-view          "C4_Dynamic.puml"
   :deployment-view       "C4_Deployment.puml"})

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
  {:down  "_D"
   :left  "_L"
   :right "_R"
   :up    "_U"})

(def style->method
  {:element  "AddElementTag"
   :rel      "AddRelTag"
   :boundary "AddBoundaryTag"})

(def line-style->method
  {:bold   "BoldLine()"
   :dashed "DashedLine()"
   :dotted "DottedLine()"})

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
  (count (sorted-sprite-mappings tech->sprite))
  )

;;;
;;; Rendering
;;;

(defn renderer
  "Returns the renderer for the diagram"
  [options diagram]
  (:el diagram))

(defmulti render-view
  "Renders the diagram with PalantUML"
  renderer
  :hierarchy #'view/view-hierarchy)

;;
;; Elements
;; 
(defn alias-name
  "Returns a valid PlantUML alias for the namespaced keyword `kw`."
  [kw]
  (symbol (str (sstr/hyphen-to-camel-case (namespace kw)) "_"
               (sstr/hyphen-to-camel-case (name kw)))))
(defn short-name
  "Returns a valid PlantUML alias for the name part of the keyword `kw`."
  [kw]
  (sstr/hyphen-to-camel-case (name kw)))

(defmulti render-element
  "Renders an element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ e] (:el e))
  :hierarchy #'view/element-hierarchy)

(defmethod render-element :boundary
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (view/element-name e) "\""
                     (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-element view (+ indent 2) %)
                     children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (element->method (:el e)) "("
          (alias-name (:id e)) ", \""
          (view/element-name e) "\""
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

(defmethod render-element :person
  [_ indent e]
  [(str (view/render-indent indent)
        (element->method (:el e))
        (when (:external e) "_Ext") "("
        (alias-name (:id e)) ", \""
        (view/element-name e) "\""
        (when (:desc e) (str ", $descr=\"" (:desc e) "\""))
        (when (:type e) (str ", $type=\"" (:type e) "\""))
        (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
        ")")])

(defmethod render-element :system
  [_ indent e]
  [(str (view/render-indent indent)
        (element->method (:el e))
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

(defmethod render-element :container
  [_ indent e]
  [(str (view/render-indent indent)
        (element->method (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
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

(defmethod render-element :component
  [_ indent e]
  [(str (view/render-indent indent)
        (element->method (:el e))
        (when (:subtype e) (subtype->suffix (:subtype e)))
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

(defmethod render-element :node
  [view indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render view (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (element->method (:el e)) "("
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
                (map #(render-element view (+ indent 2) %)
                     children)
                (str (view/render-indent indent) "}")]))
    [(str (view/render-indent indent)
          (element->method (:el e)) "("
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

(defmethod render-element :rel
  [_ indent e]
  (if (:constraint e) ; TODO :hidden or :constraint
    [(str (view/render-indent indent) "Lay"
          (when (:direction e) (directions (:direction e))) "("
          (alias-name (:from e)) ", "
          (alias-name (:to e))
          ")")]
    [(str (view/render-indent indent)
          (element->method (:el e))
          (when (:direction e) (directions (:direction e))) "("
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

(defmethod render-element :use-case
  [_ indent e]
  ; TODO add color based on :level
  [(str "usecase \"" (view/element-name e) "\" as (" (alias-name (:id e)) ")")])

(defmethod render-element :actor
  [_ indent e]
  [(str "actor \"" (view/element-name e) "\" as " (alias-name (:id e)))])

(defmethod render-element :goal
  [_ indent e]
  [(str (alias-name (:from e)) " --> "
        (alias-name (:to e)))])

(defmethod render-element :include
  [_ indent e]
  [(str (alias-name (:from e)) " ..> "
        (alias-name (:to e)) " : include")])

(defmethod render-element :extends
  [_ indent e]
  [(str (alias-name (:from e)) " ..> "
        (alias-name (:to e)) " : extends")])

(defmethod render-element :generalizes
  [_ indent e]
  [(str (alias-name (:from e)) " --|> "
        (alias-name (:to e)))])


(defmethod render-element :state
  [_ indent e]
  ; TODO render substates
  [(str "state \"" (view/element-name e) "\" as " (alias-name (:id e)))])

(defmethod render-element :start-state
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<start>>")])

(defmethod render-element :end-state
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<end>>")])

(defmethod render-element :fork
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<fork>>")])

(defmethod render-element :join
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<join>>")])

(defmethod render-element :choice
  [_ indent e]
  [(str "state " (alias-name (:id e)) " <<choice>>")])

(defmethod render-element :transition
  [_ indent e]
  [(str  (alias-name (:from e)) " --> "
         (alias-name (:to e)) " : \"" (view/element-name e) "\"" )])


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
  "Collects all sprites for the collection of elsements."
  [coll]
  (filter sprite? (set/union (view/collect-technologies coll) (collect-sprites coll))))

(defn sprites-for-diagram
  "Collects the sprites for the"
  [view]
  (->> view
       (view/elements-to-render)
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
  [view]
  (let [icon-libs (get-in view [:spec :plantuml :sprite-libs])
        icons (sprites-for-diagram view)]
    [(map (partial render-spritelib-import view) icon-libs)
     (map (partial render-sprite-import view) icons)]))

(comment
  (tech->sprite "Angular"))

;;
;; C4 Imports
;;

(defn render-imports
  "Renders the imports for the diagram."
  [view]
  (if (get-in view [:spec :plantuml :remote-imports])
    (str "!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/"
         (view-type->import (:el view)))
    (str "!include <C4/"
         (view-type->import (:el view)) ">")))

;;;
;;; Diagram Styles
;;;

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
  (fn [view style] (:el style)) :hierarchy #'styles-hierarchy)

; AddElementTag (tagStereo, ?bgColor, ?fontColor, ?borderColor, ?shadowing, ?shape, ?sprite, ?techn, ?legendText, ?legendSprite)
(defmethod render-style :element
  [view style]
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
  [view style]
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

;;;
;;; Diagram Layout
;;;

(defn render-layout
  "Renders the layout for the diagram."
  [view]
  (let [spec (:spec view)]
    (flatten [(when (:styles spec)
                (into [] (map #(render-style view %)) (:styles spec)))
              (when (:sketch spec)
                "LAYOUT_AS_SKETCH()")
              (when (:layout spec)
                (layouts (:layout spec)))
              (when (:linetype spec)
                (linetypes (:linetype spec)))])))

(defn render-legend
  "Renders the legend for the diagram."
  [view]
  (let [spec (:spec view)]
    [(when-not (:no-legend spec)
       "SHOW_LEGEND()")]))

(defn render-title
  "Renders the title of the diagram."
  [view]
  (when (:title view) (str "title " (:title view))))

(defmethod render-view :c4-view
  [options view]
  (let [children (view/elements-to-render view)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (alias-name (:id view)))
              (render-imports view)
              (render-sprite-imports view)
              (render-layout view)
              (render-title view)
              (map #(render-element view 0 %) children)
              (render-legend view)
              "@enduml"])))

(defmethod render-view :uml-view
  [options view]
  (let [children (view/elements-to-render view)]
    (flatten [(str "@startuml " (alias-name (:id view)))
             (map #(render-element view 0 %) children)
              "@enduml"])
    ))

;;;
;;; PlantUML file export
;;;

(defmethod exp/export-file :plantuml
  [options view]
  (let [dir-name (str (:export-dir options) "/plantuml/" (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".puml"))))

(defmethod exp/export-view :plantuml
  [options view]
  (with-open [wrt (io/writer (exp/export-file options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-view options view))))))

(defmethod exp/export :plantuml
  [options]
  (doseq [view (core/get-views)]
    (exp/export-view options view)))

