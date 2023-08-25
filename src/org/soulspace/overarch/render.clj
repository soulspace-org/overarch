(ns org.soulspace.overarch.render
  "Contains dispatch functions for the rendering of the views.")

;;;
;;; Export multimethods 
;;;  

(defn render-format
  "Returns the render format for the multimethod invocation."
  ([format]
   format)
  ([format options]
   format)
  ([format options view]
   format))

(defmulti render-file
  "Returns the render file for the given format."
  render-format)

(defmulti render-view
  "Renders the view in the given format." 
  render-format)

(defmulti render
  "Renders all relevant views in the given format."
  render-format)
