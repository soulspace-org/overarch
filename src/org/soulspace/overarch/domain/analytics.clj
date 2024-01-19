(ns org.soulspace.overarch.domain.analytics
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

;;;
;;; Analytics
;;;
(defn namespaces-xf
  "Returns a transducer to extract the namespaces of some elements."
  []
  (comp
   (map :id)
   (map namespace)))

(defn count-namespaces
  "Returns a map with the count of identifiable elements per namespace in the given coll."
  [coll]
  (->> coll
       (eduction (namespaces-xf))
       (frequencies)))

(defn count-relations
  "Returns a map with the count of relations per type in the given coll."
  [coll]
  (->> coll
       (filter e/relational?)
       (map :el)
       (frequencies)))

(defn count-views
  "Returns a map with the count of views per type in the given coll."
  [coll]
  (->> coll
       (filter view/view?)
       (map :el)
       (frequencies)))

(defn unidentifiable-elements
  "Returns the elements without an id."
  [coll]
  (->> coll
       (remove e/identifiable?)))

(defn unnamed-elements
  "Returns the elements without an id."
  [coll]
  (->> coll
       (remove e/named?)))

(defn key-set
  "Returns a set of the keys of a map."
  [m]
  (into #{} (keys m)))

(defn unrelated
  "Returns the set of ids of identifiable elements not taking part in any relation."
  [model]
  ; TODO registry contains relations and views
  (let [id-set (->> (:elements model)
                    (filter e/identifiable?)
                    (remove e/relational?)
                    (remove view/view?)
                    (map :id)
                    (into #{}))]
    (set/difference id-set
                    (key-set (:referrer model))
                    (key-set (:referred model)))))
