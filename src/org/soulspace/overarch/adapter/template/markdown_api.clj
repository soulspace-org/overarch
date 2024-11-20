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
        (when (seq (:subdir context))
          (str (:subdir context) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path e))
          (str (m/element-namespace-path e) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id e))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
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
        (when (seq (m/root-path c))
          (str (m/root-path c) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path e))
          (str (m/element-namespace-path e) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id e))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
          (str "." (:extension context))
          ".md")
        ")")))

(defn view-link
  "Renders an image link to the view `v`, using the optional `context` for customization."
  ([v]
   (view-link v {}))
  ([v context]
   (str "[" (v/title v) "]"
        "("
        (when (seq (:subdir context))
          (str (:subdir context) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path v))
          (str (m/element-namespace-path v) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id v))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
          (str "." (:extension context))
          ".md")
        ")")))

(defn relative-view-link
  "Renders a relative image link from the current element `c` to the view `v`, using the optional `context` for customization."
  ([c v]
   (relative-view-link c v {}))
  ([c v context]
   (str "[" (v/title v) "]"
        "("
        (when (seq (m/root-path c))
          (str (m/root-path c) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path v))
          (str (m/element-namespace-path v) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id v))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
          (str "." (:extension context))
          ".md")
        ")")))

(defn diagram-link
  "Renders an image link to the view `v`, using the optional `context` for customization."
  ([v]
   (diagram-link v {}))
  ([v context]
   (str "![" (v/title v) "]"
        "("
        (when (seq (:subdir context))
          (str (:subdir context) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path v))
          (str (m/element-namespace-path v) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id v))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
          (str "." (:extension context))
          ".png")
        ")")))

(defn relative-diagram-link
  "Renders a relative image link from the current element `c` to the view `v`, using the optional `context` for customization."
  ([c v]
   (relative-diagram-link c v {}))
  ([c v context]
   (str "![" (v/title v) "]"
        "("
        (when (seq (m/root-path c))
          (str (m/root-path c) "/"))
        (when (seq (:namespace-prefix context))
          (str (:namespace-prefix context) "/"))
        (when (seq (m/element-namespace-path v))
          (str (m/element-namespace-path v) "/"))
        (when (seq (:namespace-suffix context))
          (str (:namespace-suffix context) "/"))
        (when (seq (:prefix context))
          (:prefix context))
        (name (:id v))
        (when (seq (:suffix context))
          (:suffix context))
        (if (seq (:extension context))
          (str "." (:extension context))
          ".png")
        ")")))

(defn file-link
  "Renders a link to the file `f`, using the optional `context` for customization."
  ([filename name]
   (str "[" name "]"
        "(" filename ")")))

(defn relative-file-link
  "Renders a relative link from the current element `c` to the file `f`, using the optional `context` for customization."
  ([c filename name]
   (str "[" name "]"
        "("
        (when (seq (m/root-path c))
          (str (m/root-path c) "/"))
        filename
        ")")))

(defn image-link
  "Renders an image link to the file `f`, using the optional `context` for customization."
  ([filename name]
   (str "![" name "]"
        "(" filename ")")))

(defn relative-image-link
  "Renders a relative image link from the current element `c` to the file `f`, using the optional `context` for customization."
  ([c filename name]
   (str "![" name "]"
        "("
        (when (seq (m/root-path c))
          (str (m/root-path c) "/"))
        filename
        ")")))

(comment ; link generation
  (element-link {:id :y :name "Y"})
  (element-link {:id :a.b.c/x :name "X"})
  (relative-element-link {:id :y :name "Y"} {:id :a.b.c/x :name "X"})
  (relative-element-link {:id :a.b.c/x :name "X"} {:id :y :name "Y"})
  (view-link {:id :y :name "Y"})
  (view-link {:id :a.b.c/x :name "X"})
  (relative-view-link {:id :y :name "Y"} {:id :a.b.c/x :name "X"})
  (relative-view-link {:id :a.b.c/x :name "X"} {:id :y :name "Y"})
  (diagram-link {:id :y :name "Y"})
  (diagram-link {:id :a.b.c/x :name "X"})
  (diagram-link {:id :y :name "Y"} {:extension "svg"})
  (diagram-link {:id :a.b.c/x :name "X"} {:extension "svg"})
  (relative-diagram-link {:id :y :name "Y"} {:id :a.b.c/x :name "X"})
  (relative-diagram-link {:id :a.b.c/x :name "X"} {:id :y :name "Y"})
  (file-link "file.md" "File")
  (relative-file-link {:id :y :name "Y"} "file.md" "File")
  (relative-file-link {:id :a.b.c/x :name "X"} "file.md" "File")
  (image-link "file.png" "File")
  (relative-image-link {:id :y :name "Y"} "file.png" "File")
  (relative-image-link {:id :a.b.c/x :name "X"} "file.png" "File")

  ;
  )