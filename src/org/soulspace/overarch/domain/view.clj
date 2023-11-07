(ns org.soulspace.overarch.domain.view
  "Functions for the definition and handling of views."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.overarch.domain.model :as model]))

;;;
;;; Type definitions
;;;

;;
;; C4 category definitions
;; 
(def c4-view-types
  "The set of C4 view types."
  #{:context-view :container-view :component-view
    :deployment-view :system-landscape-view
    :dynamic-view})

;;
;; UML category definitions
;;
(def uml-view-types
  "The set of UML view types."
  #{:use-case-view :state-machine-view :class-view})

;;
;; Concept category definitions
;;
(def concept-view-types
  "The set of concept views types."
  #{:concept-view :glossary-view})

;; 
;; General category definitions
;;
(def view-types
  "The set of view types."
  (set/union c4-view-types uml-view-types concept-view-types))

(def hierarchical-view-types
  "The set of hierarchical view types."
  #{:context-view :container-view :component-view
    :deployment-view :system-landscape-view
    ;:dynamic-view
    :state-machine-view :class-view
    :glossary-view
    })

;;
;; Predicates
;;
(defn view?
  "Returns true if the given element `e` is a view."
  [e]
  (contains? view-types (:el e)))

(defn hierarchical-view?
  "Returns true if the given element `e` is a hierarchical view."
  [e]
  (contains? hierarchical-view-types (:el e)))

(defn context-view-element?
  "Returns true if the given element `e` is rendered in a C4 context view."
  [e]
  (contains? model/context-types (:el e)))

(defn container-view-element?
  "Returns true if the given element `e` is rendered in a C4 container view."
  [e]
  (contains? model/container-types (:el e)))

(defn component-view-element?
  "Returns true if the given element `e` is rendered in a C4 component view."
  [e]
  (contains? model/component-types (:el e)))

(defn code-view-element?
  "Returns true if the given element `e` is rendered in a code view."
  [e]
  (contains? model/code-types (:el e)))

(defn dynamic-view-element?
  "Returns true if the given element `e` is rendered in a C4 dynamic view."
  [e]
  (contains? model/dynamic-types (:el e)))

(defn system-landscape-view-element?
  "Returns true if the given element `e` is rendered
   in a C4 system landscape view."
  [e]
  (contains? model/system-landscape-types (:el e)))

(defn deployment-view-element?
  "Returns true if the given element `e` is rendered in a C4 deployment view."
  [e]
  (contains? model/deployment-types (:el e)))

(defn use-case-view-element?
  "Returns true if the given element `e` is rendered in a UML use case view."
  [e]
  (contains? model/use-case-types (:el e)))

(defn state-machine-view-element?
  "Returns true if the given element `e` is rendered in a UML state view."
  [e]
  (contains? model/state-machine-types (:el e)))

(defn class-view-element?
  "Returns true if the given element `e` is rendered in a UML state view."
  [e]
  (contains? model/class-types (:el e)))

(defn glossary-view-element?
  "Returns true if the given element `e` is rendered in a glossary view."
  [e]
  (contains? model/concept-types (:el e)))

(defn concept-view-element?
  "Returns true if the given element `e` is rendered in a context view."
  [e]
  (contains? model/concept-types (:el e)))

;;;
;;; Schema definitions
;;;

(s/def :overarch/spec map?)
(s/def :overarch/title string?)

(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title]))


(defn views
  "Filters the given collection of elements `coll` for views."
  [coll]
  (filter view? coll))

(defn get-views
  "Returns the collection of views."
  ([m]
   (views (:elements m))))

(defn get-view
  "Returns the view with the given id."
  ([m id]
   ((:registry m) id)))


;;;
;;; View functions
;;;
(defn referenced-model-nodes
  "Returns the model nodes explicitly referenced in the given view."
  [m view]
  (->> (:ct view)
       (map (partial model/resolve-ref m))
       (filter model/model-node?)))

(defn referenced-relations
  "Returns the relations explicitly referenced in the given view."
  [m view]
  (->> (:ct view)
       (map (partial model/resolve-ref m))
       (filter model/relation?)))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given view."
  [m view]
  (->> (:ct view)
       (map (partial model/resolve-ref m))))

(defn specified-model-nodes
  "Returns the model nodes specified in the given view.
   Takes the view spec into account for resolving model nodes not explicitly referenced."
  [m view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-model-nodes m view)
      :relations (referenced-model-nodes m view)
      :related (let [referenced-nodes (referenced-model-nodes m view)
                     referenced-rels (referenced-relations m view)
                     related-nodes (into #{} (map (partial model/resolve-ref m) (model/related-nodes m referenced-rels)))]
                 (set/union referenced-nodes related-nodes)) ; TODO check
      )))

