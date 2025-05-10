(ns org.soulspace.overarch.adapter.reader.input-model-reader
  "Functions to read and build the working model from overarch input models.
   
The built model is a map with the following keys

| Key                     | Description 
|-------------------------|-------------
| **Relation keys**       | 
| :nodes                  | the set of all nodes
| :relations              | the set of all relations
| :views                  | the set of views
| :themes                 | the set of themes
| **Index keys**          | 
| :id->element            | a map from id to element (nodes, relations and views)
| :id->parent-id          | a map from id to parent node id
| :id->children           | a map from id to a vector of contained nodes (deprecated)
| :referrer-id->relations | a map from id to set of relations where the id is the referrer (:from)
| :referred-id->relations | a map from id to set of relations where the id is referred (:to)
| **Problem keys**        | 
| :problems               | the set of problems found during model building

The input model is transformed by
* computing missing ids, if possible
* converting the node hierarchies to :contained-in relations
   "
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.adapter.reader.model-reader :as model-reader]
            [clojure.string :as str]))

;;
;; Input checks
;;
(def problem->severity
  "Map of problem to severity."
  {:missing-id :error
   :duplicate-id :error
   :parent-override :error
   :unresolved-ref-in-relation :error
   :unresolved-ref-in-view :error})
  
(defn check-missing-id
  ([acc e]
   (check-missing-id acc nil e))
  ([acc p e]
   (when (not (or (:id e) (:ref e)))
     {:type :missing-id
      :element e
      :parent p})))

(defn check-duplicate-id
  ([acc e]
   (check-duplicate-id acc nil e))
  ([acc p e]
   (when (and (:id e) (get-in acc [:id->element (:id e)]))
     {:type :duplicate-id
      :element e
      :parent p})))

(defn check-parent-override
  ([acc e]
   ; contained in relations
   (when (and (el/model-relation? e)
              (= :contained-in (:el e))
              (get-in acc [:id->parent-id (:from e)]))
     {:type :parent-override
      :element e
      :parent (:to e)}))
  ([acc p e]
   ; nodes and refs
   (when (and p (or (get-in acc [:id->parent-id (:id e)])
                    (get-in acc [:id->parent-id (:ref e)])))
     {:type :parent-override
      :element e
      :parent p})))

(def check-element
  (juxt check-missing-id check-duplicate-id check-parent-override))

;;;
;;; Merging (TODO)
;;;
(defn main-node?
  "Returns true if the `node` is the main node."
  [node]
  (or (= (el/internal? node) true)
      (seq (:ct node))))

(defn merge-nodes
  "Returns a node that is the merge of `node` and `new-node`."
  ([] {})
  ([node] node)
  ([node new-node]
   (if (main-node? node)
     (merge new-node node)
     (merge node new-node))))

(defn merge-relations
  "Returns a relation that is the merge of `relation` and `new-relation`."
  ([] {})
  ([relation] relation)
  ([relation new-relation]
   (merge relation new-relation)))

(defn merge-views
  "Returns a view that is the merge of `view` and `new-view`."
  ([] {})
  ([view] view)
  ([view new-view]
   (let [new-spec (merge (:spec view) (:spec new-view))
         new-ct (concat (:ct view) (:ct new-view))]
     (assoc view :spec new-spec :ct new-ct))))

;;;
;;; Building
;;;
(defn input-child?
  "Returns true, if element `e` is a child of model element `p` in the input model."
  [e p]
  (boolean (and (seq e)
                (seq p)
                (el/identifiable-element? p)
                (el/model-element? p)
                ; working on the input, so use :ct here
                (contains? (set (:ct p)) e))))

;; TODO rename to prepare-*
;; TODO convert :tech to ordered-set with el/technology-set
;; TODO check for other transformations from input to working elements
(defn identified-node
  "Returns the node `e` with the id set. Generates the id from `e`s name and the parent `p`s id."
  [e p]
  (if (or (:id e) (:ref e))
    e
    (assoc e :id (el/generate-node-id e p))))

(defn identified-relation
  "Returns the relation `e` with the id set. Generates the id from `e`s name and the parent `p`s id."
  [e]
  (if (:id e)
    e
    (assoc e :id (el/generate-relation-id e))))

; TODO hoist spec entries to view
(defn prepare-view
  "Returns the prepered view `e` with the id set. Generates the id from `e`s name."
  [e]
  #_(if (:id e)
    e
    (assoc e :id (el/generate-view-id e p)))
  )

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

