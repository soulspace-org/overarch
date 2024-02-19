;;;;
;;;; Functions for the definition and handling of views
;;;;
(ns org.soulspace.overarch.domain.view
  "Functions for the definition and handling of views."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.overarch.domain.model :as model] 
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as el]))

;;;
;;; Type definitions
;;;

;;
;; C4 category definitions
;; 
(def c4-view-types
  "The set of C4 view types."
  #{:context-view :container-view :component-view
    :deployment-view :system-landscape-view
    :dynamic-view})

;;
;; UML category definitions
;;
(def uml-view-types
  "The set of UML view types."
  #{:use-case-view :state-machine-view :class-view})

;;
;; Concept category definitions
;;
(def concept-view-types
  "The set of concept views types."
  #{:concept-view :glossary-view})

;; 
;; General category definitions
;;
(def view-types
  "The set of view types."
  (set/union c4-view-types uml-view-types concept-view-types))

(def hierarchical-view-types
  "The set of hierarchical view types."
  #{:context-view :container-view :component-view
    :deployment-view :system-landscape-view
    ;:dynamic-view
    :state-machine-view :class-view
    :glossary-view
    })

;;
;; Predicates
;;
(defn view?
  "Returns true if the given element `e` is a view."
  [e]
  (contains? view-types (:el e)))

(defn hierarchical-view?
  "Returns true if the given element `e` is a hierarchical view."
  [e]
  (contains? hierarchical-view-types (:el e)))

(defn view-type
  "Returns the type of the `view`."
  ([view]
   (:el view))
  ([view & _]
   (view-type view)))

;; TODO find good names for model elements, nodes relations,
;; relation participants, etc.
(defmulti render-model-node?
  "Returns true if the element `e` is rendered in the `view`"
  view-type)

(defmulti include-content?
  "Returns true if the content of element `e` is rendered in the `view`"
  view-type)

(defmulti render-relation-node?
  "Returns true if the node will"
  view-type)

;;;
;;; Schema definitions
;;;

(s/def :overarch/spec map?)
(s/def :overarch/title string?)

(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title]))

(defn get-views
  "Returns the collection of views from the `model`."
  ([model]
   (filter view? (:elements model))))

(defn get-view
  "Returns the view with the given `id` from the `model`."
  ([model id]
   ((:id->element model) id)))

(defn include-spec
  "Returns the include specification for the `view`."
  ([view]
   (get-in view [:spec :include] :referenced-only)))

(defn layout-spec
  "Returns the layout specification for the `view`."
  ([view]
   (get-in view [:spec :layout] :top-down)))

;;;
;;; View functions
;;;

(defn render-relation?
  "Returns true if the relation should be rendered in the context of the view."
  [model rel pred]
  (let [rendered? pred
        from (model/resolve-element model (:from rel))
        to   (model/resolve-element model (:to rel))]
    (when (and (rendered? rel) (rendered? from) (rendered? to))
      rel)))

(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [model view rel]
  (let [; rendered? (render-predicate m view-type)
        from (model/resolve-element model (:from rel))
        to   (model/resolve-element model (:to rel))]))
  ; TODO promote relations to higher levels?
  

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
       (filter el/relation?)))

(defn referenced-elements
  "Returns the relations explicitly referenced in the given view."
  [model view]
  (->> (:ct view)
       (map (partial model/resolve-element model))))

