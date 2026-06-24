(ns lib.text
  "Functions for templates generating text."
  (:require [clojure.string :as str]))

;;;
;;; Type to name mapping
;;;
(def type->name
  "Maps an element type to a display name."
  {:person   "Person/Role"
   :use-case "Use Case"
   :context-boundary "Subdomain/Bounded Context"
   :subject "Subject"
   :type "Type"})

;;;
;;; General Render functions
;;;
(defn tokenize-string
  "Returns a vector of strings by tokenizing the string `s` with the optional regex `re`
   (comma per default)."
  ([s]
   (tokenize-string s #","))
  ([s re]
   (when s
     (->> (str/split s re)
          (mapv str/trim)))))

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
  (when-let [name (:name e)]
    (str/upper-case (first name))))

(defn single-line
  "Converts the string to a single line string. Returns an empty string, if `s` is nil."
  [s]
  (if s
    (->> s
         (str/split-lines)
         (map str/trim)
         (str/join " "))
    ""))

(defn wrap-string
  "Wraps the argument `s` in double quotes."
  [s]
  (str "\"" s "\""))

(defn escape-html
  "Escapes the HTML special characters in the string `s`. Returns an empty string, if `s` is nil."
  [s]
  (if (seq s)
    (-> s
        (str/replace "&" "&amp;")
        (str/replace "<" "&lt;")
        (str/replace ">" "&gt;")
        (str/replace "\"" "&quot;")
        (str/replace "'" "&apos;"))
    ""))

