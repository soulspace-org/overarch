(ns org.soulspace.overarch.application.model-repository
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [org.soulspace.overarch.domain.element :as el]
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

(defn input-elements
  "Returns the set of input elements."
  ([]
   (input-elements (model)))
  ([model]
   (:input-elements model)))

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
   (model-elements (model)))
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
  (count (themes))
  ;
  )
