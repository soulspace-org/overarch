;;;;
;;;; Functions for the definition and handling of views
;;;;
(ns org.soulspace.overarch.domain.view
  "Functions for the definition and handling of views."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.model :as model] 
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]))

;;;
;;; Accessors
;;;

(defn view-type
  "Returns the type of the `view`."
  ([view]
   (:el view))
  ([model view]
   (view-type view))
  ([model view & _]
   (view-type view)))

(defn views
  "Returns the set of views from the `model`."
  [model]
  (:views model))

(defn themes
  "Returns the set of themes from the `model`."
  [model]
  (:themes model))

(defn view
  "Returns the view with the given `id` from the `model`."
  ([model id]
   ((:id->element model) id)))

;;
;; View spec elements
;;

(defn layout-spec
  "Returns the layout specification for the `view`."
  ([view]
   (get-in view [:spec :layout] :top-down)))

(defn themes->styles
  "Returns the vector of styles from the themes of the given `view`."
  [model view]
  (->> (get-in view [:spec :themes] [])
       (map (partial model/resolve-element model))
       (map :styles)))

(defn styles-spec
  "Returns the styles specification for the `model` and the `view`."
  [model view]
  (apply set/union (conj
                    (themes->styles model view)
                    (get-in view [:spec :styles] #{}))))

(defn include-spec
  "Returns the include specification for the `view`."
  [view]
  (get-in view [:spec :include] :referenced-only))

(defn selection-spec
  "Returns the selection specification for the `view`."
  [view]
  (get-in view [:spec :selection]))

(defn sketch-spec
  "Returns the sketch specification for the `view`."
  [view]
  (get-in view [:spec :sketch]))

(defn linetype-spec
  "Returns the linetype specification for the `view`."
  [view]
  (get-in view [:spec :linetype]))

;;;
;;; View functions
;;;

;;
;; Context based content filtering
;;
(defmulti render-model-element?
  "Returns true if the element `e` is rendered in the `view` in the context of the `model`."
  view-type)

(defmulti include-content?
  "Returns true if the content of element `e` is rendered in the `view` in the context of the `model`."
  view-type)

(defmulti render-relation-node?
  "Returns true if the node `e` will be rendered for the `view` in the context of the `model`."
  view-type)

(defmulti element-to-render
  "Returns the model element to be rendered for element `e` for the `view` in the context of the `model`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  view-type)

(defn render-element?
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [model view e]
  (or (and (el/model-relation? e)
           (render-model-element? model view (model/model-element model (:from e)))
           (render-model-element? view (model/model-element model (:to e))))
      (and (render-model-element? view e)
           (el/internal? (model/parent model e)))))

; TODO Remove
(defn render-relation?
  "Returns true if the relation should be rendered in the context of the view."
  [model rel pred]
  (let [rendered? pred
        from (model/resolve-element model (:from rel))
        to   (model/resolve-element model (:to rel))]
    (when (and (rendered? rel) (rendered? from) (rendered? to))
      rel)))

; TODO Remove
(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [model view rel]
  (let [; rendered? (render-predicate m view-type)
        from (model/resolve-element model (:from rel))
        to   (model/resolve-element model (:to rel))]))
  ; TODO promote relations to higher levels?
  
;;
;; View based element aggregation
;;

(defn referenced-model-nodes
  "Returns the model nodes explicitly referenced in the given view."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))
       (filter el/model-node?)))

(defn referenced-relations
  "Returns the relations explicitly referenced in the given view."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))
       (filter el/model-relation?)))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given view."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

