(ns org.soulspace.overarch.adapter.template.model-api
  "Public API with useful functions on top of the model for use in templates. (Not yet stable!)"
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.adapter.template.model-api :as m]))

;;;;
;;;; Not yet stable!
;;;;

;;; TODO close over model!?

(def model-node-type-order
  "The canonical order of model element types as vector."
  el/model-node-type-order)

(def model-relation-type-order
  "The canonical order of model element types as vector."
  el/model-relation-type-order)

(def model-element-type-order
  "The canonical order of model element types as vector."
  el/model-element-type-order)

(def view-type-order
  "The canonical order of view types as vector."
  el/view-type-order)


;;;
;;; Element Predicates
;;;
(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (el/model-element? e))

(defn model-node?
  "Returns true if the given element `e` is a model node."
  [e]
  (el/model-node? e))

(defn model-relation?
  "Returns true if the given element `e` is a model relation."
  [e]
  (el/model-relation? e))

(defn architecture-model-element?
  "Returns true if the given element `e` is an architecture model element."
  [e]
  (isa? el/element-hierarchy (:el e) :architecture-model-element))

(defn code-model-element?
  "Returns true if the given element `e` is a code model element."
  [e]
  (isa? el/element-hierarchy (:el e) :code-model-element))

(defn concept-model-element?
  "Returns true if the given element `e` is a concept model element."
  [e]
  (isa? el/element-hierarchy (:el e) :concept-model-element))

(defn deployment-model-element?
  "Returns true if the given element `e` is a deployment model element."
  [e]
  (isa? el/element-hierarchy (:el e) :deployment-model-element))

(defn state-machine-model-element?
  "Returns true if the given element `e` is a state-machine model element."
  [e]
  (isa? el/element-hierarchy (:el e) :state-machine-model-element))

(defn use-case-model-element?
  "Returns true if the given element `e` is a use-case model element."
  [e]
  (isa? el/element-hierarchy (:el e) :use-case-model-element))

(defn organization-model-element?
  "Returns true if the given element `e` is a organization model element."
  [e]
  (isa? el/element-hierarchy (:el e) :organization-model-element))

(defn process-model-element?
  "Returns true if the given element `e` is a process model element."
  [e]
  (isa? el/element-hierarchy (:el e) :process-model-element))

;;;
;;; Element functions
;;;
(defn element-type
  "Renders the element type for element `e`."
  [e]
  (str/capitalize (name (:el e))))

(defn element-name
  "Returns the name of the element `e`."
  [e]
  (el/element-name e))

(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (el/element-namespace e))

(defn element-namespace-path
  "Returns a path for the namespace of element `e`."
  [e]
  (str/replace (element-namespace e) \. \/))

(defn element-path
  "Returns a path for the namespace of element `e`."
  [e]
  (str (element-namespace-path e) "/" (name (:id e))))

