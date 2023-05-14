(ns org.soulspace.overarch.structurizr
  "Functions for the export to structurizr."
  (:require [clojure.string :as str]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.diagram :as dia]))


(defn render-element
  "Renders a structurizr views model element."
  [e]
  [(str (dia/render-indent 4) "{")
   (str (dia/render-indent 4) "}")]
  )

(defn render-model
  "Renders the structurizr model."
  [elements]
  [(str (dia/render-indent 2) "model {")
   (map render-element elements)
   (str (dia/render-indent 2) "}")]
  )

(defn render-view
  "Renders a structurizr view."
  [view]
  [(str (dia/render-indent 4) "{")
   
   (str (dia/render-indent 4) "}")]
  )

(defn render-views
  "Renders the structurizr views."
  [views]
  [(str (dia/render-indent 2) "views {")
   (map render-view views)
   (str (dia/render-indent 4) "theme default")
   (str (dia/render-indent 2) "}")]
  )

(defn render-workspace
  "Renders a structurizr workspace."
  []
  [(str "workspace {")
   (render-model (core/get-model-elements))
   (render-views (core/get-diagrams))
   "}"])
