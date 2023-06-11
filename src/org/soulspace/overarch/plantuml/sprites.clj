(ns org.soulspace.overarch.plantuml.sprites
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [charred.api :as csv]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.overarch.io :as oio]))

(def excluded-libs #{"aws" "awslib" "awslib10" "archimate" "C4" "classy" "classy-c4" "DomainStory" "kubernetes"})

(defn capitalize-parts
  "Returns a version of the string with capitalized parts, separated by `separator`"
  [s separator]
  (->> (str/split s (re-pattern separator))
       (map str/capitalize)
       (str/join separator)))

; convert names correctly
(defn tech-name
  "Create a tech name from the sprite name s."
  [s]
  (-> s
      (sstr/camel-case-to-hyphen)
      (str/replace "_" "-")
      (str/replace "-" " ")
      (capitalize-parts " ")
      ))

(defn plantuml-imports
  "Returns a collection of vectors of all the PlantUML '*.puml' files in
   the given directory `dir` containing the path relative to the `dir`
   and the base name of the import file."
  [dir]
  (->> dir
       (file/all-files-by-extension "puml")
       (map (partial file/relative-path dir))
       (map (juxt file/parent-path file/base-name))
       ;(map file/base-name)
       ))

(defn write-csv
  "Writes the collection `coll` in CSV format to `file`."
  [file coll]
  (with-open [wrt (io/writer file)]
    (csv/write-csv wrt coll)))

(defn sprite-entry
  "Prepares sprite entry info from collection PlantUML imports."
  [x]
  (let [path-entries (str/split (first x) #"/")
        sprite-prefix (first path-entries)
        sprite-path (str/join "/" (rest path-entries))
        sprite-name (last x)
        sprite-key (tech-name (last x))]
    (if (= "tupadr3" sprite-prefix)
      {:key sprite-key
       :lib sprite-path
       :prefix sprite-prefix
       :path sprite-path
       :name sprite-name}
      {:key sprite-key
       :lib sprite-prefix
       :prefix sprite-prefix
       :path sprite-path
       :name sprite-name})))

(defn key-length
  "Returns the length of the key of `x`."
  [x]
  (count (:key x)))

; find max length of sprite keys and pad/indent maps
; use text instead of println with *out* binding
(defn write-sprite-map
  "Writes the collection `coll` to `file`."
  [file coll]
  (let [max-length (reduce max 0 (map key-length coll))]
    (println "max-length " max-length)
    (with-open [wrt (io/writer file)]
      (binding [*out* wrt]
        (println "{")
        (doseq [entry coll]
          (println (str "  \"" (:key entry) "\""
;                        (str/join (repeat (- (+ max-length 1) (count (:key entry))) " "))
                        " {:lib \"" (:lib entry)
                        "\" :prefix \"" (:prefix entry)
                        "\" :path \"" (:path entry)
                        "\" :name \"" (:name entry) "\"}")))
        (println "}")))))

(defn write-sprite-maps
  "Writes the tech to sprite mappings for the libs in the map."
  [m]
  (doseq [[k v] m]
    (write-sprite-map (str k ".edn") v)))

(comment
  (count "/home/soulman/devel/tmp/plantuml-stdlib")
  (write-csv "dev/stdlib.csv" (into [] (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib")))

  (count (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))

  (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))
  (write-sprite-map "stdlib" (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/")))

  (group-by :lib (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/")))
  (write-sprite-maps (group-by :lib (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))))
  )