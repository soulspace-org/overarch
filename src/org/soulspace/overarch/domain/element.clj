;;;;
;;;; contains element specific logic
;;;;
(ns org.soulspace.overarch.domain.element
  (:require [clojure.set :as set]))

;;;
;;; Category definitions
;;;

;;
;; Category definitions for C4 architecture and deployment models
;; 
(def technical-architecture-node-types
  "Technical node types in the architecture model."
  #{:system :container :component})

(def architecture-node-types
  "Node types in the architecture model."
  (set/union technical-architecture-node-types #{:person :enterprise-boundary :context-boundary}))

(def architecture-relation-types
  "Relation types in the architecture model."
  #{:rel :request :response :publish :subscribe :send :dataflow})

(def deployment-node-types
  "Node types for deployment models."
  (set/union technical-architecture-node-types #{:node}))

(def deployment-relation-types
  "Relation types for deployment models."
  #{:link})

;;
;; Category definitions for UML models
;;
(def usecase-node-types
  "Node types for usecase models."
  #{:use-case :actor :person :system :context-boundary})
(def usecase-relation-types
  "Relation types for usecase models."
  #{:uses :include :extends :generalizes})

(def statemachine-node-types
  "Node types for statemachine models."
  #{:state-machine :start-state :end-state :state
    :fork :join :choice :history-state :deep-history-state})
(def statemachine-relation-types
  "Relation types for statemachine models."
  #{:transition})

(def class-node-types
  "Node types for class models."
  #{:class :enum :interface :field :method :function
    :package :namespace :stereotype :annotation :protocol})
(def class-relation-types
  "Relation types for class models."
  #{:inheritance :implementation :composition :aggregation :association :dependency})

(def uml-node-types
  "Node types for UML models."
  (set/union usecase-node-types statemachine-node-types class-node-types))

(def uml-relation-types
  "Relation types of UML models."
  (set/union usecase-relation-types statemachine-relation-types class-relation-types))

;;
;; Concept category definitions
;;
(def concept-node-types
  "Node types for concept models."
  (set/difference (set/union architecture-node-types #{:concept}) #{:component}))

(def concept-relation-types
  "Relation types of concept models."
  #{:rel})

;; 
;; General category definitions
;;
(def boundary-types
  "Element types of boundaries."
  #{:enterprise-boundary :context-boundary :system-boundary :container-boundary})

(def reference-types
  "Element types of references."
  #{:ref})

(def model-node-types
  "Node types of the model."
  (set/union architecture-node-types
             deployment-node-types
             uml-node-types
             concept-node-types))

(def model-relation-types
  "Relation types of the model."
  (set/union #{:rel}
             architecture-relation-types
             deployment-relation-types
             uml-relation-types
             concept-relation-types))

(def model-element-types
  "Element types for the model."
  (set/union model-node-types model-relation-types))

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


(defn tech?
  "Returns true if the given element `e` has a tech (:tech key)."
  [e]
  (not= nil (:tech e)))

(defn boundary?
  "Returns true if `e` is a boundary."
  [e]
  (contains? boundary-types (:el e)))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-element-types (:el e)))

(defn model-relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (contains? model-relation-types (:el e)))

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (model-relation? e))))

(defn child?
  "Returns true, if element `e` is a child of model element `p`."
  [e p]
  ; TODO check (:ct p) for e
  (and (identifiable-element? e)
       (identifiable-element? p)
       (model-element? p)))


(defn technical-architecture-node?
  "Returns true if the given element `e` is a technical architecture model node."
  [e]
  (contains? technical-architecture-node-types (:el e)))

(defn architecture-node?
  "Returns true if the given element `e` is a architecture model node."
  [e]
  (contains? architecture-node-types (:el e)))

(defn architecture-relation?
  "Returns true if the given element `e` is a architecture model relation."
  [e]
  (contains? architecture-relation-types (:el e)))

(defn deployment-node?
  "Returns true if the given element `e` is a deployment model node."
  [e]
  (contains? deployment-node-types (:el e)))

(defn deployment-relation?
  "Returns true if the given element `e` is a deployment model relation."
  [e]
  (contains? deployment-relation-types (:el e)))

(defn usecase-node?
  "Returns true if the given element `e` is a usecase model node."
  [e]
  (contains? usecase-node-types (:el e)))

(defn usecase-relation?
  "Returns true if the given element `e` is a usecase model relation."
  [e]
  (contains? usecase-relation-types (:el e)))

(defn statemachine-node?
  "Returns true if the given element `e` is a statemachine model node."
  [e]
  (contains? statemachine-node-types (:el e)))

(defn statemachine-relation?
  "Returns true if the given element `e` is a statemachine model relation."
  [e]
  (contains? statemachine-relation-types (:el e)))

(defn class-node?
  "Returns true if the given element `e` is a class model node."
  [e]
  (contains? class-node-types (:el e)))

(defn class-relation?
  "Returns true if the given element `e` is a class model relation."
  [e]
  (contains? class-relation-types (:el e)))

(defn concept-node?
  "Returns true if the given element `e` is a concept model node."
  [e]
  (contains? concept-node-types (:el e)))

(defn concept-relation?
  "Returns true if the given element `e` is a concept model relation."
  [e]
  (contains? concept-relation-types (:el e)))

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
  [kind e]
  (and (model-node? e) (= (:el e) kind)))

(defn relation-of?
  "Returns true if the given element `e` is a relation of `kind`."
  [kind e]
  (and (model-relation? e) (= (:el e) kind)))

;;
;; Functions 
;;

(defn traverse
  "Traverses the `coll` of elements and returns the elements selected by the `select-fn`
   and transformed by the `step-fn`.

   select-fn - a predicate on the current element
   step-fn - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the step-fn should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator."
  ([step-fn coll]
   ; selection might be handled in the step function
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (first coll)]
                 (recur (trav (step-fn acc e) (:ct e))
                        (rest coll)))
               (step-fn acc)))]
     (trav (step-fn) coll)))
  ([select-fn step-fn coll]
   ; selection handled by th select function
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (first coll)]
                 (if (select-fn e)
                   (recur (trav (step-fn acc e) (:ct e))
                          (rest coll))
                   (recur (trav acc (:ct e))
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) coll))))

(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (when-let [id (:id e)]
    (namespace id)))

(defn tree->set
  "Converts a hierarchical tree of elements to a flat set of elements."
  ([] #{})
  ([acc] acc)
  ([acc e] (conj acc e)))

(defn descendant-nodes
  "Returns the descendants of the `node`."
  [el]
  (traverse model-node? tree->set (:ct el)))

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
