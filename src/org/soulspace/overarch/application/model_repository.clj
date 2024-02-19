(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.domain.view :as view]))

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
       (model/build-registry)
       (reset! state)))

(defn elements
  "Returns the set of elements."
  []
  (:elements @state))

(defn update-acc
  "Update the accumulator `acc` of the hierarchical model with the element `e`."
  [acc p e]
    ; TODO fill lookup maps
    ; :id->element
    ; :id->parent
    ; :referred-id->relations
    ; :referrer-id->relations
  (cond
    (el/model-node? e)
    (if (el/child? e p)
      (let [r {:el :parent-of
               :id (el/relation-id :parent-of (:id p) (:id e))
               :from (:id p)
               :to (:id e)}]
        (assoc acc
               :nodes (conj (:nodes acc)
                            ;(dissoc e :ct)
                            e)
               :relations (conj (:relations acc)
                                r)))
      (assoc acc :nodes (conj (:nodes acc)
                              ;(dissoc e :ct)
                              e)))

    (el/relation? e)
    (assoc acc :relations (conj (:relations acc) e))

    (view/view? e)
    (assoc acc :views (conj (:views acc) e))

    (el/reference? e)
    (if (el/model-node? p)
      ; reference is a child of a node, create relation
      (let [r {:el :parent-of
               :id (el/relation-id :parent-of (:id p) (:ref e))
               :from (:id p)
               :to (:ref e)}]
        (assoc acc
               :relations (conj (:relations acc)
                                r)))
      ; reference is a child of a view, leave as is
      acc)
    
    ; unhandled element
    :else (do (println "Unhandled:" e) acc)))

(defn ->relational-model
  "Step function for the conversion of the hierachical input model into a relational model of nodes, relations and views."
  ([] [{:type :relational
        :nodes #{}
        :relations #{}
        :views #{} 
        :id->element {}
        :id->parent {}
        :referred-id->relations {}
        :referrer-id->relations {}
        } '()])
  ([[res ctx]]
   (if-not (empty? ctx)
     [res (pop ctx)]
     res))
  ([[res ctx] e]
   (let [p (peek ctx)]
     [(update-acc res p e) (conj ctx e)])))

(defn build-relational-model
  "Builds a relational working model from the hierarchical inpur model."
  [coll]
  (let [relational (model/traverse ->relational-model coll)]
    (assoc relational :elements coll)))

(comment
  (update-state! "models")
  
  (= (:parents @state) (model/traverse model/id->parent (:elements @state)))
  (= (:registry @state) (model/traverse el/identifiable? model/id->element (:elements @state)))
  (= (:parents @state) (model/traverse el/model-node? model/id->parent (:elements @state)))
  (= (:referred @state) (model/traverse el/relation? model/referred-id->rel (:elements @state)))
  (= (:referrer @state) (model/traverse el/relation? model/referrer-id->rel (:elements @state)))
  (build-relational-model (elements))

  ;
  :rcf)
