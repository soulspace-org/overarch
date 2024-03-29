(ns org.soulspace.overarch.util.functions 
  (:require [clojure.string :as str]))

;;
;; Helper functions
;;
(defn keyword-set
  "Converts the `coll` of strings or symbols into a set of keywords."
  [coll]
  (->> coll
       (map keyword)
       (into #{})))

(defn tokenize-string
  "Returns a vector of strings by tokenizing the string `s` with the optional regex `re`
   (comma per default)."
  ([s]
   (tokenize-string s #","))
  ([s re]
   (->> (str/split s re)
        (map str/trim)
        (into []))))

;;
;; Tapping data
;;
(defn data-tapper
  "Sends the `data` and and optional context `ctx` to the tap.
   Useful for viewing data and debugging."
  ([data]
   (tap> data)
   data)
  ([ctx data]
   (tap> {:ctx ctx :data data})
   data))


