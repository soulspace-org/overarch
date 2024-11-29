;;;;
;;;; Schema specification and functions
;;;;
(ns org.soulspace.overarch.domain.spec
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [org.soulspace.overarch.domain.element :as el]))

;;;
;;; Elements
;;;
(s/def :overarch/el keyword?)
(s/def :overarch/id keyword?)

;;
;; Model Elements
;;
(s/def :overarch/subtype keyword?)
(s/def :overarch/external boolean?)
(s/def :overarch/name string?)
(s/def :overarch/desc string?)
(s/def :overarch/doc string?)
(s/def :overarch/tech string?)
(s/def :overarch/maturity keyword?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/direction (s/and keyword? #{:left :right :up :down}))
(s/def :overarch/constraint boolean?)
(s/def :overarch/tags (s/and set? (s/coll-of string?)))
(s/def :overarch/link (s/or :key keyword? :url string?))
(s/def :overarch/type string?) ; check
(s/def :overarch/index int?)   ; check
(s/def :overarch/sprite string?)

(s/def :overarch/element
  (s/keys :req-un [:overarch/el]
          :opt-un [:overarch/id
                   :overarch/name :overarch/desc :overarch/doc]))

(s/def :overarch/ref keyword?)
(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/index :overarch/external :overarch/direction
                   :overarch/constraint :overarch/link :overarch/index]))

(s/def :overarch/ct
  (s/coll-of
   (s/or :model-node  :overarch/element
         :element-ref :overarch/element-ref)))

(s/def :overarch/model-element
  (s/keys :req-un [:overarch/el]
          :opt-un [:overarch/id
                   :overarch/subtype
                   :overarch/name :overarch/desc :overarch/doc
                   :overarch/maturity :overarch/external
                   :overarch/tech :overarch/tags
                   :overarch/ct]))

(s/def :overarch/model-relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/id
                   :overarch/index
                   :overarch/name :overarch/desc :overarch/doc
                   :overarch/maturity :overarch/tech
                   :overarch/direction :overarch/constraint
                   :overarch/tags]))

