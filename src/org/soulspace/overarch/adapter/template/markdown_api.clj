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

(defn relative-element-link
  "Renders a relative link from the current element `c` to the element `e`, using the optional `context` for customization."
  ([c e]
   (relative-element-link c e {}))
  ([c e context]
   (str "[" (:name e) "]"
        "("
        (str (m/root-path c) "/")
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

(defn view-link
  "Renders a link to the view `v`, using the optional `context` for customization."
  ([v]
   (view-link v {}))
  ([v context]
   (str "![" (v/title v) "]"
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
          ".png")
        ")")))

(defn relative-view-link
  "Renders a relative link from the current element `c` to the view `v`, using the optional `context` for customization."
  ([c v]
   (relative-view-link c v {}))
  ([c v context]
   (str "![" (v/title v) "]"
        "("
        (str (m/root-path c) "/")
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
          ".png")
        ")")))