(defn root-path
  "Returns a relative upwards path from the element `e` to the root of the model."
  [e]
  (let [e-ns (element-namespace e)]
    (if (seq e-ns)
      (->> (str/split e-ns #"\.")
           (map (constantly ".."))
           (str/join "/"))
      ".")))

(comment
  (str/split "a.b.c" #"\.")
  (element-namespace {:id :x})
  (element-namespace {:id :a.b.c/x})
  (root-path {:id :x})
  (root-path {:id :a.b.c/x})
  ;
  )

;;;
;;; Model navigation
;;;
(defn resolve-element
  "Resolves the element `e` in the `model`."
  [model e]
  (model/resolve-element model e))

(defn resolve-view
  "Resolves a view of the given `view-name` for the element `e` in the `model`."
  [model e view-name]
  (let [result (resolve-element model (keyword (str (namespace (:id e)) "/" (name view-name))))]
    (if (el/unresolved-ref? result)
      nil
      result)))

(defn criteria-predicate
  "Returns a predicate function for the `model` and the `criteria`."
  [model criteria]
  (model/criteria-predicate model criteria))

(defn parent
  "Returns the parent of the node `e` in the `model`."
  [model e]
  (model/parent model e))

(defn children
  "Returns the children of the node `e` in the `model`."
  [model e]
  (model/children model e))

(defn ancestor-nodes
  "Returns the set of ancestor nodes of the model node `e` in the `model`."
  [model e]
  (model/ancestor-nodes model e))

(defn descendant-nodes
  "Returns the set of descendants of the model node `e` in the `model`."
  [model e]
  (model/descendant-nodes model e))

(defn root-nodes
  "Returns the set of root nodes of the `coll` of elements.
   The root nodes are not contained as descendants in any of the element nodes."
  [model coll]
  (model/root-nodes model coll))

(defn related-nodes
  "Returns the related nodes for the given `coll` of relations in the context of the `model`."
  [model coll]
  (model/related-nodes model coll))

(defn from-name
  "Returns the name of the from reference of the relation `rel` in the context of the `model`."
  [model rel]
  (model/from-name model rel))

(defn to-name
  "Returns the name of the from reference of the relation `rel` in the context of the `model`."
  [model rel]
  (model/to-name model rel))

(defn referring-nodes
  "Returns the nodes referring to `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (model/referring-nodes model e))
  ([model e criteria]
   (model/referring-nodes model e criteria)))

(defn referred-nodes
  "Returns the nodes referred by `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (model/referred-nodes model e))
  ([model e criteria]
   (model/referred-nodes model e criteria)))

(defn referring-relations
  "Returns the relations referring to `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (model/referring-relations model e))
  ([model e criteria]
   (model/referring-relations model e criteria)))

(defn referred-relations
  "Returns the relations referred by `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (model/referred-relations model e))
  ([model e criteria]
   (model/referred-relations model e criteria)))

;;
;; Architecture model navigation
;;
(defn dependency-nodes
  "Returns the direct dependencies of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{}
             (model/referrer-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

(defn dependent-nodes
  "Returns the direct dependents of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{}
             (model/referred-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

(defn sync-dependencies
  "Returns the transitive dependencies of the `element` in the `model`."
  [model e]
  (model/transitive-search model {:referred-node-selection {:el :request}} e))

(defn sync-dependents
  "Returns the transitive dependants of the `element` in the `model`."
  [model e]
  (model/transitive-search model {:referring-node-selection {:el :request}} e))

(defn requests-incoming
  "Returns the request relations served by service `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :request}))

(defn requests-outgoing
  "Returns the request relations issued by client `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :request}))

(defn responses-incoming
  "Returns the response relations handled by client `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :response}))

(defn responses-outgoing
  "Returns the response relations served by service `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :response}))

(defn sends-incoming
  "Returns the send relations of receiver `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :send}))

(defn sends-outgoing
  "Returns the send relations of sender `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :send}))

(defn publishes-incoming
  "Returns the publish relations of `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :publish}))

(defn publishes-outgoing
  "Returns the publish relations of `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :publish}))

(defn subscribes-incoming
  "Returns the subscribe relations of `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :subscribe}))

(defn subscribes-outgoing
  "Returns the subscribe relations of `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :subscribe}))

(defn dataflows-incoming
  "Returns the incoming dataflow relations served by service `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :dataflow}))

(defn dataflows-outgoing
  "Returns the outgoing dataflow relations of `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :dataflow}))

(defn subscribers-of
  "Returns the subscribers of the queue or publish relation `e` in the `model`."
  [model e]
  (cond
    (= :publish (:el e))
    (->> (:to e)
         (get (:referrer-id->relations model))
         (into #{} (model/referrer-xf model #(= :subscribe (:el %)))))

    (and (= :container (:el e)) (= :queue (:subtype e)))
    (->> (:id e)
         (get (:referrer-id->relations model))
         (into #{} (model/referrer-xf model #(= :subscribe (:el %)))))))

(defn publishers-of
  "Returns the publishers of the queue or subscribe relation `e` in the `model`."
  [model e]
  (cond
    (= :subscribe (:el e))
    (->> (:to e)
         (get (:referred-id->relations model))
         (into #{} (model/referred-xf model #(= :publish (:el %)))))

    (and (= :container (:el e)) (= :queue (:subtype e)))
    (->> (:id e)
         (get (:referred-id->relations model))
         (into #{} (model/referred-xf model #(= :publish (:el %)))))))

;; TODO rename or replace with more general functions
#_(defn synchronously-coupled
  "Returns the nodes in the `model` on which `e` is dependent on via a synchronous request relation."
  [model e]
  (->> (get (:referrer-id->relations model) (:id (resolve-element model e)))
       (filter #(contains? #{:request} (:el %)))
       ;(map #(resolve-element model (:to %)))
       (map :to)))

#_(defn requested-nodes-resolver
  "Returns a resolver function for dependent nodes in the `model`."
  [model]
  (fn [e]
    (requested-nodes model e)))

;;
;; Code model navigation
;;
(defn superclasses
  "Returns the set of direct superclasses of the class element `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :inheritance}))

(defn subclasses
  "Returns the set of direct subclasses of the class element `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :inheritance}))

(defn interfaces
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :implementation}))

(defn implementations
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :implementation}))

(defn supertypes
  "Returns the set of direct supertypes (classes or interfaces) of the class element `e` in the `model`."
  [model e]
  (referring-nodes model e {:els #{:implementation :inheritance}}))

(defn referencing
  "Returns the referenced elements of `e` in the `model`."
  [model e]
  (referring-nodes model e {:els #{:association :aggregation :composition}}))

(defn referenced-by
  "Returns the elements referencing element `e` in the `model`."
  [model e]
  (referred-nodes model e {:els #{:association :aggregation :composition}}))

;; TODO type hierarcy with cycle detection/prevention
#_(defn type-hierarchy
    "Returns the type hierarchy of the class or interface element `e` in the model."
    [model e]
    (let []))

(defn collect-fields
  "Returns the fields of all classes in the `coll`."
  [model coll]
  (->> coll
       (filter #(= :class (:el %)))
       (map #(children model %))
       (remove nil?)
       (map set)
       (apply set/union)
       (filter #(= :field (:el %)))
       (sort-by :name)))

(defn collect-methods
  "Returns the methods of all classes in the `coll`."
  [model coll]
  (->> coll
       (filter #(= :class (:el %)))
       (map #(children model %))
       (remove nil?)
       (map set)
       (apply set/union)
       (filter #(= :method (:el %)))
       (sort-by :name)))

;;
;; Concept model navigation
;;
(defn superordinates
  "Returns the superordinates of the concept `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :is-a}))

(defn subordinates
  "Returns the subordinates of the concept `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :is-a}))

(defn features
  "Returns the features of the concept `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :has}))

(defn feature-of
  "Returns the concepts the concept `e` is a feature of in the `model`."
  [model e]
  (referred-nodes model e {:el :has}))

;;
;; Deployment model navigation
;;
(defn deployed-on
  "Returns the architecture nodes deployed on the node `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :deployed-to}))

(defn deployed-to
  "Returns the deployment nodes the architecture node `e` is deployed to in the ``model`."
  [model e]
  (referred-nodes model e {:el :deployed-to}))

(defn links
  "Returns the deployment nodes the deployment node `e` links in the `model`"
  [model e]
  (referred-nodes model e {:el :link}))

(defn linked-by
  "Returns the deployment nodes the deployment node `e` is linked by in the `model`"
  [model e]
  (referring-nodes model e {:el :link}))

;;
;; Statemachine model navigation
;;
(defn transitions-incoming
  "Returns the incoming transitions of state `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :transition}))

(defn transitions-outgoing
  "Returns the outgoing transitions of state `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :transition}))

;;
;; Use Case model navigation
;;
(defn actors
  "Returns the actors of a use case `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :uses}))

(defn supporting-actors
  "Returns the supporting actors of a use case `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :uses}))

(defn using
  "Returns the to side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :uses}))

(defn used-by
  "Returns the from side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :uses}))

(defn extensions
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :extends}))

(defn included
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (referring-nodes model e {:el :include}))

;;
;; Organization model navigation
;;
(defn responsible-for
  "Returns the nodes the organization unit `e` is responsible for in the `model`."
  [model e]
  (referring-nodes model e {:el :responsible-for}))

(defn responsibility-of
  "Returns the organization unit responsible for node `e` in the `model`."
  [model e]
  (referred-nodes model e {:el :responsible-for}))

(defn collaborates-with
  "Returns the organization model nodes the organization unit `e` collaborates with in the `model`."
  [model e]
  (referring-nodes model e {:el :collaborates-with}))

(defn collaboration-with
  "Returns the organization model nodes the organization unit `e` has a collaboration with in the `model`. Reverse direction of collaborates with."
  [model e]
  (referred-nodes model e {:el :collaborates-with}))

(defn roles
  "Returns the person/user roles assigned in this node `e` in the model."
  [model e]
  (referred-nodes model e {:el :role-in}))

(defn roles-in
  "Returns the nodes the person/user role 'e' has a role in in the `model`."
  [model e]
  (referring-nodes model e {:el :role-in}))
