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
  ([view & _]
   (view-type view)))

(defn get-views
  "Returns the collection of views from the `model`."
  ([model]
   (filter el/view? (:elements model))))

(defn get-view
  "Returns the view with the given `id` from the `model`."
  ([model id]
   ((:id->element model) id)))

(defn include-spec
  "Returns the include specification for the `view`."
  ([view]
   (get-in view [:spec :include] :referenced-only)))

;;
;; View spec elements
;;

(defn layout-spec
  "Returns the layout specification for the `view`."
  ([view]
   (get-in view [:spec :layout] :top-down)))

(defn themes->styles
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

;;;
;;; View functions
;;;

;;
;; Context based content filtering
;;
(defmulti render-model-element?
  "Returns true if the element `e` is rendered in the `view`"
  view-type)

(defmulti include-content?
  "Returns true if the content of element `e` is rendered in the `view`"
  view-type)

(defmulti render-relation-node?
  "Returns true if the node will"
  view-type)

(defmulti element-to-render
  "Returns the model element to be rendered for element `e` for the `view`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  view-type)

(defn render-element?
  "Returns true if the element is should be rendered for this view type.
   Checks both sides of a relation."
  [model view e]
  (or (and (= :rel (:el e))
           (render-model-element? view (model/model-element model (:from e)))
           (render-model-element? view (model/model-element model (:to e))))
      (and (render-model-element? view e)
           (el/internal? (model/parent model e)))))

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


;; TODO: take the rendered children of referenced nodes into account and exclude
;;       nodes rendered as boundaries
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
       
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      ;;; nodes
      ;; boundaries
      (derive :enterprise-boundary               :boundary)
      (derive :system-boundary                   :boundary)
      (derive :container-boundary                :boundary)
      (derive :context-boundary                  :boundary)
      ;; roles
      (derive :actor                             :role)
      (derive :person                            :role)

      ;; architecture model nodes
      (derive :system                            :technical-architecture-model-node)
      (derive :container                         :technical-architecture-model-node)
      (derive :component                         :technical-architecture-model-node)
      (derive :technical-architecture-model-node :architecture-model-node)
      (derive :person                            :architecture-model-node)

      ;; deployment model nodes
      (derive :node                              :deployment-model-node)

      ;; use case model nodes 
      (derive :use-case                          :use-case-model-node)
      (derive :actor                             :use-case-model-node)
      (derive :person                            :use-case-model-node)
      (derive :system                            :use-case-model-node)

      ;; state machine model nodes
      (derive :state-machine                     :state-machine-model-node)
      (derive :start-state                       :state-machine-model-node)
      (derive :end-state                         :state-machine-model-node)
      (derive :state                             :state-machine-model-node)
      (derive :fork                              :state-machine-model-node)
      (derive :join                              :state-machine-model-node)
      (derive :choice                            :state-machine-model-node)
      (derive :history-state                     :state-machine-model-node)
      (derive :deep-history-state                :state-machine-model-node)

      ;; class model nodes
      (derive :class                             :class-model-node)
      (derive :enum                              :class-model-node)
      (derive :interface                         :class-model-node)
      (derive :protocol                          :class-model-node)
      (derive :field                             :class-model-node)
      (derive :method                            :class-model-node)
      (derive :function                          :class-model-node)
      (derive :package                           :class-model-node)
      (derive :namespace                         :class-model-node)
      (derive :stereotype                        :class-model-node)
      (derive :annotation                        :class-model-node)

      ;; concept model nodes
      (derive :concept                           :concept-model-node)

      ;; model nodes
      (derive :architecture-model-node           :model-node)
      (derive :deplyoment-model-node             :model-node)
      (derive :use-case-model-node               :model-node)
      (derive :state-machine-model-node          :model-node)
      (derive :class-model-node                  :model-node)
      (derive :concept-model-node                :model-node)
      (derive :boundary                          :model-node)

      ;;; model relations
      ;; architecture model relations
      (derive :request                           :architecture-model-relation)
      (derive :response                          :architecture-model-relation)
      (derive :publish                           :architecture-model-relation)
      (derive :subscribe                         :architecture-model-relation)
      (derive :send                              :architecture-model-relation)
      (derive :dataflow                          :architecture-model-relation)

      ;; deployment model relations
      (derive :link                              :deployment-model-relation)

      ;; use case model relations
      (derive :uses                              :use-case-model-relation)
      (derive :include                           :use-case-model-relation)
      (derive :extends                           :use-case-model-relation)
      (derive :generalizes                       :use-case-model-relation)

      ;; state machine model relations
      (derive :transition                        :state-machine-model-relation)

      ;; class model relations
      (derive :inheritance                       :class-model-relation)
      (derive :implementation                    :class-model-relation)
      (derive :composition                       :class-model-relation)
      (derive :aggregation                       :class-model-relation)
      (derive :association                       :class-model-relation)
      (derive :dependency                        :class-model-relation)

      ;; model relations
      (derive :architecture-model-relation       :model-relation)
      (derive :deployment-model-relation         :model-relation)
      (derive :use-case-model-relation           :model-relation)
      (derive :state-machine-model-relation      :model-relation)
      (derive :class-model-relation              :model-relation)

      (derive :rel                               :model-relation)

      ;;; model elements
      (derive :model-node                        :model-element)
      (derive :model-relation                    :model-element)
      ))

