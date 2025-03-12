(ns org.soulspace.overarch.adapter.template.template-api
  (:require [clojure.string :as str]
            [org.soulspace.overarch.util.functions :as fns]))

;;;
;;; General Render functions
;;;
(defn single-line
  "Converts the string `s` to a single line string."
  [s]
  (fns/single-line s))

(defn multi-lines
  "Converts the string `s` to a multiline string with a maximum line length of `line-length`."
  ([s]
   (multi-lines s 40))
  ([s line-length]
   (fns/multi-lines s line-length)))

(defn escape-html
  "Escapes the HTML special characters in the string `s`."
  [s]
  (fns/escape-html s))