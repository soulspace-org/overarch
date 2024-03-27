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

;;
;; View spec elements
;;

(defn themes->styles
  "Returns the vector of styles from the themes of the given `view`."
  [model view]
  (->> (get-in view [:spec :themes] [])
       (map (partial model/resolve-element model))
       (map :styles)))

(defn include-spec
  "Returns the include specification for the `view`."
  [view]
  (get-in view [:spec :include] :referenced-only))

(defn layout-spec
  "Returns the layout specification for the `view`."
  ([view]
   (get-in view [:spec :layout] :top-down)))

(defn legend-spec
  "Returns the legend specification for the `view`."
  ([view]
   (get-in view [:spec :legend] true)))

(defn linetype-spec
  "Returns the linetype specification for the `view`."
  [view]
  (get-in view [:spec :linetype]))

(defn selection-spec
  "Returns the selection specification for the `view`."
  [view]
  (get-in view [:spec :selection]))

(defn sketch-spec
  "Returns the sketch specification for the `view`."
  [view]
  (get-in view [:spec :sketch]))

(defn styles-spec
  "Returns the styles specification for the `model` and the `view`."
  [model view]
  (apply set/union (conj
                    (themes->styles model view)
                    (get-in view [:spec :styles] #{}))))

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

(defmulti element-to-render
  "Returns the model element to be rendered for element `e` for the `view` in the context of the `model`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  view-type)

;;
;; View based element aggregation
;;
(defn referenced-nodes
  "Returns the model nodes explicitly referenced in the given `view`."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))
       (filter el/model-node?)))

(defn referenced-relations
  "Returns the relations explicitly referenced in the given `view`."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))
       (filter el/model-relation?)))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given `view`."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

(defn selected-elements
  "Returns the model elements selected by criteria for the `view`."
  [model view]
  (if-let [criteria (selection-spec view)]
   (filter (model/filter-xf model criteria)
           (model/model-elements model))
   #{}))

;; TODO: take the rendered children of referenced nodes into account and exclude
;;       nodes rendered as boundaries
(defn specified-nodes
  "Returns the model nodes specified in the given `view`.
   Takes the view spec into account for resolving model nodes not explicitly referenced."
  [model view]
  (let [include (include-spec view)]
    (case include
      :referenced-only (let [referenced-nodes (referenced-nodes model view)
                             _ (fns/data-tapper {:fn "specified-relations"
                                                 :view (:id view)
                                                 :selector include
                                                 :referenced-rels referenced-nodes})]
                         referenced-nodes)
      :relations (let [referenced-nodes (referenced-nodes model view)
                       _ (fns/data-tapper {:fn "specified-relations"
                                           :view (:id view)
                                           :selector include
                                           :referenced-rels referenced-nodes})]
                   referenced-nodes)
      :related (let [referenced-nodes (referenced-nodes model view)
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
  "Returns the relations specified in the given `view`.
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
      :relations (let [referenced-nodes (referenced-nodes model view)
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
  "Returns the model elements and relations explicitly specified in the `view`.
   Takes the view spec into account for resolving relations not explicitly referenced."
  [model view]
  (let [include (include-spec view)]
    (case include
      :referenced-only (referenced-elements model view)
      :relations (concat (specified-nodes model view)
                         (specified-relations model view))
      :related (concat (specified-nodes model view)
                       (specified-relations model view)))))

(defn merged-elements
  "Returns the model elements for the given `view` which are selected by critera merged
   with the content references.  Preserves overrides of keys in the content references
   included in the view."
  [model view]
  (let [selected-set (selected-elements model view)
        selected-map (el/traverse el/id->element selected-set)
        referenced-set (referenced-elements model view)
        referenced-map (el/traverse el/id->element referenced-set)
        merged-set (vals (merge selected-map referenced-map))]
    merged-set))

(defn included-elements
  "Takes the specified elements and includes the neccessary elements for the input spec."
  [model view coll]
  (let [include (include-spec view)]
    (case include
      :referenced-only coll
      :relations
      (let [include-set (into #{} (map (partial model/resolve-element model)
                                       (model/related-nodes model (filter el/model-relation? coll))))
            include-map (el/traverse el/id->element include-set)
            coll-map (el/traverse el/id->element coll)]
        (vals (merge include-map coll-map)))
      
      :related
      (let [include-set (into #{} (model/relations-of-nodes model (filter el/model-node? coll)))
            include-map (el/traverse el/id->element include-set)
            coll-map (el/traverse el/id->element coll)]
        (vals (merge include-map coll-map)))
      )))


(defn rendered-nodes
  "Returns the model nodes to be rendered by the given `view`."
  [model view]
  (let [specified-nodes (specified-nodes model view)]
    specified-nodes
    ; TODO
    )
  ;
  )

(defn rendered-relations
  "Returns the relations to be rendered by the given `view`.
   Takes the view spec into account for resolving relations not explicitly specified."
  [model view]
  (let [specified-rels (specified-relations model view)]
    specified-rels
    ; TODO
    )
  ;
  )

(defn rendered-elements
  "Returns the model elements to be rendered by the given `view`.
   Takes the view spec into account for resolving model elements not explicitly specified."
  [model view]
  ; TODO merge selected with referenced and the result with included

  (concat (rendered-nodes model view)
          (rendered-relations model view))
  ;
  )

; Q: Which of the rendered elements are the relevant top level elements for the hierarchical views?
;    All relations and the nodes which are not rendered by an included ancestor?

;;;
;;; Rendering functions
;;;
(defn elements-to-render
  "Returns the list of elements to render from the `view`
   or the given `coll` of elements, depending on the type
   of the view."
  ([model view]
   (elements-to-render model view (:ct view)))
  ([model view coll]
   (->> coll
        (map (partial model/resolve-element model))
        (filter (partial render-model-element? model view))
        (map #(element-to-render model view %)))))

(defn elements-in-view
  "Returns the elements rendered in the `view`."
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
  "Returns the technologies in the `view`."
  [model view]
  (->> view
       (elements-in-view model)
       (map :tech)
       (remove nil?)
       (into #{})))

