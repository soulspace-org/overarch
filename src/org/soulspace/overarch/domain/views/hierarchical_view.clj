;;;;
;;;; Functions for hierarchical views
;;;;
(ns org.soulspace.overarch.domain.views.hierarchical-view
  (:require [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

(defn contains-parent?
  "Returns true, if `coll` contains the parent of `e`."
  [model coll e]
  (let [parent (model/parent model e)]
    (and (seq parent)
         (contains? coll parent))))

(defn render-element?
  ""
  [model coll e]
  (and (or (not (el/model-node? e))
           (not (contains-parent? model coll e)))
       (or (not (el/model-relation? e))
           (view/contains-related? model coll e))))

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

(defmethod view/rendered-elements :hierarchical-view
  [model view]
  (let [collected (view/collected-elements model view)
        rendered (filter (partial view/render-model-element? model view)
                         collected)]
    rendered))

; Q: Which of the rendered elements are the relevant top level elements for the hierarchical views?
;    All relations and the nodes which are not rendered by an included ancestor?

