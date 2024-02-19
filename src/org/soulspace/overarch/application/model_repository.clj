(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.domain.view :as view]))

(defn update-acc
  "Update the accumulator `acc` of the model with the element `e`
   in the context of the parent `p` (if given)."
  [acc p e]
  (cond
    ;; nodes
    (el/model-node? e)
    (if (el/child? e p)
      ; a child node, add a parent-of relationship, too
      (let [r {:el :parent-of
               :id (el/relation-id :parent-of (:id p) (:id e))
               :from (:id p)
               :to (:id e)}]
        (assoc acc
               :nodes (conj (:nodes acc)
                            ;(dissoc e :ct)
                            e)
               :relations (conj (:relations acc)
                                r)
               :id->element (assoc (:id->element acc)
                                   (:id e) e
                                   (:id r) r)
               ; currently only one parent is supported here
               ; all parents are reachable via :parent-of relations
               :id->parent (assoc (:id->parent acc)
                                  (:id e) p)
               :referrer-id->relations (assoc (:referrer-id->relations acc)
                                              (:from r) (conj (get acc (:from r) #{}) r))
               :referred-id->relations (assoc (:referred-id->relations acc)
                                              (:to r) (conj (get acc (:to r) #{}) r))))
      ; not a child node, just add the node
      (assoc acc
             :nodes (conj (:nodes acc)
                          ;(dissoc e :ct)
                          e)
             :id->element (assoc (:id->element acc)
                                 (:id e) e)))
    ;; relations
    (el/relation? e)
    (assoc acc
           :relations (conj (:relations acc) e)
           :id->element (assoc (:id->element acc)
                               (:id e) e)
           :referrer-id->relations (assoc (:referrer-id->relations acc)
                                          (:from e) (conj (get acc (:from e) #{}) e))
           :referred-id->relations (assoc (:referred-id->relations acc)
                                          (:to e) (conj (get acc (:to e) #{}) e))           ;:referrer-id->relations (assoc ((:from e) acc) )
           )

    ;; views
    (view/view? e)
    (assoc acc
           :views (conj (:views acc) e)
           :id->element (assoc (:id->element acc)
                               (:id e) e))

    ;; references
    (el/reference? e)
    (if (el/model-node? p)
      ; reference is a child of a node, add a parent-of relationship
      (let [r {:el :parent-of
               :id (el/relation-id :parent-of (:id p) (:ref e))
               :from (:id p)
               :to (:ref e)}]
        (assoc acc
               :relations (conj (:relations acc)
                                r)
               :id->element (assoc (:id->element acc)
                                   (:id r) r)
               ; currently only one parent is supported here
               :id->parent (assoc (:id->parent acc)
                                  (:id e) p)
               :referrer-id->relations (assoc (:referrer-id->relations acc)
                                              (:from r) (conj (get acc (:from r) #{}) r))
               :referred-id->relations (assoc (:referred-id->relations acc)
                                              (:to r) (conj (get acc (:to r) #{}) r))))
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
  "Builds the working model from the input `coll` of elements."
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

(defn relations
  "Returns the set of nodes."
  ([]
   (:relations @state))
  ([model]
   (:relations model)))

(defn views
  "Returns the set of nodes."
  ([]
   (:views @state))
  ([model]
   (:views model)))

(comment
  (update-state! "models")
  
  (= (:id->parent @state) (model/traverse model/id->parent (:elements @state)))
  (= (:id->element @state) (model/traverse el/identifiable? model/id->element (:elements @state)))
  (= (:id->parent @state) (model/traverse el/model-node? model/id->parent (:elements @state)))
  (= (:referred-id->relations @state) (model/traverse el/relation? model/referred-id->rel (:elements @state)))
  (= (:referrer-id->relations @state) (model/traverse el/relation? model/referrer-id->rel (:elements @state)))
  (build-model (elements))

  ;
  :rcf)
