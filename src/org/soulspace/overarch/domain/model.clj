;;;;
;;;; Functions for the definition and handling of the overarch model
;;;;
(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model.

   The loaded overarch working model is a map with the following keys

   | Key                     | Description 
   |-------------------------|-------------
   | **Relation keys**       | 
   | :nodes                  | the set of all nodes (incl. child nodes)
   | :relations              | the set of all relations (incl. contained-in relations)
   | :views                  | the set of views
   | :themes                 | the set of themes
   | **Index keys**          | 
   | :id->element            | a map from id to element (nodes, relations and views)
   | :id->parent-id          | a map from id to parent node id (deprecated)
   | :id->children           | a map from id to a vector of contained nodes (deprecated)
   | :referrer-id->relations | a map from id to set of relations where the id is the referrer (:from)
   | :referred-id->relations | a map from id to set of relations where the id is referred (:to)
   | **Problem keys**        | 
   | :problems               | the set of problems found during model building
   "
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.element :as el]
            [clojure.string :as str]))

;;;
;;; Basic accessor functions
;;;
(defn model-element
  "Returns the model element with the given `id`."
  ([model id]
   (get-in model [:id->element id])))

(defn resolve-ref
  "Resolves the model element for the ref `r` and associates any additional keys of `r` to the model element."
  [model r]
  (if-let [e (model-element model (:ref r))]
    (merge e (dissoc r :ref)) ; merge additional keys on ref
    {:unresolved-ref (:ref r)}))

(defn resolve-id
  "Resolves the model element for the `id`"
  [model id]
  (if-let [e (model-element model id)]
    e
    {:unresolved-ref id}))

(defn resolve-element
  "Resolves the model element for the ref `e`."
  ([model e]
   (cond
     (keyword? e) (resolve-id model e)
     (el/reference? e) (resolve-ref model e)
     (el/element? e) e
     :else nil)))

; specific resolver for elements to avoid the `apply` in `partial` for performance
(defn element-resolver
  "Returns an element resolver function for the given `model`."
  [model]
  (fn [e]
    (resolve-element model e)))

(defn parent
  "Returns the parent of the model node `e`."
  [model e]
  (if-let [p-id ((:id->parent-id model) (:id e))]
    (resolve-element model p-id)
    nil))

; TODO check correctness and performance and replace parent if ok
(defn parent-new
  "Returns the parent of the model node `e`."
  [model e]
  (->> (get-in model [:referred-id->relations (:id e)])
       (filter #(= :contained-in (:el %)))
       (first)
       (:to)
       (resolve-element model)))

; specific resolver for elements to avoid the `apply` in `partial` for performance
(defn parent-resolver
  "Returns a parent resolver function for the given `model`."
  [model]
  (fn [e]
    (parent model e)))

(defn children
  "Returns the children of the model node `e`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filterv #(= :contained-in (:el %)))
       (mapv (comp (element-resolver model) :from))))
  
; specific resolver for elements to avoid the `apply` in `partial` for performance
(defn children-resolver
  "Returns a children resolver function for the given `model`."
  [model]
  (fn [e]
    (children model e)))

(defn resolveable-element?
  "Returns true if the element is resolveable in the model."
  [model e]
  (boolean (resolve-element model e)))

(defn resolveable-relation?
  "Returns true if relation `e` and the referrer and referred nodes of `e` are resolveable in the `model`."
  [model e]
  (if-let [rel (resolve-element model e)]
    (and (resolve-element model (:from rel))
         (resolve-element model (:to rel)))
    false))

;;
;; Model transducer functions
;;
(defn referrer-xf
  "Transducer to resolve referrer elements of in the `model`.
   Optionally takes a `filter-fn` to filter the relations to take into account."
  ([model]
   (comp
    (map :to)
    (map (element-resolver model))))
  ([model filter-fn]
   (comp
    (filter filter-fn)
    (map :to)
    (map (element-resolver model)))))

(defn referred-xf
  "Transducer to resolve referred elements in the `model`.
   Optionally takes a `filter-fn` to filter the relations to take into account."
  ([model]
   (comp
    (map :from)
    (map (element-resolver model))))
  ([model filter-fn]
   (comp
    (filter filter-fn)
    (map :from)
    (map (element-resolver model)))))

(defn unresolved-refs-xf
  "Returns a transducer to extract unresolved refs"
  [model]
  (comp (filter el/reference?)
        (map (element-resolver model))
        (filter el/unresolved-ref?)))

