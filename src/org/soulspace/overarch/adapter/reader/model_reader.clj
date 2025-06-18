(ns org.soulspace.overarch.adapter.reader.model-reader
  "Functions for reading and building models and setting the repository state.
   The multimethods should be implemented by specific readers."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.application.model-repository :as repo]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

;;;
;;; Reader functions
;;;
(defn reader-type
  "Returns the reader type."
  ([options]
   (:reader-type options))
  ([options _]
   (:reader-type options)))

(defn model-type
  "Returns the model type."
  ([options]
   (:input-model-format options))
  ([options _]
   (:input-model-format options)))
(defmulti read-models
  "Reads the models as specified in the `options`."
  reader-type)

;;;
;;; Model building functions
;;;
(defn build-id->element
  "Returns the id->element index."
  [elements]
  (->> elements
       (map (fn [e] [(:id e) e]))
       (into {})))

(defn build-id->parent-id
  "Returns the id->parent-id index."
  [elements]
  (->> elements
       (filter el/model-relation?)
       (filter #(= :contained-in (:el %)))
       (map (fn [e] [(:from e) (:to e)]))
       (into {})))

(defn build-referrer-id-relations
  "Returns the referrer-id-relations index"
  [elements]
  (->> elements
       (filter el/model-relation?)
       (group-by :from)))

(defn build-referred-id-relations
  "Returns the referred-id-relations index"
  [elements]
  (->> elements
       (filter el/model-relation?)
       (group-by :to)))

(defn update-indices
  "Returns a `model` with updated indices."
  [model]
  (assoc model
         :id->element (build-id->element (model/elements model))
         :id->parent-id (build-id->parent-id (model/elements model))
         :referred-id->relations (build-referred-id-relations (model/elements model))
         :referrer-id->relations (build-referrer-id-relations (model/elements model))))

(comment
  (def model-elements
    #{{:el :system :id :a/a-system}
      {:el :container :id :a.a/a-container}
      {:el :system :id :b/b-system}
      {:el :contained-in
       :id :a.a/a-container-contained-in-a-system
       :from :a.a/a-container
       :to :a/a-system}
      {:el :request
       :id :a/a-system-calls-b-system
       :from :a/a-system
       :to :b/b-system}
      {:el :request
       :id :a.a/a-container-calls-b-system
       :from :a.a/a-container
       :to :b/b-system}})
  (fns/data-tapper (build-id->element model-elements))
  (fns/data-tapper (build-id->parent-id model-elements))
  (fns/data-tapper (build-referrer-id-relations model-elements))
  (fns/data-tapper (build-referred-id-relations model-elements))
  ;
  )

(defn merge-model
  "Merges the `model` with the `other` model."
  ([]
   {:nodes #{}
    :relations #{}
    :views #{}
    :themes #{}
    :id->element {}
    :id->parent-id {}
    :referrer-id->relations {}
    :referred-id->relations {}
    :problems #{}})
  ([model]
   model)
  ([model other-model]
   {:nodes (set/union (:nodes model) (:nodes other-model))
    :relations (set/union (:relations model) (:relations other-model))
    :views (set/union (:views model) (:views other-model))
    :themes (set/union (:themes model) (:themes other-model))
    :id->element (merge (:id->element model) (:id->element other-model))
    :id->parent-id (merge (:id->parent-id model) (:id->parent-id other-model))
    :referrer-id-relations (merge (:referrer-id-relations model) (:referrer-id-relations other-model))
    :referred-id-relations (merge (:referred-id-relations model) (:referred-id-relations other-model))
    :problems (set/union (:problems model) (:problems other-model))}))

(defmulti build-model
  "Builds the model."
  model-type)

(defn update-state!
  "Updates the state with the registered data read from `path`."
  [options]
  (->> (read-models options)
       (build-model options)
       (reset! repo/state)))

