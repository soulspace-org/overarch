(ns org.soulspace.overarch.util.functions 
  (:require [clojure.string :as str]))

;;;
;;; Helper functions
;;;
(defn key-set
  "Returns a set of the keys of the map `m`."
  [m]
  (into #{} (keys m)))

(defn keyword-set
  "Converts the `coll` of strings or symbols into a set of keywords."
  [coll]
  (->> coll
       (map keyword)
       (into #{})))

;;;
;;; String functions
;;;
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

(defn multi-lines
  "Converts the string `s` to a multiline string with a maximum line length of `line-length`."
  ([s]
   (multi-lines s 40))
  ([s line-length]
   (let [tokens (tokenize-string s #"\s+")]
     (reduce (fn [acc line]
               (if (empty? acc)
                 (conj acc line)
                 (let [last-line (last acc)
                       new-line (str last-line " " line)]
                   (if (<= (count new-line) line-length)
                     (conj (pop acc) new-line)
                     (conj acc line)))))
             [] tokens))))


(defn first-char-uppercase
  "Returns the first character of the name of an element `e`."
  [e]
  (str/upper-case (first (:name e))))

(defn single-line
  "Converts the string to a single line string."
  [s]
  (when s
    (->> s
         (str/split-lines)
         (map str/trim)
         (str/join " "))))

(defn wrap-string
  "Wraps the argument `s` in double quotes."
  [s]
  (str "\"" s "\""))

(defn escape-html
  "Escapes the HTML special characters in the string `s`."
  [s]
  (-> s
      (str/replace #"&" "&amp;")
      (str/replace #"<" "&lt;")
      (str/replace #">" "&gt;")
      (str/replace #"\"" "&quot;")
      (str/replace #"'" "&apos;")))

(comment ; string functions
  (single-line "This is a multiline string,
                which even could be much longer,
                that should be joint into a single line.")
  (multi-lines "This is a very long string, which even could be much longer, that should be split into multiple lines." 20)
  (multi-lines "This is a very long string, which even could be much longer, that should be split into multiple lines." 40)
  (multi-lines "This is a very long string, which even could be much longer, that should be split into multiple lines.")
  (escape-html "<html>")
  (escape-html "Risk & Compliance")
  ; 
  )

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
  "Sends the `data` and and optional context `ctx` to the tap. Useful for viewing data and debugging."
  ([data]
   (tap> data)
   data)
  ([ctx data]
   (tap> {:ctx ctx :data data})
   data))


