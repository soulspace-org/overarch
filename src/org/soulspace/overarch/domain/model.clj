;;;;
;;;; Functions for the definition and handling of the overarch model
;;;;
(ns org.soulspace.overarch.domain.model
  "Functions for the definition and handling of the overarch model.

   The loaded overarch working model is a map with these keys:
   
   :nodes                  -> the set of all nodes (incl. child nodes)
   :relations              -> the set of all relations (incl. contained-in relations)
   :views                  -> the set of views
   :themes                 -> the set of themes
   :id->element            -> a map from id to element (nodes, relations and views)
   :id->parent-id          -> a map from id to parent node id
   :id->children           -> a map from id to a vector of contained nodes
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
     (el/element? e) e
     :else nil)))

(defn element-resolver
  "Returns an element resolver function for the given `model`."
  [model]
  (fn [e]
    (resolve-element model e)))

(defn children
  "Returns the children of the model node `e`."
  [model e]
  (->> e
       (el/id)
       (get (:referred-id->relations model))
       (filterv #(= :contained-in (:el %)))
       (mapv (comp (element-resolver model) :from))))
  
(defn children-resolver
  "Returns a children resolver function for the given `model`."
  [model]
  (fn [e]
    (children model e)))

;;
;; recursive traversal of the hierarchical data
;;
;; TODO just one traverse function with the actual algorithm
;;      maybe some convenience fns for input/model traversion
(defn traverse
  "Recursively traverses the `coll` of elements and returns the elements
   (selected by the optional `pred-fn`) and transformed by the `step-fn`.

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
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (element-fn (first coll))]
                 (if (pred-fn e)
                   (recur (trav (step-fn acc e) (children-fn e))
                          (rest coll))
                   (recur (trav acc (children-fn e))
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) coll))))

