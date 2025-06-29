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
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as rndr]
            [org.soulspace.overarch.util.io :as oio]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.util.functions :as fns]))

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
  ; TODO deprecate :class-view and remove 
  (-> (make-hierarchy)
      (derive :system-landscape-view :c4-view)
      (derive :context-view          :c4-view)
      (derive :container-view        :c4-view)
      (derive :component-view        :c4-view)
      (derive :deployment-view       :c4-view)
      (derive :dynamic-view          :c4-view)
      (derive :use-case-view         :uml-view)
      (derive :state-machine-view    :uml-view)
      (derive :code-view             :uml-view)
      (derive :class-view            :uml-view)
      (derive :c4-view               :view)
      (derive :uml-view              :view)
      ))

(def linetypes
  "Maps linetype keys to PlantUML C4."
  {:orthogonal "skinparam linetype ortho"
   :polygonal  "skinparam linetype polyline"})

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

(comment ; sprite mapping
  (load-sprite-mappings-from-resource ["azure" "awslib14"])
  (count (sorted-sprite-mappings tech->sprite))
  ;
  )

;;;
;;; Rendering
;;;
(defn renderer
  "Returns the renderer for the view."
  [_ _ view]
  (:el view))

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

;;;
;;; Imports
;;;

;;
;; Sprite Imports
;;
(defn collect-sprites
  "Returns the set of sprites for the elements of the `coll`."
  [coll]
  (model/traverse :sprite model/sprite-collector coll))

(defn collect-technologies
  "Returns the set of technologies for the elements of the coll."
  [coll]
  (model/traverse :tech model/tech-collector coll))

(defn collect-all-sprites
  "Collects all sprites for the collection of elements."
  [coll]
  (filter tech->sprite (set/union (collect-technologies coll) (collect-sprites coll))))

(defn sprites-for-view
  "Collects the sprites for the `view`."
  [model view]
  (->> view
       (view/rendered-elements model) 
       (collect-all-sprites)
       ;(fns/data-tapper "Sprites")
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

;; TODO use plantuml-spec from view
(defn render-sprite-import
  "Renders the import for an sprite."
  [view sprite]
  (if (get (view/plantuml-spec view) :remote-imports)
    (remote-import (sprite-path sprite))
    (local-import (sprite-path sprite))))

(defn render-spritelib-import
  "Renders the imports for an sprite library."
  [view sprite-lib]
  (if (get (view/plantuml-spec view) :remote-imports)
    [(str "!define " (:remote-prefix sprite-lib) (:remote-url sprite-lib))
     (map (partial remote-import (:remote-prefix sprite-lib))
          (:remote-imports (sprite-libraries sprite-lib)))]
    [(map (partial local-import (:local-prefix (sprite-libraries sprite-lib)))
          (:local-imports (sprite-libraries sprite-lib)))]))

(defn render-sprite-imports
  "Renders the imports for icon/sprite libraries."
  [model view]
  (let [sprite-libs (get (view/plantuml-spec view) :sprite-libs [:awslib :azure :devicons :devicons2 :font-awesome-5 :logos])
        sprites (sprites-for-view model view)]
    [(distinct (map (partial render-spritelib-import view) sprite-libs))
     (map (partial render-sprite-import view) sprites)]))

(comment
  (tech->sprite "Angular")
  (tech->sprite "Azure Virtual Network"))

;;;
;;; Diagram Layout
;;;

(defn render-title
  "Renders the title of the diagram."
  [view]
  (when-let [title (view/title view)]
    (str "title " title)))

(defn render-skinparam
  "Renders a skinparam for the plantuml diagram."
  [[k v]]
  (str "skinparam " k " " v))

(defn render-skinparams
  "Renders skinparams for the plantuml diagram."
  [view]
  (when-let [skinparams (get (view/plantuml-spec view) :skinparams)]
    (->> skinparams
         (map render-skinparam)
         (str/join "\n"))))

;;;
;;; PlantUML Rendering dispatch
;;;
(def plantuml-views
  "Contains the views to be rendered with plantuml."
  #{:system-landscape-view :context-view :container-view :component-view
    :deployment-view :dynamic-view :code-view :use-case-view
    :state-machine-view})

(defn plantuml-view?
  "Returns true, if the view is to be rendered with plantuml."
  [view]
  (contains? plantuml-views (:el view)))

(defmethod rndr/render-file :plantuml
  [_model _format options view]
  (let [dir-name (str (:render-dir options) "/"
                      (when (:render-format-subdirs options)
                        "plantuml/")
                      (str/replace (namespace (:id view)) "." "/"))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".puml"))))

(defmethod rndr/render-view :plantuml
  [model format options view]
  (when (plantuml-view? view)
    (let [result (render-plantuml-view model options view)]
      (with-open [wrt (io/writer (rndr/render-file model format options view))]
        (binding [*out* wrt]
          (println (str/join "\n" result)))))))
