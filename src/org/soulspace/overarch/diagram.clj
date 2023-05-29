(ns org.soulspace.overarch.diagram
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.export :as exp]))


; general, multimethod?
(def view-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-view          core/context-level?
   :container-view        core/container-level?
   :component-view        core/component-level?
   :code-view             core/code-level?
   :system-landscape-view core/system-landscape-level?
   :dynamic-view          core/dynamic-level?
   :deployment-view       core/deployment-level?})

; general
(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {[:container-view :system]          :system-boundary
   [:component-view :system]          :system-boundary
   [:component-view :container]       :container-boundary})

;;;
;;; Context based content filtering
;;;

; general
(defn render-predicate
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [view-type]
  (let [element-predicate (view-type->element-predicate view-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (core/get-model-element (:from e)))
               (element-predicate (core/get-model-element (:to e))))
          (element-predicate e)))))

; general
(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this view type, false otherwise."
  [view-type e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary [view-type (:el e)])))

;;;
;;; Rendering functions
;;;

; general
(defn element-to-render
  "Returns the model element to be rendered in the context of the view."
  [view-type e]
  (let [boundary (as-boundary? view-type e)]
    (if boundary
    ; e has a boundary type and has children, render as boundary
      (assoc e :el boundary)
    ; render e as normal model element
      e)))

; general
(defn elements-to-render
  "Returns the list of elements to render from the view
   or the given collection of elements, depending on the type
   of the view."
  ([view]
   (elements-to-render view (:ct view)))
  ([view coll]
   (let [view-type (:el view)]
     (->> coll
          (map core/resolve-ref)
          (filter (render-predicate view-type))
          (map #(element-to-render view-type %))))))

; general
(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [view rel]
  ; TODO promote relations to higher levels?
  )

(defn collect-technologies
  "Returns a map of id to element for the elements of the coll."
  ([coll]
   (collect-technologies #{} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (:tech e)
         (recur (collect-technologies (set/union m #{(:tech e)}) (:ct e)) (rest coll))
         (recur (collect-technologies m (:ct e)) (rest coll))))
     m)))

; general
(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

(defn element-name
  "Returns the name of the element."
  [e]
  (if (:name e)
    (:name e)
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

; general?
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)))
(defmulti render-diagram
  "Renders a diagram"
  exp/export-format)

(comment
    (collect-technologies (:elements @core/state))
  )