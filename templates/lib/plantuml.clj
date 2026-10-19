(ns lib.plantuml
  "Functions for templates generating PlantUML files."
  (:require [clojure.string :as str]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.overarch.adapter.template.view-api :as view]))

;;;
;;; PlantUML Aliases
;;;
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
;;; Text helpers
;;;
(defn indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

;;;
;;; Diagram Layout
;;;
(def layouts
  "Maps layout keys to PlantUML layout directives."
  {:top-down   "top to bottom direction"
   :left-right "left to right direction"})

(def linetypes
  "Maps linetype keys to PlantUML skinparam directives."
  {:orthogonal "skinparam linetype ortho"
   :polygonal  "skinparam linetype polyline"})

(defn render-layout
  "Renders the layout directives for the `view` as a vector of lines.
   Directives not set in the view specification are omitted.
   The optional `opts` map can override the `:sketch` directive and the `:layouts` map
   (e.g. for C4 diagrams, which use macros for these directives)."
  ([view]
   (render-layout view {}))
  ([view opts]
   (let [plantuml-spec (view/plantuml-spec view)
         layout-map (get opts :layouts layouts)]
     (->> [(when (view/sketch-spec view)
             (get opts :sketch "skinparam handwritten true"))
           (when (view/layout-spec view)
             (layout-map (view/layout-spec view)))
           (when (view/linetype-spec view)
             (linetypes (view/linetype-spec view)))
           (when (:node-separation plantuml-spec)
             (str "skinparam nodesep " (:node-separation plantuml-spec)))
           (when (:rank-separation plantuml-spec)
             (str "skinparam ranksep " (:rank-separation plantuml-spec)))]
          (remove nil?)
          (vec)))))

;;;
;;; Sprite Imports
;;;
(def sprite-libraries
  "Definition of sprite libraries."
  {:azure         {:name "azure"
                   :local-prefix "azure"
                   :local-imports ["AzureCommon"
                                   "AzureC4Integration"]
                   :remote-prefix "AZURE"
                   :remote-url ""
                   :remote-imports ["AzureCommon"
                                    "AzureC4Integration"]}
   :awslib        {:name "awslib"
                   :local-prefix "awslib14"
                   :local-imports ["AWSCommon"
                                   "AWSC4Integration"]
                   :remote-prefix "AWS"
                   :remote-url ""
                   :remote-imports ["AWSCommon"
                                    "AWSC4Integration"]}
   :gcp           {:name "gcp"
                   :local-prefix "gcp"
                   :local-imports ["GCPCommon"
                                   "GCPC4Integration"]
                   :remote-prefix "GCP"
                   :remote-url ""
                   :remote-imports ["GCPCommon"
                                    "GCPC4Integration"]}
   :cloudinsight  {:name "cloudinsight"
                   :local-prefix "cloudinsight"
                   :local-imports []
                   :remote-prefix "CLOUDINSIGHT"
                   :remote-url ""}
   :cloudogu      {:name "cloudogu"
                   :local-prefix "cloudogu"
                   :local-imports ["common"]
                   :remote-prefix "CLOUDOGU"
                   :remote-url ""}
   :logos         {:name "logos"
                   :local-prefix "logos"}
   :devicons      {:name "devicons"
                   :local-prefix "tupadr3"
                   :local-imports ["common"]
                   :remote-prefix "DEVICONS"
                   :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"}
   :devicons2     {:name "devicons2"
                   :local-prefix "tupadr3"
                   :local-imports ["common"]
                   :remote-prefix "DEVICONS2"
                   :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons2"}
   :font-awesome-5 {:name "font-awesome-5"
                    :local-prefix "tupadr3"
                    :local-imports ["common"]
                    :remote-prefix "FONTAWESOME"
                    :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"}
   :material      {:name "material"
                   :local-prefix "material"
                   :local-imports ["common"]}})

(def default-sprite-libs
  "The sprite libraries imported by default."
  [:awslib :azure :devicons :devicons2 :font-awesome-5 :logos])

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
  "Renders the import for a sprite."
  [view sprite]
  (if (get (view/plantuml-spec view) :remote-imports)
    (remote-import (view/sprite-path sprite))
    (local-import (view/sprite-path sprite))))

(defn render-spritelib-import
  "Renders the imports for the sprite library with the key `sprite-lib`."
  [view sprite-lib]
  (let [lib (sprite-libraries sprite-lib)]
    (if (get (view/plantuml-spec view) :remote-imports)
      [(when (:remote-prefix lib)
         (str/trim (str "!define " (:remote-prefix lib) " " (:remote-url lib))))
       (when (:remote-prefix lib)
         (map (partial remote-import (:remote-prefix lib))
              (:remote-imports lib)))]
      [(map (partial local-import (:local-prefix lib))
            (:local-imports lib))])))

(defn render-sprite-imports
  "Renders the imports for the icon/sprite libraries and the sprites used in the `view`
   as a vector of lines."
  [model view]
  (let [sprite-libs (get (view/plantuml-spec view) :sprite-libs default-sprite-libs)
        sprites (view/sprites-for-view model view)]
    (->> [(distinct (map (partial render-spritelib-import view) sprite-libs))
          (map (partial render-sprite-import view) sprites)]
         (flatten)
         (remove nil?)
         (vec))))

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
