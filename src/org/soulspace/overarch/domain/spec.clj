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
;(s/def :overarch/sprite string?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/direction (s/and keyword? #{:left :right :up :down}))
(s/def :overarch/constraint boolean?)
(s/def :overarch/tags (s/and set? (s/coll-of string?)))
(s/def :overarch/link (s/or :key keyword? :url string?))
(s/def :overarch/index int?)   ; check
(s/def :overarch/type string?) ; check

(s/def :overarch/ct
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation)))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el]
          :opt-un [:overarch/id
                   :overarch/subtype
                   :overarch/name :overarch/desc :overarch/doc
                   :overarch/maturity :overarch/external
                   :overarch/tech :overarch/tags
                   :overarch/ct]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/id
                   :overarch/index
                   :overarch/name :overarch/desc :overarch/doc
                   :overarch/maturity :overarch/tech
                   :overarch/direction :overarch/constraint
                   :overarch/tags]))

(s/def :overarch/ref keyword?)
(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/index :overarch/external :overarch/direction
                   :overarch/constraint :overarch/link :overarch/index]))

(s/def :overarch/identifiable
  (s/keys :req-un [:overarch/id]))

(s/def :overarch/named
  (s/keys :req-un [:overarch/name]))

;;
;; Criteria for element selection 
;;
; TODO add missing criteria
(s/def :overarch/keys? vector?)
(s/def :overarch/keys vector?)
(s/def :overarch/els (s/and set? (s/coll-of keyword?))) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/namespace string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/namespaces (s/and set? (s/coll-of string?))) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/namespace-prefix string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/from-namespace string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/from-namespaces (s/and set? (s/coll-of string?))) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/from-namespace-prefix string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/to-namespace string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/to-namespaces (s/and set? (s/coll-of string?))) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/to-namespace-prefix string?) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/id? boolean?)
(s/def :overarch/subtype? boolean?)
(s/def :overarch/subtypes (s/and set? (s/coll-of keyword?))) ; or (s/or :string string? :keyword keyword?)?
(s/def :overarch/maturity? boolean?)
(s/def :overarch/external? boolean?)
(s/def :overarch/name? boolean?)
(s/def :overarch/name string?)
(s/def :overarch/desc? boolean?)
(s/def :overarch/desc string?)
(s/def :overarch/doc? boolean?)
(s/def :overarch/doc string?)
(s/def :overarch/tech? boolean?)
(s/def :overarch/techs (s/and set? (s/coll-of string?)))
(s/def :overarch/all-techs (s/and set? (s/coll-of string?)))
(s/def :overarch/tags? boolean?)
(s/def :overarch/tag string?)
(s/def :overarch/tags (s/and set? (s/coll-of string?)))
(s/def :overarch/all-tags (s/and set? (s/coll-of string?)))
(s/def :overarch/children? boolean?)
(s/def :overarch/child? boolean?)
(s/def :overarch/refers? boolean?)
(s/def :overarch/referred? boolean?)
(s/def :overarch/refers-to keyword?)
(s/def :overarch/referred-by keyword?)

(s/def :overarch/criteria
  (s/keys :opt-un [:overarch/el
                   :overarch/els
                   :overarch/keys?
                   :overarch/keys
                   :overarch/namespace
                   :overarch/namespaces
                   :overarch/namespace-prefix
                   :overarch/from-namespace
                   :overarch/from-namespaces
                   :overarch/from-namespace-prefix
                   :overarch/to-namespace
                   :overarch/to-namespaces
                   :overarch/to-namespace-prefix
                   :overarch/id?
                   :overarch/id
                   :overarch/subtype?
                   :overarch/subtype
                   :overarch/subtypes
                   :overarch/external?
                   :overarch/maturity?
                   :overarch/maturity
                   :overarch/name?
                   :overarch/name
                   :overarch/desc?
                   :overarch/desc
                   :overarch/doc?
                   :overarch/doc
                   :overarch/tech?
                   :overarch/tech
                   :overarch/techs
                   :overarch/all-techs
                   :overarch/tags?
                   :overarch/tag
                   :overarch/tags
                   :overarch/all-tags
                   :overarch/children?
                   :overarch/child?
                   :overarch/refers?
                   :overarch/refers-to
                   :overarch/referred?
                   :overarch/referred-by]))

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
                   :overarch.view.spec/layout :overarch.view.spec/linetype
                   :overarch.view.spec/sketch :overarch.view/styles :overarch.view.spec/themes
                   :overarch.view.spec/plantuml :overarch.view.spec/markdown
                   :overarch.view.spec/graphviz]))

(s/def :overarch.view/title string?)
(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/name :overarch/desc :overarch/doc
                   :overarch.view/spec :overarch.view/title :overarch/ct]))

;;;
;;; Input model
;;;
(s/def :overarch/input-model
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :view        :overarch/view
         :theme       :overarch.view/theme)))

;;;
;;; Schema functions
;;;
(defn check-input-model
  "Checks the `input model` against its specification. If valid returns the input-model, otherwise returns the validation errors."
  ([input-model]
   (if (s/valid? :overarch/input-model input-model)
     input-model
     (expound/expound :overarch/input-model input-model {:print-specs? false}))))

(defn check-selection-criteria
  "Checks the `selection criteria` against its specification. If valid returns the selection-criteria, otherwise returns the validation errors."
  [selection-criteria]
  (if (s/valid? :overarch/selection-criteria selection-criteria)
   selection-criteria
   (do (expound/expound :overarch/selection-criteria selection-criteria {:print-specs? false})
       nil)))
