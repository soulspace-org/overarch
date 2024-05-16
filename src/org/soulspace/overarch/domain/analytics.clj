(ns org.soulspace.overarch.domain.analytics
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

;;;
;;; Analytics
;;;

;;
;; Functions
;;
(defn count-namespaces
  "Returns a map with the count of identifiable elements per namespace in the given `coll`."
  [coll]
  (->> coll
       (eduction el/namespaces-xf)
       (frequencies)
       (into (sorted-map))))

(defn count-nodes
  "Returns a map with the count of relations per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/model-node?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-relations
  "Returns a map with the count of relations per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/relational?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-views
  "Returns a map with the count of views per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/view?)
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn count-elements
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


(defn key-set
  "Returns a set of the keys of the map `m`."
  [m]
  (into #{} (keys m)))

(defn unrelated-nodes
  "Returns the set of ids of identifiable model nodes not taking part in any relation."
  [model]
  ; TODO registry contains relations and views
  (let [id-set (into #{} el/node-ids-xf (model/all-elements model))]
    (set/difference id-set
                    (key-set (:referrer-id->relations model))
                    (key-set (:referred-id->relations model)))))

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
  (->> (:ct element)
       (filter el/reference?)
       (map (partial model/resolve-ref model))
       (filter el/unresolved-ref?)
       (map #(assoc % :parent (:id element)))))


(defn check-relations
  "Validates the relations in the model."
  [model]
  (->> (model/all-elements model)
       (filter el/relational-element?)
       (map (partial unresolved-related model))
       (flatten)))

(defn check-nodes
  "Validates the references in the model."
  []
  )

(defn check-views
  "Validates the references in the views."
  [model]
  (->> (model/views model)
       (map (partial unresolved-refs model))
       (flatten)))
