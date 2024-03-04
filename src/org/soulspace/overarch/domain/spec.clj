;;;;
;;;; Schema specification and functions
;;;;
(ns org.soulspace.overarch.domain.spec
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))


;;;
;;; Elements
;;;
(s/def :overarch/el keyword?)
(s/def :overarch/id keyword?)
(s/def :overarch/subtype keyword?)
(s/def :overarch/external boolean?)
(s/def :overarch/name string?)
(s/def :overarch/desc string?)
(s/def :overarch/tech string?)
;(s/def :overarch/sprite string?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/direction keyword?)
(s/def :overarch/constraint boolean?)

; (s/def :overarch/tags map?)    ; check
; (s/def :overarch/type string?) ; check
; (s/def :overarch/index int?)   ; check
; (s/def :overarch/href string?) ; TODO url?
; (s/def :overarch/link
;  (s/keys :req-un [:overarch/name :overarch/href]))

(s/def :overarch/ct
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation)))

(s/def :overarch/element
  (s/keys :req-un [:overarch/el]
          :opt-un [:overarch/id
                   :overarch/name :overarch/desc :overarch/ct
                   :overarch/subtype :overarch/external
                   :overarch/tech]))

(s/def :overarch/relation
  (s/keys :req-un [:overarch/el :overarch/from :overarch/to]
          :opt-un [:overarch/name :overarch/desc :overarch/tech
                   :overarch/direction :overarch/constraint]))

(s/def :overarch/ref keyword?)
(s/def :overarch/element-ref
  (s/keys :req-un [:overarch/ref]
          :opt-un [:overarch/icon :overarch/link]))

(s/def :overarch/identifiable
  (s/keys :req-un [:overarch/id]))

(s/def :overarch/named
  (s/keys :req-un [:overarch/name]))

;;;
;;; Views
;;;
;;
;; Styles/Themes
;;
(s/def :overarch/for keyword?)
(s/def :overarch/line-style keyword?)
(s/def :overarch/line-color string?)
(s/def :overarch/border-color string?)
(s/def :overarch/text-color string?)
(s/def :overarch/legend-text string?)

(s/def :overarch/style
  (s/keys :req-un [:overarch/id]
          :opt-un [:overarch/for :overarch/el :overarch/line-style
                   :overarch/legend-text :overarch/border-color
                   :overarch/line-color :overarch/text-color]))

(s/def :overarch/styles
  (s/coll-of :overarch/style))

(s/def :overarch/theme
  (s/keys :req-un [:overarch/el :overarch/id :overarch/styles]))

(s/def :overarch/themes
  (s/coll-of :overarch/theme))

(s/def :overarch/include keyword?)
(s/def :overarch/layout keyword?)
(s/def :overarch/linetype keyword?)
(s/def :overarch/sketch boolean?)

;;
;; PlantUML
;;
(s/def :overarch/sprite-lib keyword?)
(s/def :overarch/node-separation integer?)
(s/def :overarch/rank-separation integer?)

(s/def :overarch/sprite-libs
  (s/and vector?
         (s/coll-of :overarch/sprite-lib)))

(s/def :overarch/plantuml
  (s/keys :req-un []
          :opt-un [:overarch/node-separation :overarch/rank-separation
                   :overarch/sprite-libs]))

;;
;; Markdown
;;
(s/def :overarch/references boolean?)
(s/def :overarch/markdown
  (s/keys :req-un []
          :opt-un [:overarch/references]))

;;
;; View Spec
;;
(s/def :overarch/spec
  (s/keys :opt-un [:overarch/include :overarch/layout :overarch/linetype
                   :overarch/sketch :overarch/styles :overarch/themes
                   :overarch/plantuml :overarch/markdown]))

(s/def :overarch/title string?)
(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title :overarch/ct]))

;;
;; Input model
;;
(s/def :overarch/model
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :view        :overarch/view
         :theme       :overarch/theme)))

;;;
;;; Schema functions
;;;
(defn check
  "Check model specification."
  [elements]
  (if (s/valid? :overarch/model elements)
    elements
    ;(s/explain :overarch/model elements)
    (expound/expound :overarch/model elements {:print-specs? false})))
