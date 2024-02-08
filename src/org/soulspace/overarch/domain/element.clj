;;;;
;;;; contains element specific logic
;;;;
(ns org.soulspace.overarch.domain.element
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]))

;;;
;;; Category definitions
;;;

;;
;; C4 category definitions
;; 
(def context-types
  "Element types of a C4 context view."
  #{:person :system :enterprise-boundary :context-boundary
    :rel :request :response :publish :subscribe :send :dataflow})

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

(def architecture-relation-types
  "Relation types in the architecture model."
  #{:rel :request :response :publish :subscribe :send})

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
    :field :method :function
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
(def boundary-types
  "Element types of boundaries"
  #{:enterprise-boundary :context-boundary :system-boundary :container-boundary})

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
  "Returns true if the given element `e` is an element and named."
  [e]
  (and (element? e) (identifiable? e)))

(defn namespaced?
  "Returns true, if the id of element `e` has a namespace."
  [e]
  (and (:id e) (not= nil (namespace (:id e)))))

(defn named?
  "Returns true if the given element `e` has a name (:name key)."
  [e]
  (not= nil (:name e)))

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

(defn external?
  "Returns true if the given element `e` is external."
  [e]
  (:external e))

(defn internal?
  "Returns true if the given element `e` is internal."
  [e]
  (not (external? e)))


(defn relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (contains? relation-types (:el e)))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-types (:el e)))

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (relation? e))))


(defn tech?
  "Returns true if the given element `e` has a tech (:tech key)."
  [e]
  (not= nil (:tech e)))

(defn boundary?
  "Returns true if `e` is a boundary."
  [e]
  (contains? boundary-types (:el e)))

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

(defn reference?
  "Returns true if the given element `e` is a reference."
  [e]
  (:ref e))

(defn unresolved-ref?
  "Returns true if the given element `e` is a reference."
  [e]
  (:unresolved-ref e))

;;
;; Functions 
;;
(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (namespace (:id e)))
