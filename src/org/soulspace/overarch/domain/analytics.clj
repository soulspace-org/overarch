;;;;
;;;; Model Analytics
;;;;
(ns org.soulspace.overarch.domain.analytics
  "This namespace contains functions for model statistics and analytics."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

;;;
;;; Model Statistics
;;;
(defn count-elements-per-namespace
  "Returns a map with the count of identifiable elements per namespace in the given `coll`."
  [coll]
  (->> coll
       (eduction el/namespaces-xf)
       (frequencies)
       (into (sorted-map))))

(defn count-nodes-per-type
  "Returns a map with the count of nodes per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/model-node?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-relations-per-type
  "Returns a map with the count of relations per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/relational?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-views-per-type
  "Returns a map with the count of views per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/view?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-elements-per-type
  "Returns a map with the count of views per type in the given `coll`."
  [coll]
  (->> coll
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-external
  "Returns a map with the count of external and internal elements in the given `coll`."
  [coll]
  (->> coll
       (map #(if (:external %) :external :internal))
       (frequencies)))

(defn count-synthetic
  "Returns a map with the count of synthetic and normal elements in the given `coll`."
  [coll]
  (->> coll
       (map #(if (:synthetic %) :synthetic :normal))
       (frequencies)))

;;;
;;; Information model
;;;
(defn all-keys
  "Returns a set of all keys used by the maps in `coll`."
  [coll]
  (->> coll
       (mapv keys)
       (mapv set)
       (apply set/union)))

(defn all-values-for-key
  "Returns a set of all keys used by the maps in `coll`."
  [key coll]
  (->> coll
       (mapv key)
       (into #{})))

;;;
;;; Missing information checks
;;;
(defn unidentifiable-elements
  "Returns the elements without an id in the given `coll`."
  [coll]
  (->> coll
       (remove el/identifiable?)))

(defn unnamespaced-elements
  "Returns the elements without a namespaced id."
  [coll]
  (->> coll
       (remove el/namespaced?)
       (map :id)))

(defn unnamed-elements
  "Returns the elements without an id in the given `coll`."
  [coll]
  (->> coll
       (remove el/named?)
       (map :id)))

(defn namespace-match?
  "Returns true, if the relation namespace matches the referrer namespace."
  [r]
  (= (namespace (:id r)) (namespace (:from r))))

(defn unmatched-relation-namespaces
  "Checks if the relation namespace matches the referrer namespace."
  [coll]
  (->> coll
       (filter el/identifiable-relational-element?)
       (remove namespace-match?)))

(defn unrelated-nodes
  "Returns the set of ids of identifiable model nodes not taking part in any relation."
  [model]
  (let [id-set (into #{} el/node-ids-xf (model/nodes model))]
    (set/difference id-set
                    (fns/key-set (:referrer-id->relations model))
                    (fns/key-set (:referred-id->relations model)))))

;;;
;;; Reference checks
;;;
(defn unresolved-related
  "Checks references in a relation."
  [model rel]
  (let [from-el (model/resolve-id model (:from rel))
        to-el (model/resolve-id model (:to rel))]
    (remove nil?
            [(when (el/unresolved-ref? from-el)
               (assoc from-el :parent (:id rel)))
             (when (el/unresolved-ref? to-el)
               (assoc to-el :parent (:id rel)))])))

(defn unresolved-refs
  "Checks references in an element."
  [model element]
  (->> (model/children model element)
       (filter el/reference?)
       (map (partial model/resolve-ref model))
       (filter el/unresolved-ref?)
       (map #(assoc % :parent (:id element)))))

(defn unresolved-refs-in-view
  "Checks references in a view."
  [model view]
  (->> (:ct view)
       (filter el/reference?)
       (map (partial model/resolve-ref model))
       (filter el/unresolved-ref?)
       (map #(assoc % :parent (:id view)))))

(defn check-relations
  "Validates the relations in the model."
  [model]
  (mapcat (partial unresolved-related model) (model/relations model)))

(defn check-nodes
  "Validates the content references in the model."
  [model]
  (mapcat (partial unresolved-refs model) (model/nodes model)))

(defn check-views
  "Validates the references in the views."
  [model]
  (mapcat (partial unresolved-refs-in-view model) (model/views model)))