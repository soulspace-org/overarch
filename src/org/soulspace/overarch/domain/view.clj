;;;;
;;;; Functions for the definition and handling of views
;;;;
(ns org.soulspace.overarch.domain.view
  "Functions for the definition and handling of views."
  (:require [clojure.set :as set]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.element :as el]))

(def element->boundary
  "Maps model types to boundary types depending on the view type."
  {:system    :system-boundary
   :container :container-boundary
   :component :component-boundary})

;;;
;;; Config data for views (TODO read from classpath or fs)
;;;
(def view-config
  {:context-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                 :system :person}}
                  :relations {:els #{:request :response :publish
                                     :subscribe :send :dataflow}}
                  :hierarchical? true}
   :container-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                   :system :container :person}}
                    :relations {:els #{:request :response :publish
                                       :subscribe :send :dataflow}
                                :!to {:el :system :children? true :external? false}
                                :!from {:el :system :children? true :external? false}}
                    :as-boundary {:el :system :external? false}
                    :hierarchical? true}
   :component-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                   :system :container :component :person}}
                    :relations {:els #{:request :response :publish
                                       :subscribe :send :dataflow}}
                    :as-boundary {:els #{:system :container} :external? false}
                    :hierarchical? true}
   :system-structure-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                          :system :container :component :person}}
                           :relations {:els #{:request :response :publish
                                              :subscribe :send :dataflow}}
                           :hierarchical? true}
   :system-landscape-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                          :system :person}}
                           :relations {:els #{:request :response :publish
                                              :subscribe :send :dataflow}}
                           :hierarchical? true}
   :dynamic-view {:nodes {:els #{:enterprise-boundary :context-boundary
                                 :system :container :component :person}}
                  :relations {:els #{:step}}}
   :deployment-view {:nodes {:els #{:node :container :artifact}}
                     :relations {:els #{:link :deployed-to}}
                     :hierarchical? true}
   :deployment-architecture-view {:nodes {:els #{:node :system :container :artifact}}
                     :relations {:els #{:link :deployed-to :request :response :send :publish :subscribe :dataflow}}
                     :hierarchical? true}
   :deployment-structure-view {:nodes {:els #{:node :container}}
                               :relations {:els #{:link :deployed-to}}
                               :hierarchical? true}
   :code-view {:nodes {:els #{:annotation :class :enum :enum-value
                              :field :function :interface :method
                              :namespace :package :parameter
                              :protocol :stereotype}}
               :relations {:els #{:aggregation :association :composition
                                  :dependency :implementation :inheritance}}
               :hierarchical? true}
   :state-machine-view {:nodes {:els #{:state-machine :start-state :end-state
                                       :state :fork :join :choice
                                       :history-state :deep-history-state}}
                        :relations {:els #{:transition}}
                        :hierarchical? true}
   :use-case-view {:nodes {:els #{:use-case :actor :person :system :container
                                  :context-boundary}}
                   :relations {:els #{:uses :include :extends :generalizes}}}
   :concept-view {:nodes {:els #{:concept}}
                  :relations {:els #{:is-a :has :part-of :rel}}}
   :glossary-view {:nodes {:els #{:concept :person :system :container
                                  :organization
                                  :enterprise-boundary :context-boundary}}
                   :relations {:els #{:is-a :has :part-of :rel}}}
   :organization-structure-view {:nodes {:els #{:organization :org-unit}}
                                 :relations {:els #{:collaborates-with}}
                                 :hierarchical? true}
   :process-view {:nodes {:els #{:capability :knowledge :information :process
                                 :artifact :requirement :decision}}
                  :relations {:els #{:role-in :required-for :input-of :output-of}}}
   :model-view {:nodes {:model-node? true}
                :relations {:model-relation? true}}
           ;
   })

;;;
;;; Functions based on view configurations
;;;
(defn hierarchical?
  ""
  [view config]
  (get-in config [(:el view) :hierarchical?] false))

(defn node-pred
  "Returns a predicate function to check if a node should be rendered
   as specified in the `config` for the `view`"
  [model view config]
  (if-let [node-criteria (get-in config [(:el view) :nodes])]
    (model/criteria-predicate model node-criteria)
    (constantly false)))

(defn relation-pred
  "Returns a predicate function to check if a node should be rendered
   as specified in the `config` for the `view`"
  [model view config]
  (if-let [relation-criteria (get-in config [(:el view) :nodes])]
    (model/criteria-predicate model relation-criteria)
    (constantly false)))

(defn boundary-pred
  "Returns a predicate function to check if an element should be transformed to a boundary
   as specified in the `config` for the `view`"
  [model view config]
  (if-let [boundary-criteria (get-in config [(:el view) :as-boundary])]
    (model/criteria-predicate model boundary-criteria)
    (constantly false)))

(defn element-to-render_
  "Returns the element to render in the `view` for the given element `e` in the context of the `model`.
   Transforms a node to a boundary, if necessary."
  [model view config e]
  (if ((model view config) e)
    (assoc e :el (element->boundary (:el e)))
    e)
  )

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
(defn include-spec
  "Returns the include specification for the `view`. Defaults to :referenced-only."
  [view]
  (get view :include :referenced-only))

(defn layout-spec
  "Returns the layout specification for the `view`. Defaults to :top-down."
  [view]
  (get view :layout))

(defn legend-spec
  "Returns the legend specification for the `view`. Defaults to true."
  [view]
  (not (get view :no-legend false)))

(defn linetype-spec
  "Returns the linetype specification for the `view`."
  [view]
  (get view :linetype))

(defn selection-spec
  "Returns the selection specification for the `view`."
  [view]
  (get view :selection))

(defn sketch-spec
  "Returns the sketch specification for the `view`. Defaults to false."
  [view]
  (get view :sketch false))

(defn expand-external-spec
  "Returns the expand external specification for the `view`."
  [view]
  (get view :expand-external false))

(defn themes-spec
  "Returns the themes specification for the `view`."
  [view]
  (get view :themes []))

(defn themes->styles
  "Returns the vector of styles from the themes of the given `view`."
  [model view]
  (->> (themes-spec view)
       (map (model/element-resolver model))
       (map :styles)))

(defn styles-spec
  "Returns the styles specification for the `model` and the `view`."
  [model view]
  (apply set/union (conj
                    (themes->styles model view)
                    (get view :styles #{}))))

(defn plantuml-spec
  "Returns the plantuml specification for the `view`."
  [view]
  (get view :plantuml {}))

(defn graphviz-spec
  "Returns the graphviz specification for the `view`."
  [view]
  (get view :graphviz {}))

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
  ; use :ct here, not model/children, because the contained-in relations
  ; are not added for refs in views at the moment
  (->> (:ct view)
       (map (model/element-resolver model))))

(defn selected-elements
  "Returns the `model` elements selected by criteria for the `view`."
  [model view]
  (if-let [criteria (selection-spec view)]
    (->> model
         (model/model-elements)
         (into #{} (comp (model/filter-xf model criteria)
                         ; TODO replace partial with function
                         ; filter drop elements not rendered
                         (filter (partial render-model-element? model view)))))
    #{}))

(defn specified-elements
  "Returns the `model` elements specified for the view,
   either as refs or as selection."
  [model view]
  (let [selected (selected-elements model view)
        referenced (referenced-elements model view)]
    (el/union-by-id selected referenced)))

(defn included-elements
  "Returns the `model` elements specified and included for the view,
   e.g. related nodes or relations."
  [model view specified]
  (case (include-spec view)
    :relations
    (let [nodes (->> specified
                     (filter el/model-node?)
                     (mapcat (partial model/descendant-nodes model))
                     (map (model/element-resolver model))
                     (into #{})
                     (set/union specified)
                     (filter (partial render-model-element? model view)))
          included (->> nodes
                        (model/relations-of-nodes model)
                        (into #{}))
          elements (el/union-by-id included specified)]
      elements)

    :related
    (let [included (model/related-nodes model (filter el/model-relation? specified))
          elements (el/union-by-id included specified)]
      elements)

    :referenced-only
    specified

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
    (filter (partial render-model-element? model view) elements)))

(defn root-elements
  "Returns the root elements for a collection of `model` `elements`,
   e.g. to start the rendering of a hierarchical view with."
  [model elements]
  ; Difference of the sets of elements have to be done with difference-by-id which treats the elements as entities
  ; to preserve overrides of keys in the content references included in the view.
  ; When keys have been added or overridden in the reference in a view, the elements in the different sets
  ; are not the same values anymore and so have to be treated as entities with equals implemented on id,
  ; even when they are still immutable.
  (let [descendants (model/all-descendant-nodes model elements)]
    (el/difference-by-id elements descendants)))

(defn rendered-elements
  "Returns the elements to render in the `view` for the given `model`."
  ([model view]
   (rendered-elements model view (view-elements model view)))
  ([model view coll]
   (->> coll
        (model/all-descendant-nodes model)
        (el/union-by-id coll)
        (map (model/element-resolver model))
        (filter (partial render-model-element? model view))
        (map #(element-to-render model view %)))))

(defn elements-to-render
  "Returns the list of elements to render in the `view` or the given `coll` of
   elements, depending on the type of the view."
  ([model view]
   (root-elements model (view-elements model view)))
  ([model view coll]
   (->> coll
        (map (model/element-resolver model))
        (filter (partial render-model-element? model view))
        (map #(element-to-render model view %)))))