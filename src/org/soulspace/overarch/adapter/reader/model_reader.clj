(ns org.soulspace.overarch.adapter.reader.model-reader
  "Functions for reading models from different sources."
  (:require [org.soulspace.overarch.application.model-repository :as model-repository]
            [org.soulspace.overarch.domain.spec :as spec]))

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
  "Reads the models with the reader of type `rtype` from all locations of the given `path`."
  reader-type)

(defmulti build-model
  "Builds the model."
  model-type)

(defn update-state!
  "Updates the state with the registered data read from `path`."
  [options]
  (let [path (:model-dir options)
        scope (:scope options)]
    (->> path
         ; TODO don't hardcode repo type
         (read-models options)
         ;(spec/check-input-model) ; TODO check input model in adapter
         ; TODO transform input model (e.g. set internal/external scope)
         ; extract model type from options
         (build-model options)
         (reset! model-repository/state))))