(defn specified-model-nodes
  "Returns the model nodes specified in the given view.
   Takes the view spec into account for resolving model nodes not explicitly referenced."
  [model view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (let [referenced-nodes (referenced-model-nodes model view)
                             _ (fns/data-tapper {:fn "specified-relations"
                                                 :view (:id view)
                                                 :selector selector
                                                 :referenced-rels referenced-nodes})]
                         referenced-nodes)
      :relations (let [referenced-nodes (referenced-model-nodes model view)
                       _ (fns/data-tapper {:fn "specified-relations"
                                           :view (:id view)
                                           :selector selector
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
                                         :selector selector
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
  (let [selector (get-in view [:spec :include] :referenced-only)] 
    (case selector
      :referenced-only (let [referenced-rels (referenced-relations model view)
                             _ (fns/data-tapper {:fn "specified-relations"
                                                 :view (:id view)
                                                 :selector selector
                                                 :referenced-rels referenced-rels})]
                         referenced-rels)
      :relations (let [referenced-nodes (referenced-model-nodes model view)
                       referenced-rels (referenced-relations model view)
                       related-rels (into #{} (model/relations-of-nodes model referenced-nodes))
                       specified-rels (set/union referenced-rels related-rels)
                       _ (fns/data-tapper {:fn "specified-relations"
                                           :view (:id view)
                                           :selector selector
                                           :referenced-nodes referenced-nodes
                                           :referenced-rels referenced-rels
                                           :related-rels related-rels
                                           :specified-rels specified-rels})]
                   specified-rels)
      :related (let [referenced-rels (referenced-relations model view)
                     _ (fns/data-tapper {:fn "specified-relations"
                                         :view (:id view)
                                         :selector selector
                                         :referenced-rels referenced-rels})]
                 referenced-rels))))

(defn specified-elements
  "Returns the model elements and relations explicitly specified in the given view."
  [model view]
  (let [selector (get-in view [:spec :include] :referenced-only)]
    (case selector
      :referenced-only (referenced-elements model view)
      :relations (concat (specified-model-nodes model view)
                         (specified-relations model view))
      :related (concat (specified-model-nodes model view)
                       (specified-relations model view)))))

(defn rendered-model-nodes
  "Returns the model nodes to be rendered by the given view."
  [model view])

(defn rendered-relations
  "Returns the relations to be rendered by the given view.
   Takes the view spec into account for resolving relations not explicitly specified."
  [model view])

(defn rendered-elements
  "Returns the model elements to be rendered by the given view.
   Takes the view spec into account for resolving model elements not explicitly specified."
  [model view])

;;
;; Context based content filtering
;;
(defn render-element?
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [model view e]
  (or (and (= :rel (:el e))
           (render-model-node? view (model/get-model-element model (:from e)))
           (render-model-node? view (model/get-model-element model (:to e))))
      (and (render-model-node? view e)
           (el/internal? (model/get-parent-element model e)))))

(defmulti element-to-render
  "Returns the model element to be rendered for element `e` for the `view`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  view-type)

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
       
(defn tech-collector
  "Adds the tech of `e` to the accumulator `acc`."
  ([] #{})
  ([acc] acc)
  ([acc e] (set/union acc #{(:tech e)})))

; TODO reimplement with elements-in-view 
(defn collect-technologies
  "Returns the set of technologies for the elements of the coll."
  [coll]
  (model/traverse :tech tech-collector coll))

(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

(defn element-name
  "Returns the name of the element."
  [e]
  (if (:name e)
    (:name e)
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary   :boundary)
      (derive :system-boundary       :boundary)
      (derive :container-boundary    :boundary)
      (derive :context-boundary      :boundary)
      (derive :system                :technical-architecture-node)
      (derive :container             :technical-architecture-node)
      (derive :component             :technical-architecture-node)
      (derive :request               :architecture-relation)
      (derive :response              :architecture-relation)
      (derive :publish               :architecture-relation)
      (derive :subscribe             :architecture-relation)
      (derive :send                  :architecture-relation)
      (derive :dataflow              :architecture-relation)
      (derive :link                  :deployment-relation)
      (derive :architecture-relation :relation)
      (derive :deployment-relation   :relation)
      (derive :rel                   :relation)
      ))


(comment
  ;(collect-technologies (:elements @model/state))
  (specified-model-nodes @model/state (get-view @model/state :banking/system-context-view))
  (specified-relations @model/state (get-view @model/state :banking/system-context-view))
  (specified-elements @model/state (get-view @model/state :test/banking-container-view-related))
  (specified-elements @model/state (get-view @model/state :test/banking-container-view-relations))
  
  (model/related-nodes @model/state (referenced-relations @model/state (get-view @model/state :test/banking-container-view-related)))
  (model/relations-of-nodes @model/state (referenced-model-nodes @model/state (get-view @model/state :test/banking-container-view-relations)))

  (elements-in-view @model/state (get-view @model/state :banking/container-view))
  (technologies-in-view @model/state (get-view @model/state :banking/container-view))
  ;
  )
