(ns org.soulspace.overarch.adapter.template.markdown-api
  (:require [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]))

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

; TODO add upward path
(defn relative-element-link
  "Renders a relative link from the current element `c` to the element `e`, using the optional `context` for customization."
  ([c e]
   (element-link e {}))
  ([c e context]
   (str "[" (:name e) "]"
        "("
        (when (:subdir context)
          (str (:subdir context) "/"))
        (when (:namespace-prefix context)
          (str (:namespace-prefix context) "/"))
        (m/root-path c) "/"
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

(defn view-link
  "Renders a link to the view `v`, using the optional `context` for customization."
  ([v]
   (element-link v {}))
  ([v context]
   (str "[" (v/title v) "]"
        "("
        (when (:subdir context)
          (str (:subdir context) "/"))
        (when (:namespace-prefix context)
          (str (:namespace-prefix context) "/"))
        (m/element-namespace-path v) "/"
        (when (:namespace-suffix context)
          (str (:namespace-suffix context) "/"))
        (when (:prefix context)
          (:prefix context))
        (name (:id v))
        (when (:suffix context)
          (:suffix context))
        (if (:extension context)
          (str "." (:extension context))
          ".md")
        ")")))

; TODO add upward path
(defn relative-view-link
  "Renders a relative link from the current element `c` to the view `v`, using the optional `context` for customization."
  ([c v]
   (element-link v {}))
  ([c v context]
   (str "[" (v/title v) "]"
        "("
        (when (:subdir context)
          (str (:subdir context) "/"))
        (when (:namespace-prefix context)
          (str (:namespace-prefix context) "/"))
        (m/root-path c) "/"
        (m/element-namespace-path v) "/"
        (when (:namespace-suffix context)
          (str (:namespace-suffix context) "/"))
        (when (:prefix context)
          (:prefix context))
        (name (:id v))
        (when (:suffix context)
          (:suffix context))
        (if (:extension context)
          (str "." (:extension context))
          ".md")
        ")")))

