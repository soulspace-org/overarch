;;;;
;;;; Functions for the definition and handling of the overarch model
;;;;
(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as e]))

;;;
;;; Data handling
;;;

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
  (filter e/model-element? coll))

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

(defn resolve-element
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
        (map (partial resolve-element m))
        (into #{}))))

(defn related
  "Returns the related elements for the given collection of relations"
  ([m coll]
   (->> coll
        (mapcat (fn [e] [(:from e) (:to e)]))
        (map (partial resolve-element m))
        (into #{}))))

(defn relations-of-nodes
  "Returns the relations of the model `m` connecting nodes from the given collection of model nodes."
  ([m coll]
   (let [els (into #{} (map :id coll))
         rels (filter e/relation? (get-model-elements m))
         filtered (->> rels
                       (filter (fn [r] (and (contains? els (:from r)) (contains? els (:to r))))))
         _ (fns/data-tapper {:els els :rels rels :filtered filtered})]
     filtered)))

(defn related-nodes
  "Returns the set of nodes of the model `m` that are part of at least one relation in the `coll`."
  [m coll]
  (->> coll
       (filter e/relation?)
       (map (fn [rel] #{(resolve-element m (:from rel)) (resolve-element m (:to rel))}))
       (reduce set/union #{})))

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

; TODOs
; ctx/ctx-fn - capture context, e.g. parent, indent
; post-fn    - do something if (seq coll) is false
; follow-fn  - filter coll

(defn traverse
  "Traverses the `coll` of elements and returns the elements selected by the `select-fn`
   and transformed by the `transform-fn`.

   select-fn - a predicate on the current element
   transform-fn - a function with two signatures [] and [acc e]
   
   The no args signature should return an empty accumulator, the 2 args signature
   receives the accumulator and the current element and should add the transformed
   element to the accumulator."
  ([select-fn transform-fn coll]
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (first coll)]
                 (if (select-fn e)
                   (recur (trav (transform-fn acc e) (:ct e))
                          (rest coll))
                   (recur (trav acc (:ct e))
                          (rest coll))))
               acc))]
     (trav (transform-fn) coll))))

;;
;; State preparation
;;

(defn id->element
  "Adds the association of the id of the element `e` to the map `m`."
  ([] {})
  ([m e]
   (assoc m (:id e) e)))

(defn referrer-id->rel
  "Adds the relation `r` to the set associated with the id of the :from reference in the map `m`."
  ([] {})
  ([m e]
   (assoc m (:from e) (conj (get m (:from e) #{}) e))))

(defn referred-id->rel
  "Adds the relation `r` to the set associated with the id of the :to reference in the map `m`."
  ([] {})
  ([m r]
   (assoc m (:to r) (conj (get m (:to r) #{}) r))))

(defn build-id->parent
  "Returns a map of child id to parent element for the elements in `coll`."
  ([coll]
   (build-id->parent {} nil coll))
  ([m p coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (and (e/identifiable-element? e) (e/identifiable-element? p) (e/model-element? p))
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
  {:elements elements
   :registry (traverse e/identifiable? id->element elements)
   :parents (build-id->parent elements)
   :referrer (traverse e/relation? referrer-id->rel elements)
   :referred (traverse e/relation? referred-id->rel elements)})


(comment

  ;
  :rcf)
