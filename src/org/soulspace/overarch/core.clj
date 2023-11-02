(ns org.soulspace.overarch.core
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]))

;;;
;;; Schema definitions
;;;
;;
;; C4 category definitions
;; 
(def context-types
  "Element types of a C4 context view."
  #{:rel :person :system :boundary :enterprise-boundary :context-boundary})

(def container-types
  "Element types of a C4 container view."
  (set/union context-types #{:system-boundary :container}))

(def component-types
  "Element types of a C4 component view."
  (set/union container-types #{:container-boundary :component}))

(def code-types
  "Element types of a C4 code view."
  #{})

(def system-landscape-types
  "Element types of a C4 system-landscape view."
  context-types)

(def deployment-types
  "Element types of a C4 deployment view."
  (set/union container-types #{:node}))

(def dynamic-types
  "Element types of a C4 dynamic view."
  component-types)

(def c4-view-types
  "C4 view types."
  #{:context-view :container-view :component-view
    :code-view :deployment-view :system-landscape-view
    :dynamic-view})

;;
;; UML category definitions
;;
(def use-case-types
  "Element types of a use case view."
  #{:use-case :actor :person :system :context-boundary
    :uses :include :extends :generalizes})

(def state-machine-types
  "Element types of a state machine view."
  #{:state-machine :start-state :end-state :state :transition
    :fork :join :choice :history-state :deep-history-state})

(def class-types
  "Element types of a class view."
  #{:class :enum :interface
    :field :method
    :inheritance :implementation :composition :aggregation :association :dependency
    :package :namespace :stereotype :annotation :protocol})

(def uml-relation-types
  "Relation types of UML views."
  #{:goal :include :extends :generalizes :transition :composition
    :aggregation :inheritance :implementation})

(def uml-types
  "Element types of UML views."
  (set/union use-case-types state-machine-types class-types))

(def uml-view-types
  "UML view types."
  #{:use-case-view :state-machine-view :class-view})

;;
;; Concept category definitions
;;
(def concept-types
  "Element types of a glossary view."
  (set/union container-types #{:concept}))

(def concept-view-types
  "Concept views types."
  #{:concept-view :glossary-view})

;; 
;; General category definitions
;;
(def view-types
  "View types."
  (set/union c4-view-types uml-view-types concept-view-types))

(def relation-types
  "Element types of relations"
  (set/union #{:rel} uml-relation-types))

(def reference-types
  "Element types of references"
  #{:ref})

(def model-types
  "Element types for the architectural model."
  (set/union component-types deployment-types uml-types relation-types ))


;;
;; Predicates
;;

(defn element?
  "Returns true if the given element `e` has a type (:el key)."
  [e]
  (not= nil (:el e)))

(defn identifiable-element?
  "Returns true if the given element `e` has an ID (:id key)."
  [e]
  (not= nil (:id e)))

(defn named-element?
  "Returns true if the given element `e` has a name (:name key)."
  [e]
  (not= nil (:name e)))

(defn identifiable-named-element?
  "Returns true if the given element `e` is identifiable and named."
  [e]
  (and (identifiable-element? e) (named-element? e)))

(defn relational-element?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (identifiable-element? e) (not= nil (:from e)) (not= nil (:to e))))

(defn named-relational-element?
  "Returns true if the given element `e` is a named relation."
  [e]
  (and (named-element? e) (relational-element? e)))

(defn relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (contains? relation-types (:el e)))

(defn reference?
  "Returns true if the given element `e` is a reference."
  [e]
  (:ref e))

(defn person?
  "Returns true if the given element `e` is a person element."
  [e]
  (= :person (:el e)))

(defn system?
  "Returns true if the given element `e` is a system element."
  [e]
  (= :system (:el e)))

(defn container?
  "Returns true if the given element `e` is a container element."
  [e]
  (= :container (:el e)))

(defn component?
  "Returns true if the given element `e` is a container element."
  [e]
  (= :component (:el e)))

(defn node?
  "Returns true if the given element `e` is a node element."
  [e]
  (= :node (:el e)))

(defn external?
  "Returns true if the given element `e` is external."
  [e]
  (:external e))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-types (:el e)))

(defn view?
  "Returns true if the given element `e` is a view."
  [e]
  (contains? view-types (:el e)))

(defn context-view-element?
  "Returns true if the given element `e` is rendered in a C4 context view."
  [e]
  (contains? context-types (:el e)))

(defn container-view-element?
  "Returns true if the given element `e` is rendered in a C4 container view."
  [e]
  (contains? container-types (:el e)))

(defn component-view-element?
  "Returns true if the given element `e` is rendered in a C4 component view."
  [e]
  (contains? component-types (:el e)))

(defn code-view-element?
  "Returns true if the given element `e` is rendered in a code view."
  [e]
  (contains? code-types (:el e)))

(defn dynamic-view-element?
  "Returns true if the given element `e` is rendered in a C4 dynamic view."
  [e]
  (contains? dynamic-types (:el e)))

(defn system-landscape-view-element?
  "Returns true if the given element `e` is rendered
   in a C4 system landscape view."
  [e]
  (contains? system-landscape-types (:el e)))

(defn deployment-view-element?
  "Returns true if the given element `e` is rendered in a C4 deployment view."
  [e]
  (contains? deployment-types (:el e)))

(defn use-case-view-element?
  "Returns true if the given element `e` is rendered in a UML use case view."
  [e]
  (contains? use-case-types (:el e)))

(defn state-machine-view-element?
  "Returns true if the given element `e` is rendered in a UML state view."
  [e]
  (contains? state-machine-types (:el e)))

(defn class-view-element?
  "Returns true if the given element `e` is rendered in a UML state view."
  [e]
  (contains? class-types (:el e)))

(defn glossary-view-element?
  "Returns true if the given element `e` is rendered in a glossary view."
  [e]
  (contains? concept-types (:el e)))

(defn concept-view-element?
  "Returns true if the given element `e` is rendered in a context view."
  [e]
  (contains? concept-types (:el e)))

;;
;; Schema specification
;;

;; TODO factor out diagrams

(s/def :overarch/el keyword?)
(s/def :overarch/id keyword?)
(s/def :overarch/name string?)
(s/def :overarch/desc string?)
(s/def :overarch/subtype keyword?)
(s/def :overarch/external boolean?)
(s/def :overarch/tech string?) ; check
(s/def :overarch/tags map?)    ; check
(s/def :overarch/icon string?) ; check
(s/def :overarch/type string?) ; check
;(s/def :overarch/index int?)   ; check
(s/def :overarch/ref keyword?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/href string?) ; TODO url?

(s/def :overarch/spec map?)
(s/def :overarch/title string?)

(s/def :overarch/link (s/keys :req-un [:overarch/name :overarch/href]))

(s/def :overarch/ct
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation)))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/name :overarch/desc :overarch/ct
                   :overarch/subtype :overarch/external
                   :overarch/tech]))

(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/icon :overarch/link]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/name :overarch/desc]))

