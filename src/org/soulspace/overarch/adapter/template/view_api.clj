(ns org.soulspace.overarch.adapter.template.view-api
  "Public API with useful functions on top of the view for use in templates. (Not yet stable!)"
  (:require [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.model-repository :as repo]
            [clojure.string :as str]))

;;;;
;;;; Not yet stable!
;;;;

;;;
;;; View Predicates
;;;
(defn render-model-element?
  "Returns true if the element `e` is rendered in the `view` in the context of the `model`."
  [model view e]
  (view/render-model-element? model view e))

(defn element-to-render
  "Returns the model element to be rendered for element `e` for the `view` in the context of the `model`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  [model view e]
  (view/element-to-render model view e))

;;;
;;; View Functions
;;;
(defn view-elements
  "Returns the `model` elements specified for the given `view`."
  [model view]
  (view/view-elements model view))

; TODO refactor, move to model?
(defn root-elements
  "Returns the root elements for a collection of `model` `elements` to start the rendering of the `view` with."
  [model elements]
  ; Difference of the sets of elements have to be done with difference-by-id which treats the elements as entities
  ; to preserve overrides of keys in the content references included in the view.
  ; When keys have been added or overridden in the reference in a view, the elements in the different sets
  ; are not the same values anymore and so have to be treated as entities, even when they are still immutable.
  (view/root-elements model elements))

(defn elements-to-render
  "Returns the list of elements to render from the `view` or the given `coll` of elements, depending on the type of the view."
  ([model view]
   (view/elements-to-render model view))
  ([model view coll]
   (view/elements-to-render model view coll)))
