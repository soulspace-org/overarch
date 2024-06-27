;;;;
;;;; Functions for the definition and handling of the overarch model
;;;;
(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model.

   The loaded overarch working model is a map with these keys:
   
   :input-elements         -> the given data
   :nodes                  -> the set of nodes (incl. child nodes)
   :relations              -> the set of relations (incl. contains relations)
   :views                  -> the set of views
   :themes                 -> the set of themes
   :id->element            -> a map from id to element (nodes, relations and views)
   :id->parent             -> a map from id to parent element
   :referrer-id->relations -> a map from id to set of relations where the id is the referrer (:from)
   :referred-id->relations -> a map from id to set of relations where the id is referred (:to)
"
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]))

;;;
;;; Accessor functions
;;;
(defn model-element
  "Returns the model element with the given `id`."
  ([model id]
   ((:id->element model) id)))

(defn resolve-ref
  "Resolves the model element for the ref `r`."
  [model r]
  (if-let [e (model-element model (:ref r))]
    (merge e (dissoc r :ref))
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
     :else e)))

(defn element-resolver
  "Returns a element resolver function for the given `model`."
  [model]
  (fn [e]
    (resolve-element model e)))

;;
;; recursive traversal of the hierarchical model
;;
; TODO use traverse from element with model closure
;      and resolve-element as element fn?
(defn traverse-with-model
  "Recursively traverses the `coll` of elements and returns the elements
   (selected by the optional `pred-fn`) and transformed by the `step-fn`.

   `pred-fn`     - a predicate on the current element
   `children-fn` - a function to resolve the children of the current element
   `step-fn`     - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the `step-fn` should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator.
   
   The children-fn takes 2 args [model e], the model and the current element."
  ([model step-fn coll]
   (traverse-with-model model identity (fn [model e] (:ct e)) step-fn coll))
  ([model pred-fn step-fn coll]
   (traverse-with-model model pred-fn (fn [model e] (:ct e)) step-fn coll))
  ([model pred-fn children-fn step-fn coll]
   ; selection handled by the select function
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (resolve-element model (first coll))]
                 (if (pred-fn e)
                   (recur (trav (step-fn acc e) (children-fn model e))
                          (rest coll))
                   (recur (trav acc (children-fn model e))
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) coll))))

;;
;; Model transducer functions
;;
(defn related-xf
  "Transducer to resolve the elements in the `model` referenced by relations."
  [model]
  (comp
   (filter el/model-relation?)
   (mapcat (fn [e] [(:from e) (:to e)]))
   (map (partial resolve-id model))))

(defn referrer-xf
  "Transducer to resolve referrer elements of in the `model`.
   Optionally takes a `filter-fn` to filter the relations to take into account."
  ([model]
   (comp
    (map :to)
    (map (partial resolve-id model))))
  ([model filter-fn]
   (comp
    (filter filter-fn)
    (map :to)
    (map (partial resolve-id model)))))

(defn referred-xf
  "Transducer to resolve referred elements in the `model`.
   Optionally takes a `filter-fn` to filter the relations to take into account."
  ([model]
   (comp
    (map :from)
    (map (partial resolve-id model))))
  ([model filter-fn]
   (comp
    (filter filter-fn)
    (map :from)
    (map (partial resolve-id model)))))

(defn unresolved-refs-xf 
  "Returns a transducer to extract unresolved refs"
  [model]
  (comp (filter el/reference?)
        (map (partial resolve-ref model))
        (filter el/unresolved-ref?)))

;;
;; Accessors
;;
(defn input-elements
  "Returns the collection of elements."
  [model]
  (:input-elements model))

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

(defn parent
  "Returns the parent of the model node `e`."
  [model e]
  ((:id->parent model) (:id e)))

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

;; TODO transitive dependencies with cycle detection/prevention
(defn ->transitive-related
  "Step function to build a set of transitively related nodes (with cycle detection)."
  ([]
   [#{} ; transitive nodes
    #{} ; visited nodes
    ])
  ([acc]
   (first acc))
  ([acc e]
   ))

