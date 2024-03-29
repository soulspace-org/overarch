;;;;
;;;; Functions for flat views
;;;;
(ns org.soulspace.overarch.domain.views.flat-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

;;
;; View based element aggregation
;;
(defn id->element-map
  "Converts the `coll` of elements into a map from id to element."
  ([coll]
   (->> coll
        (filter el/identifiable-element?)
        (map (fn [e] [(:id e) e]))
        (into {}))))

(defmethod view/union-by-id :flat-view
  [_ _ & sets]
  (->> sets
       (map id->element-map)
       (apply merge)
       (vals)
       (set)))

(defmethod view/selected-elements :flat-view
  [model view]
  (if-let [criteria (view/selection-spec view)]
    (filter (model/filter-xf model criteria)
            (model/model-elements model))
    #{}))

(defmethod view/referenced-elements :flat-view
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

(defmethod view/collected-elements :flat-view
  "Returns the model elements for the given `view` which are selected by critera merged
   with the content references.  Preserves overrides of keys in the content references
   included in the view."
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

(defmethod view/rendered-elements :flat-view
  [model view]
  (let [collected (view/collected-elements model view)
        rendered (filter (partial view/render-model-element? model view)
                         collected)]
    rendered))