(defn traverse-cycle
  "Recursively traverses the `coll` of elements and returns the elements
   (selected by the optional `pred-fn`) and transformed by the `step-fn`.

   `element-fn`  - a resolver function for an element, defaults to `identity`
   `pred-fn`     - a predicate on the current element, defaults to `identity`
   `children-fn` - a function to resolve the children of the current element
   `step-fn`     - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the `step-fn` should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator."
  ([step-fn coll]
   (traverse-cycle identity identity :ct step-fn coll))
  ([pred-fn step-fn coll]
   (traverse-cycle identity pred-fn :ct step-fn coll))
  ([pred-fn children-fn step-fn coll]
   (traverse-cycle identity pred-fn children-fn step-fn coll))
  ([element-fn pred-fn children-fn step-fn coll]
   (letfn [(trav [acc visited coll]
             (if (seq coll)
               (let [e (element-fn (first coll))
                     v (conj visited e)]
                 (if (and (pred-fn e) (not (contains? visited e)))
                   (recur (trav (step-fn acc e) v (children-fn e))
                          v
                          (rest coll))
                   (recur (trav acc v (children-fn e))
                          v
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) #{} coll))))

;;
;; recursive traversal of the hierarchical model
;;
; TODO use traverse from element with model closure
;      and resolve-element as element fn?
(defn traverse-with-model
  "Recursively traverses the `coll` of elements and returns the elements
   (selected by the optional `pred-fn`) and transformed by the `step-fn`.

   `pred-fn`     - a predicate on the current element, defaults to `identity`
   `children-fn` - a function to resolve the children of the current element
   `step-fn`     - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the `step-fn` should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator.
   
   The children-fn takes 2 args [model e], the model and the current element."
  ([model step-fn coll]
   (traverse-with-model model identity (fn [model e] (children model e)) step-fn coll))
  ([model pred-fn step-fn coll]
   (traverse-with-model model pred-fn (fn [model e] (children model e)) step-fn coll))
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

(defn traverse-model
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
   (traverse-model model identity (fn [model e] (children model e)) step-fn coll))
  ([model pred-fn step-fn coll]
   (traverse-model model pred-fn (fn [model e] (children model e)) step-fn coll))
  ([model pred-fn children-fn step-fn coll]
   ; selection handled by the select function
   (letfn [(trav [acc visited coll]
             (if (seq coll)
               (let [e (resolve-element model (first coll))
                     v (conj visited e)]
                 (if (and (pred-fn e) (not (contains? visited e)))
                   (recur (trav (step-fn acc e) v (children-fn model e)) 
                          v
                          (rest coll))
                   (recur (trav acc v (children-fn model e)) 
                          v
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) #{} coll))))

;;;
;;; Build model
;;;
;; TODOs:
;;  * remove :ct key in model nodes
;;  * set :external from scope, if given
;;  * drop input model from model
(defn input-child?
  "Returns true, if element `e` is a child of model element `p` in the input model."
  [e p]
  (boolean (and (seq e)
                (seq p)
                (el/identifiable-element? p)
                (el/model-element? p)
                ; working on the input, so use :ct here
                (contains? (set (:ct p)) e))))

(defn identified-node
  "Returns the node `e` with the id set. Generates the id from `e`s name and the parent `p`s id."
  [e p]
  (if (:id e)
    e
    (assoc e :id (el/generate-node-id e p))))

(defn contained-in-relation
  "Returns a contained-in relation for parent `p` and element `e`."
  [p-id e-id]
  {:el :contained-in
   :id (el/generate-relation-id :contained-in e-id p-id)
   :from e-id
   :to p-id
   :name "contained-in"
   :synthetic true})

(defn scope-fn
  [scope]
  (fn [e]
    (if (str/starts-with? (el/element-namespace e) scope)
      (assoc e :external false)
      (assoc e :external true))))

(defn add-node
  "Update the accumulator `acc` of the model with the node `e`
   in the context of the parent `p` (if given)."
  [acc p e] 
  (if (and p (input-child? e p))
    ; a child node, add a contained in relationship, too
    ; add syntetic ids for nodes without ids (e.g. fields, methods)
    (let [e (identified-node e p)
          c-rel (contained-in-relation (:id p) (:id e))]
      (assoc acc
             :nodes
             (conj (:nodes acc) e)

             :id->element
             (assoc (:id->element acc)
                    (:id e) e
                    (:id c-rel) c-rel)

             ; currently only one parent is supported here
             :id->parent-id
             (if-let [po ((:id->parent-id acc) (:id e))]
               (println "Error: Illegal override of parent" po "with" (:id p) "for element id" (:id e))
               (assoc (:id->parent-id acc) (:id e) (:id p)))

             :id->children
             (assoc (:id->children acc)
                    (:id p)
                    (conj (get-in acc [:id->children (:id p)] []) e))

             :relations
             (conj (:relations acc)
                   c-rel)

             :referrer-id->relations
             (assoc (:referrer-id->relations acc)
                    (:from c-rel)
                    (conj (get-in acc [:referrer-id->relations (:from c-rel)] #{}) c-rel))

             :referred-id->relations
             (assoc (:referred-id->relations acc)
                    (:to c-rel)
                    (conj (get-in acc [:referred-id->relations (:to c-rel)] #{}) c-rel))))

    ; not a child node, just add the node
    (assoc acc
           :nodes
           (conj (:nodes acc) e)

           :id->element
           (assoc (:id->element acc) (:id e) e))))

(defn add-reference
  "Update the accumulator `acc` of the model with the reference `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (if (el/model-node? p)
    ; reference is a child of a node, add a contained-in relationship
    (let [c-rel (contained-in-relation (:id p) (:ref e))]
      (assoc acc
             :relations
             (conj (:relations acc)
                   c-rel)

             :id->element
             (assoc (:id->element acc)
                    (:id e) e
                    (:id c-rel) c-rel)

             ; currently only one parent is supported here
             :id->parent-id
             (if-let [po ((:id->parent-id acc) (:ref e))]
               (println "Error: Illegal override of parent" po "with" (:id p) "for element id" (:ref e))
               (assoc (:id->parent-id acc) (:ref e) (:id p)))
             
             ; TODO currently adding the ref here, should add the (transformed) element
             ;      therefore a lookup by id is neccessary for the input elements?!?
             :id->children
             (assoc (:id->children acc)
                    (:id p)
                    (conj (get-in acc [:id->children (:id p)] []) e))

             :referrer-id->relations
             (assoc (:referrer-id->relations acc)
                    (:from c-rel)
                    (conj (get-in acc [:referrer-id->relations (:from c-rel)] #{}) c-rel))

             :referred-id->relations
             (assoc (:referred-id->relations acc)
                    (:to c-rel)
                    (conj (get-in acc [:referred-id->relations (:to c-rel)] #{}) c-rel))))
    ; else this reference is a child of a view, leave acc as is
    acc))

(defn add-relation
  "Update the accumulator `acc` of the model with the relation `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (assoc acc
         :relations
         (conj (:relations acc) e)

         :id->element
         (assoc (:id->element acc) (:id e) e)

         :id->parent-id
         (if (= :contained-in (:el e))
             ; contained-in relation, add the relation and update the :id->parent-id map
           (if-let [po ((:id->parent-id acc) (:from e))]
             (println "Error: Illegal override of parent" (:id po) "with" (:to e) "for element id" (:from e))
             (assoc (:id->parent-id acc) (:from e) (:to e)))
             ; a normal relation, no changes to :id->parent-id map
           (:id->parent-id acc))

         :referrer-id->relations
         (assoc (:referrer-id->relations acc)
                (:from e)
                (conj (get-in acc [:referrer-id->relations (:from e)] #{}) e))

         :referred-id->relations
         (assoc (:referred-id->relations acc)
                (:to e)
                (conj (get-in acc [:referred-id->relations (:to e)] #{}) e))))

(defn add-theme
  "Update the accumulator `acc` of the model with the view `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (assoc acc
         :themes
         (conj (:themes acc) e)

         :id->element
         (assoc (:id->element acc) (:id e) e)))

(defn add-view
  "Update the accumulator `acc` of the model with the view `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  ;; views
  (assoc acc
         :views
         (conj (:views acc) e)

         :id->element
         (assoc (:id->element acc) (:id e) e)))

(defn update-acc
  "Update the accumulator `acc` of the model with the element `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (cond
    ;; nodes
    (el/model-node? e)
    (add-node acc p e)

    ;; relations
    (el/model-relation? e)
    (add-relation acc p e)

    ;; views
    (el/view? e)
    (add-view acc p e)

    ;; references
    (el/reference? e)
    (add-reference acc p e)

    ;; themes
    (el/theme? e)
    (add-theme acc p e)

    ; unhandled element
    :else (do (println "Unhandled:" e) acc)))

(defn ->relational-model
  "Step function for the conversion of the hierachical input model into a relational model of nodes, relations and views."
  ([]
   ; initial compound accumulator with empty model and context as a stack list
   [{:nodes #{}
     :relations #{}
     :views #{}
     :themes #{}
     :id->element {}
     :id->parent-id {}
     :id->children {}
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
  ([coll]
   (build-model {} coll))
  ([options coll]
   ; TODO if scope option is set, use scope-fn as element-fn
   ; TODO drop :ct key?
   (if-let [scope (:scope options)]
     (traverse (scope-fn scope) identity :ct ->relational-model coll)
     (traverse ->relational-model coll))))

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
   (traverse (element-resolver model) identity f ->transitive-related #{e})))

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
  (if-let [p-id ((:id->parent-id model) (:id e))]
    (resolve-element model p-id)
    nil))

+(defn from-name
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
    (traverse (element-resolver model) el/model-node? (children-resolver model) el/tree->set (children model e)))) ; (:ct e)

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

(defn referring-nodes
  "Returns the nodes referring to `e` in the `model`.
   Optionally takes a set of relation types `rels` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (into #{} (referred-xf model))))
  ([model e rels]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (into #{} (referred-xf model #(contains? rels (:el %)))))))

(defn referred-nodes
  "Returns the nodes referred by `e` in the `model`.
   Optionally takes a set of relation types `rels` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (into #{} (referrer-xf model))))
  ([model e rels]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (into #{} (referrer-xf model #(contains? rels (:el %)))))))

(defn referring-relations
  "Returns the relations referring to `e` in the `model`.
   Optionally takes a set of relation types `rels` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referred-id->relations model))))
  ([model e rels]
   (->> e
        (el/id)
        (get (:referred-id->relations model))
        (filter #(contains? rels (:el %))))))

(defn referred-relations
  "Returns the relations referred by `e` in the `model`.
   Optionally takes a set of relation types `rels` to filter for."
  ([model e]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))))
  ([model e rels]
   (->> e
        (el/id)
        (get (:referrer-id->relations model))
        (filter #(contains? rels (:el %))))))

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

;;;
;;; filtering element colletions by criteria
;;;

;; TODO add criteria for model type (architecture, etc.)
;;      select nodes of model category
;;      select relations of category if both nodes are of category
;;      (rel is in multiple categories)
(defn external-check?
  "Returns true if the check for external on `e` equals the boolean value `v`"
  [model v e]
  (= v (if (el/model-node? e)
         (boolean (el/external? e))
         (or (el/external? (resolve-element model (:from e)))
             (el/external? (resolve-element model (:to e)))))))

(defn child-check?
  "Returns true, if the check for `e` is a child in the `model` equals the boolean value `v`."
  [model v e]
  (= v (boolean ((:id->parent-id model) (:id e)))))

; TODO test
(defn child?
  "Returns true, if `e` is a child of the node with id `v` in the `model`."
  [model v e]
  (contains? (children model (resolve-id model v)) e))

(defn parent-check?
  "Returns true if the check for children of `e` equals the boolean value `v`"
  [model v e]
  (= v (empty? (children model e))))

(defn parent?
  "Returns true if `v` is the parent of `e`"
  [model v e]
  (= v ((:id->parent-id model) (:id e))))

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

    ;; TODO add generic handling of operators [?, !]
    (= :key? k)                    (partial el/key-check? v)
    (= :key k)                     (partial el/key? v)
    (= :el k)                      (partial el/el? v)
    (= :els k)                     (partial el/els? v)
    (= :!els k)                    (complement (partial el/els? v))
    (= :namespace k)               (partial el/namespace? v)
    (= :!namespace k)              (complement (partial el/namespace? v))
    (= :namespaces k)              (partial el/namespaces? v)
    (= :namespace-prefix k)        (partial el/namespace-prefix? v)
    (= :namespace-prefixes k)      (partial el/namespace-prefixes? v)
    (= :from-namespace k)          (partial el/from-namespace? v)
    (= :from-namespaces k)         (partial el/from-namespaces? v)
    (= :from-namespace-prefix k)   (partial el/from-namespace-prefix? v)
    (= :from-namespace-prefixes k) (partial el/from-namespace-prefixes? v)
    (= :to-namespace k)            (partial el/to-namespace? v)
    (= :to-namespaces k)           (partial el/to-namespaces? v)
    (= :to-namespace-prefix k)     (partial el/to-namespace-prefix? v)
    (= :to-namespace-prefixes k)   (partial el/to-namespace-prefixes? v)
    (= :id? k)                     (partial el/id-check? v)
    (= :id k)                      (partial el/id? v)
    (= :!id k)                     (complement (partial el/id? v))
    (= :from k)                    (partial el/from? v)
    (= :!from k)                   (complement (partial el/from? v))
    (= :to k)                      (partial el/to? v)
    (= :!to k)                     (complement (partial el/to? v))
    (= :subtype? k)                (partial el/subtype-check? v)
    (= :subtype k)                 (partial el/subtype? v)
    (= :subtypes k)                (partial el/subtypes? v)
    (= :!subtypes k)               (complement (partial el/subtypes? v))
    (= :maturity? k)               (partial el/maturity-check? v)
    (= :maturity k)                (partial el/maturity? v)
    (= :maturities k)              (partial el/maturities? v)
    (= :!maturities k)             (complement (partial el/maturities? v))
    (= :external? k)               (partial external-check? model v)
    (= :synthetic? k)              (partial el/synthetic-check? v)
    (= :name? k)                   (partial el/name-check? v)
    (= :name k)                    (partial el/name? v)
    (= :name-prefix k)             (partial el/name-prefix? v)
    (= :desc? k)                   (partial el/desc-check? v)
    (= :desc k)                    (partial el/desc? v)
    (= :doc? k)                    (partial el/doc-check? v)
    (= :doc k)                     (partial el/doc? v)
    (= :tech? k)                   (partial el/tech-check? v)
    (= :tech k)                    (partial el/tech? v)
    (= :techs k)                   (partial el/techs? v)
    (= :!techs k)                  (complement (partial el/techs? v))
    (= :all-techs k)               (partial el/all-techs? v)
    (= :tags? k)                   (partial el/tags-check? v)
    (= :tag k)                     (partial el/tag? v)
    (= :tags k)                    (partial el/tags? v)
    (= :!tags k)                   (complement (partial el/tags? v)) ; el/all-tags?
    (= :all-tags k)                (partial el/all-tags? v)

    ;; model related
    (= :refers? k)                 (partial refers-check? model v)
    (= :referred? k)               (partial referred-check? model v)
    (= :refers-to k)               (partial refers-to? model v)
    (= :!refers-to k)              (complement (partial refers-to? model v))
    (= :referred-by k)             (partial referred-by? model v)
    (= :!referred-by k)            (complement (partial referred-by? model v))
    (= :child? k)                  (partial child-check? model v)
    (= :child-of k)                (partial child? model v)
    (= :!child-of k)               (complement (partial child? model v))
    (= :descendant-of k)           (partial descendant-of? model v)
    (= :!descendant-of k)          (complement (partial descendant-of? model v))
    (= :children? k)               (partial parent-check? model v) ; deprecate
    (= :parent? k)                 (partial parent-check? model v)
    (= :parent-of k)               (partial parent? model v)
    (= :!parent-of k)              (complement (partial parent? model v))
    (= :ancestor-of k)             (partial ancestor-of? model v)
    (= :!ancestor-of k)            (complement (partial ancestor-of? model v))

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
