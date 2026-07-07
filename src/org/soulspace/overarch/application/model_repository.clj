(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

;;;
;;; Repository functions
;;;

;;
;; Application state
;;
; Application state is not needed for the overarch CLI, but maybe helpful for other clients
(def state (atom {}))

(defn model
  "Returns the model."
  []
  @state)

(defn nodes
  "Returns the set of nodes."
  ([]
   (nodes (model)))
  ([model]
   (model/nodes model)))

(defn relations
  "Returns the set of relations."
  ([]
   (relations (model)))
  ([model]
   (model/relations model)))

(defn model-elements
  "Returns the set of model elements (nodes and relations)."
  ([]
   (model-elements (model)))
  ([model]
   (model/model-elements model)))

(defn elements
  "Returns the set of elements (nodes, relations, views, themes)."
  ([]
   (elements (model)))
  ([model]
   (model/elements model)))

(defn namespaces
  "Returns the set of namespaces."
  ([]
   (namespaces (model)))
  ([model]
   (model/namespaces model)))

(defn node-by-id
  "Returns the node with the given `id`."
  ([id]
   (model/node-by-id (model) id)))

(defn nodes-by-criteria
  "Returns a set of nodes that match the `criteria`"
  [criteria]
    (model/nodes-by-criteria (model) criteria))

(defn relation-by-id
  "Returns the relation with the given `id`."
  ([id]
   (model/relation-by-id (model) id)))

(defn relations-by-criteria
  "Returns a set of relations that match the `criteria`"
  [criteria]
    (model/relations-by-criteria (model) criteria))

(defn model-elements-by-criteria
  "Returns a set of model elements that match the `criteria`"
  [criteria]
    (model/model-elements-by-criteria (model) criteria))

(defn view-specs
  "Returns the set of view specs."
  ([]
   (view-specs (model)))
  ([model]
   (model/view-specs model)))

(defn view-spec-by-id
  "Returns the view spec with the given `id`."
  ([id]
   (view-spec-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/view-spec? el)
       el))))

(defn views
  "Returns the set of views."
  ([]
   (views (model)))
  ([model]
   (model/views model)))

(defn view-by-id
  "Returns the view with the given `id`."
  ([id]
   (view-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/view? el)
       el))))

(defn views-by-criteria
  "Returns a set of model elements that match the `criteria`"
  [criteria]
  (model/views-by-criteria (model) criteria))

(defn themes
  "Returns the set of themes."
  ([]
   (themes (model)))
  ([model]
   (model/themes model)))

(defn theme-by-id
  "Returns the theme with the given `id`."
  ([id]
   (theme-by-id (model) id))
  ([model id]
   (when-let [el (get (:id->element model) id)]
     (when (el/theme? el)
       el))))

(comment ; repo
  (count (nodes))
  (count (relations))
  (count (views))
  (count (view-specs))
  (count (themes))
  ;
  )
