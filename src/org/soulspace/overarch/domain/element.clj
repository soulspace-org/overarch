;;;;
;;;; Contains element specific logic
;;;;
(ns org.soulspace.overarch.domain.element
  "This namespace contains element specific logic.
   It defines the different element categories of model elements and views
   and the hierarchical relationships. The namespace also defines predicates
   to query the elements and functionality requiring only elements or collections
   of elements without references to the model as a whole."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.util.functions :as fns]))

;;;
;;; Category definitions
;;;

;;
;; Architecture model
;; 
;
; add :used-by relation?
;
(def technical-architecture-node-types
  "Technical node types in the architecture model."
  #{:system :container :component})

(def architecture-node-types
  "Node types in the architecture model."
  (set/union technical-architecture-node-types #{:person :enterprise-boundary :context-boundary}))

(def architecture-relation-types
  "Relation types in the architecture model."
  #{:rel :request :response :publish :subscribe :send :dataflow :step})

(def architecture-dependency-relation-types
  #{:request :publish :subscribe :send})

;;
;; Code Model
;;
(def code-node-types
  "Node types for code models."
  #{:annotation :class :enum :enum-value :field :function :interface
    :method :namespace :package :parameter :protocol :stereotype})
(def code-relation-types
  "Relation types for code models."
  #{:aggregation :association :composition :dependency :implementation :inheritance})

;;
;; Concept Model
;;
(def concept-node-types
  "Node types for concept models."
  (set/difference (set/union architecture-node-types #{:concept})
                  #{:component}))

(def concept-relation-types
  "Relation types of concept models."
  #{:rel :is-a :has :part-of})

;;
;; Deployment model
;;
;
; add :environment node?
;
(def deployment-node-types
  "Node types for deployment models."
  ; TODO only node and container?
  (set/union technical-architecture-node-types #{:node}))

(def deployment-relation-types
  "Relation types for deployment models."
  #{:link :deployed-to})

;;
;; State Machine model
;;
(def statemachine-node-types
  "Node types for statemachine models."
  #{:state-machine :start-state :end-state :state
    :fork :join :choice :history-state :deep-history-state})
(def statemachine-relation-types
  "Relation types for statemachine models."
  #{:transition})

;;
;; Use Case model
;;
(def usecase-node-types
  "Node types for usecase models."
  #{:use-case :actor :person :system :container :context-boundary})
(def usecase-relation-types
  "Relation types for usecase models."
  #{:uses :include :extends :generalizes})

;;
;; Organization model
;;
(def organization-node-types
  "Node types for organization models."
  (set/union #{:organization :org-unit}))

(def organization-relation-types
  "Relation types for organization models."
  #{:collaborates-with :role-in})

;;
;; Responsibility model
;;
(def responsibility-node-types
  "Node types for responsibility models."
  (set/union organization-node-types technical-architecture-node-types #{:person :context-boundary}))

(def responsibility-relation-types
  "Relation types for responsibility models."
  #{:responsible-for})

;;
;; Process model
;;
(def process-node-types
  "Node types for process models."
  #{:capability :knowledge :information :process :artifact :requirement :decision})

(def process-relation-types
  "Node types for process models."
  #{:role-in :required-for :input-of :output-of})
; :supports :resource-of?

;; 
;; General category definitions
;;
(def boundary-types
  "Element types of boundaries."
  #{:enterprise-boundary :context-boundary :system-boundary :container-boundary})

(def reference-types
  "Element types of references."
  #{:ref})

(def uml-node-types
  "Node types for UML models."
  (set/union usecase-node-types statemachine-node-types code-node-types))

(def uml-relation-types
  "Relation types of UML models."
  (set/union usecase-relation-types statemachine-relation-types code-relation-types))

(def model-node-types
  "Node types of the model."
  (set/union architecture-node-types
             deployment-node-types
             uml-node-types
             concept-node-types
             organization-node-types
             responsibility-node-types
             process-node-types))

(def model-relation-types
  "Relation types of the model."
  (set/union #{:rel :contained-in :implements}
             architecture-relation-types
             deployment-relation-types
             uml-relation-types
             concept-relation-types
             organization-relation-types
             responsibility-relation-types
             process-relation-types))

(def model-element-types
  "Element types for the model."
  (set/union model-node-types model-relation-types))

;;
;; C4 view category definitions
;; 
(def c4-view-types
  "The set of C4 view types."
  #{:context-view :container-view :component-view
    :deployment-view :system-landscape-view
    :dynamic-view})

(def context-view-element-types
  "Element types of a C4 context view."
  (set/union architecture-relation-types
             #{:person :system :enterprise-boundary :context-boundary}))

(def container-view-element-types
  "Element types of a C4 container view."
  (set/union context-view-element-types
             #{:system-boundary :container}))

(def component-view-element-types
  "Element types of a C4 component view."
  (set/union container-view-element-types
             #{:container-boundary :component}))

(def system-landscape-view-element-types
  "Element types of a C4 system-landscape view."
  context-view-element-types)

(def deployment-view-element-types
  "Element types of a C4 deployment view."
  (set/union #{:node}
             deployment-relation-types
             container-view-element-types))

(def dynamic-view-element-types
  "Element types of a C4 dynamic view."
  ; TODO
  (set/union component-view-element-types #{:step}))

;;
;; UML view category definitions
;;
(def uml-view-types
  "The set of UML view types."
  #{:use-case-view :state-machine-view :code-view})

(def use-case-view-element-types
  "Element types of a use case view."
  (set/union usecase-node-types
             usecase-relation-types))

(def state-machine-view-element-types
  "Element types of a state machine view."
  (set/union statemachine-node-types
             statemachine-relation-types))

(def code-view-element-types
  "Element types of a code view."
  (set/union code-node-types
             code-relation-types))

(def uml-view-element-types
  "Element types of UML views."
  (set/union use-case-view-element-types
             state-machine-view-element-types
             code-view-element-types))

;;
;; Concept view category definitions
;;
(def concept-view-types
  "The set of concept views types."
  #{:concept-view :glossary-view})

(def concept-view-element-types
  "Element types of a concept view."
  (set/union concept-node-types
             concept-relation-types))

(def glossary-view-element-types
  "Element types of a glossary view."
  (set/union concept-node-types
             concept-relation-types))

(def process-view-types
  "The set of process view types."
  #{:process-view})

(def process-view-element-types
  "Element types of a process view."
  (set/union process-node-types
             process-relation-types
             #{:contained-in :context-boundary}))

;;
;; Structure view types
;;
(def structure-view-types
  "The set of structure view types."
  #{:system-structure-view :deployment-structure-view :organization-structure-view})

(def system-structure-view-element-types
  "Element types of a system structure view"
  ; Technical architecture node types only?
  (set/union technical-architecture-node-types architecture-relation-types))

(def deployment-structure-view-element-types
  "Element types of a system structure view"
  ; Technical architecture node types only?
  (set/union deployment-node-types))

(def organization-structure-view-element-types
  "Element types of a system structure view"
  (set/union organization-node-types organization-relation-types))

;;
;; Other view types
;;
;  :model-view
(def model-view-types
  "The set of model-view types."
  #{:model-view})

(def model-view-element-types
  "Element types of a model-view."
  model-element-types)

;; 
;; General view category definitions
;;
(def view-types
  "The set of view types."
  (set/union c4-view-types uml-view-types concept-view-types
             structure-view-types process-view-types model-view-types))

(def hierarchical-view-types
  "The set of hierarchical view types."
  #{:context-view :container-view :component-view
    :deployment-view :deployment-structure-view
    :system-landscape-view :system-structure-view
    :state-machine-view :code-view :glossary-view
    :organization-structure-view})

;;
;; Element type vectors for canonical ordering
;;
(def model-node-type-order
  "The set of model element types as vector."
  [; nodes
   :person :organization :org-unit
   :concept
   :capability :knowledge :information :process :artifact :requirement :decision
   :use-case :actor
   :system :container :component :enterprise-boundary :context-boundary
   :state-machine :start-state :end-state
   :state :fork :join :choice :history-state :deep-history-state
   :annotation :class :enum :enum-value :field :function :interface
   :method :namespace :package :parameter :protocol :stereotype
   :node])

(def model-relation-type-order
  "The set of model element types as vector."
  [; relations
   :collaborates-with :responsible-for :role-in
   :has :is-a :part-of
   :input-of :output-of :required-for
   :uses :include :extends :generalizes
   :request :response :publish :subscribe :send :dataflow :step
   :transition
   :implementation :inheritance :composition :aggregation :association :dependency
   :link :deployed-to
   :rel :contained-in :implements])

(def model-element-type-order
  "The set of model element types as vector."
  (into [] (concat model-node-type-order model-relation-type-order)))

(def view-type-order
  "The set of view types as vector."
  [:concept-view :process-view :use-case-view :context-view :container-view :component-view
   :system-landscape-view :system-structure-view :dynamic-view
   :state-machine-view :code-view :deployment-view :deployment-structure-view
   :organization-structure-view :glossary-view :model-view])

(comment
  (count model-element-types)
  (count model-element-type-order)
  (= (count model-element-types) (count model-element-type-order))
  (= model-element-types (set model-element-type-order))
  (set/difference model-element-types (set model-element-type-order))
  (count view-types)
  (count view-type-order)
  (= (count view-types) (count view-type-order))
  (= view-types (set view-type-order))
  (set/difference view-types (set view-type-order))
  ;
  )

;;;
;;; Hierarchy of element types
;;;
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

      ;;; architecture model
      ;; architecture model nodes
      (derive :system                            :technical-architecture-model-node)
      (derive :container                         :technical-architecture-model-node)
      (derive :component                         :technical-architecture-model-node)
      (derive :technical-architecture-model-node :architecture-model-node)
      (derive :person                            :architecture-model-node)
      (derive :architecture-model-node           :architecture-model-element)

      ;; architecture model relations
      (derive :request                           :architecture-model-relation)
      (derive :response                          :architecture-model-relation)
      (derive :publish                           :architecture-model-relation)
      (derive :subscribe                         :architecture-model-relation)
      (derive :send                              :architecture-model-relation)
      (derive :dataflow                          :architecture-model-relation)
      (derive :step                              :architecture-model-relation)
      (derive :architecture-model-relation       :architecture-model-element)

      ;;; deployment model
      ;; deployment model nodes
      (derive :node                              :deployment-model-node)
      (derive :deployment-model-node             :deployment-model-element)

      ;; deployment model relations
      (derive :link                              :deployment-model-relation)
      (derive :deployed-to                       :deployment-model-relation)
      (derive :deployment-model-relation         :deployment-model-element)

      ;;; use case model
      ;; use case model nodes 
      (derive :actor                             :actor-node)
      (derive :person                            :actor-node)
      (derive :system                            :actor-node)
      (derive :container                         :actor-node)

      (derive :use-case                          :use-case-model-node)
      (derive :actor-node                        :use-case-model-node)
      (derive :use-case-model-node               :use-case-model-element)

      ;; use case model relations
      (derive :uses                              :use-case-model-relation)
      (derive :include                           :use-case-model-relation)
      (derive :extends                           :use-case-model-relation)
      (derive :generalizes                       :use-case-model-relation)
      (derive :use-case-model-relation           :use-case-model-element)

      ;;; state machine model
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
      (derive :state-machine-model-node          :state-machine-model-element)

      ;; state machine model relations
      (derive :transition                        :state-machine-model-relation)
      (derive :state-machine-model-relation      :state-machine-model-element)

      ;;; code model
      ;; code model nodes
      (derive :annotation                        :code-model-node)
      (derive :class                             :code-model-node)
      (derive :enum                              :code-model-node)
      (derive :enum-value                        :code-model-node)
      (derive :field                             :code-model-node)
      (derive :function                          :code-model-node)
      (derive :interface                         :code-model-node)
      (derive :method                            :code-model-node)
      (derive :namespace                         :code-model-node)
      (derive :package                           :code-model-node)
      (derive :parameter                         :code-model-node)
      (derive :protocol                          :code-model-node)
      (derive :stereotype                        :code-model-node)
      (derive :code-model-node                   :code-model-element)

      ;; code model relations
      (derive :inheritance                       :code-model-relation)
      (derive :implementation                    :code-model-relation)
      (derive :composition                       :code-model-relation)
      (derive :aggregation                       :code-model-relation)
      (derive :association                       :code-model-relation)
      (derive :dependency                        :code-model-relation)
      (derive :code-model-relation               :code-model-element)

      ;;; concept model
      ;; concept model nodes
      (derive :concept                           :concept-model-node)
      (derive :concept-model-node                :concept-model-element)

      ;; concept model relations
      (derive :is-a                              :concept-model-relation)
      (derive :has                               :concept-model-relation)
      (derive :part-of                           :concept-model-relation)
      (derive :concept-model-relation            :concept-model-element)

      ;;; organization model
      ;; organization model nodes
      (derive :organization                      :organization-model-node)
      (derive :org-unit                          :organization-model-node)
      (derive :organization-model-node           :organization-model-element)

      ;; organization model relations
      (derive :collaborates-with                 :organization-model-relation)
      (derive :role-in                           :organization-model-relation)
      (derive :organization-model-relation       :organization-model-element)

      ;;; responsibility model
      ;; responsibility model nodes
      (derive :organization                      :responsibility-model-node)
      (derive :org-unit                          :responsibility-model-node)
      (derive :system                            :responsibility-model-node)
      (derive :container                         :responsibility-model-node)
      (derive :component                         :responsibility-model-node)
      (derive :person                            :responsibility-model-node)
      (derive :context-boundary                  :responsibility-model-node)
      (derive :responsibility-model-node         :responsibility-model-element)

      ;; responsibility model relations
      (derive :responsible-for                   :responsibility-model-relation)
      (derive :responsibility-model-relation     :responsibility-model-element)

      ;;; process model
      ;; process model nodes
      (derive :capability                        :process-model-node)
      (derive :knowledge                         :process-model-node)
      (derive :information                       :process-model-node)
      (derive :process                           :process-model-node)
      (derive :artifact                          :process-model-node)
      (derive :requirement                       :process-model-node)
      (derive :decision                          :process-model-node)
      ; (derive :test                              :process-model-node)
      (derive :process-model-node                :process-model-element)
      ;; process model relations
      (derive :role-in                           :process-model-relation)
      (derive :required-for                      :process-model-relation)
      (derive :input-of                          :process-model-relation)
      (derive :output-of                         :process-model-relation)
      (derive :process-model-relation            :process-model-element)

      ;; model nodes
      (derive :architecture-model-node           :model-node)
      (derive :deployment-model-node             :model-node)
      (derive :use-case-model-node               :model-node)
      (derive :state-machine-model-node          :model-node)
      (derive :code-model-node                   :model-node)
      (derive :concept-model-node                :model-node)
      (derive :organization-model-node           :model-node)
      (derive :process-model-node                :model-node)
      (derive :boundary                          :model-node)

      ;; model relations
      (derive :architecture-model-relation       :model-relation)
      (derive :deployment-model-relation         :model-relation)
      (derive :use-case-model-relation           :model-relation)
      (derive :state-machine-model-relation      :model-relation)
      (derive :code-model-relation               :model-relation)
      (derive :concept-model-relation            :model-relation)
      (derive :organization-model-relation       :model-relation)
      (derive :process-model-relation            :model-relation)

      (derive :implements                        :model-relation)
      (derive :contained-in                      :model-relation)
      (derive :rel                               :model-relation)

      ;;; model elements
      (derive :model-node                        :model-element)
      (derive :model-relation                    :model-element)))

;;
;; Predicates
;;
(defn element?
  "Returns true if the given element `e` has a type (:el key)."
  [e]
  (not= nil (:el e)))

(defn identifiable?
  "Returns true if the given element `e` has an ID (:id key)."
  [e]
  (not= nil (:id e)))

(defn identifiable-element?
  "Returns true if the given element `e` is an element and named."
  [e]
  (and (element? e) (identifiable? e)))

(defn namespaced?
  "Returns true, if the id of element `e` has a namespace."
  [e]
  (and (:id e) (not= nil (namespace (:id e)))))

(defn named?
  "Returns true if the given element `e` has a name (:name key)."
  [e]
  (not= nil (:name e)))

(defn named-element?
  "Returns true if the given element `e` is an element and named."
  [e]
  (and (element? e) (named? e)))

(defn identifiable-named-element?
  "Returns true if the given element `e` is an element, identifiable and named."
  [e]
  (and (element? e) (identifiable? e) (named? e)))

(defn relational?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (not= nil (:from e)) (not= nil (:to e))))

(defn relational-element?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (element? e) (relational? e)))

(defn identifiable-relational-element?
  "Returns true if the given element `e` is an identifiable relation."
  [e]
  (and (element? e) (identifiable? e) (relational? e)))

(defn named-relational-element?
  "Returns true if the given element `e` is a named relation."
  [e]
  (and (named-element? e) (relational-element? e)))

(defn external?
  "Returns true if the given element `e` is external."
  [e]
  (get e :external false))

(defn internal?
  "Returns true if the given element `e` is internal."
  [e]
  (not (external? e)))

(defn collapsed?
  "Returns true if the given element `e` is external."
  [e]
  (get e :collapsed false))

(defn boundary?
  "Returns true if `e` is a boundary."
  [e]
  (contains? boundary-types (:el e)))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-element-types (:el e)))

(defn model-relation?
  "Returns true if the given element `e` is a model relation."
  [e]
  (and (relational? e)
       (contains? model-relation-types (:el e))))

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (model-relation? e))))

(defn technical-architecture-node?
  "Returns true if the given element `e` is a technical architecture model node."
  [e]
  (contains? technical-architecture-node-types (:el e)))

(defn architecture-node?
  "Returns true if the given element `e` is a architecture model node."
  [e]
  (contains? architecture-node-types (:el e)))

(defn architecture-relation?
  "Returns true if the given element `e` is a architecture model relation."
  [e]
  (contains? architecture-relation-types (:el e)))

(defn deployment-node?
  "Returns true if the given element `e` is a deployment model node."
  [e]
  (contains? deployment-node-types (:el e)))

(defn deployment-relation?
  "Returns true if the given element `e` is a deployment model relation."
  [e]
  (contains? deployment-relation-types (:el e)))

(defn usecase-node?
  "Returns true if the given element `e` is a usecase model node."
  [e]
  (contains? usecase-node-types (:el e)))

(defn usecase-relation?
  "Returns true if the given element `e` is a usecase model relation."
  [e]
  (contains? usecase-relation-types (:el e)))

(defn statemachine-node?
  "Returns true if the given element `e` is a statemachine model node."
  [e]
  (contains? statemachine-node-types (:el e)))

(defn statemachine-relation?
  "Returns true if the given element `e` is a statemachine model relation."
  [e]
  (contains? statemachine-relation-types (:el e)))

(defn code-model-node?
  "Returns true if the given element `e` is a code model node."
  [e]
  (contains? code-node-types (:el e)))

(defn code-model-relation?
  "Returns true if the given element `e` is a code model relation."
  [e]
  (contains? code-relation-types (:el e)))

(defn concept-model-node?
  "Returns true if the given element `e` is a concept model node."
  [e]
  (contains? concept-node-types (:el e)))

(defn concept-model-relation?
  "Returns true if the given element `e` is a concept model relation."
  [e]
  (contains? concept-relation-types (:el e)))

(defn organization-model-node?
  "Returns true if the given element `e` is a organization model node."
  [e]
  (contains? organization-node-types (:el e)))

(defn organization-model-relation?
  "Returns true if the given element `e` is a organization model relation."
  [e]
  (contains? organization-relation-types (:el e)))

(defn process-model-node?
  "Returns true if the given element `e` is a process model node."
  [e]
  (contains? process-node-types (:el e)))

(defn process-model-relation?
  "Returns true if the given element `e` is a process model relation."
  [e]
  (contains? process-relation-types (:el e)))

(defn responsibility-model-node?
  "Returns true if the given element `e` is a responsibility model node."
  [e]
  (contains? responsibility-node-types (:el e)))

(defn responsibility-model-relation?
  "Returns true if the given element `e` is a responsibility model relation."
  [e]
  (contains? responsibility-relation-types (:el e)))

(defn reference?
  "Returns true if the given element `e` is a reference."
  [e]
  (boolean (:ref e)))

(defn unresolved-ref?
  "Returns true if the given element `e` is an unresolved reference."
  [e]
  (boolean (:unresolved-ref e)))

(defn node-of?
  "Returns true if the given element `e` is a node of `kind`."
  [kind e]
  (boolean (and (model-node? e) (= (:el e) kind))))

(defn relation-of?
  "Returns true if the given element `e` is a relation of `kind`."
  [kind e]
  (boolean (and (model-relation? e) (= (:el e) kind))))

(defn theme?
  "Returns true if the given element `e` is a theme."
  [e]
  (= :theme (:el e)))

(defn view?
  "Returns true if the given element `e` is a view."
  [e]
  (contains? view-types (:el e)))

(defn hierarchical-view?
  "Returns true if the given element `e` is a hierarchical view."
  [e]
  (contains? hierarchical-view-types (:el e)))

(defn view-of?
  "Returns true if the given element `e` is a view of `kind`."
  [e kind]
  (and (view? e) (= (:el e) kind)))

(defn synthetic?
  "Returns true, if the element `e` is a synthetic element."
  [e]
  (get e :synthetic false))

;;
;; Element transducer functions
;;
(defn value-xf
  "Returns a transducer to extract the value of the key `k` of each element."
  [k]
  (map k))

(def ids-xf
  "Returns a transducer to extract the id of each element."
  (comp (filter identifiable?)
        (map :id)))

(def namespaces-xf
  "Returns a transducer to extract the namespaces of some elements."
  (comp ids-xf
        (map namespace)))

(def node-ids-xf
  "Returns a transducer to extract the id of each node."
  (comp (remove relational?)
        (remove view?)
        ids-xf))

;;
;; Functions 
;;
(defn id
  "Returns the id of the element or ref `e`."
  [e]
  (cond
    (keyword? e) e
    (reference? e) (:ref e)
    (identifiable? e) (:id e)))

(defn element->ref
  "Returns a ref for the element `e`, if it is identifiable."
  [e]
  (when-let [id (:id e)]
    {:ref id}))

(defn element-name
  "Returns the name of the element `e`."
  [e]
  (if (:name e)
    (fns/single-line (:name e))
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (if-let [id (:id e)]
    (if-let [el-ns (namespace id)]
      el-ns
      "")
    ""))

(defn elements-by-namespace
  "Returns the elements of the `coll` grouped by namespace."
  [coll]
  (group-by element-namespace coll))

(defn generate-node-id
  "Generates an identifier for element `e` based on the id of the parent `p`.
   
   The generated id takes the id of `p` as prefix and appends the lowercase
   name of `e` and the element type of `e` separated by a hyphen."
  ([e p]
   (if (and e p (:id p) (:name e))
     (let [p-namespace (namespace (:id p))
           p-name (name (:id p))]
       (keyword (str p-namespace
                     "/"
                     p-name
                     "-"
                     (str/replace (str/lower-case (:name e)) " " "-") ; replace spaces with hyphens
                     "-"
                     (name (:el e)))))
     (println "generate-node-id: Missing parent or name for element" e))))

(defn generate-relation-id
  "Generates an identifier for a relation `r`.
   
   The generated id takes the id of the referrer as prefix and appends the relation type
   of the relation and the name part of the referred id separated by a hyphen."
  ([{:keys [el from to]}]
   (generate-relation-id el from to))
  ([el from to]
   (keyword (str (namespace from) "/"
                 (name from) "-" (name el) "-" (name to)))))

(defn scope-element
  "Transforms the element `e` with the internal/external attribute set according to the namespace `scope-ns`."
  [scope-ns e]
  (if (str/starts-with? (element-namespace e) scope-ns)
    (assoc e :external false)
    (assoc e :external true)))

;;
;; Accessors and transformations
;;
(defn id->element-map
  "Returns am map of id -> element for the given `elements`."
  ([elements]
   (->> elements
        (filter identifiable-element?)
        (map (fn [e] [(:id e) e]))
        (into {}))))

(defn union-by-id
  "Returns a set that is the union of the input `sets`.
   Equality is based on the :id key of the element maps in the sets, not on value equality of the maps (entity equality vs. value equality).
   Element maps with the same id will be merged in left-to-right order. If a key occurs in more than one map, the mapping from the latter (left-to-right) will be the mapping in the result."
  [& sets]
  (->> sets
       ; (map (partial traverse id->element))
       (map id->element-map)
       (apply (partial merge-with merge))
       (vals)
       (set)))

(comment
  (union-by-id #{} #{})
  (union-by-id #{{:el :test :id :hello :name "Hello"}} #{{:el :test :id :world  :name "World"}})
  (union-by-id #{{:el :test :id :hello :name "Hello"} {:el :test :id :wonderful :name "Wonderful"}} #{{:el :test :id :world  :name "World"}})
  (union-by-id #{{:el :test :id :hello :name "Hello"}} #{{:el :test :id :hello :name "Hello" :collapsed true}})
  (union-by-id #{{:el :test :id :hello :name "Hello" :collapsed true}} #{{:el :test :id :hello :name "Hello"}})
  (union-by-id #{{:el :test :id :hello :name "Hello" :collapsed true}} #{{:el :test :id :hello :name "Hello" :collapsed false}})
  (union-by-id #{{:el :test :id :hello :name "Hello"}} #{{:el :test :id :hello :name "Hello" :collapsed true}}  #{{:el :test :id :hello :name "Hello" :external true}})
  ;
  )

(defn difference-by-id
  "Returns a set of elements that is the difference of the `base-set` and the rest of the `sets`.
   Equality is based on the id (:id key) of the element maps in the sets, not on value equality of the maps (entity equality vs. value equality)."
  [base-set & sets]
  (let [base-map (id->element-map base-set)
        base-ids (into #{} (keys base-map))
        diff-ids (into #{} (mapcat #(map :id %) sets))
        remaining-ids (set/difference base-ids diff-ids)]
    (into #{} (map base-map remaining-ids))))

(defn technologies
  "Returns a vector of the technologies used by the element `e`."
  [e]
  (fns/tokenize-string (get e :tech "")))

;;;
;;; Criteria Predicates
;;; 
(defn model-node-pred
  "Returns a predicate that returns true, if the check for model node on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (model-node? e))))

(defn model-relation-pred
  "Returns a predicate that returns true, if the check for model relation on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (model-relation? e))))

(defn namespace-pred
  "Returns a predicate that returns true, if `v`is the namespace of element `e`."
  [v]
  (fn [e]
    (= (name v) (element-namespace e))))

(defn namespaces-pred
  "Returns a predicate that returns true, if the set of namespaces `v` contains the namespace of element `e`."
  [v]
  (fn [e]
    (contains? (name v) (element-namespace e))))

(defn namespace-prefix-pred
  "Returns a predicate that returns true, if `v`is a prefix of the namespace of element `e`."
  [v]
  (fn [e]
    (and (identifiable-element? e)
         (str/starts-with? (element-namespace e) v))))

(defn namespace-prefixes-pred
  "Returns a predicate that returns true, if one of `v`is a prefix of the namespace of element `e`."
  [v]
  (fn [e]
    (some #(and (identifiable-element? e)
                (str/starts-with? (element-namespace e) %)) v)))

(defn from-namespace-pred
  "Returns a predicate that returns true, if `v`is the namespace of the element referenced by the from id of relation `e`."
  [v]
  (fn [e]
    (= (name v) (namespace (get e :from :no-namespace/no-name)))))

(defn from-namespaces-pred
  "Returns a predicate that returns true, if the set of namespaces `v` contains the namespace of the from id of relation `e`."
  [v]
  (fn [e]
    (contains? (name v) (namespace (get e :from :no-namespace/no-name)))))

(defn from-namespace-prefix-pred
  "Returns a predicate that returns true, if `v`is a prefix of the namespace of element referenced by the from id of relation `e`."
  [v]
  (fn [e]
    (str/starts-with? (namespace (get e :from :no-namespace/no-name)) v)))

(defn from-namespace-prefixes-pred
  "Returns a predicate that returns true, if one of `v`is a prefix of the namespace of element referenced by the from id of relation `e`."
  [v]
  (fn [e]
    (some #(str/starts-with? (namespace (get e :from :no-namespace/no-name)) %) v)))

(defn to-namespace-pred
  "Returns a predicate that returns true, if `v`is the namespace of the element referenced by the to id of relation `e`."
  [v]
  (fn [e]
    (= (name v) (namespace (get e :to :no-namespace/no-name)))))

(defn to-namespaces-pred
  "Returns a predicate that returns true, if the set of namespaces `v` contains the namespace of the to id of relation `e`."
  [v]
  (fn [e]
    (contains? (name v) (namespace (get e :to :no-namespace/no-name)))))

(defn to-namespace-prefix-pred
  "Returns a predicate that returns true, if `v`is a prefix of the namespace of element referenced by the to id of relation `e`."
  [v]
  (fn [e]
    (str/starts-with? (namespace (get e :to :no-namespace/no-name)) v)))

(defn to-namespace-prefixes-pred
  "Returns a predicate that returns true, if `v`is a prefix of the namespace of element referenced by the to id of relation `e`."
  [v]
  (fn [e]
    (some #(str/starts-with? (namespace (get e :to :no-namespace/no-name)) %) v)))

(defn id-check-pred
  "Returns a predicate that returns true if the check for id on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :id false))))

(defn id-pred
  "Returns a predicate that returns true if the id of `e` is equal to `v`."
  [v]
  (fn [e]
    (= (keyword v) (:id e))))

(defn ids-pred
  "Returns a predicate that returns true if the id of `e` is contained in `v`."
  [v]
  (fn [e]
    (contains? v (:id e))))

(defn from-pred
  "Returns a predicate that returns true if the from id of the relation `e` is equal to `v`."
  [v]
  (fn [e]
    (= (keyword v) (:from e))))

(defn to-pred
  "Returns a predicate that returns true if the to id of the relation `e` is equal to `v`."
  [v]
  (fn [e]
    (= (keyword v) (:to e))))

(defn el-pred
  "Returns a predicate that returns true if the element type of `e` is equal to `v`."
  [v]
  (fn [e]
    (isa? element-hierarchy (:el e) v)))

(defn els-pred
  "Returns a predicate that returns true if the element type of `e` is contained in `v`."
  [v]
  (fn [e]
    (contains? v (:el e))) ; TODO use isa? too
  )

(defn subtype-check-pred
  "Returns a predicate that returns true if the check for subtype on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :subtype false))))

(defn subtype-pred
  "Returns a predicate that returns true if the subtype of `e` is equal to `v`."
  [v]
  (fn [e]
    (= (keyword v) (:subtype e))))

(defn subtypes-pred
  "Returns a predicate that returns true if the subtype of `e` is contained in `v`."
  [v]
  (fn [e]
    (contains? v (:subtype e))))

(defn maturity-check-pred
  "Returns a predicate that returns true if the check for maturity on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :maturity false))))

(defn maturity-pred
  "Returns a predicate that returns true if the maturity of `e` is equal to `v`."
  [v]
  (fn [e]
    (= (keyword v) (:maturity e))))

(defn maturities-pred
  "Returns a predicate that returns true if the maturity of `e` is contained in `v`."
  [v]
  (fn [e]
    (contains? v (:maturity e))))

(defn synthetic-check-pred
  "Returns a predicate that returns true if the check for synthetic on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (boolean (synthetic? e)))))

(defn name-check-pred
  "Returns a predicate that returns true if the check for name on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :name false))))

(defn name-pred
  "Returns a predicate that returns true if the name of `e` matches `v`."
  [v]
  (fn [e]
    (if-let [s (:name e)]
      (re-matches (re-pattern v) s)
      false)))

(defn name-prefix-pred
  "Returns a predicate that returns true if the name of `e` starts with `v`."
  [v]
  (fn [e]
    (str/starts-with? (:name e) v)))

(defn desc-check-pred
  "Returns a predicate that returns true if the check for desc on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :desc false))))

(defn desc-pred
  "Returns a predicate that returns true if the description of `e` matches the regular expression`v`."
  [v]
  (fn [e]
    (if-let [s (:desc e)]
      (re-matches (re-pattern v) s)
      false)))

(defn doc-check-pred
  "Returns a predicate that returns true if the check for documentation on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :doc false))))

(defn doc-pred
  "Returns a predicate that returns true if the documentation of `e` matches the regular expression`v`."
  [v]
  (fn [e]
    (if-let [s (:doc e)]
      (re-matches (re-pattern v) s)
      false)))

(defn tech-check-pred
  "Returns a predicate that returns true if the check for tech on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :tech false))))

(defn tech-pred
  "Returns a predicate that returns true if `v` is a technology of `e`."
  [v]
  (fn [e]
    (contains? (set (technologies e)) v)))

(defn techs-pred
  "Returns a predicate that returns true if any of the technologies in `v` are technologies of `e`."
  [v]
  (fn [e]
    (seq (set/intersection v (set (technologies e))))))

(defn all-techs-pred
  "Returns a predicate that returns true if all of the technologies in `v` are technologies of `e`."
  [v]
  (fn [e]
    (set/subset? (set v) (set (technologies e)))))

(defn tags-check-pred
  "Returns a predicate that returns true if the check for tags on `e` equals the boolean value `v`"
  [v]
  (fn [e]
    (= v (get e :tags false))))

(defn tag-pred
  "Returns a predicate that returns true if `v` is a tag of `e`."
  [v]
  (fn [e]
    (contains? (:tags e) v)))

(defn tags-pred
  "Returns a predicate that returns true if any of the tags in `v` are tags of `e`."
  [v]
  (fn [e]
    (seq (set/intersection v (:tags e)))))

(defn all-tags-pred
  "Returns a predicate that returns true if all of the tags in `v` are tags of `e`."
  [v]
  (fn [e]
    (set/subset? (set v) (:tags e))))

(defn key-check-pred
  "Returns a predicate that returns true if the check for the key `k` on element `e` equals the boolean value `v`.
   Useful to check for custom keys."
  [[k v]]
  (fn [e]
    (= v (boolean (get e (keyword k) false)))))

(defn key-pred
  "Returns a predicate that returns true if the check for `entry` on element `e` equals the boolean value `v."
  [[k v]]
  (fn [e]
    (= v (get e (keyword k)))))

(comment
  ((tech-pred "Datomic") {:tech "Datomic"})
  ((tag-pred "Datomic") {:tags #{"Datomic"}})
  ((key-check-pred [:tech true]) {:tech "Datomic"})
  ((key-check-pred [:tech true]) {:name "Datomic"})
  ((key-pred [:tech "Datomic"]) {:tech "Datomic"})
  ((key-pred [:tech "Datomic"]) {:tech "Blubb"})
  ((desc-pred "(?i).*account.*") {:desc "Application to manage accounts."})
  ((desc-pred "(?i).*account.*") {})
  ;
  )