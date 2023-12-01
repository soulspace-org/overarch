(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.model :as m]))

(defn repo-type
  "Returns the repository type."
  ([r-type]
   r-type)
  ([r-type location]
   r-type))

(defmulti read-model 
  "Reads the model with the repository of type `r-type` from the given `location`."
  repo-type
  )

;;
;; Application state
;;
; TODO get rid of global state at some point
(def state (atom {}))

(defn update-state!
  "Updates the state with the registered data read from `dir`."
  [dir]
  (->> dir
       (read-model :filesystem) ; TODO don't hardcode repo type
       (m/build-registry)
       (reset! state)))