;;
;; Criteria for element selection 
;;
(s/def :overarch.criterium/keys? vector?)
(s/def :overarch.criterium/keys vector?)
(s/def :overarch.criterium/el keyword?)
(s/def :overarch.criterium/els (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/!els (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/namespace string?)
(s/def :overarch.criterium/!namespace string?)
(s/def :overarch.criterium/namespaces (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/namespace-prefix string?)
(s/def :overarch.criterium/namespace-prefixes (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/from-namespace string?)
(s/def :overarch.criterium/from-namespaces (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/from-namespace-prefix string?)
(s/def :overarch.criterium/from-namespace-prefixes (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/to-namespace string?)
(s/def :overarch.criterium/to-namespaces (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/to-namespace-prefix string?)
(s/def :overarch.criterium/to-namespace-prefixes (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/id? boolean?)
(s/def :overarch.criterium/id keyword?)
(s/def :overarch.criterium/!id keyword?)
(s/def :overarch.criterium/from keyword?)
(s/def :overarch.criterium/!from keyword?)
(s/def :overarch.criterium/to keyword?)
(s/def :overarch.criterium/!to keyword?)
(s/def :overarch.criterium/subtype? boolean?)
(s/def :overarch.criterium/subtype keyword?)
(s/def :overarch.criterium/subtypes (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/!subtypes (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/maturity? boolean?)
(s/def :overarch.criterium/maturity keyword?)
(s/def :overarch.criterium/maturities (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/!maturities (s/and set? (s/coll-of keyword?)))
(s/def :overarch.criterium/external? boolean?)
(s/def :overarch.criterium/name? boolean?)
(s/def :overarch.criterium/name string?)
(s/def :overarch.criterium/desc? boolean?)
(s/def :overarch.criterium/desc string?)
(s/def :overarch.criterium/doc? boolean?)
(s/def :overarch.criterium/doc string?)
(s/def :overarch.criterium/tech? boolean?)
(s/def :overarch.criterium/tech string?)
(s/def :overarch.criterium/techs (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/!techs (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/all-techs (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/tags? boolean?)
(s/def :overarch.criterium/tag string?)
(s/def :overarch.criterium/tags (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/!tags (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/all-tags (s/and set? (s/coll-of string?)))
(s/def :overarch.criterium/refers? boolean?)
(s/def :overarch.criterium/referred? boolean?)
(s/def :overarch.criterium/refers-to keyword?)
(s/def :overarch.criterium/!refers-to keyword?)
(s/def :overarch.criterium/referred-by keyword?)
(s/def :overarch.criterium/!referred-by keyword?)
(s/def :overarch.criterium/child? boolean?)
(s/def :overarch.criterium/child-of keyword?)
(s/def :overarch.criterium/!child-of keyword?)
(s/def :overarch.criterium/descendant-of keyword?)
(s/def :overarch.criterium/!descendant-of keyword?)
(s/def :overarch.criterium/children? boolean?)
(s/def :overarch.criterium/parent? boolean?)
(s/def :overarch.criterium/parent-of keyword?)
(s/def :overarch.criterium/!parent-of keyword?)
(s/def :overarch.criterium/ancestor-of keyword?)
(s/def :overarch.criterium/!ancestor-of keyword?)

(s/def :overarch/criteria
  (s/keys :opt-un [:overarch.criterium/el
                   :overarch.criterium/els
                   :overarch.criterium/!els
                   :overarch.criterium/keys?
                   :overarch.criterium/keys
                   :overarch.criterium/namespace
                   :overarch.criterium/!namespace
                   :overarch.criterium/namespaces
                   :overarch.criterium/namespace-prefix
                   :overarch.criterium/namespace-prefixes
                   :overarch.criterium/from-namespace
                   :overarch.criterium/from-namespaces
                   :overarch.criterium/from-namespace-prefix
                   :overarch.criterium/from-namespace-prefixes
                   :overarch.criterium/to-namespace
                   :overarch.criterium/to-namespaces
                   :overarch.criterium/to-namespace-prefix
                   :overarch.criterium/to-namespace-prefixes
                   :overarch.criterium/id?
                   :overarch.criterium/id
                   :overarch.criterium/!id
                   :overarch.criterium/from
                   :overarch.criterium/!from
                   :overarch.criterium/to
                   :overarch.criterium/!to
                   :overarch.criterium/subtype?
                   :overarch.criterium/subtype
                   :overarch.criterium/subtypes
                   :overarch.criterium/!subtypes
                   :overarch.criterium/external?
                   :overarch.criterium/maturity?
                   :overarch.criterium/maturity
                   :overarch.criterium/maturities
                   :overarch.criterium/!maturities
                   :overarch.criterium/name?
                   :overarch.criterium/name
                   :overarch.criterium/desc?
                   :overarch.criterium/desc
                   :overarch.criterium/doc?
                   :overarch.criterium/doc
                   :overarch.criterium/tech?
                   :overarch.criterium/tech
                   :overarch.criterium/techs
                   :overarch.criterium/!techs
                   :overarch.criterium/all-techs
                   :overarch.criterium/tags?
                   :overarch.criterium/tag
                   :overarch.criterium/tags
                   :overarch.criterium/!tags
                   :overarch.criterium/all-tags
                   :overarch.criterium/child?
                   :overarch.criterium/child-of
                   :overarch.criterium/!child-of
                   :overarch.criterium/descendant-of
                   :overarch.criterium/!descendant-of
                   :overarch.criterium/children?
                   :overarch.criterium/parent?
                   :overarch.criterium/parent-of
                   :overarch.criterium/!parent-of
                   :overarch.criterium/ancestor-of
                   :overarch.criterium/!ancestor-of
                   :overarch.criterium/refers?
                   :overarch.criterium/referred?
                   :overarch.criterium/refers-to
                   :overarch.criterium/!refers-to
                   :overarch.criterium/referred-by
                   :overarch.criterium/!referred-by]))

(s/def :overarch/criteria-vector
  (s/and vector? (s/coll-of :overarch/criteria)))

(s/def :overarch/selection-criteria
  (s/or :criteria :overarch/criteria
        :criteria-vector :overarch/criteria-vector))

;;;
;;; Views
;;;
;;
;; Styles/Themes
;;
(s/def :overarch.view.style/for keyword?)
(s/def :overarch.view.style/line-style keyword?)
(s/def :overarch.view.style/line-color string?)
(s/def :overarch.view.style/border-color string?)
(s/def :overarch.view.style/text-color string?)
(s/def :overarch.view.style/legend-text string?)

(s/def :overarch.view/style
  (s/keys :req-un [:overarch/id]
          :opt-un [:overarch.view.style/for :overarch/el :overarch.view.style/line-style
                   :overarch.view.style/legend-text :overarch.view.style/border-color
                   :overarch.view.style/line-color :overarch.view.style/text-color]))

(s/def :overarch.view/styles
  (s/coll-of :overarch.view/style))

(s/def :overarch.view/theme
  (s/keys :req-un [:overarch/el :overarch/id :overarch.view/styles]))

(s/def :overarch.view.spec/themes
  (s/coll-of :overarch.view/theme))

(s/def :overarch.view.spec/expand-external boolean?)
(s/def :overarch.view.spec/include (s/and keyword? #{:referenced-only :relations :related}))
(s/def :overarch.view.spec/layout (s/and keyword? #{:top-down :left-right}))
(s/def :overarch.view.spec/linetype keyword?)
(s/def :overarch.view.spec/sketch boolean?)

;;
;; PlantUML
;;
(s/def :overarch.view.spec.plantuml/sprite-lib keyword?)
(s/def :overarch.view.spec.plantuml/node-separation integer?)
(s/def :overarch.view.spec.plantuml/rank-separation integer?)
(s/def :overarch.view.spec.plantuml/skinparams map?)

(s/def :overarch.view.spec.plantuml/sprite-libs
  (s/and vector?
         (s/coll-of :overarch.view.spec.plantuml/sprite-lib)))

(s/def :overarch.view.spec/plantuml
  (s/keys :req-un []
          :opt-un [:overarch.view.spec.plantuml/node-separation
                   :overarch.view.spec.plantuml/rank-separation
                   :overarch.view.spec.plantuml/sprite-libs
                   :overarch.view.spec.plantuml/skinparams]))

;;
;; Markdown
;;
(s/def :overarch.view.spec.markdown/references boolean?)
(s/def :overarch.view.spec/markdown
  (s/keys :req-un []
          :opt-un [:overarch.view.spec.markdown/references]))

;;
;; Graphviz
;;
(s/def :overarch.view.spec.graphviz/engine keyword?)
(s/def :overarch.view.spec/graphviz
  (s/keys :req-un []
          :opt-un [:overarch.view.spec.graphviz/engine]))

;;
;; View Spec
;;
(s/def :overarch.view/spec
  (s/keys :opt-un [:overarch/selection :overarch.view.spec/include
                   :overarch.view.spec/expand-external
                   :overarch.view.spec/layout :overarch.view.spec/linetype
                   :overarch.view.spec/sketch :overarch.view/styles
                   :overarch.view.spec/themes
                   :overarch.view.spec/plantuml :overarch.view.spec/markdown
                   :overarch.view.spec/graphviz]))

(s/def :overarch.view/title string?)
(s/def :overarch.view/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/name :overarch/desc :overarch/doc
                   :overarch.view/spec :overarch.view/title :overarch/ct]))

;;;
;;; Input model
;;;
(s/def :overarch/input-model
  (s/coll-of
   (s/or :element     :overarch/model-element
         :element-ref :overarch/element-ref
         :relation    :overarch/model-relation
         :view        :overarch.view/view
         :theme       :overarch.view/theme)))

;;;
;;; Schema functions
;;;
(defn check-input-model
  "Checks the `input model` against its specification. If valid returns the input-model, otherwise returns the validation errors."
  ([input-model]
   (if (s/valid? :overarch/input-model input-model)
     input-model
     (expound/expound :overarch/input-model input-model {:print-specs? false})))
  ([^java.io.File file input-model]
   (if (s/valid? :overarch/input-model input-model)
     input-model
     (do
       (println "Error loading" (.getName file))
       (expound/expound :overarch/input-model input-model {:print-specs? false})))))

(defn check-selection-criteria
  "Checks the `selection criteria` against its specification. If valid returns the selection-criteria, otherwise returns the validation errors."
  [selection-criteria]
  (if (s/valid? :overarch/selection-criteria selection-criteria)
   selection-criteria
   (do (expound/expound :overarch/selection-criteria selection-criteria {:print-specs? false})
       nil)))