(defn selected-model-nodes
  "Returns the model elements selected by criteria"
  [model view]
  (if-let [criteria (get-in view [:spec :selection])]
    (filter (model/filter-xf model criteria) (:nodes model))
    #{}))

(defn selected-model-relations
  "Returns the model elements selected by criteria"
  [model view]
  (if-let [criteria (get-in view [:spec :selection])]
    (filter (model/filter-xf model criteria) (:relations model))
    #{}))

(defn selected-model-elements
  "Returns the model elements selected by criteria"
  [model view]
  (concat (selected-model-nodes model view)
          (selected-model-relations model view)))

;; TODO: take the rendered children of referenced nodes into account and exclude
;;       nodes rendered as boundaries
(defn specified-model-nodes
  "Returns the model nodes specified in the given view.
   Takes the view spec into account for resolving model nodes not explicitly referenced."
  [model view]
  (let [include (include-spec view)]
    (case include
      :referenced-only (let [referenced-nodes (referenced-model-nodes model view)
                             _ (fns/data-tapper {:fn "specified-relations"
                                                 :view (:id view)
                                                 :selector include
                                                 :referenced-rels referenced-nodes})]
                         referenced-nodes)
      :relations (let [referenced-nodes (referenced-model-nodes model view)
                       _ (fns/data-tapper {:fn "specified-relations"
                                           :view (:id view)
                                           :selector include
                                           :referenced-rels referenced-nodes})]
                   referenced-nodes)
      :related (let [referenced-nodes (referenced-model-nodes model view)
                     referenced-rels (referenced-relations model view)
                     related-nodes (into #{}
                                         (map (partial model/resolve-element model)
                                              (model/related-nodes model referenced-rels)))
                     specified-nodes (set/union referenced-nodes related-nodes)
                     _ (fns/data-tapper {:fn "specified-model-nodes"
                                         :view (:id view)
                                         :selector include
                                         :referenced-nodes referenced-nodes
                                         :referenced-rels referenced-rels
                                         :related-nodes related-nodes
                                         :specified-nodes specified-nodes})]
                 specified-nodes) ; TODO check
      )))

(defn specified-relations
  "Returns the relations specified in the given view.
   Takes the view spec into account for resolving relations not explicitly referenced."
  [model view]
  (let [include (include-spec view)] 
    (case include
      :referenced-only (let [referenced-rels (referenced-relations model view)
                             _ (fns/data-tapper {:fn "specified-relations"
                                                 :view (:id view)
                                                 :selector include
                                                 :referenced-rels referenced-rels})]
                         referenced-rels)
      :relations (let [referenced-nodes (referenced-model-nodes model view)
                       referenced-rels (referenced-relations model view)
                       related-rels (into #{} (model/relations-of-nodes model referenced-nodes))
                       specified-rels (set/union referenced-rels related-rels)
                       _ (fns/data-tapper {:fn "specified-relations"
                                           :view (:id view)
                                           :selector include
                                           :referenced-nodes referenced-nodes
                                           :referenced-rels referenced-rels
                                           :related-rels related-rels
                                           :specified-rels specified-rels})]
                   specified-rels)
      :related (let [referenced-rels (referenced-relations model view)
                     _ (fns/data-tapper {:fn "specified-relations"
                                         :view (:id view)
                                         :selector include
                                         :referenced-rels referenced-rels})]
                 referenced-rels))))

(defn specified-elements
  "Returns the model elements and relations explicitly specified in the given view.
   Takes the view spec into account for resolving relations not explicitly referenced."
  [model view]
  (let [include (include-spec view)]
    (case include
      :referenced-only (referenced-elements model view)
      :relations (concat (specified-model-nodes model view)
                         (specified-relations model view))
      :related (concat (specified-model-nodes model view)
                       (specified-relations model view)))))

(defn rendered-model-nodes
  "Returns the model nodes to be rendered by the given view."
  [model view]
  (let [specified-nodes (specified-model-nodes model view)]
    specified-nodes
    ; TODO
    )
  ;
  )

(defn rendered-relations
  "Returns the relations to be rendered by the given view.
   Takes the view spec into account for resolving relations not explicitly specified."
  [model view]
  (let [specified-rels (specified-relations model view)]
    specified-rels
    ; TODO
    )
  ;
  )

(defn rendered-elements
  "Returns the model elements to be rendered by the given view.
   Takes the view spec into account for resolving model elements not explicitly specified."
  [model view]
  (concat (rendered-model-nodes model view)
          (rendered-relations model view))
  ;
  )

;;;
;;; Rendering functions
;;;
(defn elements-to-render
  "Returns the list of elements to render from the view
   or the given collection of elements, depending on the type
   of the view."
  ([model view]
   (elements-to-render model view (:ct view)))
  ([model view coll]
   (->> coll
        (map (partial model/resolve-element model))
        (filter (partial render-element? model view))
        (map #(element-to-render view %)))))

(defn elements-in-view
  "Returns the elements rendered in the view."
  ([model view]
   (elements-in-view model #{} view (elements-to-render model view (:ct view))))
  ([model elements view coll]
   (if (seq coll)
     (let [e (first coll)]
       (recur model (elements-in-view model (conj elements e)
                                view
                                (elements-to-render model view (:ct e)))
              view
              (rest coll)))
     elements)))

(defn technologies-in-view
  "Returns the technologies in the view."
  [model view]
  (->> view
       (elements-in-view model)
       (map :tech)
       (remove nil?)
       (into #{})))

