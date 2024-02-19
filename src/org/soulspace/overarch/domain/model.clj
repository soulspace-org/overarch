;;;;
;;;; Functions for the definition and handling of the overarch model
;;;;
(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]))

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
  (filter el/model-element? coll))

(defn get-model-elements
  "Returns the collection of model elements."
  ([m]
   (model-elements (:elements m))))

(defn get-model-element
  "Returns the model element with the given `id`."
  ([m id]
   ((:id->element m) id)))

(defn get-parent-element
  "Returns the parent of the element `e`."
  ([m e]
   ((:id->parent m) (:id e))))

(defn parent
  "Returns the parent of the element `e`."
  [m e]
  ; TODO  implement based on relations
  ((:id->parent m) (:id e)))

(defn children
  "Returns the children of the element `e`."
  [m e]
  ; TODO implement based on relations
  )

(defn resolve-ref
  "Resolves the model element for the ref `r`."
  [m r]
  (if-let [e (get-model-element m (:ref r))]
    (merge e (dissoc r :ref))
    {:unresolved-ref (:ref r)}))

(defn resolve-id
  "Resolves the model element for the `id`"
  [m id]
  (if-let [e (get-model-element m id)]
    e
    {:unresolved-ref id}))

(defn resolve-element
  "Resolves the model element for the ref `e`."
  ([m e]
   (cond
     (keyword? e) (resolve-id m e)
     (el/reference? e) (resolve-ref m e)
     :else e)))

(defn all-elements
  "Returns a set of all elements."
  ([m]
   (->> (:id->element m)
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
         rels (filter el/relation? (get-model-elements m))
         filtered (->> rels
                       (filter (fn [r] (and (contains? els (:from r)) (contains? els (:to r))))))
         _ (fns/data-tapper {:els els :rels rels :filtered filtered})]
     filtered)))

(defn related-nodes
  "Returns the set of nodes of the model `m` that are part of at least one relation in the `coll`."
  [m coll]
  (->> coll
       (filter el/relation?)
       (map (fn [rel] #{(resolve-element m (:from rel)) (resolve-element m (:to rel))}))
       (reduce set/union #{})))

(defn aggregable-relation?
  "Returns true, if the relations `r1` and `r2` are aggregable."
  ([m r1 r2]
   (and (= (:tech r1) (:tech r2))
        ; (= (:name r1) (:name r2))
        ; (= (:desc r1) (:desc r2))
        (or (= (:from r1) (:from r2))
            (= (get-parent-element m (:from r1))
               (get-parent-element m (:from r2))))
        (or (= (:to r1) (:to r2))
            (= (get-parent-element m (:to r1))
               (get-parent-element m (:to r2)))))))

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

;;
;; State preparation
;;
(comment
(defn id->parent
  "Adds the association from the id of element `e` to the parent `p` to the map `acc`."
  ([] [{} '()])
  ([[res ctx]]
   (if-not (empty? ctx)
     [res (pop ctx)]
     res))
  ([[res ctx] e]
   (let [p (peek ctx)]
     (if (el/child? e p)
       [(assoc res (:id e) p) (conj ctx e)]
       [res (conj ctx e)]))))

(defn id->element
  "Adds the association of the id of the element `e` to the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc e]
   (assoc acc (:id e) e)))

(defn referrer-id->rel
  "Adds the relation `r` to the set associated with the id of the :from reference in the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc e]
   (assoc acc (:from e) (conj (get acc (:from e) #{}) e))))

(defn referred-id->rel
  "Adds the relation `r` to the set associated with the id of the :to reference in the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc r]
   (assoc acc (:to r) (conj (get acc (:to r) #{}) r))))

(defn build-registry
  "Returns a map with the original `elements` and registries by id for lookups.
   
   The map has the following shape:

   :elements -> the given data
   :id->element -> a map from id to element
   :id->parent  -> a map from id to parent element
   :referrer-id->relations -> a map from id to set of relations where the id is the referrer (:from)
   :referred-id->relations -> a map from id to set of relations where the id is referred (:to)"
  [elements]
  ; TODO add additional keys :nodes, :relations, :views
  ; :nodes -> flat model nodes, no content
  ; :relations -> uniform relations (incl. parent/child)
  ; :views -> views with content
  (let [id->element (traverse el/identifiable? id->element elements)
        parents (traverse id->parent elements)
        referrer-id->relations (traverse el/relation? referrer-id->rel elements)
        referred-id->relations (traverse el/relation? referred-id->rel elements)]
    {:elements elements
     :id->element id->element
     :id->parent parents
     :referrer-id->relations referrer-id->relations
     :referred-id->relations referred-id->relations}))
)