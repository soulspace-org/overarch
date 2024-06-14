(ns org.soulspace.overarch.adapter.template.model-api
  "Public API with useful functions on top of the model for use in templates. (Not yet stable!)"
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.application.model-repository :as repo]
            [clojure.string :as str]))

;;;;
;;;; Not yet stable!
;;;;

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
  ((isa? el/element-hierarchy (:el e) :architecture-model-element)))

(defn class-model-element?
  "Returns true if the given element `e` is a class model element."
  [e]
  ((isa? el/element-hierarchy (:el e) :class-model-element)))

(defn concept-model-element?
  "Returns true if the given element `e` is a concept model element."
  [e]
  ((isa? el/element-hierarchy (:el e) :concept-model-element)))

(defn deployment-model-element?
  "Returns true if the given element `e` is a deployment model element."
  [e]
  ((isa? el/element-hierarchy (:el e) :deployment-model-element)))

(defn state-machine-model-element?
  "Returns true if the given element `e` is a state-machine model element."
  [e]
  ((isa? el/element-hierarchy (:el e) :state-machine-model-element)))

(defn use-case-model-element?
  "Returns true if the given element `e` is a use-case model element."
  [e]
  ((isa? el/element-hierarchy (:el e) :use-case-model-element)))

;;;
;;; Element functions
;;;
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
    (->> (str/split e-ns #"\.")
         (map (constantly ".."))
         (str/join "/"))))

(comment
  (str/split "a.b.c" #"\.")
  (element-namespace {:id :a.b.c/x})
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
  (resolve-element model (keyword (str (namespace (:id e)) "/" (name view-name)))))

(defn parent
  "Returns the parent of the node `e` in the `model`."
  [model e]
  (model/parent model e))

(defn children
  "Returns the parent of the node `e` in the `model`."
  [model e]
  (el/children e))

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

;;
;; Architecture Model navigation
;;

;;
;; Class model navigation
;;
(defn all-fields
  "Returns a sequence of all fields of the given collection of `classes`."
  [model classes]
  (el/collect-fields classes))

(defn all-methods
  "Returns a sequence of all methods of the given collection of `classes`."
  [model classes]
  (el/collect-methods classes))

(defn superclasses
  "Returns the set of direct superclasses of the class element `e` in the `model`."
  [model e]
  (model/superclasses model e))

(defn subclasses
  "Returns the set of direct subclasses of the class element `e` in the `model`."
  [model e]
  (model/subclasses model e))

(defn interfaces
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (model/interfaces model e))

(defn implementations
  "Returns the set of direct implementations of the class element `e` in the `model`."
  [model e]
  (model/implementations model e))

(defn supertypes
  "Returns the set of direct supertypes (classes or interfaces) of the class element `e` in the `model`."
  [model e]
  (model/supertypes model e))

;;
;; Concept model navigation
;;
(defn superordinates
  "Returns the set of direct superordinates of the concept element `e` in the `model`."
  [model e]
  (model/superordinates model e))

(defn subordinates
  "Returns the set of direct subordinates of the concept element `e` in the `model`."
  [model e]
  (model/subordinates model e))

;;
;; Deployment model navigation
;;
(defn deployed-on
  "Returns the architecture nodes deployed on the node `e` in the ``model`."
  [model e]
  (model/deployed-on model e))

(defn deployed-to
  "Returns the deployment nodes the architecture node `e` is deployed to in the ``model`."
  [model e]
  (model/deployed-to model e))


;;
;; Statemachine model navigation
;;

;;
;; Use Case model navigation
;;
(defn actors
  "Returns the actors of a use case `e` in the `model`."
  [model e]
  (model/used-by model e))

(defn supporting-actors
  "Returns the supporting actors of a use case `e` in the `model`."
  [model e]
  (model/using model e))

(defn using
  "Returns the to side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (model/using model e))

(defn used-by
  "Returns the from side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (model/used-by model e))

(defn extensions
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (model/extensions model e))

(defn included
  "Returns the included use cases of a use case `e` in the `model`."
  [model e]
  (model/included model e))

