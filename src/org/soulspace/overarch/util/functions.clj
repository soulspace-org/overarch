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
  (when s
    (->> (str/split s re)
         (map str/trim)
         (into [])))))

(defn to-singleline
  "Converts the string to a single line string."
  [s]
  (when s
    (->> s
         (str/split-lines)
         (map str/trim)
         (str/join " "))))

(defn wrap-str
  "Wraps the argument `s` in double quotes.line-seq"
  [s]
  (str "\"" s "\""))

(defn binding-vector
  "Creates a binding vector for the entries of the data map `m`."
  [m]
  (let [keysyms (->> (keys m)
                     (map name)
                     (map symbol))
        values (vals m)]
    (into [] (interleave keysyms values))))

(comment
  (binding-vector {:e {:el :system
                       :id :foo/foo-bar}})
  )


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


