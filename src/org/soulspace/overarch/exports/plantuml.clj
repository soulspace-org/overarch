(ns org.soulspace.overarch.exports.plantuml
  "Functions to export views to PlantUML."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.view :as view]
            [org.soulspace.overarch.exports.core :as exp]
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
  "Map from element type to PlantUML method."
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

(defn print-sprite-mappings
  "Prints the given list of the sprite mappings."
  ([]
   (print-sprite-mappings (sorted-sprite-mappings tech->sprite)))
  ([sprite-mappings]
   (doseq [sprite sprite-mappings]
     (println (str (:key sprite) " : " (sprite-path sprite))))))

(defn sprite?
  "Returns true if the icon-map contains an icon for the given technology."
  [tech]
  (tech->sprite tech))

(comment
  (load-sprite-mappings-from-resource ["azure" "awslib14"])
  (count (sorted-sprite-mappings tech->sprite))
  (print-sprite-mappings)
  )

;;;
;;; Rendering
;;;

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
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render diagram (:ct e))]
      (flatten [(str (view/render-indent indent)
                     (element->method (:el e)) "("
                     (alias-name (:id e)) ", \""
                     (view/element-name e) "\""
                     (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
                     ") {")
                (map #(render-element diagram (+ indent 2) %)
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
  [diagram indent e]
  (if (seq (:ct e))
    (let [children (view/elements-to-render diagram (:ct e))]
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
                (map #(render-element diagram (+ indent 2) %)
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
  [diagram indent e]
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
            (str ", $sprite=\"" (:name (tech->sprite (:sprite e))) "\"")
            (when (sprite? (:tech e))
              (str ", $sprite=\"" (:name (tech->sprite (:tech e))) "\"")))
          (when (:style e) (str ", $tags=\"" (short-name (:style e)) "\""))
          ")")]))

;;;
;;; Imports
;;;

;;
;; Sprite Imports
;;

(defn sprites-for-diagram
  "Collects the sprites for the"
  [diagram]
  (->> diagram
       (view/elements-to-render)
       (view/collect-technologies)
       (filter sprite?)
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
  [diagram sprite]
  (if (get-in diagram [:spec :plantuml :remote-imports])
    (remote-import (sprite-path sprite))
    (local-import (sprite-path sprite))))

(defn render-spritelib-import
  "Renders the imports for an sprite library."
  [diagram sprite-lib]
  (if (get-in diagram [:spec :plantuml :remote-imports])
    [(str "!define " (:remote-prefix sprite-lib) (:remote-url sprite-lib))
     (map (partial remote-import (:remote-prefix sprite-lib))
          (:remote-imports (sprite-libraries sprite-lib)))]
    [(map (partial local-import (:local-prefix (sprite-libraries sprite-lib)))
          (:local-imports (sprite-libraries sprite-lib)))]))

(defn render-sprite-imports
  "Renders the imports for icon/sprite libraries."
  [diagram]
  (let [icon-libs (get-in diagram [:spec :plantuml :sprite-libs])
        icons (sprites-for-diagram diagram)]
    [(map (partial render-spritelib-import diagram) icon-libs)
     (map (partial render-sprite-import diagram) icons)]))

;;
;; C4 Imports
;;

(defn render-imports
  "Renders the imports for the diagram."
  [diagram]
  (if (get-in diagram [:spec :plantuml :remote-imports])
    (str "!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/"
         (view-type->import (:el diagram)))
    (str "!include <C4/"
         (view-type->import (:el diagram)) ">")))

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

;;;
;;; Diagram Layout
;;;

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
    [(when-not (:no-legend spec)
       "SHOW_LEGEND()")]))

(defn render-title
  "Renders the title of the diagram."
  [diagram]
  (when (:title diagram) (str "title " (:title diagram))))

(defn render-diagram
  [options diagram]
  (let [children (view/elements-to-render diagram)]
    ;(user/data-tapper "resolved" children)
    (flatten [(str "@startuml " (alias-name (:id diagram)))
              (render-imports diagram)
              (render-sprite-imports diagram)
              (render-layout diagram)
              (render-title diagram)
              (map #(render-element diagram 0 %) children)
              (render-legend diagram)
              "@enduml"])))

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
      (println (str/join "\n" (render-diagram options view))))))

(defmethod exp/export :plantuml
  [options]
  (doseq [view (core/get-views)]
    (exp/export-view options view)))

