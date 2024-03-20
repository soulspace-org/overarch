;;;;
;;;; contains element specific logic
;;;;
(ns org.soulspace.overarch.domain.element
  (:require [clojure.string :as str]
            [clojure.set :as set]))

;;;
;;; Category definitions
;;;

;;
;; Category definitions for C4 architecture and deployment models
;; 
(def technical-architecture-node-types
  "Technical node types in the architecture model."
  #{:system :container :component})

(def architecture-node-types
  "Node types in the architecture model."
  (set/union technical-architecture-node-types #{:person :enterprise-boundary :context-boundary}))

(def architecture-relation-types
  "Relation types in the architecture model."
  #{:rel :request :response :publish :subscribe :send :dataflow})

(def deployment-node-types
  "Node types for deployment models."
  (set/union technical-architecture-node-types #{:node}))

(def deployment-relation-types
  "Relation types for deployment models."
  #{:link})

;;
;; Category definitions for UML models
;;
(def usecase-node-types
  "Node types for usecase models."
  #{:use-case :actor :person :system :container :context-boundary})
(def usecase-relation-types
  "Relation types for usecase models."
  #{:uses :include :extends :generalizes})

(def statemachine-node-types
  "Node types for statemachine models."
  #{:state-machine :start-state :end-state :state
    :fork :join :choice :history-state :deep-history-state})
(def statemachine-relation-types
  "Relation types for statemachine models."
  #{:transition})

(def class-node-types
  "Node types for class models."
  #{:class :enum :interface :field :method :function
    :package :namespace :stereotype :annotation :protocol})
(def class-relation-types
  "Relation types for class models."
  #{:inheritance :implementation :composition :aggregation :association :dependency})

(def uml-node-types
  "Node types for UML models."
  (set/union usecase-node-types statemachine-node-types class-node-types))

(def uml-relation-types
  "Relation types of UML models."
  (set/union usecase-relation-types statemachine-relation-types class-relation-types))

;;
;; Concept category definitions
;;
(def concept-node-types
  "Node types for concept models."
  (set/difference (set/union architecture-node-types #{:concept}) #{:component}))

(def concept-relation-types
  "Relation types of concept models."
  #{:rel :is-a :has})

;; 
;; General category definitions
;;
(def boundary-types
  "Element types of boundaries."
  #{:enterprise-boundary :context-boundary :system-boundary :container-boundary})

(def reference-types
  "Element types of references."
  #{:ref})

(def model-node-types
  "Node types of the model."
  (set/union architecture-node-types
             deployment-node-types
             uml-node-types
             concept-node-types))

(def model-relation-types
  "Relation types of the model."
  (set/union #{:rel}
             architecture-relation-types
             deployment-relation-types
             uml-relation-types
             concept-relation-types))

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

(def code-view-element-types
  "Element types of a C4 code view."
  #{})

(def system-landscape-view-element-types
  "Element types of a C4 system-landscape view."
  context-view-element-types)

(def deployment-view-element-types
  "Element types of a C4 deployment view."
  (set/union container-view-element-types
             deployment-relation-types
             #{:node}))

(def dynamic-view-element-types
  "Element types of a C4 dynamic view."
  component-view-element-types)

;;
;; UML view category definitions
;;
(def uml-view-types
  "The set of UML view types."
  #{:use-case-view :state-machine-view :class-view})

(def use-case-view-element-types
  "Element types of a use case view."
  (set/union usecase-node-types
             usecase-relation-types))

(def state-machine-view-element-types
  "Element types of a state machine view."
  (set/union statemachine-node-types
             statemachine-relation-types))

(def class-view-element-types
  "Element types of a class view."
  (set/union class-node-types
             class-relation-types))

(def uml-view-element-types
  "Element types of UML views."
  (set/union use-case-view-element-types
             state-machine-view-element-types
             class-view-element-types))

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

;; 
;; General view category definitions
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
    :glossary-view})


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

      ;; architecture model nodes
      (derive :system                            :technical-architecture-model-node)
      (derive :container                         :technical-architecture-model-node)
      (derive :component                         :technical-architecture-model-node)
      (derive :technical-architecture-model-node :architecture-model-node)
      (derive :person                            :architecture-model-node)

      ;; deployment model nodes
      (derive :node                              :deployment-model-node)

      ;; use case model nodes 
      (derive :actor                             :actor-node)
      (derive :person                            :actor-node)
      (derive :system                            :actor-node)
      (derive :container                         :actor-node)

      (derive :use-case                          :use-case-model-node)
      (derive :actor-node                        :use-case-model-node)

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

      ;; concept model relations
      (derive :is-a                              :concept-model-relation)
      (derive :has                               :concept-model-relation)

      ;; model relations
      (derive :architecture-model-relation       :model-relation)
      (derive :deployment-model-relation         :model-relation)
      (derive :use-case-model-relation           :model-relation)
      (derive :state-machine-model-relation      :model-relation)
      (derive :class-model-relation              :model-relation)
      (derive :concept-model-relation            :model-relation)

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
  (and (element? e) (not= nil (:from e)) (not= nil (:to e))))

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

(defn tech?
  "Returns true if the given element `e` has a tech (:tech key)."
  [e]
  (not= nil (:tech e)))

(defn boundary?
  "Returns true if `e` is a boundary."
  [e]
  (contains? boundary-types (:el e)))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-element-types (:el e)))

(defn model-relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (and (relational? e)
       (contains? model-relation-types (:el e))))

(defn model-node?
  "Returns true if the given element is a node in the model element graph.
   A model node is a model element which is not a relation."
  [e]
  (and (model-element? e) (not (model-relation? e))))

(defn child?
  "Returns true, if element `e` is a child of model element `p`."
  [e p]
  ; TODO check (:ct p) for e
  (and (identifiable-element? e)
       (identifiable-element? p)
       (model-element? p)))


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

(defn class-model-node?
  "Returns true if the given element `e` is a class model node."
  [e]
  (contains? class-node-types (:el e)))

(defn class-model-relation?
  "Returns true if the given element `e` is a class model relation."
  [e]
  (contains? class-relation-types (:el e)))

(defn concept-model-node?
  "Returns true if the given element `e` is a concept model node."
  [e]
  (contains? concept-node-types (:el e)))

(defn concept-model-relation?
  "Returns true if the given element `e` is a concept model relation."
  [e]
  (contains? concept-relation-types (:el e)))

(defn reference?
  "Returns true if the given element `e` is a reference."
  [e]
  (:ref e))

(defn unresolved-ref?
  "Returns true if the given element `e` is a reference."
  [e]
  (:unresolved-ref e))

(defn node-of?
  "Returns true if the given element `e` is a node of `kind`."
  [kind e]
  (and (model-node? e) (= (:el e) kind)))

(defn relation-of?
  "Returns true if the given element `e` is a relation of `kind`."
  [kind e]
  (and (model-relation? e) (= (:el e) kind)))

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

;;
;; Functions 
;;
(defn element-name
  "Returns the name of the element `e`."
  [e]
  (if (:name e)
    (:name e)
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

(defn element-namespace
  "Returns the namespace of the element `e`."
  [e]
  (when-let [id (:id e)]
    (namespace id)))

(defn elements-by-namespace
  "Returns the elements of the `coll` grouped by namespace."
  [coll]
  (group-by element-namespace coll))

(defn generate-node-id
  "Generates an identifier for element `e` based on the id of the parent `p`.
   
   The generated id takes the id of `p` as prefix and appends the lowercase
   name of `e` and the element type of `e` separated by a hyphen."
  ([e p]
   (when (and e p (:id p))
     (let [p-namespace (namespace (:id p))
           p-name (name (:id p))]
       (keyword (str p-namespace "/"
                     p-name "-"
                     (str/lower-case (:name e)) "-"
                     (name (:el e))))))))

(defn generate-relation-id
  "Generates an identifier for a relation `r`.
   
   The generated id takes the id of the referrer as prefix and appends the relation type
   of the relation and the name part of the referred id separated by a hyphen."
  ([{:keys [el from to]}]
   (generate-relation-id el from to))
  ([el from to]
   (keyword (str (namespace from) "/"
                 (name from) "-" (name el) "-" (name to)))))

;;
;; filtering element colletions by criteria
;;
(defn criterium-predicate
  "Returns a predicate for the given `criterium`."
  [[k v]]
  (cond
    (= :namespace k)
    #(= v (element-namespace %))
    (= :namespaces k)
    #(contains? v (element-namespace %))
    (= :namespace-prefix k)
    #(str/starts-with? (element-namespace %) v)

    (= :type k)
    #(= v (:el %))
    (= :types k)
    #(contains? v (:el %))

    (= :subtype k)
    #(= v (:subtype %))
    (= :subtypes k)
    #(contains? v (:subtype %))

    (= :external k)
    #(= v (external? %))

    (= :tech k)
    #(= v (:tech %))
    (= :techs k)
    #(contains? v (:tech %))

    (= :tag k)
    #(contains? (:tags %) v)
    (= :tags k)
    #(set/intersection v (:tags %))

    :else
    (println "unknown criterium" (name k))))

(defn filter-xf
  "Returns a filter transducer for the given `criteria`."
  [criteria]
  (loop [remaining criteria
         filter-predicates []]
    (if (seq remaining)
      (recur (rest remaining)
             (conj filter-predicates (criterium-predicate (first remaining))))
      ; compose the filtering functions and create a filter transducer
      (filter (apply every-pred (remove nil? filter-predicates))))))

;;
;; recursive traversal of the hierarchical model
;;
(defn traverse
  "Recursively traverses the `coll` of elements and returns the elements (selected
   by the optional `select-fn`) and transformed by the `step-fn`.

   `select-fn` - a predicate on the current element
   `step-fn` - a function with three signatures [], [acc] and [acc e]
   
   The no args signature of the `step-fn` should return an empty accumulator,
   the one args signature extracts the result from the accumulator on return
   and the 2 args signature receives the accumulator and the current element and
   should add the transformed element to the accumulator."
  ([step-fn coll]
   ; selection might be handled in the step function
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (first coll)]
                 (recur (trav (step-fn acc e) (:ct e))
                        (rest coll)))
               (step-fn acc)))]
     (trav (step-fn) coll)))
  ([select-fn step-fn coll]
   ; selection handled by th select function
   (letfn [(trav [acc coll]
             (if (seq coll)
               (let [e (first coll)]
                 (if (select-fn e)
                   (recur (trav (step-fn acc e) (:ct e))
                          (rest coll))
                   (recur (trav acc (:ct e))
                          (rest coll))))
               (step-fn acc)))]
     (trav (step-fn) coll))))

;;
;; step functions for traverse
;;
(defn tree->set
  "Step function to convert a hierarchical tree of elements to a flat set of elements."
  ([] #{})
  ([acc] acc)
  ([acc e] (conj acc e)))

(defn tech-collector
  "Step function to collect the technologies.
   Adds the tech of `e` to the accumulator `acc`."
  ([] #{})
  ([acc] acc)
  ([acc e] (set/union acc #{(:tech e)})))

(defn id->element 
  "Step function to create an id to element map.
   Adds the association of the id of the element `e` to the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc e]
   (assoc acc (:id e) e)))

(defn id->parent
  "Step function to create an id to parent element map.
   Adds the association from the id of element `e` to the parent `p` to the map `acc`."
  ([] [{} '()])
  ([[res ctx]]
   (if-not (empty? ctx)
     [res (pop ctx)]
     res))
  ([[res ctx] e]
   (let [p (peek ctx)]
     (if (child? e p)
       [(assoc res (:id e) p) (conj ctx e)]
       [res (conj ctx e)]))))

(defn referrer-id->rel
  "Step function to create an map of referrer ids to relations.
   Adds the relation `r` to the set associated with the id of the :from reference in the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc e]
   (assoc acc (:from e) (conj (get acc (:from e) #{}) e))))

(defn referred-id->rel
  "Step function to create an map of referred ids to relations.
   Adds the relation `r` to the set associated with the id of the :to reference in the map `acc`."
  ([] {})
  ([acc] acc)
  ([acc r]
   (assoc acc (:to r) (conj (get acc (:to r) #{}) r))))

(defn descendant-nodes
  "Returns the set of descendants of the node `e`."
  [e]
  (when (model-node? e)
    (traverse model-node? tree->set (:ct e))))

(defn collect-technologies
  "Returns the set of technologies for the elements of the coll."
  [coll]
  (traverse :tech tech-collector coll))