;; TODO derive :index from vector position, if :ct contains vector?
(defn add-node
  "Update the accumulator `acc` of the model with the node `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (let [identified-e (dissoc (identified-node e p) :ct)
        problems (remove nil? (check-element acc p identified-e))]
    (if (and p (input-child? e p))
            ; a child node, add a contained in relationship, too
            ; add syntetic ids for nodes without ids (e.g. fields, methods)
      (let [c-rel (contained-in-relation (:id p) (:id identified-e))]
        (assoc acc
               :nodes

               (conj (:nodes acc) identified-e)

               :id->element
               (if-let [el (get-in acc [:id->element (:id identified-e)])]
                 (println "Error: Duplicate element id" (:id identified-e) "for" e "and" el)
                 (assoc (:id->element acc)
                        (:id identified-e) identified-e
                        (:id c-rel) c-rel))

               ; currently only one parent is supported here
               :id->parent-id
               (if-let [po (get-in acc [:id->parent-id (:id identified-e)])]
                 (println "Error: Illegal override of parent" po "with" (:id p) "for element id" (:id identified-e))
                 (assoc (:id->parent-id acc) (:id identified-e) (:id p)))

               :id->children
               (assoc (:id->children acc)
                      (:id p)
                      (conj (get-in acc [:id->children (:id p)] []) identified-e))

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
                      (conj (get-in acc [:referred-id->relations (:to c-rel)] #{}) c-rel))

               :build-problems
               (concat (:build-problems acc) problems)))

            ; not a child node, just add the node
      (assoc acc
             :nodes
             (conj (:nodes acc) identified-e)

             :id->element
             (if-let [el (get-in acc [:id->element (:id identified-e)])]
               (println "Error: Duplicate element id" (:id identified-e) "for" e "and" el)
               (assoc (:id->element acc) (:id identified-e) identified-e))

             :build-problems
             (concat (:build-problems acc) problems)))))

(defn add-reference
  "Update the accumulator `acc` of the model with the reference `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (if (el/model-node? p)
    ; reference is a child of a node, add a contained-in relationship for the referred node 
    (let [c-rel (contained-in-relation (:id p) (:ref e))
          problems (remove nil? (check-element acc p e))]
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
             (if-let [po (get-in acc [:id->parent-id (:ref e)])]
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
                    (conj (get-in acc [:referred-id->relations (:to c-rel)] #{}) c-rel))
             :build-problems
             (concat (:build-problems acc) problems)))
    ; else this reference is a child of a view, leave acc as is
    acc))

(defn add-relation
  "Update the accumulator `acc` of the model with the relation `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (let [identified-rel (identified-relation e)
        problems (remove nil? (check-element acc identified-rel))]
    (assoc acc
           :relations
           (conj (:relations acc) identified-rel)

           :id->element
           (assoc (:id->element acc) (:id identified-rel) identified-rel)

           :id->parent-id
           (if (= :contained-in (:el identified-rel))
             ; contained-in relation, add the relation and update the :id->parent-id map
             (assoc (:id->parent-id acc) (:from identified-rel) (:to identified-rel))
             ; a normal relation, no changes to :id->parent-id map
             (:id->parent-id acc))

           :referrer-id->relations
           (assoc (:referrer-id->relations acc)
                  (:from identified-rel)
                  (conj (get-in acc [:referrer-id->relations (:from identified-rel)] #{}) identified-rel))

           :referred-id->relations
           (assoc (:referred-id->relations acc)
                  (:to identified-rel)
                  (conj (get-in acc [:referred-id->relations (:to identified-rel)] #{}) identified-rel))

           :build-problems
           (concat (:build-problems acc) problems))))

(defn add-theme
  "Update the accumulator `acc` of the model with the view `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (let [problems (remove nil? (check-element acc e))]
    (assoc acc
           :themes
           (conj (:themes acc) e)

           :id->element
           (assoc (:id->element acc) (:id e) e)

           :build-problems
           (concat (:build-problems acc) problems))))

; TODO update views in model building by promoting keys from spec to views 
(defn add-view
  "Update the accumulator `acc` of the model with the view `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  ;; views
  (let [problems (remove nil? (check-element acc e))]
    (assoc acc
           :views
           (conj (:views acc) e)

           :id->element
           (assoc (:id->element acc) (:id e) e)

           :build-problems
           (concat (:build-problems acc) problems))))

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
  "Step function for the conversion of the hierachical input model into a relational model of nodes, relations and views.
   `res` accumulates the result and the context `ctx` acts as the stack of the recusive calls."
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
     :referrer-id->relations {}
     :build-problems #{}}
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

(defmethod model-reader/build-model :overarch-input
  [options coll]
   ; TODO drop :ct key?
   ; if scope option is set, use scope-fn as element-fn
   (if-let [scope (:scope options)]
     (model/traverse (scope-fn scope) identity :ct ->relational-model coll)
     (model/traverse ->relational-model coll)))
