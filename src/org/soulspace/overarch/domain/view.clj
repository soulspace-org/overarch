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
   ; TODO deprecate :class-view and remove 
   (if (= :class-view (:el view))
     :code-view
     (:el view)))
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
(defn title
  "Returns the title of the view `v`, uses the name, if no title is set."
  [v]
  (if-let [title (:title v)]
    title
    (el/element-name v)))

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
(defn referenced-elements
  "Returns the `model` elements explicitly referenced in the given `view`."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

(defn selected-elements
  "Returns the `model` elements selected by criteria for the `view`."
  [model view]
  (if-let [criteria (selection-spec view)]
    (->> model
         (model/model-elements)
         (into #{} (model/filter-xf (concat (model/nodes model) (model/relations model)) criteria))
         (remove el/synthetic?))
    #{}))

(defn specified-elements
  "Returns the `model` elements specified for the view, either as refs or as selection."
  [model view]
  (let [selected (selected-elements model view)
        referenced (referenced-elements model view)]
    (el/union-by-id selected referenced)))

(defn included-elements
  "Returns the `model` elements specified and included for the view, e.g. related nodes or relations."
  [model view specified]
  (case (include-spec view)
    :relations (let [nodes (->> specified
                                (filter el/model-node?)
                                (mapcat (partial model/descendant-nodes model))
                                (map (partial model/resolve-element model))
                                (into #{})
                                (set/union specified)
                                (filter (partial render-model-element? model view)))
                     included (->> nodes
                                   (model/relations-of-nodes model)
                                   (into #{}))
                     elements (el/union-by-id included specified)]
                 elements)
    :related (let [included (model/related-nodes model (filter el/model-relation? specified))
                   elements (el/union-by-id included specified)]
               elements)
    :referenced-only specified
    (do
      (println "Unhandled include spec" (include-spec view) "in view" (:id view))
      specified)))

(defn view-elements
  "Returns the `model` elements specified for the given `view`."
  [model view]
  (let [elements (if (include-spec view)
                   ; has include spec
                   (if (selection-spec view)
                     (included-elements model view (specified-elements model view))
                     (included-elements model view (referenced-elements model view)))
                   ; no include spec
                   (if (selection-spec view)
                     (specified-elements model view)
                     (referenced-elements model view)))]
    (remove el/synthetic? (filter (partial render-model-element? model view) elements))))

; TODO refactor, move to model?
(defn root-elements
  "Returns the root elements for a collection of `model` `elements`, e.g. to start the rendering of a hierarchical view with."
  [model elements]
  ; Difference of the sets of elements have to be done with difference-by-id which treats the elements as entities
  ; to preserve overrides of keys in the content references included in the view.
  ; When keys have been added or overridden in the reference in a view, the elements in the different sets
  ; are not the same values anymore and so have to be treated as entities, even when they are still immutable.
  (let [descendants (->> elements
                         (filter el/model-node?)
                         (mapcat (partial model/descendant-nodes model))
                         (map (model/element-resolver model))
                         (into #{}))]
    (el/difference-by-id elements descendants)))

(defn elements-to-render
  "Returns the list of elements to render from the `view` or the given `coll` of elements, depending on the type of the view."
  ([model view]
   (root-elements model (view-elements model view)))
  ([model view coll]
   (->> coll
        (map (partial model/resolve-element model))
        (filter (partial render-model-element? model view))
        (map #(element-to-render model view %)))))
