(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.spec :as spec]
            [org.soulspace.overarch.domain.view :as view]))

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
       (model/build-model)
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
     (when (el/view? el)
       el))))

(comment
  (update-state! "models")

  (= (:id->parent @state) (el/traverse model/id->parent (:elements @state)))
  (= (:id->element @state) (el/traverse el/identifiable? model/id->element (:elements @state)))
  (= (:id->parent @state) (el/traverse el/model-node? model/id->parent (:elements @state)))
  (= (:referred-id->relations @state) (el/traverse el/model-relation? model/referred-id->rel (:elements @state)))
  (= (:referrer-id->relations @state) (el/traverse el/model-relation? model/referrer-id->rel (:elements @state)))
  (model/build-model (elements))

  (count (nodes))

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
