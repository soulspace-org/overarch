(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.model :as m]))

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
       (read-models :file) ; TODO don't hardcode repo type
       (m/build-registry)
       (reset! state)))
