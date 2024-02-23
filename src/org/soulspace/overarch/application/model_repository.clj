(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.domain.view :as view]))

(defn parent-of-relation
  "Returns a parent-of relation for parent `p` and element `e`."
  [p-id e-id]
  {:el :contains
   :id (el/generate-relation-id :contains p-id e-id)
   :from p-id
   :to e-id
   :name "contains"})

(defn update-acc
  "Update the accumulator `acc` of the model with the element `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (cond
    ;; nodes
    ;; TODO add syntetic ids for nodes without ids (e.g. fields, methods)
    (el/model-node? e)
    (if (el/child? e p)
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
               ; all parents are reachable via :contains relations
               :id->parent
               (assoc (:id->parent acc) (:id e) p)

               :referrer-id->relations
               (assoc (:referrer-id->relations acc)
                      (:from r)
                      (conj (get-in acc [:referred-id->relations (:from r)] #{}) r))

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
    (view/view? e)
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
               (assoc (:id->parent acc) (:id e) p)

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

    ; unhandled element
    :else (do (println "Unhandled:" e) acc)))

(defn ->relational-model
  "Step function for the conversion of the hierachical input model into a relational model of nodes, relations and views."
  ([]
   ; initial compound accumulator with empty model and context
   [{:nodes #{}
     :relations #{}
     :views #{}
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
  "Builds the working model from the input `coll` of elements.
   
   The map contains the following keys:

   :elements               -> the given data
   :nodes                  -> the set of nodes (incl. child nodes)
   :relations              -> the set of relations (incl. contains relations)
   :views                  -> the set of views
   :id->element            -> a map from id to element (nodes, relations and views)
   :id->parent             -> a map from id to parent element
   :referrer-id->relations -> a map from id to set of relations where the id is the referrer (:from)
   :referred-id->relations -> a map from id to set of relations where the id is referred (:to)
   "
  [coll]
  (let [relational (model/traverse ->relational-model coll)]
    (assoc relational :elements coll)))

;;;
;;; Repository functions
;;;

(defn repo-type
  "Returns the repository type."
  ([rtype]
   rtype)
  ([rtype _]
   rtype))

(defmulti read-models
  "Reads the models with the repository of type `rtype` from all locations of the given `path`."
  repo-type)

;;
;; Application state
;;
; Application state is not needed for the overarch CLI, but maybe helpful for other clients
(def state (atom {}))

(defn update-state!
  "Updates the state with the registered data read from `path`."
  [path]
  (->> path
       ; TODO don't hardcode repo type
       (read-models :file)
       (spec/check)
       (build-model)
       (reset! state)))


(defn elements
  "Returns the set of elements."
  ([]
   (:elements @state))
  ([model]
   (:elements model)))

(defn nodes
  "Returns the set of nodes."
  ([]
   (:nodes @state))
  ([model]
   (:nodes model)))

(defn node-by-id
  "Returns the node with the given `id`."
  ([id]
   (node-by-id @state id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/model-node? el)
       el))))

(defn relations
  "Returns the set of relations."
  ([]
   (relations @state))
  ([model]
   (:relations model)))

(defn relation-by-id
  "Returns the relation with the given `id`."
  ([id]
   (relation-by-id @state id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/model-relation? el)
       el))))

(defn views
  "Returns the set of views."
  ([]
   (:views @state))
  ([model]
   (:views model)))

(defn view-by-id
  "Returns the view with the given `id`."
  ([id]
   (view-by-id @state id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (view/view? el)
       el))))

(comment
  (update-state! "models")

  (= (:id->parent @state) (model/traverse model/id->parent (:elements @state)))
  (= (:id->element @state) (model/traverse el/identifiable? model/id->element (:elements @state)))
  (= (:id->parent @state) (model/traverse el/model-node? model/id->parent (:elements @state)))
  (= (:referred-id->relations @state) (model/traverse el/model-relation? model/referred-id->rel (:elements @state)))
  (= (:referrer-id->relations @state) (model/traverse el/model-relation? model/referrer-id->rel (:elements @state)))
  (build-model (elements))

  (view/specified-model-nodes @state (view-by-id :banking/system-context-view))
  (view/specified-relations @state (view-by-id :banking/system-context-view))
  (view/specified-elements @state (view-by-id :test/banking-container-view-related))
  (view/specified-elements @state (view-by-id :test/banking-container-view-relations))

  (model/related-nodes @state (view/referenced-relations @state (view-by-id :test/banking-container-view-related)))
  (model/relations-of-nodes @state (view/referenced-model-nodes @state (view-by-id :test/banking-container-view-relations)))

  (view/elements-in-view @state (view-by-id :banking/container-view))
  (view/technologies-in-view @state (view-by-id :banking/container-view))

  ;
  :rcf)
