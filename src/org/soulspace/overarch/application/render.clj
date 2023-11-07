(ns org.soulspace.overarch.application.render
  "Contains dispatch functions for the rendering of the views.")

;;;
;;; Export multimethods 
;;;  

(defn render-format
  "Returns the render format for the multimethod invocation."
  ([m format]
   format)
  ([m format options]
   format)
  ([m format options view]
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