;;
;; recursive traversal of the graph
;;
; TODO work on a single element, too. Apply children-fn on the element then.
; TODO rename children-fn to something more general (e.g. connected-fn or related-fn)?
(defn traverse
  "Recursively traverses the `coll` of elements filtered by an optional `pred-fn`
   and returns the elements transformed by the `step-fn`.

   `element-fn`  - a resolver function for an element, defaults to `identity`
   `pred-fn`     - a predicate on the current element, defaults to `identity`
   `children-fn` - a function to resolve the children of the current element
   `step-fn`     - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the `step-fn` should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator."
  ([step-fn coll]
   (traverse identity identity :ct step-fn coll))
  ([pred-fn step-fn coll]
   (traverse identity pred-fn :ct step-fn coll))
  ([pred-fn children-fn step-fn coll]
   (traverse identity pred-fn children-fn step-fn coll))
  ([element-fn pred-fn children-fn step-fn coll]
   (letfn [(trav [acc visited coll]
                 ;(println "t-visited" visited)
                 ;(println "t-coll" coll)
                 (if (seq coll)
                   (let [e (element-fn (first coll))
                         v (conj visited e)]
                     ;(println "t-element" e)
                     ;(println "t-v" v)
                     (if (not (contains? visited e))
                       (if (pred-fn e)
                         (recur (trav (step-fn acc e) v (children-fn e))
                                v
                                (rest coll))
                         (recur (trav acc v (children-fn e))
                                v
                                (rest coll)))
                       (step-fn acc)))
                   (step-fn acc)))]
     (trav (step-fn) #{} coll))))

;;;
;;; Step functions for traverse
;;;
(defn collect-in-vector
  "Step function to collect elements `e` in the accumulator `acc`.
   No transformation on the element is applied."
  ([]
   [])
  ([acc]
   acc)
  ([acc e]
   (conj acc e)))

(defn collect-in-set
  "Step function to collect elements `e` in the accumulator `acc`.
   No transformation on the element is applied."
  ([]
   [])
  ([acc]
   acc)
  ([acc e]
   (conj acc e)))

(defn tree->set
  "Step function to convert a hierarchical tree of elements to a flat set of elements."
  ([] #{})
  ([acc] acc)
  ([acc e] (conj acc e)))

(defn tech-collector
  "Step function to collect the technologies.
   Adds the tech of `e` to the accumulator `acc`."
  ([] #{})
  ([acc] acc)
  ([acc e] (set/union acc #{(:tech e)})))

(defn sprite-collector
  "Adds the sprite of `e` to the accumulator `acc`."
  ([] #{})
  ([acc] acc)
  ([acc e] (set/union acc #{(:sprite e)})))

(defn key->element
  "Returns a step function to create an key `k` to element map.
   Adds the association of the id of the element `e` to the map `acc`."
  [k]
  (fn
    ([] {})
    ([acc] acc)
    ([acc e]
     (assoc acc (k e) e))))

(defn id->element 
  "Step function to create an id to element map.
   Adds the association of the id of the element `e` to the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc e]
   (assoc acc (:id e) e)))

;;
;; Accessors
;;
(defn nodes
  "Returns the collection of model nodes."
  [model]
  (:nodes model))

(defn relations
  "Returns the collection of model relations."
  [model]
  (:relations model))

(defn model-elements
  "Returns the collection of model elements."
  ([model]
   (concat (nodes model) (relations model))))

(defn views
  "Returns the set of views from the `model`."
  [model]
  (:views model))

(defn themes
  "Returns the set of themes from the `model`."
  [model]
  (:themes model))

(defn from-name
  "Returns the name of the from reference of the relation `rel` in the context of the `model`."
  [model rel]
  (->> rel
       (:from)
       (resolve-id model)
       (:name)))

(defn to-name
  "Returns the name of the from reference of the relation `rel` in the context of the `model`."
  [model rel]
  (->> rel
       (:to)
       (resolve-id model)
       (:name)))

(defn related-xf
  "Transducer to resolve the elements in the `model` referenced by relations."
  [model]
  (comp
   (filter el/model-relation?)
   (mapcat (fn [e] [(:from e) (:to e)]))
   (map (partial resolve-id model))))

(defn related-nodes
  "Returns the set of nodes of the `model` that are part of at least one relation in the `coll`."
  ([model coll]
   (into #{} (related-xf model) coll)))

(defn related
  "Returns the set of nodes of the `model` that are part of at least one relation in the `coll`."
  [model coll]
  (->> coll
       (filter el/model-relation?)
       (map (fn [rel] #{(resolve-element model (:from rel))
                        (resolve-element model (:to rel))}))
       (reduce set/union #{})))

(defn relations-of-nodes
  "Returns the relations of the `model` connecting nodes from the given `coll` of model nodes."
  ([model coll]
   (let [els (into #{} (map :id coll))
         rels (filter el/model-relation? (model-elements model))
         filtered (->> rels
                       (filter (fn [r] (and (contains? els (:from r))
                                            (contains? els (:to r)))))
                       (into #{}))]
     filtered)))

; TODO check for invinite loop, should not happen because the model build prevents this
(defn ancestor-nodes
  "Returns the ancestor nodes of the model node `e` in the `model`."
  [model e]
  (loop [acc #{} p (parent model e)]
    (if (seq p)
      (recur (conj acc p) (parent model p))
      acc)))

; TODO check for invinite loop, should not happen because the model build prevents this
(defn ancestor-node?
  "Returns true, if `c` is an ancestor of node `e` in the `model`."
  [model e c]
  (loop [p (parent model e)]
    (if (seq p)
      (if (= (:id p) (:id c))
        true
        (recur (parent model p)))
      false)))

(defn descendant-nodes
  "Returns the set of descendants of the node `e` in the `model`."
  [model e]
  (when (el/model-node? (resolve-element model e))
    (traverse (element-resolver model) el/model-node? (children-resolver model) tree->set (children model e)))) ; (:ct e)

(defn descendant-node?
  "Returns true, if `c` is a descendant of `e` in the `model`."
  [model e c]
  (contains? (descendant-nodes model e) (resolve-element model c)))

(defn all-descendant-nodes
  "Returns the set of descendants of the nodes in the collection of `elements` in the `model`."
  [model elements]
  (->> elements
       (filter el/model-node?)
       (mapcat (partial descendant-nodes model))
       (into #{})))

(defn root-nodes
  "Returns the set of root nodes of the `elements` in the `model`.
   The root nodes are not contained as descendants in any of the element nodes."
  [model elements]
  (let [descendants (all-descendant-nodes model elements)]
    (set/difference (set elements) descendants)))

(defn all-nodes
  "Returns the set of all nodes, including descendants, of the `elements` in the `model`."
  [model elements]
  (let [descendants (all-descendant-nodes model elements)]
    (set/union (set elements) descendants)))

(defn all-elements
  "Returns a set of all elements in the `model`."
  ([model]
   (->> (:id->element model)
        (vals)
        (map (element-resolver model))
        (into #{}))))

;;;
;;; filtering element collections by criteria
;;;
(declare criteria-predicate)

;; TODO add criteria for model type (architecture, etc.)
;;      select nodes of model category
;;      select relations of category if both nodes are of category
;;      (rel is in multiple categories)

(defn from-pred
  "Returns a predicate that returns true, if referring node of relation `e` is matched by `v` in the `model`."
  [model v]
  (if (or (map? v) (vector? v))
    (fn [e]
      ((criteria-predicate model v) (resolve-element model (:from e))))
    (el/from-pred v)))

(defn to-pred
  "Returns a predicate that returns true, if referred node of relation `e` is matched by `v` in the `model`."
  [model v]
  (if (or (map? v) (vector? v))
    (fn [e]
      ((criteria-predicate model v) (resolve-element model (:to e))))
    (el/to-pred v)))

(defn external-check-pred
  "Returns a predicate that returns true, if the check for external on `e` equals the boolean value `v` in the `model`."
  [model v]
  (fn [e]
    (= v (if (el/model-node? e)
           (boolean (el/external? e))
           (or (el/external? (resolve-element model (:from e)))
               (el/external? (resolve-element model (:to e))))))))

(defn refers-check-pred
  "Returns a predicate that returns true, if the check for `e` as a referrer equals the boolean value `v` in the `model`."
  [model v]
  (fn [e]
    (= v (boolean ((:referrer-id->relations model) (:id e))))))

(defn referred-check-pred
  "Returns a predicate that returns true if the check for `e` as referred to equals the boolean value `v` in the `model`."
  [model v]
  (fn [e]
    (= v (boolean ((:referred-id->relations model) (:id e))))))

(defn refers-pred
  "Returns a predicate that returns true, if a referrer relation of the node `e` matches `v` in the `model`."
  [model v]
  (fn [e]
    (->> (:id e)
         (get (:referrer-id->relations model))
         (some (criteria-predicate model v)))))

(defn referred-pred
  "Returns a predicate that returns true, if a referrer relation of the node `e` matches `v` in the `model`."
  [model v]
  (fn [e]
    (->> (:id e)
         (get (:referred-id->relations model))
         (some (criteria-predicate model v)))))

(defn refers-to-pred
  "Returns a predicate that returns true, if the node with id `v` refers to `e` in the `model`."
  [model v]
  (fn [e]
    (contains? (into #{}
                   (map :to ((:referrer-id->relations model) (:id e))))
             v)))

(defn referred-by-pred
  "Returns a predicate that returns true, if `e` is referred by the node with id `v` in the `model`."
  [model v]
  (fn [e]
    (contains? (into #{}
                   (map :from ((:referred-id->relations model) (:id e))))
             v)))

(defn child-check-pred
  "Returns a predicate that returns true, if the check for `e` is a child in the `model` equals the boolean value `v`."
  [model v]
  (fn [e]
    (= v (boolean ((:id->parent-id model) (:id e))))))

(defn child-pred
  "Returns a predicate that returns true, if `e` is a child of the node with id `v` in the `model`."
  [model v]
  (fn [e]
    (contains? (children model (resolve-id model v)) e)))

(defn parent-check-pred
  "Returns a predicate that returns true if the check for children of `e` equals the boolean value `v`"
  [model v]
  (fn [e]
    (= v (empty? (children model e)))))

(defn parent-pred
  "Returns a predicate that returns true if `v` is the parent of `e`"
  [model v]
  (fn [e]
    (= v ((:id->parent-id model) (:id e)))))

(defn descendant-of-pred
  "Returns a predicate that returns true, if `e` is a descendant of the node with id `v` in the `model`."
  [model v]
  (fn [e]
    (contains? (descendant-nodes model (resolve-id model v)) e)))

(defn ancestor-of-pred
  "Returns a predicate that returns true, if `e` is an ancestor of the node with id `v` in the `model`."
  [model v]
  (fn [e]
    (contains? (ancestor-nodes model (resolve-id model v)) e)))

;;
;; Building criterium predicates and filters
;;
(defn criterium-predicate
  "Returns a predicate for the given `criterium`."
  [model [k v]]
  (let [key-name (name k)]
    
    (if (str/starts-with? key-name "!")
      (complement (criterium-predicate model [(keyword (subs key-name 1)) v]))
      (cond
        ;; element related
        (= :key? k)                    (el/key-check-pred v)
        (= :key k)                     (el/key-pred v)
        (= :model-node? k)             (el/model-node-pred v)
        (= :model-relation? k)         (el/model-relation-pred v)
        (= :el k)                      (el/el-pred v)
        (= :els k)                     (el/els-pred v)
        (= :namespace k)               (el/namespace-pred v)
        (= :namespaces k)              (el/namespaces-pred v)
        (= :namespace-prefix k)        (el/namespace-prefix-pred v)
        (= :namespace-prefixes k)      (el/namespace-prefixes-pred v)
        (= :from-namespace k)          (el/from-namespace-pred v) ; deprecated
        (= :from-namespaces k)         (el/from-namespaces-pred v) ; deprecated
        (= :from-namespace-prefix k)   (el/from-namespace-prefix-pred v) ; deprecated
        (= :from-namespace-prefixes k) (el/from-namespace-prefixes-pred v) ; deprecated
        (= :to-namespace k)            (el/to-namespace-pred v) ; deprecated
        (= :to-namespaces k)           (el/to-namespaces-pred v) ; deprecated
        (= :to-namespace-prefix k)     (el/to-namespace-prefix-pred v) ; deprecated
        (= :to-namespace-prefixes k)   (el/to-namespace-prefixes-pred v) ; deprecated
        (= :id? k)                     (el/id-check-pred v)
        (= :id k)                      (el/id-pred v)
        (= :subtype? k)                (el/subtype-check-pred v)
        (= :subtype k)                 (el/subtype-pred v)
        (= :subtypes k)                (el/subtypes-pred v)
        (= :maturity? k)               (el/maturity-check-pred v)
        (= :maturity k)                (el/maturity-pred v)
        (= :maturities k)              (el/maturities-pred v)
        (= :synthetic? k)              (el/synthetic-check-pred v)
        (= :name? k)                   (el/name-check-pred v)
        (= :name k)                    (el/name-pred v)
        (= :name-prefix k)             (el/name-prefix-pred v)
        (= :desc? k)                   (el/desc-check-pred v)
        (= :desc k)                    (el/desc-pred v)
        (= :doc? k)                    (el/doc-check-pred v)
        (= :doc k)                     (el/doc-pred v)
        (= :tech? k)                   (el/tech-check-pred v)
        (= :tech k)                    (el/tech-pred v)
        (= :techs k)                   (el/techs-pred v)
        (= :all-techs k)               (el/all-techs-pred v)
        (= :tags? k)                   (el/tags-check-pred v)
        (= :tag k)                     (el/tag-pred v)
        (= :tags k)                    (el/tags-pred v)
        (= :all-tags k)                (el/all-tags-pred v)

        ;; model related
        (= :from k)                    (from-pred model v)
        (= :to k)                      (to-pred model v)
        (= :external? k)               (external-check-pred model v)
        (= :refers? k)                 (refers-check-pred model v)
        (= :referred? k)               (referred-check-pred model v)
        (= :refers k)                  (refers-pred model v)
        (= :referred k)                (referred-pred model v)
        (= :refers-to k)               (refers-to-pred model v)
        (= :referred-by k)             (referred-by-pred model v)
        (= :child? k)                  (child-check-pred model v)
        (= :child-of k)                (child-pred model v)
        (= :descendant-of k)           (descendant-of-pred model v)
        (= :children? k)               (parent-check-pred model v) ; deprecated
        (= :parent? k)                 (parent-check-pred model v)
        (= :parent-of k)               (parent-pred model v)
        (= :ancestor-of k)             (ancestor-of-pred model v)

      :else
      (do (println "unknown criterium" (name k))
          (fn [_] false))))))

(defn criteria-map-predicate
  "Returns a filter predicate for the given `criteria`.
   The resulting predicate is a logical conjunction (and) of the predicates for
   each criterium."
  [model criteria]
  (loop [remaining criteria
         predicates []]
    (if (seq remaining)
      (recur (rest remaining)
             (conj predicates (criterium-predicate model (first remaining))))
      ; add a predicate which returns true to return at least one predicate
      (apply every-pred (conj (remove nil? predicates) (fn [_] true))))))

(defn criteria-predicate
  "Returns a filter predicate for the given `criteria`.
   If a vector of criteria maps is given, the resulting predicate is a logical
   disjunction (or) of the predicates."
  [model criteria]
  (if (vector? criteria)
    ; vector of criteria
    (loop [remaining criteria
           predicates []]
      (if (seq remaining)
        (recur (rest remaining)
               (conj predicates (criteria-map-predicate model (first remaining))))
        (apply some-fn predicates)))
    ; simple criteria map
    (criteria-map-predicate model criteria)))

(defn filter-xf
  "Returns a filter transducer for the given `criteria`."
  [model criteria]
  (let [filter-predicates (criteria-predicate model criteria)]
    ; compose the filtering functions and create a filter transducer
    (filter filter-predicates)))

;;
;; Graph based functions
;;
(defn referring-nodes
  "Returns the nodes referring to `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (into #{} (referred-xf model))))
  ([model e criteria]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (into #{} (referred-xf model (criteria-predicate model criteria))))))

(defn referred-nodes
  "Returns the nodes referred by `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (into #{} (referrer-xf model))))
  ([model e criteria]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (into #{} (referrer-xf model (criteria-predicate model criteria))))))

(defn referring-relations
  "Returns the relations referring to `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referred-id->relations model))))
  ([model e criteria]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (filter (criteria-predicate model criteria)))))

(defn referred-relations
  "Returns the relations referred by `e` in the `model`.
   Optionally takes `criteria` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))))
  ([model e criteria]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (filter (criteria-predicate model criteria)))))

(defn transitive-search
  "Returns the result of a transitive search for the `model` based on the `search-criteria`."
  ([model search-criteria]
   (transitive-search model search-criteria (:element-selection search-criteria)))
  ([model search-criteria e]
   (transitive-search model collect-in-vector search-criteria e))
  ([model collect-fn search-criteria e]
   (let [pred-fn (if-let [element-criteria (:element-selection search-criteria)]
                   (criteria-predicate model element-criteria)
                   identity)
         children-fn (cond
                       (:referred-node-selection search-criteria)
                       (fn [e] (referred-nodes model e (:referred-node-selection search-criteria)))
                       (:referring-node-selection search-criteria)
                       (fn [e] (referring-nodes model e (:referring-node-selection search-criteria)))
                       :else
                       (children-resolver model))]
     (traverse (element-resolver model) ; resolver element function
               pred-fn ; criteria based element predicate
               children-fn ; children function
               collect-fn ; collector step function
               (children-fn e)))))

(defn t-descendants
  "Returns the descendants of the `element` in the `model`."
  [model e]
  (transitive-search model {:referring-node-selection {:el :contained-in}} e))
  
(defn t-ancestors
  "Returns the ancestors of the `element` in the `model`."
  [model e]
  (transitive-search model {:referred-node-selection {:el :contained-in}} e))


;;;
;;; Accessors for specific models
;;;

;;
;; architecture model
;;
(defn dependency-nodes
  "Returns the direct dependencies of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{}
             (referrer-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

(defn dependent-nodes
  "Returns the direct dependents of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{}
             (referred-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

(defn sync-dependencies
  "Returns the transitive dependencies of the `element` in the `model`."
  [model e]
  (transitive-search model {:referred-node-selection {:el :request}} e))

(defn sync-dependents
  "Returns the transitive dependants of the `element` in the `model`."
  [model e]
  (transitive-search model {:referring-node-selection {:el :request}} e))

(defn requests-incoming
  "Returns the request relations served by service `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :request (:el %)))))

(defn requests-outgoing
  "Returns the request relations issued by client `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :request (:el %)))))

(defn responses-incoming
  "Returns the response relations handled by client `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :response (:el %)))))

(defn responses-outgoing
  "Returns the response relations served by service `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :response (:el %)))))

(defn sends-incoming
  "Returns the send relations of receiver `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :sends (:el %)))))

(defn sends-outgoing
  "Returns the send relations of sender `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :sends (:el %)))))

(defn publishes-incoming
  "Returns the publish relations of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :publish (:el %)))))

(defn publishes-outgoing
  "Returns the publish relations of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :publish (:el %)))))

(defn subscribes-incoming
  "Returns the subscribe relations of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :subscribe (:el %)))))

(defn subscribes-outgoing
  "Returns the subscribe relations of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :subscribe (:el %)))))

(defn dataflows-incoming
  "Returns the incoming dataflow relations served by service `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :dataflow (:el %)))))

(defn dataflows-outgoing
  "Returns the outgoing dataflow relations of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :dataflow (:el %)))))

(defn subscribers-of
  "Returns the subscribers of the queue or publish relation `e` in the `model`."
  [model e]
  (cond
    (= :publish (:el e))
    (->> (:to e)
         (get (:referrer-id->relations model))
         (into #{} (referrer-xf model #(= :subscribe (:el %)))))

    (and (= :container (:el e)) (= :queue (:subtype e)))
    (->> (:id e)
         (get (:referrer-id->relations model))
         (into #{} (referrer-xf model #(= :subscribe (:el %)))))))

(defn publishers-of
  "Returns the publishers of the queue or subscribe relation `e` in the `model`."
  [model e]
  (cond
    (= :subscribe (:el e))
    (->> (:to e)
         (get (:referred-id->relations model))
         (into #{} (referred-xf model #(= :publish (:el %)))))

    (and (= :container (:el e)) (= :queue (:subtype e)))
    (->> (:id e)
         (get (:referred-id->relations model))
         (into #{} (referred-xf model #(= :publish (:el %)))))))

;; TODO rename or replace with more general functions
(defn requested-nodes
  "Returns the nodes in the `model` on which `e` is dependent on via a synchronous request relation."
  [model e]
  (->> (get (:referrer-id->relations model) (:id (resolve-element model e)))
       (filter #(contains? #{:request} (:el %)))
       ;(map #(resolve-element model (:to %)))
       (map :to)))

(defn requested-nodes-resolver
  "Returns a resolver function for dependent nodes in the `model`."
  [model]
  (fn [e]
    (requested-nodes model e)))



;;
;; statemachine model
;;
(defn transitions-incoming
  "Returns the incoming transitions of state `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filter #(= :transition (:el %)))))

(defn transitions-outgoing
  "Returns the outgoing transitions of state `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (filter #(= :transition (:el %)))))

;;
;; code model
;;
(defn superclasses
  "Returns the set of direct superclasses of the class element `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :inheritance (:el %))))))

(defn subclasses
  "Returns the set of direct subclasses of the class element `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :inheritance (:el %))))))

(defn interfaces
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :implementation (:el %))))))

(defn implementations
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :implementation (:el %))))))

(defn supertypes
  "Returns the set of direct supertypes (classes or interfaces) of the class element `e` in the `model`."
  [model e]
  (->> e
      (el/id)
      (get (:referrer-id->relations model))
      (into #{} (referrer-xf model #(contains? #{:implementation :inheritance} (:el %))))))

(defn referencing
  "Returns the referenced elements of `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(contains? #{:association :aggregation :composition} (:el %))))))

(defn referenced-by
  "Returns the elements referencing element `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(contains? #{:association :aggregation :composition} (:el %))))))

;; TODO type hierarcy with cycle detection/prevention
(defn type-hierarchy
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
;; concept model
;;
(defn superordinates
  "Returns the superordinates of the concept `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :is-a (:el %))))))

(defn subordinates
  "Returns the subordinates of the concept `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :is-a (:el %))))))

(defn features
  "Returns the features of the concept `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :has (:el %))))))

(defn feature-of
  "Returns the concepts the concept `e` is a feature of in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :has (:el %))))))

;;
;; deployment model
;;
(defn deployed-on
  "Returns the architecture nodes deployed on the node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :deployed-to (:el %))))))

(defn deployed-to
  "Returns the deployment nodes the architecture node `e` is deployed to in the ``model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :deployed-to (:el %))))))

(defn links
  "Returns the deployment nodes the deployment node `e` links in the `model`"
  [model e]
  (->> e
      (el/id)
      (get (:referred-id->relations model))
      (into #{} (referred-xf model #(= :link (:el %))))))

(defn linked-by
  "Returns the deployment nodes the deployment node `e` is linked by in the `model`"
  [model e]
  (->> e
      (el/id)
      (get (:referrer-id->relations model))
      (into #{} (referrer-xf model #(= :link (:el %))))))

;;
;; use case model
;;
(defn actors
  "Returns the actors of a use case `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :uses (:el %))))))

(defn supporting-actors
  "Returns the supporting actors of a use case `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :uses (:el %))))))

(defn using
  "Returns the to side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :uses (:el %))))))

(defn used-by
  "Returns the from side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :uses (:el %))))))

(defn extensions
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :extends (:el %))))))

(defn included
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :include (:el %))))))

;;
;; organization model
;;
(defn responsible-for
  "Returns the nodes the organization unit `e` is responsible for in the `model`."
  [model e]
  (->> e
      (el/id)
      (get (:referrer-id->relations model))
      (into #{} (referrer-xf model #(= :responsible-for (:el %))))))

(defn responsibility-of
  "Returns the organization unit responsible for node `e` in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :responsible-for (:el %))))))

(defn collaborates-with
  "Returns the organization model nodes the organization unit `e` collaborates with in the `model`."
  [model e]
  (->> e
       (el/id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :collaborates-with (:el %))))))

(defn collaboration-with
  "Returns the organization model nodes the organization unit `e` has a collaboration with in the `model`. Reverse direction of collaborates with."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :collaborates-with (:el %))))))

(defn roles
  "Returns the person/user roles assigned in this node `e` in the model."
  [model e]
  (->> e
      (el/id)
      (get (:referred-id->relations model))
      (into #{} (referred-xf model #(= :role-in (:el %))))))

(defn roles-in
  "Returns the nodes the person/user role 'e' has a role in in the `model`."
  [model e]
  (->> e
      (el/id)
      (get (:referrer-id->relations model))
      (into #{} (referrer-xf model #(= :role-in (:el %))))))

(comment
  (defn aggregable-relation?
    "Returns true, if the relations `r1` and `r2` are aggregable."
    ([model r1 r2]
     (and (= (:el r1) (:el r2))
          (= (:tech r1) (:tech r2))
        ; (= (:name r1) (:name r2))
        ; (= (:desc r1) (:desc r2))
          (or (= (:from r1) (:from r2))
              (= (parent model (:from r1))
                 (parent model (:from r2))))
          (or (= (:to r1) (:to r2))
              (= (parent model (:to r1))
                 (parent model (:to r2)))))))
  )
