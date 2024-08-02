(ns org.soulspace.overarch.adapter.template.markdown-api
  (:require [org.soulspace.overarch.adapter.template.model-api :as m]))


(defn element-link
  "Renders a link to the element `e`, using the optional `context` for customization."
  ([e]
   (element-link e {}))
  ([e context]
   (str "[" (:name e) "]"
        "("
        (when (:subdir context)
          (str (:subdir context) "/"))
        (when (:namespace-prefix context)
          (str (:namespace-prefix context) "/"))
        (m/element-namespace-path e) "/"
        (when (:namespace-suffix context)
          (str (:namespace-suffix context) "/"))
        (when (:prefix context)
          (:prefix context))
        (name (:id e))
        (when (:suffix context)
          (:suffix context))
        (if (:extension context)
          (str "." (:extension context))
          ".md")
        ")")))

