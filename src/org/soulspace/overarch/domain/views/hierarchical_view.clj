;;;;
;;;; Functions for hierarchical views
;;;;
(ns org.soulspace.overarch.domain.views.hierarchical-view
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

;;
;; View based element aggregation
;;
(defmethod view/union-by-id :hierarchical-view
  [_ _ & sets]
  (->> sets
       (map (partial el/traverse el/id->element))
       (apply merge)
       (vals)
       (set)))

; TODO check if needed? Implement correctly
(defmethod view/difference-by-id :hierarchical-view
  [_ _ & sets]
  (->> sets
       (set/difference)
       (map (partial el/traverse el/id->element))
       (apply merge)
       (vals)
       (set)))

(defmethod view/selected-elements :hierarchical-view
  [model view]
  (if-let [criteria (view/selection-spec view)]
    (filter (model/filter-xf model criteria)
            (model/model-elements model))
    #{}))

(defmethod view/referenced-elements :hierarchical-view
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

; TODO add descendants for hierarchical views
(defmethod view/collected-elements :hierarchical-view
  [model view]
  (let [selected (view/selected-elements model view)
        referenced (view/referenced-elements model view)
        merged (view/union-by-id model view selected referenced)]
    (if-let [include (view/include-spec view)]
      (case include
        :relations
        (view/union-by-id model view (view/relation-includes model view merged) merged)
        :related
        (view/union-by-id model view (view/related-includes model view merged) merged)
        :else
        (do (println "Unknown include option:" include) ; TODO logging/tapping?
            merged))
      merged)))

(defmethod view/rendered-elements :hierarchical-view
  [model view coll]
  (filter (partial view/render-model-element? model view)
          coll))

(defmethod view/toplevel-elements :hierarchical-view
  [model view coll]
  (->> coll
       (filter (partial view/render-model-element? model view))
       (mapcat el/descendant-nodes)
       (into #{})
       (partial set/difference coll)))
