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
       (spec/check-input-model)
       (model/build-model)
       (reset! state)))

(defn model
  "Returns the model."
  []
  @state)

(defn elements
  "Returns the set of input elements."
  ([]
   (elements (model)))
  ([model]
   (:elements model)))

(defn nodes
  "Returns the set of nodes."
  ([]
   (nodes (model)))
  ([model]
   (:nodes model)))

(defn relations
  "Returns the set of relations."
  ([]
   (relations (model)))
  ([model]
   (:relations model)))

(defn model-elements
  "Returns the set of model elements (nodes and relations)."
  ([]
   (model-elements (nodes)))
  ([model]
   (concat (nodes model) (relations model))))

(defn views
  "Returns the set of views."
  ([]
   (views (model)))
  ([model]
   (:views model)))

(defn themes
  "Returns the set of themes."
  ([]
   (themes (model)))
  ([model]
   (:themes model)))

(defn node-by-id
  "Returns the node with the given `id`."
  ([id]
   (node-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/model-node? el)
       el))))

(defn relation-by-id
  "Returns the relation with the given `id`."
  ([id]
   (relation-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/model-relation? el)
       el))))

(defn view-by-id
  "Returns the view with the given `id`."
  ([id]
   (view-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/view? el)
       el))))

(comment
  (update-state! "models")
  
  (model/build-model (elements))

  (count (nodes))
  (count (relations))
  (count (views))
  (count (themes))

  (view/specified-model-nodes (model) (view-by-id :banking/system-context-view))
  (view/specified-relations (model) (view-by-id :banking/system-context-view))
  (view/specified-elements (model) (view-by-id :test/banking-container-view-related))
  (view/specified-elements (model) (view-by-id :test/banking-container-view-relations))

  (model/related-nodes (model) (view/referenced-relations (model) (view-by-id :test/banking-container-view-related)))
  (model/relations-of-nodes (model) (view/referenced-model-nodes (model) (view-by-id :test/banking-container-view-relations)))

  (view/elements-in-view (model) (view-by-id :banking/container-view))
  (view/technologies-in-view (model) (view-by-id :banking/container-view))

  ;
  :rcf)
