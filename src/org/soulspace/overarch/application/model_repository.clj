(ns org.soulspace.overarch.application.model-repository
  (:require [clojure.spec.alpha :as s]
            [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

(defn repo-type
  "Returns the repository type."
  ([rtype]
   rtype)
  ([rtype _]
   rtype))

(defmulti read-models
  "Reads the models with the repository of type `rtype` from all locations of the given `path`."
  repo-type)

(defn build-registry
  "Returns a map with the original `elements` and a registry by id for lookups.
   
   The map has the following shape:

   :elements -> the given data
   :registry -> a map from id to element
   :parents  -> a map from id to parent element
   :referrer -> a map from id to set of relations where the id is the referrer (:from)
   :referred -> a map from id to set of relations where the id is referred (:to)"
  [elements]
  (if (s/valid? :overarch/elements elements)
    {:elements elements
     :registry (model/traverse e/identifiable? model/id->element elements)
     :parents (model/build-id->parent elements)
     :referrer (model/traverse e/relation? model/referrer-id->rel elements)
     :referred (model/traverse e/relation? model/referred-id->rel elements)}
    (s/explain :overarch/elements elements)))


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
       (build-registry)
       (reset! state)))

(comment
  (update-state! "models")
  (= (:registry @state) (model/traverse model/identifiable? model/id->element (:elements @state)))
  (= (:referred @state) (model/traverse model/relation? model/referred-id->rel (:elements @state)))
  (= (:referrer @state) (model/traverse model/relation? model/referrer-id->rel (:elements @state)))

  ;
  :rcf)
