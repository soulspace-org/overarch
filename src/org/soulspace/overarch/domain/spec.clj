;;;;
;;;; Schema specification and functions
;;;;
(ns org.soulspace.overarch.domain.spec
  (:require [clojure.spec.alpha :as s]
            [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.view :as view]))


;;;
;;; Schema specification
;;;
;;
;; Elements
;;
(s/def :overarch/el keyword?)
(s/def :overarch/id keyword?)
(s/def :overarch/subtype keyword?)
(s/def :overarch/external boolean?)
(s/def :overarch/name string?)
(s/def :overarch/desc string?)
(s/def :overarch/tech string?)
(s/def :overarch/sprite string?)
(s/def :overarch/from keyword?)
(s/def :overarch/to keyword?)
(s/def :overarch/direction keyword?)
(s/def :overarch/constraint boolean?)

; (s/def :overarch/tags map?)    ; check
; (s/def :overarch/type string?) ; check
; (s/def :overarch/index int?)   ; check
; (s/def :overarch/href string?) ; TODO url?

;(s/def :overarch/link
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

;;
;; Views
;;
(s/def :overarch/title string?)

; TODO spec keys
(s/def :overarch/include keyword?)
(s/def :overarch/layout keyword?)
(s/def :overarch/linetype keyword?)
(s/def :overarch/sketch boolean?)

(s/def :overarch/spec map?)

(s/def :overarch/view
  (s/keys :req-un [:overarch/el :overarch/id]
          :opt-un [:overarch/spec :overarch/title :overarch:ct]))

;;
;; Input model
;;
(s/def :overarch/model
  (s/coll-of
   (s/or :element     :overarch/element
         :element-ref :overarch/element-ref
         :relation    :overarch/relation
         :view        :overarch/view)))

;;;
;;; Schema functions
;;;
(defn check
  "Check model specification."
  [elements]
  (if (s/valid? :overarch/model elements)
    elements
    (s/explain :overarch/model elements)))

