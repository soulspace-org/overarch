(ns org.soulspace.overarch.view
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.core :as core]))


; general, multimethod?
(def view-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-view          core/context-view-element?
   :container-view        core/container-view-element?
   :component-view        core/component-view-element?
   :code-view             core/code-view-element?
   :system-landscape-view core/system-landscape-view-element?
   :dynamic-view          core/dynamic-view-element?
   :deployment-view       core/deployment-view-element?
   :use-case-view         core/use-case-view-element?
   :state-view            core/state-view-element?})

; general
(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {[:container-view :system]          :system-boundary
   [:component-view :system]          :system-boundary
   [:component-view :container]       :container-boundary})

;;;
;;; Context based content filtering
;;;

(defn render-predicate
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [view-type]
  (let [element-predicate (view-type->element-predicate view-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (core/get-model-element (:from e)))
               (element-predicate (core/get-model-element (:to e))))
          (and (element-predicate e)
               (not (:external (core/get-parent-element e))))))))

(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this view type, false otherwise."
  [view-type e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary [view-type (:el e)])
   (not (:external e))))

;;;
;;; Rendering functions
;;;

(defn element-to-render
  "Returns the model element to be rendered in the context of the view."
  [view-type e]
  (let [boundary (as-boundary? view-type e)]
    (if boundary
    ; e has a boundary type and has children, render as boundary
      (assoc e :el (keyword (str (name (:el e)) "-boundary")))
    ; render e as normal model element
      e)))

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

(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [view rel]
  ; TODO promote relations to higher levels?
  )

(defn elements-in-view
  "Returns the elements rendered in the view."
  ([view]
   (elements-in-view #{} view (elements-to-render view (:ct view))))
  ([elements view coll]
   (if (seq coll)
     (let [e (first coll)]
       (recur (elements-in-view (conj elements e)
                                view
                                (elements-to-render view (:ct e)))
              view
              (rest coll)))
     elements)))

; TODO reimplement with elements-in-view 
(defn collect-technologies
  "Returns the set of technologies for the elements of the coll."
  ([coll]
   (collect-technologies #{} coll))
  ([techs coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (:tech e)
         (recur (collect-technologies (set/union techs #{(:tech e)}) (:ct e))
                (rest coll))
         (recur (collect-technologies techs (:ct e)) (rest coll))))
     techs)))

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

(def view-hierarchy
  "Hierarchy for views"
  (-> (make-hierarchy)
      (derive :system-landscape-view :c4-view)
      (derive :context-view :c4-view)
      (derive :container-view :c4-view)
      (derive :component-view :c4-view)
      (derive :deployment-view :c4-view)
      (derive :dynamic-view :c4-view)
      (derive :use-case-view :uml-view)
      (derive :state-view :uml-view)
      ))

; general?
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)
      (derive :context-boundary    :boundary)))

(comment
    (collect-technologies (:elements @core/state))
  )