(defn specified-relations
  "Returns the relations specified in the given view.
   Takes the view spec into account for resolving relations not explicitly referenced."
  [m view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-relations m view)
      :relations (let [referenced-nodes (referenced-model-nodes m view)
                       referenced-rels (referenced-relations m view)
                       related-rels (into #{} (model/relations-of-nodes m referenced-nodes))]
                   (set/union referenced-rels related-rels))
      :related (referenced-relations m view))))

(defn specified-elements
  "Returns the model elements and relations explicitly specified in the given view."
  [m view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-elements m view)
      :relations (concat (specified-model-nodes m view) (specified-relations m view))
      :related (concat (specified-model-nodes m view) (specified-relations m view)))))

(defn rendered-model-nodes
  "Returns the model nodes to be rendered by the given view."
  [m view])

(defn rendered-relations
  "Returns the relations to be rendered by the given view.
   Takes the view spec into account for resolving relations not explicitly specified."
  [m view])

(defn rendered-elements
  "Returns the model elements to be rendered by the given view.
   Takes the view spec into account for resolving model elements not explicitly specified."
  [m view])


;;
;; Context based content filtering
;;

(def view-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-view          context-view-element?
   :container-view        container-view-element?
   :component-view        component-view-element?
   :code-view             code-view-element?
   :system-landscape-view system-landscape-view-element?
   :dynamic-view          dynamic-view-element?
   :deployment-view       deployment-view-element?
   :use-case-view         use-case-view-element?
   :state-machine-view    state-machine-view-element?
   :class-view            class-view-element?
   :glossary-view         glossary-view-element?
   :concept-view          concept-view-element?})

(defn render-predicate
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [m view-type]
  (let [element-predicate (view-type->element-predicate view-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (model/get-model-element m (:from e)))
               (element-predicate (model/get-model-element m (:to e))))
          (and (element-predicate e)
               (not (:external (model/get-parent-element m e))))))))

(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {[:container-view :system]          :system-boundary
   [:component-view :system]          :system-boundary
   [:component-view :container]       :container-boundary})

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
  "Returns the model element to be rendered for element `e` for the `view-type`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
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
  ([m view]
   (elements-to-render m view (:ct view)))
  ([m view coll]
   (let [view-type (:el view)]
     (->> coll
          (map (partial model/resolve-ref m))
          (filter (render-predicate m view-type))
          (map #(element-to-render view-type %))))))

(defn elements-in-view
  "Returns the elements rendered in the view."
  ([m view]
   (elements-in-view m #{} view (elements-to-render m view (:ct view))))
  ([m elements view coll]
   (if (seq coll)
     (let [e (first coll)]
       (recur m (elements-in-view m (conj elements e)
                                view
                                (elements-to-render m view (:ct e)))
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

(defn technologies-in-view
  [m view]
  (->> view
       (elements-in-view m)
       (map :tech)
       (remove nil?)
       (into #{})))
       
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
      (derive :container-boundary  :boundary)
      (derive :context-boundary    :boundary)))


(defn include-criteria?
  "Returns true, if the `view` should include elements selected by criteria."
  [view]
  (map? (get-in view [:spec :include])))

(defn include-relations?
  "Returns true, if the `view` should include the relations to the shown elements."
  [view]
  (= :relations (get-in view [:spec :include])))

;(defn include-related?
;  "Returns true, if the `view` should include the elements for the shown relations."
;  [view]
;  (= :related (get-in view [:spec :include])))

;(defn include-transitive?
;  "Returns true, if the `view` should include the transitve (convex) hull of the shown elements."
;  [view]
;  (= :transitive (get-in view [:spec :include])))

(defn render-relation?
  "Returns true if the relation should be rendered in the context of the view."
  [m rel pred]
  (let [rendered? pred
        from (model/resolve-ref m (:from rel))
        to   (model/resolve-ref m (:to rel))]
    (when (and (rendered? rel) (rendered? from) (rendered? to))
      rel)))

(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [m view rel]
  (let [view-type (:el view)
        rendered? (render-predicate m view-type)
        from (model/resolve-ref m (:from rel))
        to   (model/resolve-ref m (:to rel))]))
  ; TODO promote relations to higher levels?
  


(comment
  ;(collect-technologies (:elements @model/state))
  (specified-model-nodes @model/state (get-view @model/state :banking/system-context-view))
  (specified-relations @model/state (get-view @model/state :banking/system-context-view))
  (specified-elements @model/state (get-view @model/state :test/banking-container-view-related))
  (specified-elements @model/state (get-view @model/state :test/banking-container-view-relations))
  
  (model/related-nodes @model/state (referenced-relations @model/state (get-view @model/state :test/banking-container-view-related)))
  (model/relations-of-nodes @model/state (referenced-model-nodes @model/state (get-view @model/state :test/banking-container-view-relations)))

  (elements-in-view @model/state (get-view @model/state :banking/container-view))
  (technologies-in-view @model/state (get-view @model/state :banking/container-view))
  ;
  )
