(ns org.soulspace.overarch.application.model-repository
  (:require [clojure.spec.alpha :as s]
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

(defn check
  "Check model specification."
  [elements]
  (if (s/valid? :overarch/model elements)
    elements
    (s/explain :overarch/model elements)))

(defn update-state!
  "Updates the state with the registered data read from `path`."
  [path]
  (->> path
       (read-models :file) ; TODO don't hardcode repo type
       (check)
       (model/build-registry)
       (reset! state)))


(comment
  (update-state! "models")
  (= (:registry @state) (model/traverse model/identifiable? model/id->element (:elements @state)))
  (= (:referred @state) (model/traverse model/relation? model/referred-id->rel (:elements @state)))
  (= (:referrer @state) (model/traverse model/relation? model/referrer-id->rel (:elements @state)))

  ;
  :rcf)
