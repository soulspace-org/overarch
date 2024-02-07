(ns org.soulspace.overarch.application.model-repository
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.spec :as spec]))

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


(comment
  (update-state! "models")
  (= (:registry @state) (model/traverse el/identifiable? model/id->element (:elements @state)))
  (= (:parents @state) (model/traverse el/model-node? model/id->parent (:elements @state)))
  (= (:referred @state) (model/traverse el/relation? model/referred-id->rel (:elements @state)))
  (= (:referrer @state) (model/traverse el/relation? model/referrer-id->rel (:elements @state)))

  ;
  :rcf)
