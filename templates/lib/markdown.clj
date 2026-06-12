(ns lib.markdown
  (:require [clojure.string :as str]
            [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]))


;;;
;;; Markdown Links
;;;
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

;;;
;;; Markdown Tables
;;;
(defn node-description-table-row
  "Generates the markdown for a description table row for `node` and `parent`."
  [parent node]
  (str "| " (relative-element-link parent node) " | " (t/single-line (:desc node)) " |")
  )

(defn node-description-table
  "Generates the markdown for a description table for nodes of the `type` in `coll` in the context of the `node`."
  [parent type coll]
  (str "| " (get type->name type (str/capitalize (name type))) " | Description |\n"
       "|---|---|"
       (apply str
              (for [node (sort-by :name coll)]
                (str "\n" (node-description-table-row parent node))))))

(defn node-type-description-table-row
  "Generates the markdown for a description table row for `node` and `parent`."
  [parent node]
  (str "| " (relative-element-link parent node) " | " (get type->name (:type node) (str/capitalize (name (:type node)))) " | " (t/single-line (:desc node)) " |"))

(defn node-type-description-table
  "Generates the markdown for a description table for nodes of the `type` in `coll` in the context of the `node`."
  [parent coll]
  (str "| Node | Type | Description |\n"
       "|---|---|---|"
       (apply str
              (for [node (sort-by :name coll)]
                (str "\n" (node-type-description-table-row parent node))))))

(defn relation-description-table-row
  "Generates the markdown for a description table row for the `relation` in the `model`."
  [model node rel]
  (str "| " (relative-element-link node (m/referrer model rel)) " | " (:name rel) " | " (relative-element-link node (m/referred model rel)) " | " (t/single-line (:desc rel)) " |"))

(defn relation-description-table
  "Generates the markdown for a description table for relations of the `type` in `coll` in the context of the `node` and the `model`."
  [model node coll]
  (str "| From | Name | To | Description |\n"
       "|---|---|---|---|"
       (apply str
              (for [rel (sort-by :name coll)]
                (str "\n" (relation-description-table-row model node rel))))))

(defn technical-relation-description-table-row
  "Generates the markdown for a description table row for the technical `relation` in the `model`."
  [model node rel]
  (str "| " (relative-element-link node (m/referrer model rel)) " | " (:name rel) " | " (relative-element-link node (m/referred model rel)) " | " (str/join ", " (:tech rel)) " | " (t/single-line (:desc rel)) " |"))

(defn technical-relation-description-table
  "Generates the markdown for a description table for technical relations of the `type` in `coll` in the context of the `node` and the `model`."
  [model node coll]
  (str "| From | Name | To | Technology | Description |\n"
       "|---|---|---|---|---|"
       (apply str
              (for [rel (sort-by :name coll)]
                (str "\n" (technical-relation-description-table-row model node rel))))))

