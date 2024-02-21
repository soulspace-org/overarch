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
(def architecture-relation-types
  "Relation types in the architecture model."
  #{:rel :request :response :publish :subscribe :send :dataflow})

(def technical-architecture-node-types
  "Technical node types in the architecture model."
  #{:system :container :component})

(def architecture-node-types
  "Node types in the architecture model."
  (set/union technical-architecture-node-types #{:person :enterprise-boundary :context-boundary}))

(def context-view-types
  "Element types of a C4 context view."
  (set/union architecture-relation-types #{:person :system :enterprise-boundary :context-boundary}))

(def container-view-types
  "Element types of a C4 container view."
  (set/union context-view-types #{:system-boundary :container}))

(def component-view-types
  "Element types of a C4 component view."
  (set/union container-view-types #{:container-boundary :component}))

(def code-view-types
  "Element types of a C4 code view."
  #{})

(def system-landscape-view-types
  "Element types of a C4 system-landscape view."
  context-view-types)

(def deployment-relation-types
  "Relation types for deployment models."
  #{:link})

(def deployment-node-types
  "Node types for deployment models."
  (set/union technical-architecture-node-types #{:node}))

(def deployment-view-types
  "Element types of a C4 deployment view."
  (set/union container-view-types deployment-relation-types #{:node}))

(def dynamic-types
  "Element types of a C4 dynamic view."
  component-view-types)

;;
;; UML category definitions
;;
(def use-case-view-types
  "Element types of a use case view."
  #{:use-case :actor :person :system :context-boundary
    :uses :include :extends :generalizes})

(def state-machine-view-types
  "Element types of a state machine view."
  #{:state-machine :start-state :end-state :state :transition
    :fork :join :choice :history-state :deep-history-state})

(def class-view-types
  "Element types of a class view."
  #{:class :enum :interface
    :field :method :function
    :inheritance :implementation :composition :aggregation :association :dependency
    :package :namespace :stereotype :annotation :protocol})

(def uml-relation-types
  "Relation types of UML views."
  #{:uses :include :extends :generalizes :transition :composition
    :aggregation :dependency :association :inheritance :implementation})

(def uml-view-types
  "Element types of UML views."
  (set/union use-case-view-types state-machine-view-types class-view-types))

;;
;; Concept category definitions
;;
(def concept-view-types
  "Element types of a concept view."
  (set/union container-view-types #{:concept}))

(def glossary-view-types
  "Element types of a glossary view."
  (set/union container-view-types #{:concept}))

;; 
;; General category definitions
;;
(def boundary-types
  "Element types of boundaries"
  #{:enterprise-boundary :context-boundary :system-boundary :container-boundary})

(def relation-types
  "Element types of relations"
  (set/union #{:rel}
             architecture-relation-types
             deployment-relation-types
             uml-relation-types))

(def reference-types
  "Element types of references"
  #{:ref})

(def model-types
  "Element types for the architectural model."
  (set/union component-view-types deployment-view-types uml-view-types concept-view-types relation-types))

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


(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-types (:el e)))

(defn relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (contains? relation-types (:el e)))

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (relation? e))))

(defn technical-architecture-node?
  "Returns true if the given element `e` is a technical architecture node."
  [e]
  (contains? technical-architecture-node-types (:el e)))

(defn architecture-node?
  "Returns true if the given element `e` is a architecture node."
  [e]
  (contains? architecture-node-types (:el e)))

(defn architecture-relation?
  "Returns true if the given element `e` is a architecture relation."
  [e]
  (contains? architecture-relation-types (:el e)))

(defn deployment-node?
  "Returns true if the given element `e` is a deployment node."
  [e]
  (contains? deployment-node-types (:el e)))

(defn deployment-relation?
  "Returns true if the given element `e` is a deployment relation."
  [e]
  (contains? deployment-relation-types (:el e)))



(defn child?
  "Returns true, if element `e` is a child of model element `p`."
  [e p]
  ; TODO check (:ct p) for e
  (and (identifiable-element? e)
       (identifiable-element? p)
       (model-element? p)))

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

(defn node-of?
  "Returns true if the given element `e` is a node of `kind`."
  [e kind]
  (and (model-node? e) (= (:el e) kind)))

(defn relation-of?
  "Returns true if the given element `e` is a relation of `kind`."
  [e kind]
  (and (relational-element? e) (= (:el e) kind)))

;;
;; Functions 
;;
(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (when-let [id (:id e)]
    (namespace id)))

(defn generate-node-id
  "Generates an identifier for element `e` based on the id of the parent `p`."
  ([e p]
   (when (and e p (:id p))
     (let [p-namespace (namespace (:id p))
           p-name (name (:id p))]
       (keyword (str p-namespace "/"
                     p-name "-" (:name e) "-" (name (:el e))))))))

(defn generate-relation-id
  "Generates an identifier for a relation `r`."
  ([{:keys [el from to]}]
   (generate-relation-id el from to))
  ([el from to]
   (keyword (str (namespace from) "/"
                 (name from) "-" (name el) "-" (name to)))))
