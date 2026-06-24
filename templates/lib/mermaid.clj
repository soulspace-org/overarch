(ns lib.mermaid
  "Functions for templates generating Mermaid files."
  (:require [clojure.string :as str]
            [org.soulspace.clj.string :as sstr]))

(def view->diagram-type
  "Maps view types to mermaid diagram types"
  {:code-view "classDiagram"
   :state-view "stateDiagram-v2"})

