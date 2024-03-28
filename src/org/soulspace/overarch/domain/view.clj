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
  (get-in view [:spec :include]))

(defn layout-spec
  "Returns the layout specification for the `view`."
  ([view]
   (get-in view [:spec :layout])))

(defn legend-spec
  "Returns the legend specification for the `view`."
  ([view]
   (get-in view [:spec :legend])))

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
(defn id->element-map
  ""
  ([coll]
   (->> coll
        (filter el/identifiable-element?)
        (map (fn [e] [(:id e) e]))
        (into {}))))

(defn union-by-id
  "Returns a set that is the union of the input `sets`.
   Equality is based on the id (:id key) of the element maps in the sets, not on value equality of the maps (entity equality vs. value equality).
   Element maps with the same id will be merged in left-to-right order. If a key occurs in more than one map, the mapping from the latter (left-to-right) will be the mapping in the result"
  ([f & sets]
   (->> sets
        (map f)
        (apply merge)
        (vals)
        (set))))

(defn selected-elements
  "Returns the model elements selected by criteria for the `view`."
  [model view]
  (if-let [criteria (selection-spec view)]
   (filter (model/filter-xf model criteria)
           (model/model-elements model))
   #{}))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given `view`."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

;; TODO: take the rendered children of referenced nodes into account and exclude
;;       nodes rendered as boundaries
(defn relation-includes
  "Returns the set of relations connecting the model nodes in `coll`."
  [model view coll]
  (into #{}
        (model/relations-of-nodes model
                                  (filter el/model-node?
                                          coll))))

(defn related-includes
  "Returns the set of model nodes connected by the relations in `coll`."
  [model view coll]
  (into #{}
        (map (partial model/resolve-element model)
             (model/related-nodes model
                                  (filter el/model-relation?
                                          coll)))))

(defn collected-elements
  "Returns the model elements for the given `view` which are selected by critera merged
   with the content references.  Preserves overrides of keys in the content references
   included in the view."
  [model view]
  (let [id-map-fn (if (el/hierarchical-view? view)
                    (partial el/traverse el/id->element)
                    id->element-map)
        selected (selected-elements model view)
        referenced (referenced-elements model view)
        merged (union-by-id id-map-fn selected referenced)]
    (if-let [include (include-spec view)]
      (case include
        :relations
        (union-by-id id-map-fn (relation-includes model view merged) merged)
        :related
        (union-by-id id-map-fn (related-includes model view merged) merged)
        :else
        (do (println "Unknown include option:" include) ; TODO logging/tapping?
            merged))
      merged)))

(defn rendered-elements
  ""
  [model view]
  (let [collected (collected-elements model view)
        rendered (filter (partial render-model-element? model view)
                         collected)]
    rendered))

; Q: Which of the rendered elements are the relevant top level elements for the hierarchical views?
;    All relations and the nodes which are not rendered by an included ancestor?

(defn contains-parent?
  "Returns true, if `coll` contains the parent of `e`."
  [model coll e]
  (let [parent (model/parent model e)]
    (and (seq parent)
         (contains? coll parent))))

(defn contains-related?
  "Returns true, if `coll` contains the nodes related by `r`."
  [model coll r]
  (and (contains? coll (model/resolve-element model (:from r)))
       (contains? coll (model/resolve-element model (:to r)))))

(defn render-element?
  ""
  [model coll e]
  (and (or (not (el/model-node? e))
           (not (contains-parent? model coll e)))
       (or (not (el/model-relation? e))
           (contains-related? model coll e))))

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

(comment
  (id->element-map
   #{{:el :sytem
      :id :x/a-s
      :ct #{{:el :container
             :id :x/a-c}}}})
  (el/traverse el/id->element
               #{{:el :sytem
                  :id :x/a-s
                  :ct #{{:el :container
                         :id :x/a-c}}}})
  (union-by-id id->element-map #{{:id :x/a :el :a :dir :down} {:id :x/b :el :a}}
               #{{:id :x/a :el :a :dir :up}})
  ;
  )