;; TODO transitive dependencies with cycle detection/prevention
(defn related-transitive
  "Returns the nodes that are transitively related to node `e` in the `model`."
  ([model e]
   )
  ([model f e]
   (el/traverse (element-resolver model) identity f ->transitive-related #{e})))

(defn ancestor-nodes
  "Returns the ancestor nodes of the model node `e` in the `model`."
  [model e]
  (loop [acc #{} p (parent model e)]
    (if (seq p)
      (recur (conj acc p) (parent model p))
      acc)))

(defn ancestor-node?
  "Returns true, if `c` is an ancestor of node `e` in the `model`."
  [model e c]
  (loop [p (parent model e)]
    (if (seq p)
      (if (= (:id p) (:id c))
        true
        (recur (parent model p)))
      false)))

; TODO remove, if other implementation works
#_(defn descendant-nodes
  "Returns the set of descendants of the node `e`."
  [model e]
  (when (el/model-node? (resolve-element model e))
    (traverse-with-model model el/model-node? el/tree->set (:ct e))))

(defn descendant-nodes
  "Returns the set of descendants of the node `e`."
  [model e]
  (when (el/model-node? (resolve-element model e))
    (el/traverse (element-resolver model) el/model-node? :ct el/tree->set (:ct e))))

(defn descendant-node?
  "Returns true, if `c` is a descendant of `e`."
  [model e c]
  (contains? (descendant-nodes model e) (resolve-element model c)))

(defn root-nodes
  "Returns the set of root nodes of the `elements`.
   The root nodes are not contained as descendants in any of the element nodes."
  [model elements]
  (let [descendants (->> elements
                         (filter el/model-node?)
                         (map (partial descendant-nodes model))
                         (apply set/union))]
    (set/difference (set elements) descendants)))

(defn all-nodes
  "Returns the set of all nodes, including descendants, of the `elements`."
  [model elements]
  (let [descendants (->> elements
                         (filter el/model-node?)
                         (mapcat (partial descendant-nodes model))
                         (into #{}))]
    (set/union (set elements) descendants)))

(defn all-elements
  "Returns a set of all elements."
  ([model]
   (->> (:id->element model)
        (vals)
        (map (element-resolver model))
        (into #{}))))

;;
;; architecture model
;;
(defn dependency-nodes
  "Returns the direct dependencies of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{}
             (referrer-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

(defn dependent-nodes
  "Returns the direct dependents of the architecture node `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{}
             (referred-xf model #(contains? el/architecture-dependency-relation-types (:el %))))))

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

;;
;; class model
;;
(defn superclasses
  "Returns the set of direct superclasses of the class element `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :inheritance (:el %))))))

(defn subclasses
  "Returns the set of direct subclasses of the class element `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :inheritance (:el %))))))

(defn interfaces
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :implementation (:el %))))))

(defn implementations
  "Returns the set of direct interfaces of the class element `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :implementation (:el %))))))

(defn supertypes
  "Returns the set of direct supertypes (classes or interfaces) of the class element `e` in the `model`."
  [model e]
  (->> e
      (:id)
      (get (:referrer-id->relations model))
      (into #{} (referrer-xf model #(contains? #{:implementation :inheritance} (:el %))))))

(defn referencing
  "Returns the referenced elements of `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(contains? #{:association :aggregation :composition} (:el %))))))

(defn referenced-by
  "Returns the elements referencing element `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(contains? #{:association :aggregation :composition} (:el %))))))

;; TODO type hierarcy with cycle detection/prevention
(defn type-hierarchy
  "Returns the type hierarchy of the class or interface element `e` in the model."
  [model e]
  (let []))

;;
;; concept model
;;
(defn superordinates
  "Returns the superordinates of the concept `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :is-a (:el %))))))

(defn subordinates
  "Returns the subordinates of the concept `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :is-a (:el %))))))

(defn features
  "Returns the features of the concept `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :has (:el %))))))

(defn feature-of
  "Returns the concepts the concept `e` is a feature of in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :has (:el %))))))

;;
;; deployment model
;;
(defn deployed-on
  "Returns the architecture nodes deployed on the node `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :deployed-to (:el %))))))

(defn deployed-to
  "Returns the deployment nodes the architecture node `e` is deployed to in the ``model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :deployed-to (:el %))))))

;;
;; use case model
;;
(defn actors
  "Returns the actors of a use case `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :uses (:el %))))))

(defn supporting-actors
  "Returns the supporting actors of a use case `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :uses (:el %))))))

(defn using
  "Returns the to side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :uses (:el %))))))

(defn used-by
  "Returns the from side of the relation of type :uses of node `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :uses (:el %))))))

(defn extensions
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referred-id->relations model))
       (into #{} (referred-xf model #(= :extends (:el %))))))

(defn included
  "Returns the extension use cases of a use case `e` in the `model`."
  [model e]
  (->> e
       (:id)
       (get (:referrer-id->relations model))
       (into #{} (referrer-xf model #(= :include (:el %))))))

;;;
;;; Build model
;;;
(defn parent-of-relation
  "Returns a parent-of relation for parent `p` and element `e`."
  [p-id e-id]
  {:el :contains
   :id (el/generate-relation-id :contains p-id e-id)
   :from p-id
   :to e-id
   :name "contains"
   :synthetic true})

(defn update-acc
  "Update the accumulator `acc` of the model with the element `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (cond
    ;; nodes
    (el/model-node? e)
    (if (and p (el/child? e p))
      ; TODO add syntetic ids for nodes without ids (e.g. fields, methods)
      ; a child node, add a parent-of relationship, too
      (let [r (parent-of-relation (:id p) (:id e))]
        (assoc acc
               :nodes
               (conj (:nodes acc) e)

               :relations
               (conj (:relations acc) r)

               :id->element
               (assoc (:id->element acc)
                      (:id e) e
                      (:id r) r)

               ; currently only one parent is supported here
               :id->parent
               (if-let [po ((:id->parent acc) (:id e))]
                 (println "Error: Illegal override of parent" (:id po) "with" (:id p) "for element id" (:id e))
                 (assoc (:id->parent acc) (:id e) p))

               :referrer-id->relations
               (assoc (:referrer-id->relations acc)
                      (:from r)
                      (conj (get-in acc [:referrer-id->relations (:from r)] #{}) r))

               :referred-id->relations
               (assoc (:referred-id->relations acc)
                      (:to r)
                      (conj (get-in acc [:referred-id->relations (:to r)] #{}) r))))

      ; not a child node, just add the node
      (assoc acc
             :nodes
             (conj (:nodes acc) e)

             :id->element
             (assoc (:id->element acc) (:id e) e)))

    ;; relations
    (el/model-relation? e)
    (assoc acc
           :relations
           (conj (:relations acc) e)

           :id->element
           (assoc (:id->element acc) (:id e) e)

           :referrer-id->relations
           (assoc (:referrer-id->relations acc)
                  (:from e)
                  (conj (get-in acc [:referrer-id->relations (:from e)] #{}) e))

           :referred-id->relations
           (assoc (:referred-id->relations acc)
                  (:to e)
                  (conj (get-in acc [:referred-id->relations (:to e)] #{}) e)))

    ;; views
    (el/view? e)
    (assoc acc
           :views
           (conj (:views acc) e)

           :id->element
           (assoc (:id->element acc) (:id e) e))

    ;; references
    (el/reference? e)
    (if (el/model-node? p)
      ; reference is a child of a node, add a parent-of relationship
      (let [r (parent-of-relation (:id p) (:ref e))]
        (assoc acc
               :relations
               (conj (:relations acc) r)

               :id->element
               (assoc (:id->element acc) (:id r) r)

               ; currently only one parent is supported here
               :id->parent
               (if-let [po ((:id->parent acc) (:id e))]
                 (println "Error: Illegal override of parent" (:id po) "with" (:id p) "for element ref" (:ref e))
                 (assoc (:id->parent acc) (:ref e) p))

               :referrer-id->relations
               (assoc (:referrer-id->relations acc)
                      (:from r)
                      (conj (get-in acc [:referrer-id->relations (:from r)] #{}) r))

               :referred-id->relations
               (assoc (:referred-id->relations acc)
                      (:to r)
                      (conj (get-in acc [:referred-id->relations (:to r)] #{}) r))))
      ; reference is a child of a view, leave as is
      acc)

    ;; themes
    (el/theme? e)
    (assoc acc
       :themes
       (conj (:themes acc) e)

       :id->element
       (assoc (:id->element acc) (:id e) e))

    ; unhandled element
    :else (do (println "Unhandled:" e) acc)))

(defn ->relational-model
  "Step function for the conversion of the hierachical input model into a relational model of nodes, relations and views."
  ([]
   ; initial compound accumulator with empty model and context
   [{:nodes #{}
     :relations #{}
     :views #{}
     :themes #{}
     :id->element {}
     :id->parent {}
     :referred-id->relations {}
     :referrer-id->relations {}}
    '()])
  ([[res ctx]]
   ; return result from accumulator
   (if-not (empty? ctx)
     ; not done yet because context stack is not empty
     ; pop element from stack and return accumulator with
     ; current resulting model and popped context
     [res (pop ctx)]
     res))
  ([[res ctx] e]
   ; update accumulator in step by calling update function
   ; with result, parent from context stack (if any) and
   ; the current element. Also push current element to context stack.
   (let [p (peek ctx)]
     [(update-acc res p e) (conj ctx e)])))

(defn build-model
  "Builds the working model from the input `coll` of elements."
  [coll]
  (let [relational (el/traverse ->relational-model coll)]
    (assoc relational :input-elements coll)))

;;;
;;; filtering element colletions by criteria
;;;

;; TODO add criteria for model type (architecture, etc.)
;;      select nodes of model category
;;      select relations of category if both nodes are of category
;;      (rel is in multiple categories)
(defn child-check?
  "Returns true, if the check for `e` is a child in the `model` equals the boolean value `v`."
  [model v e]
  (= v (boolean ((:id->parent model) (:id e)))))

; TODO test
(defn child?
  "Returns true, if `e` is a child of the node with id `v` in the `model`."
  [model v e]
  (contains? (:ct (resolve-id model v)) e))

(defn parent?
  "Returns true if `v` is the parent of `e`"
  [model v e]
  (= (resolve-id model v) ((:id->parent model) (:id e))))

(defn refers-check?
  "Returns true if the check for `e` as a referrer equals the boolean value `v`"
  [model v e]
  (= v (boolean ((:referrer-id->relations model) (:id e)))))

(defn referred-check?
  "Returns true if the check for `e` as referred to equals the boolean value `v`"
  [model v e]
  (= v (boolean ((:referred-id->relations model) (:id e)))))

(defn refers-to?
  "Returns true, if the node with id `v` refers to `e` in the `model`."
  [model v e]
  (contains? (into #{}
                   (map :to ((:referrer-id->relations model) (:id e))))
             v))

(defn referred-by?
  "Returns true, if `e` is referred by the node with id `v` in the `model`."
  [model v e]
  (contains? (into #{}
                   (map :from ((:referred-id->relations model) (:id e))))
             v))

(defn descendant-of?
  "Returns true, if `e` is a descendant of the node with id `v` in the `model`."
  [model v e]
  (contains? (descendant-nodes model (resolve-id model v)) e))

(defn ancestor-of?
  "Returns true, if `e` is an ancestor of the node with id `v` in the `model`."
  [model v e]
  (contains? (ancestor-nodes model (resolve-id model v)) e))

(defn criterium-predicate
  "Returns a predicate for the given `criterium`."
  [model [k v]]
  (cond
    ;;
    ;; element related
    ;;
    (= :key? k)                  (partial el/key-check? v)
    (= :key k)                   (partial el/key? v)
    (= :el k)                    (partial el/el? v)
    (= :els k)                   (partial el/els? v)
    (= :namespace k)             (partial el/namespace? v)
    (= :namespaces k)            (partial el/namespaces? v)
    (= :namespace-prefix k)      (partial el/namespace-prefix? v)
    (= :from-namespace k)        (partial el/from-namespace? v)
    (= :from-namespaces k)       (partial el/from-namespaces? v)
    (= :from-namespace-prefix k) (partial el/from-namespace-prefix? v)
    (= :to-namespace k)          (partial el/to-namespace? v)
    (= :to-namespaces k)         (partial el/to-namespaces? v)
    (= :to-namespace-prefix k)   (partial el/to-namespace-prefix? v)
    (= :id? k)                   (partial el/id-check? v)
    (= :id k)                    (partial el/id? v)
    (= :from k)                  (partial el/from? v)
    (= :to k)                    (partial el/to? v)
    (= :subtype? k)              (partial el/subtype-check? v)
    (= :subtype k)               (partial el/subtype? v)
    (= :subtypes k)              (partial el/subtypes? v)
    (= :external? k)             (partial el/external-check? v)
    (= :name? k)                 (partial el/name-check? v)
    (= :name k)                  (partial el/name? v)
    (= :name-prefix k)           (partial el/name-prefix? v)
    (= :desc? k)                 (partial el/desc-check? v)
    (= :desc k)                  (partial el/desc? v)
    (= :tech? k)                 (partial el/tech-check? v)
    (= :tech k)                  (partial el/tech? v)
    (= :techs k)                 (partial el/techs? v)
    (= :all-techs k)             (partial el/all-techs? v)
    (= :tags? k)                 (partial el/tags-check? v)
    (= :tag k)                   (partial el/tag? v)
    (= :tags k)                  (partial el/tags? v)
    (= :all-tags k)              (partial el/all-tags? v)

    ;; model related
    (= :refers? k)               (partial refers-check? model v)
    (= :referred? k)             (partial referred-check? model v)
    (= :refers-to k)             (partial refers-to? model v)
    (= :referred-by k)           (partial referred-by? model v)
    (= :child? k)                (partial child-check? model v)
    (= :child-of k)              (partial child? model v)
    (= :descendant-of k)         (partial descendant-of? model v)
    (= :children? k)             (partial el/parent-check? v) ; deprecated
    (= :parent? k)               (partial el/parent-check? v)
    (= :parent-of k)             (partial parent model v)
    (= :ancestor-of k)           (partial ancestor-of? model v) 

    :else
    (do (println "unknown criterium" (name k))
        (fn [_] false))))

(defn criteria-predicate
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

(defn criteria-predicates
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
               (conj predicates (criteria-predicate model (first remaining))))
        (apply some-fn predicates)))
    ; simple criteria map
    (criteria-predicate model criteria)))

(defn filter-xf
  "Returns a filter transducer for the given `criteria`."
  [model criteria]
  (let [filter-predicates (criteria-predicates model criteria)]
    ; compose the filtering functions and create a filter transducer
    (filter filter-predicates)))

(comment
  (re-pattern "\\d+")
  (re-matches #"(?i)hello.*" "Hello World!")
  (type #"(?i)hello.*")

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
