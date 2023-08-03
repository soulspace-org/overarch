;;;;
;;;; Markdown export
;;;;
(ns org.soulspace.overarch.exports.markdown
  "Functions to export views to markdown."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.cmp.md.markdown-dsl :as md]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.view :as view]
            [org.soulspace.overarch.export :as exp]
            [org.soulspace.overarch.io :as oio]))

(defn render-element
  "Renders an `element` with markdown according to the given `options`."
  [e options view]
  [(md/h2 (str (:name e) " (" (str/capitalize (name (:el e))) ")"))
   (md/p (:desc e))])

(defn render-view
  "Renders the `view` with markdown according to the given `options`."
  [options view]
  (let [children (sort-by :name (view/elements-in-view view))]
    (flatten [(md/h1 (:title view))
              (map #(render-element % options view) children)])))

(def markdown-views
  "Contains the views rendered with markdown."
  #{:glossary-view})

(defn markdown-view?
  "Returns true, if the view is to be rendered with markdown."
  [view]
  (contains? markdown-views (:el view)))

(defmethod exp/export-file :markdown
  [options view]
  (let [dir-name (str (:export-dir options) "/markdown/"
                      (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".md"))))

(defmethod exp/export-view :markdown
  [options view]
  (with-open [wrt (io/writer (exp/export-file options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-view options view))))))

(defmethod exp/export :markdown
  [options]
  (doseq [view (core/get-views)]
    (when (markdown-view? view)
      (exp/export-view options view))))
