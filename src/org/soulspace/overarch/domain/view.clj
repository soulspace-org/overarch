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

(def view-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :class-view            :hierarchical-view)
      (derive :component-view        :hierarchical-view)
      (derive :container-view        :hierarchical-view)
      (derive :context-view          :hierarchical-view)
      (derive :deployment-view       :hierarchical-view)
      (derive :state-machine-view    :hierarchical-view)
      (derive :system-landscape-view :hierarchical-view)
      (derive :concept-view          :flat-view)
      (derive :dynamic-view          :flat-view)
      (derive :glossary-view         :flat-view)
      (derive :use-case-view         :flat-view)
;
      ))

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

;; TODO which multis are needed as an interface and which can be implemented as fns because the are implementation details?

(defmulti union-by-id
  "Returns a set that is the union of the input `sets`.
   Equality is based on the id (:id key) of the element maps in the sets, not on value equality of the maps (entity equality vs. value equality).
   Element maps with the same id will be merged in left-to-right order. If a key occurs in more than one map, the mapping from the latter (left-to-right) will be the mapping in the result."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti difference-by-id
  "Returns a set that is the difference of the input `sets`.
   Equality is based on the id (:id key) of the element maps in the sets, not on value equality of the maps (entity equality vs. value equality)."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti selected-elements
  "Returns the model elements selected by criteria for the `view`."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti referenced-elements
  "Returns the relations explicitly referenced in the given `view`."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti collected-elements
  "Returns the model elements for the given `view` which are selected by critera merged
   with the content references.  Preserves overrides of keys in the content references
   included in the view."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti rendered-elements
  "Returns all elements to be rendered in the view."
  view-type
  :hierarchy #'view-hierarchy)

(defmulti toplevel-elements
  "Returns the top level elements to be rendered in the view."
  view-type
  :hierarchy #'view-hierarchy)

(defn contains-related?
  "Returns true, if `coll` contains the nodes related by `r`."
  [model coll r]
  (and (contains? coll (model/resolve-element model (:from r)))
       (contains? coll (model/resolve-element model (:to r)))))

(defn related-includes 
  "Returns the set of model nodes connected by the relations in `coll`."
  [model view coll]
  (into #{}
        (map (partial model/resolve-element model)
             (model/related-nodes model
                                  (filter el/model-relation?
                                          coll)))))

;; TODO: take the rendered children of referenced nodes into account and exclude
;;       nodes rendered as boundaries
(defn relation-includes
  "Returns the set of relations connecting the model nodes in `coll`."
  [model view coll]
  (into #{}
        (model/relations-of-nodes model
                                  (filter el/model-node?
                                          coll))))

(defn all-rendered-nodes
  ""
  []
  )

(defn rendered-root-nodes
  ""
  []
  )

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
  )
