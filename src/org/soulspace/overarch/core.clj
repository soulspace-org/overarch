(ns org.soulspace.overarch.core
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]
            [charred.api :as json]))

;;;
;;; Schema definitions
;;;

;;
;; Category definitions
;; 

(def context-types
  "Element types of a C4 context diagram."
  #{:person :system :boundary :enterprise-boundary})

(def container-types
  "Element types of a C4 container diagram."
  (set/union context-types #{:system-boundary :container}))

(def component-types
  "Element types of a C4 component diagram."
  (set/union container-types #{:container-boundary :component}))

(def code-types
  "Element types of a C4 code diagram."
  #{})

(def system-landscape-types
  "Element types of a C4 system-landscape diagram."
  context-types)

(def deployment-types
  "Element types of a C4 deployment diagram."
  (set/union container-types #{:node}))

(def dynamic-types
  "Element types of a C4 dynamic diagram."
  component-types)

(def diagram-types
  "C4 diagram types."
  #{:context-diagram :container-diagram :component-diagram
    :code-diagram :deployment-diagram :system-landscape-diagram
    :dynamic-diagram})

(def relation-types
  "Element types of relations"
  #{:rel})

(def reference-types
  "Element types of references"
  #{:ref})

(def model-types
  "Element types for the architectural model."
  (set/union component-types deployment-types relation-types))

;;
;; Predicates
;;

(defn identifiable?
  "Returns true if the given element has an ID."
  [e]
  (not= nil (:id e)))

(defn relation?
  "Returns true if the given element is a relation."
  [e]
  (contains? relation-types (:el e)))

(defn reference?
  "Returns true if the given element is a reference."
  [e]
  (contains? reference-types (:el e)))

(defn model-element?
  "Returns true if the given element is a model element."
  [e]
  (contains? model-types (:el e)))

(defn diagram?
  "Returns true if the given element is a diagram."
  [e]
  (contains? diagram-types (:el e)))

(defn context-level?
  "Returns true if the given element is rendered in a context diagram."
  [e]
  (contains? context-types (:el e)))

(defn container-level?
  "Returns true if the given element is rendered in a container diagram."
  [e]
  (contains? container-types (:el e)))

(defn component-level?
  "Returns true if the given element is rendered in a component diagram."
  [e]
  (contains? component-types (:el e)))

(defn code-level?
  "Returns true if the given element is rendered in a code diagram."
  [e]
  (contains? code-types (:el e)))

(defn dynamic-level?
  "Returns true if the given element is rendered in a dynamic diagram."
  [e]
  (contains? dynamic-types (:el e)))

(defn system-landscape-level?
  "Returns true if the given element is rendered
   in a system landscape diagram."
  [e]
  (contains? system-landscape-types (:el e)))

(defn deployment-level?
  "Returns true if the given element is rendered in a deployment diagram."
  [e]
  (contains? deployment-types (:el e)))

;;
;; Schema specification
;;

;; TODO factor out diagrams
;; TODO factor out subtypes (db, queue) and extern

(s/def :overarch/el keyword?)
(s/def :overarch/id keyword?)
(s/def :overarch/name string?)
(s/def :overarch/desc string?)
(s/def :overarch/subtype keyword?)
(s/def :overarch/external boolean?)
(s/def :overarch/tech string?) ; check
(s/def :overarch/tags map?)    ; check
(s/def :overarch/icon string?) ; check
(s/def :overarch/link string?) ; check
(s/def :overarch/type string?) ; check
(s/def :overarch/index int?)   ; check
(s/def :overarch/ref keyword?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)

(s/def :overarch/spec map?)
(s/def :overarch/title string?)

(s/def :overarch/ct
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         ;:diagram     :overarch/diagram
         )))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el :overarch/id :overarch/name]
          :opt-un [:overarch/desc :overarch/ct
                   :overarch/subtype :overarch/external]))

(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/icon :overarch/link]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/name :overarch/desc]))

(s/def :overarch/diagram
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title]))

(s/def :overarch/elements
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :diagram :overarch/diagram)))

;;;
;;; Data handling
;;;

;;
;; Application state
;;

(def state (atom {}))

;;
;; Accessors
;;

(defn diagrams
  "Returns the collection of diagrams."
  [coll]
  (filter diagram? coll))

(defn model-elements
  "Returns the collection of diagrams."
  [coll]
  (filter model-element? coll))

(defn get-diagrams
  "Returns the collection of diagrams."
  []
  (diagrams (:elements @state)))

(defn get-diagram
  "Returns the diagram with the given id."
  [id]
  ((:registry @state) id))

(defn get-model-elements
  "Returns the collection of model elements."
  []
  (model-elements (:elements @state)))

(defn get-model-element
  "Returns the model element with the given id."
  [id]
  ((:registry @state) id))

(defn resolve-ref
  "Resolves the model element for the ref e."
  [e]
  (if (:ref e)
    (merge (get-model-element (:ref e)) e)
    e))

;;
;; State preparation
;;

(defn register-elements
  "Returns a map of id to element for the elements of the coll."
  ([coll]
   (register-elements {} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (identifiable? e)
         (recur (register-elements (assoc m (:id e) e) (:ct e)) (rest coll))
         (recur (register-elements m (:ct e)) (rest coll))))
     m)))

(defn build-registry
  "Returns a map with the original data and a registry by id for lookups.
   
   The map has the following shape:

   :elements -> the given data
   :registry -> a map from id to element"
  [elements]
  (if (s/valid? :overarch/elements elements)
    (reset! state {:elements elements
                   :registry (register-elements elements)})
    (s/explain :overarch/elements elements)))

(s/fdef read-elements
  :args [string?]
  :ret :overarch/ct)
(defn read-elements
  "Reads the elements of data from the given directory `dir`."
  [dir]
  (->> (file/all-files-by-extension "edn" dir)
       (map slurp)
       (mapcat edn/read-string)))

(defn update-state!
  "Updates the state with the registered data."
  [dir]
  (->> dir
       (read-elements)
       (build-registry)
       (reset! state))
  nil)

(comment
  (file/all-files-by-extension "edn" "models")
  (read-elements "models")

  (update-state! "models")
  
  (user/data-tapper "State" @state) 
  )

