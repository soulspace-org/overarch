(ns org.soulspace.overarch.adapter.reader.model-reader
  "Functions for reading and building models and setting the repository state.
   The multimethods should be implemented by specific readers."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.application.model-repository :as repo]))

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

