(ns org.soulspace.overarch.domain.analytics
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]))

;;;
;;; Analytics
;;;

;;
;; Transducer fuctions
;;
(defn namespaces-xf
  "Returns a transducer to extract the namespaces of some elements."
  []
  (comp
   (map :id)
   (map namespace)))

(defn node-ids-xf
  "Returns a transducer to extract the id of each node."
  []
  (comp (filter el/identifiable?)
        (remove el/relational?)
        (remove view/view?)
        (map :id)))

(defn unresolved-refs-xf
  "Returns a transducer to extract unresolved refs"
  [model]
  (comp (filter el/reference?)
        (map (partial model/resolve-ref model))
        (filter el/unresolved-ref?)))

(defn count-namespaces
  "Returns a map with the count of identifiable elements per namespace in the given `coll`."
  [coll]
  (->> coll
       (eduction (namespaces-xf))
       (frequencies)))

(defn count-nodes
  "Returns a map with the count of relations per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/model-node?)
       (map :el)
       (frequencies)))

(defn count-relations
  "Returns a map with the count of relations per type in the given `coll`."
  [coll]
  (->> coll
       (filter el/relational?)
       (map :el)
       (frequencies)))

(defn count-views
  "Returns a map with the count of views per type in the given `coll`."
  [coll]
  (->> coll
       (filter view/view?)
       (map :el)
       (frequencies)))

(defn count-elements
  "Returns a map with the count of views per type in the given `coll`."
  [coll]
  (->> coll
       (map :el)
       (frequencies)
       (into (sorted-map))))

(defn unidentifiable-elements
  "Returns the elements without an id in the given `coll`."
  [coll]
  (->> coll
       (remove el/identifiable?)))

(defn unnamespaced-elements
  "Returns the elements without a namespaced id."
  [coll]
  (->> coll
       (remove el/namespaced?)
       (map :id)))

(defn unnamed-elements
  "Returns the elements without an id in the given `coll`."
  [coll]
  (->> coll
       (remove el/named?)
       (map :id)))

(defn key-set
  "Returns a set of the keys of the map `m`."
  [m]
  (into #{} (keys m)))

(defn unrelated-nodes
  "Returns the set of ids of identifiable model nodes not taking part in any relation."
  [model]
  ; TODO registry contains relations and views
  (let [id-set (into #{} node-ids-xf (:elements model))]
    (set/difference id-set
                    (key-set (:referrer model))
                    (key-set (:referred model)))))

;; TODO return missing elements
; TODO include view id
(defn unresolved-refs
  "Checks references in an element."
  [model element]
  (->> (:ct element)
       (filter el/reference?)
       (map (partial model/resolve-ref model))
       (filter el/unresolved-ref?)
       (map #(assoc % :parent (:id element)))))

(defn validate-views
  "Checks the references in the views."
  [model]
  (->> (view/get-views model)
       (map (partial unresolved-refs model))
       (flatten)))


