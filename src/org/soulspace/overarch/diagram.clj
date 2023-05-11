(ns org.soulspace.overarch.diagram
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.export :as exp]))

; general, multimethod?
(def diagram-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-diagram          core/context-level?
   :container-diagram        core/container-level?
   :component-diagram        core/component-level?
   :code-diagram             core/code-level?
   :system-landscape-diagram core/system-landscape-level?
   :dynamic-diagram          core/dynamic-level?
   :deployment-diagram       core/deployment-level?})

; general
(def element->boundary
  "Maps model types to boundary types depending on the diagram type."
  {[:container-diagram :system]          :system-boundary
   [:component-diagram :system]          :system-boundary
   [:component-diagram :container]       :container-boundary})

;;;
;;; Context based content filtering
;;;

; general
(defn render-predicate
  "Returns true if the element is should be rendered for this diagram type.
   Checks both sides of a relation."
  [diagram-type]
  (let [element-predicate (diagram-type->element-predicate diagram-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (core/get-model-element (:from e)))
               (element-predicate (core/get-model-element (:to e))))
          (element-predicate e)))))

; general
(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this diagram type, false otherwise."
  [diagram-type e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary [diagram-type (:el e)])))

;;;
;;; Rendering functions
;;;

; general
(defn element-to-render
  "Returns the model element to be rendered in the context of the diagram."
  [diagram-type e]
  (let [boundary (as-boundary? diagram-type e)]
    (if boundary
    ; e has a boundary type and has children, render as boundary
      (assoc e :el boundary)
    ; render e as normal model element
      e)))

; general
(defn elements-to-render
  "Returns the list of elements to render from the diagram
   or the given collection of elements, depending on the type
   of the diagram."
  ([diagram]
   (elements-to-render diagram (:ct diagram)))
  ([diagram coll]
   (let [diagram-type (:el diagram)]
     (->> coll
          (map core/resolve-ref)
          (filter (render-predicate diagram-type))
          (map #(element-to-render diagram-type %))))))

; general
(defn relation-to-render
  "Returns the relation to be rendered in the context of the diagram."
  [diagram rel]
  ; TODO promote reations to higher levels?
  )

; general
(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

; general?
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)))

(defmulti render-diagram exp/export-format)