(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title]))

(s/def :overarch/system (s/and :overarch/element system?))

(s/def :overarch/elements
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :view        :overarch/view)))


;;;
;;; Data handling
;;;

;;
;; Application state
;;
; TODO get rid of global state at some point
(def state (atom {}))

;;
;; Accessors
;;
(defn id-set
  "Returns a set of id's for the elements in `coll`."
  [coll]
  (->> coll
       (map :id)
       (remove nil?)
       (into #{})))

(defn views
  "Filters the given collection of elements `coll` for views."
  [coll]
  (filter view? coll))

(defn model-elements
  "Filters the given collection of elements `coll` for model elements."
  [coll]
  (filter model-element? coll))

(defn get-views
  "Returns the collection of views."
  ([]
   (get-views @state))
  ([m]
   (views (:elements m))))

(defn get-view
  "Returns the view with the given id."
  ([id]  
   (get-view @state id))
  ([m id]
   ((:registry m) id)))

(defn get-model-elements
  "Returns the collection of model elements."
  ([]
   (get-model-elements @state))
  ([m]
   (model-elements (:elements m))))

(defn get-model-element
  "Returns the model element with the given `id`."
  ([id]
   (get-model-element @state id))
  ([m id]
   ((:registry m) id)))

(defn get-parent-element
  "Returns the parent of the element `e`."
  ([e]
   (get-parent-element @state e))
  ([m e]
   ((:parents m) (:id e))))

(defn resolve-ref
  "Resolves the model element for the ref `e`."
  ([e]
   (if (:ref e)
     (merge (get-model-element (:ref e)) e)
     e))
  ([m e]
   (if (:ref e)
     (merge (get-model-element m (:ref e)) e)
     e)))

(defn all-elements
  "Returns a set of all elements."
  ([]
   (all-elements @state))
  ([m]
   (->> (:registry m)
        (vals)
        (map resolve-ref)
        (into #{}))))

(defn related
  "Returns the related elements for the given collection of relations"
  ([coll]
   (related @state coll))
  ([m coll]
   (->> coll
        (mapcat (fn [e] [(:from e) (:to e)]))
        (map (partial get-model-element m))
        (into #{}))))

(defn relations-of-elements
  "Returns the relations connecting elements from the given collection of model elements."
  ([coll]
   (relations-of-elements @state coll))
  ([m coll]
   (let [els (into #{} (map :id coll))
         rels (filter relation? (get-model-elements m))]
     (->> rels
          (filter (fn [r] (and (contains? els (:from r)) (contains? els (:to r)))))))))

(defn aggregable-relation?
  "Returns true, if the relations `r1` and `r2` are aggregable."
  ([r1 r2]
   (aggregable-relation? @state r1 r2))
  ([m r1 r2]
   (and (= (:name r1) (:name r2))
        (= (:tech r1) (:tech r2))
        (= (:desc r1) (:desc r2))
        (or (= (:from r1) (:from r2))
            (= (get-parent-element m (:from r1))
               (get-parent-element m (:from r2))))
        (or (= (:to r1) (:to r2))
            (= (get-parent-element m (:to r1))
               (get-parent-element m (:to r2)))))))

(defn related-elements
  "Returns the set of elements that are part of at least one relation."
  [coll]
  (->> coll
       (filter relation?)
       (map (fn [rel] #{(:from rel) (:to rel)}))
;       (user/data-tapper "related")
       (reduce set/union #{})))

(defn component-set
  "Returns the set of model components."
  ([]
   (component-set @state))
  ([m]
   (->> m
        (all-elements)
        (filter component-view-element?)
        (map :id)
        (into #{}))))

(defn unconnected-components
  "Returns the set of elements that are not connected with any other element by a relation."
  ([]
   (unconnected-components @state))
  ([m]
   (let [component-set (component-set m)
         related-set (related-elements (all-elements m))]
     (set/difference component-set related-set))))

(comment
  (all-elements)
  (into #{} (map :id (filter component-view-element? (all-elements))))
  (unconnected-components)
  )

;;;
;;; View functions
;;;
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


(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {[:container-view :system]          :system-boundary
   [:component-view :system]          :system-boundary
   [:component-view :container]       :container-boundary})

(defn render-predicate
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [view-type]
  (let [element-predicate (view-type->element-predicate view-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (get-model-element (:from e)))
               (element-predicate (get-model-element (:to e))))
          (and (element-predicate e)
               (not (:external (get-parent-element e))))))))

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

;; TODO rename to model-nodes/model-relations?

(defn referenced-model-elements
  "Returns the model elements explicitly referenced in the given view."
  [view]
  (->> (:ct view)
       (map resolve-ref)
       (filter model-element?)))

(defn referenced-relations
  "Returns the relations explicitly referenced in the given view."
  [view]
  (->> (:ct view)
       (map resolve-ref)
       (filter relation?)))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given view."
  [view]
  (->> (:ct view)
       (map resolve-ref)))

(defn specified-model-elements
  "Returns the model elements specified in the given view.
   Takes the view spec into account for resolving model elements not explicitly referenced."
  [view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-model-elements view)
      :relations (referenced-model-elements view)
      :related (referenced-model-elements view) ; TODO implement
      )))

(defn specified-relations
  "Returns the relations specified in the given view.
   Takes the view spec into account for resolving relations not explicitly referenced."
  [view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-relations view)
      :relations (referenced-relations view) ; TODO implement
      :related (referenced-relations view)
      )))

(defn specified-elements
  "Returns the model elements and relations explicitly specified in the given view."
  [view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-elements view)
      :relations (concat (specified-model-elements view) (specified-relations view)))))

(defn rendered-model-elements
  "Returns the model elements to be rendered by the given view."
  [view]
  )

(defn rendered-relations
  "Returns the relations to be rendered by the given view.
   Takes the view spec into account for resolving relations not explicitly specified."
  [view]
  )

(defn rendered-elements
  "Returns the model elements to be rendered by the given view.
   Takes the view spec into account for resolving model elements not explicitly specified."
  [view]
  )

;;
;; State preparation
;;

(defn build-referrer-id->rels
  "Returns a map of referrer-id to relation for the relations in `coll`."
  ([coll]
   (build-referrer-id->rels {} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (relation? e)
         ; not recursive, relations have no relations
         (recur (assoc m (:from e) (conj (get m (:from e) #{}) e)) (rest coll))
         ; recursive to pick up relations in model elements
         (recur (build-referrer-id->rels m (:ct e)) (rest coll))))
     m)))

(defn build-referred-id->rels
  "Returns a map of referred-id to relation for the relations in `coll`."
  ([coll]
   (build-referred-id->rels {} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (relation? e)
         ; not recursive, relations have no relations
         (recur (assoc m (:to e) (conj (get m (:to e) #{}) e)) (rest coll))
         ; recursive to pick up relations in model elements
         (recur (build-referred-id->rels m (:ct e)) (rest coll))))
     m)))

(defn build-id->elements
  "Returns a map of id to element for the elements of the `coll`."
  ([coll]
   (build-id->elements {} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (identifiable-element? e)
         (recur (build-id->elements (assoc m (:id e) e) (:ct e)) (rest coll))
         (recur (build-id->elements m (:ct e)) (rest coll))))
     m)))

(defn build-id->parent
  "Returns a map of child id to parent element for the elements in `coll`."
  ([coll]
   (build-id->parent {} nil coll))
  ([m p coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (and (identifiable-element? e) (identifiable-element? p) (model-element? p))
         (recur (build-id->parent (assoc m (:id e) p) e (:ct e)) p (rest coll))
         (recur (build-id->parent m e (:ct e)) p (rest coll))))
     m)))

(defn build-registry
  "Returns a map with the original `elements` and a registry by id for lookups.
   
   The map has the following shape:

   :elements -> the given data
   :registry -> a map from id to element
   :parents  -> a map from id to parent element
   :referrer -> a map from id to set of relations where the id is the referrer (:from)
   :referred -> a map from id to set of relations where the id is referred (:to)"
  [elements]
  (if (s/valid? :overarch/elements elements)
    {:elements elements
     :registry (build-id->elements elements)
     :parents (build-id->parent elements)
     :referrer (build-referrer-id->rels elements)
     :referred (build-referred-id->rels elements)}
    (s/explain :overarch/elements elements)))

(s/fdef read-elements
  :args [string?]
  :ret :overarch/ct)
(defn read-elements
  "Reads the elements of data from the given directory `dir`."
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))

(defn update-state!
  "Updates the state with the registered data read from `dir`."
  [dir]
  (->> dir
       (read-elements)
       (build-registry)
       (reset! state)))

(comment
  (file/all-files-by-extension "edn" "models")
  (read-elements "models")

  (update-state! "models")
  (build-id->parent (:elements @state))
  (user/data-tapper "State" @state)

  (specified-model-elements (get-view :banking/system-context-view))
  (specified-relations (get-view :banking/system-context-view))
  (specified-elements (get-view :banking/system-context-view))
  )
