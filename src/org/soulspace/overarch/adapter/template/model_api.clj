(ns org.soulspace.overarch.adapter.template.model-api
  "Public API with useful functions on top of the model for use in templates. (Not yet stable!)"
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

;;;;
;;;; Not yet stable!
;;;;

;;
;; Model
;;
(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (el/model-element? e))

(defn model-node?
  "Returns true if the given element is a model node."
  [e]
  (el/model-node? e))

(defn model-relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (el/model-relation? e))

(defn element-name
  "Returns the name of the element `e`."
  [e]
  (el/element-name e))

(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (el/element-namespace e))

(defn resolve-element
  "Resolves the element `e` in the `model`."
  [model e]
  (model/resolve-element model e))

(defn parent
  "Returns the parent of the node `e` in the `model`."
  [model e]
  (model/parent model e))

#_(defn ancestor-nodes
    "Returns the set of ancestor nodes of the model node `e` in the `model`."
    [model e]
    (model/ancestor-nodes model e))

#_(defn descendant-nodes
    "Returns the set of descendants of the model node `e` in the `model`."
    [model e]
    (model/descendant-nodes model e))

#_(defn root-nodes
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
;; Architecture Model
;;

;;
;; Class model
;;
(defn all-fields
  "Returns a sequence of all fields of the given collection of `classes`."
  [classes]
  (el/collect-fields classes))

(defn all-methods
  "Returns a sequence of all methods of the given collection of `classes`."
  [classes]
  (el/collect-methods classes))

(defn superclasses
  "Returns the set of direct superclasses of the class element `e` in the `model`."
  [model e]
  (model/superclasses model e))

(defn interfaces
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (model/interfaces model e))

(defn supertypes
  "Returns the set of direct supertypes (classes or interfaces) of the class element `e` in the `model`."
  [model e]
  (model/supertypes model e))


;;
;; Concept model
;;

;;
;; Deployment model
;;

;;
;; Statemachine model
;;

;;
;; Use Case model
;;
