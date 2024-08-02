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

