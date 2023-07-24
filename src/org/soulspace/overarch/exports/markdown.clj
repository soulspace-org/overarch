;;;;
;;;; Markdown export
;;;;
(ns org.soulspace.overarch.exports.markdown
  "Functions to export views to markdown."
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.view :as view]
            [org.soulspace.overarch.export :as exp]
            [org.soulspace.overarch.io :as oio]))


(defn render-view
  "Renders the `view` in markdown according to the given `options`."
  [options view]
  ; TODO implement
  )

(defmethod exp/export-file :markdown
  [options view]
  (let [dir-name (str (:export-dir options) "/markdown/"
                      (namespace (:id view)))]
    (file/create-dir (io/as-file dir-name))
    (io/as-file (str dir-name "/"
                     (name (:id view)) ".md"))))

(defmethod exp/export-view :plantuml
  [options view]
  (with-open [wrt (io/writer (exp/export-file options view))]
    (binding [*out* wrt]
      (println (str/join "\n" (render-view options view))))))

(defmethod exp/export :markdown
  [options]
  (doseq [view (core/get-views)]
    (exp/export-view options view)))

