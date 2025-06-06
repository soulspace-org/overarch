;;;;
;;;; Development functions
;;;;
(ns dev
  (:require [clojure.string :as str]))

(def topic-docs
  "List of topics to be compiled into the usage.md file in order."
  ["overview" "rationale" "commandline" "editor" "modelling"
   "selection-criteria" "views" "exports" "templates" "best-practices"])

(defn compile-usage-doc
  "Compiles the topic docs into a single usage.md file."
  []
  (->> topic-docs
       (mapv #(slurp (str "doc/topics/" % ".md")))
       (str/join "\n")
       (spit "doc/usage.md"))
  ;
  )

(comment
  (compile-usage-doc)
  ;
  )
