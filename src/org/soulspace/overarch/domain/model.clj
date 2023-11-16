(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]))

;;;
;;; Category definitions
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
  #{:uses :include :extends :generalizes :transition :composition
    :aggregation :dependency :association :inheritance :implementation})

(def uml-types
  "Element types of UML views."
  (set/union use-case-types state-machine-types class-types))

;;
;; Concept category definitions
;;
(def concept-types
  "Element types of a concept view."
  (set/union container-types #{:concept}))

(def glossary-types
  "Element types of a glossary view."
  (set/union container-types #{:concept}))

;; 
;; General category definitions
;;
(def relation-types
  "Element types of relations"
  (set/union #{:rel} uml-relation-types))

(def reference-types
  "Element types of references"
  #{:ref})

(def model-types
  "Element types for the architectural model."
  (set/union component-types deployment-types uml-types concept-types relation-types))


;;
;; Predicates
;;

(defn element?
  "Returns true if the given element `e` has a type (:el key)."
  [e]
  (not= nil (:el e)))

(defn identifiable?
  "Returns true if the given element `e` has an ID (:id key)."
  [e]
  (not= nil (:id e)))

(defn identifiable-element?
  "Returns true if the given element `e` is an element and identifiable."
  [e]
  (and (element? e) (identifiable? e)))

(defn named?
  "Returns true if the given element `e` has a name (:name key)."
  [e]
  (not= nil (:name e)))

(defn technical?
  "Returns true if the given element `e` has a tech (:tech key)."
  [e]
  (not= nil (:tech e)))

(defn named-element?
  "Returns true if the given element `e` is an element and named."
  [e]
  (and (element? e) (named? e)))

(defn identifiable-named-element?
  "Returns true if the given element `e` is an element, identifiable and named."
  [e]
  (and (element? e) (identifiable? e) (named? e)))

(defn relational?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (not= nil (:from e)) (not= nil (:to e))))

(defn relational-element?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (element? e) (not= nil (:from e)) (not= nil (:to e))))

(defn identifiable-relational-element?
  "Returns true if the given element `e` is an identifiable relation."
  [e]
  (and (element? e) (identifiable? e) (relational? e)))

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

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (relation? e))))

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
(s/def :overarch/tech string?)
(s/def :overarch/tags map?)    ; check
(s/def :overarch/icon string?) ; check
(s/def :overarch/type string?) ; check
;(s/def :overarch/index int?)   ; check
(s/def :overarch/ref keyword?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/href string?) ; TODO url?

(s/def :overarch/link
  (s/keys :req-un [:overarch/name :overarch/href]))

(s/def :overarch/ct
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation)))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el]
          :opt-un [:overarch/id
                   :overarch/name :overarch/desc :overarch/ct
                   :overarch/subtype :overarch/external
                   :overarch/tech]))

(s/def :overarch/identifiable
  (s/keys :req-un [:overarch/id]))

(s/def :overarch/named
  (s/keys :req-un [:overarch/name]))

(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/icon :overarch/link]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/name :overarch/desc]))

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

(defn model-elements
  "Filters the given collection of elements `coll` for model elements."
  [coll]
  (filter model-element? coll))

(defn get-model-elements
  "Returns the collection of model elements."
  ([m]
   (model-elements (:elements m))))

(defn get-model-element
  "Returns the model element with the given `id`."
  ([m id]
   ((:registry m) id)))

(defn get-parent-element
  "Returns the parent of the element `e`."
  ([m e]
   ((:parents m) (:id e))))

(defn resolve-ref
  "Resolves the model element for the ref `e`."
  ([m e]
   (cond
     (keyword? e) (get-model-element m e)
     (:ref e) (merge (get-model-element m (:ref e)) e)
     :else e)))

(defn all-elements
  "Returns a set of all elements."
  ([m]
   (->> (:registry m)
        (vals)
        (map (partial resolve-ref m))
        (into #{}))))

(defn related
  "Returns the related elements for the given collection of relations"
  ([m coll]
   (->> coll
        (mapcat (fn [e] [(:from e) (:to e)]))
        (map (partial resolve-ref m))
        (into #{}))))

(defn aggregable-relation?
  "Returns true, if the relations `r1` and `r2` are aggregable."
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

(defn relations-of-nodes
  "Returns the relations connecting nodes from the given collection of model nodes."
  ([m coll]
   (let [els (into #{} (map :id coll))
         rels (filter relation? (get-model-elements m))]
     (->> rels
          (filter (fn [r] (and (contains? els (:from r)) (contains? els (:to r)))))))))

(defn related-nodes
  "Returns the set of nodes that are part of at least one relation."
  [m coll]
  (->> coll
       (filter relation?)
       (map (fn [rel] #{(:from rel) (:to rel)}))
       ; TODO resolve refs
       (reduce set/union #{})))

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

; TODO move to application
(s/fdef read-elements
  :args [string?]
  :ret :overarch/ct)
(defn read-elements
  "Reads the elements of data from the given directory `dir`."
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))

; TODO move to application
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
  )
