(ns org.soulspace.overarch.core
  "Functions for the definition and handling of the overarch model."
  (:require [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [org.soulspace.clj.java.file :as file]))

;;;
;;; Schema definitions
;;;

;;
;; Category definitions
;; 

(def context-types
  "Element types of a C4 context view."
  #{:person :system :boundary :enterprise-boundary})

(def container-types
  "Element types of a C4 container view."
  (set/union context-types #{:system-boundary :container}))

(def component-types
  "Element types of a C4 component view."
  (set/union container-types #{:container-boundary :component}))

(def code-types
  "Element types of a C4 code view."
  #{})

(def system-landscape-types
  "Element types of a C4 system-landscape view."
  context-types)

(def deployment-types
  "Element types of a C4 deployment view."
  (set/union container-types #{:node}))

(def dynamic-types
  "Element types of a C4 dynamic view."
  component-types)

(def view-types
  "C4 view types."
  #{:context-view :container-view :component-view
    :code-view :deployment-view :system-landscape-view
    :dynamic-view})

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
  "Returns true if the given element `e` has an ID."
  [e]
  (not= nil (:id e)))

(defn relation?
  "Returns true if the given element `e` is a relation."
  [e]
  (contains? relation-types (:el e)))

(defn reference?
  "Returns true if the given element `e` is a reference."
  [e]
  (contains? reference-types (:el e)))

(defn model-element?
  "Returns true if the given element `e` is a model element."
  [e]
  (contains? model-types (:el e)))

(defn view?
  "Returns true if the given element `e` is a view."
  [e]
  (contains? view-types (:el e)))

(defn context-level?
  "Returns true if the given element `e` is rendered in a context diagram."
  [e]
  (contains? context-types (:el e)))

(defn container-level?
  "Returns true if the given element `e` is rendered in a container diagram."
  [e]
  (contains? container-types (:el e)))

(defn component-level?
  "Returns true if the given element `e` is rendered in a component diagram."
  [e]
  (contains? component-types (:el e)))

(defn code-level?
  "Returns true if the given element `e` is rendered in a code diagram."
  [e]
  (contains? code-types (:el e)))

(defn dynamic-level?
  "Returns true if the given element `e` is rendered in a dynamic diagram."
  [e]
  (contains? dynamic-types (:el e)))

(defn system-landscape-level?
  "Returns true if the given element `e` is rendered
   in a system landscape diagram."
  [e]
  (contains? system-landscape-types (:el e)))

(defn deployment-level?
  "Returns true if the given element `e` is rendered in a deployment diagram."
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
         ;:view       :overarch/view
         )))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/name :overarch/desc :overarch/ct
                   :overarch/subtype :overarch/external]))

(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/icon :overarch/link]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/name :overarch/desc]))

(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title]))

(s/def :overarch/elements
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :view        :overarch/view)))

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

(defn views
  "Filters the given collection of elements `coll` for views."
  [coll]
  (filter view? coll))

(defn model-elements
  "Filters the given collection of elements `coll` for model elements."
  [coll]
  (filter model-element? coll))

(defn get-views
  "Returns the collection of diagrams."
  ([]
   (get-views @state))
  ([m]
   (views (:elements m))))

(defn get-view
  "Returns the diagram with the given id."
  ([id]  
   (get-view @state id))
  ([m id]
   ((:registry m) id)))

(defn get-model-elements
  "Returns the collection of model elements."
  ([]
   (get-model-elements @state))
  ([m]
   (model-elements (:elements m))))

(defn get-model-element
  "Returns the model element with the given id."
  ([id]
   (get-model-element @state id))
  ([m id]
   ((:registry m) id)))

(defn get-parent-element
  "Returns the parent of the element."
  ([e]
   (get-parent-element @state e))
  ([m e]
   ((:parents m) (:id e))))

(defn resolve-ref
  "Resolves the model element for the ref e."
  ([e]
   (if (:ref e)
     (merge (get-model-element (:ref e)) e)
     e))
  ([m e]
   (if (:ref e)
     (merge (get-model-element m (:ref e)) e)
     e)))

(defn aggregable-relation?
  "Returns true, if the relations are aggregable."
  ([r1 r2]
   (aggregable-relation? @state r1 r2))
  ([m r1 r2]
   (and (= (:name r1) (:name r2))
        (= (:tech r1) (:tech r2))
        (= (:desc r1) (:desc r2))
        (or (= (:from r1) (:from r2))
            (= (get-parent-element m (:from r1))
               (get-parent-element m (:from r2))))
        (or (= (:to r1) (:to r2))
            (= (get-parent-element m (:to r1))
               (get-parent-element m (:to r2)))))))

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

(defn register-parents
  "Returns a map of child id to parent element for the elements in coll."
  ([coll]
   (register-parents {} nil coll))
  ([m p coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (and (identifiable? e) (identifiable? p) (model-element? p))
         (recur (register-parents (assoc m (:id e) p) e (:ct e)) p (rest coll))
         (recur (register-parents m e (:ct e)) p (rest coll))))
     m)))

(defn build-registry
  "Returns a map with the original data and a registry by id for lookups.
   
   The map has the following shape:

   :elements -> the given data
   :registry -> a map from id to element
   :parents  -> a map from id to parent element"
  [elements]
  (if (s/valid? :overarch/elements elements)
    (reset! state {:elements elements
                   :registry (register-elements elements)
                   :parents (register-parents elements)})
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
  (register-parents (:elements @state))
  (user/data-tapper "State" @state)
  )
