(ns scratch
  (:require [tiara.data :as td]))

(def e1 {:techs #{"Clojure" "ClojureScript"}})
(def e2 {:techs (td/ordered-set "Clojure" "ClojureScript")}) 

(= e1 e2)
(contains? #{e1} e2)
(contains? #{e2} e1)

;;
;; Metadata Handling
;; Source: Slack/Tim Pradley
;;
#_(defn assoc-meta [value k v]
  "Adds `k` to value metadata if it can"
  (cond (instance? IReference value) (do (alter-meta! value assoc k v) value)
        (instance? IMeta value) (vary-meta value assoc k v)
        :else value))

#_(defn dissoc-meta [value k]
  "Removes `k` from the metadata of value if present"
  (cond (instance? IReference value) (do (alter-meta! value dissoc k) value)
        (instance? IMeta value) (vary-meta value dissoc k)
        :else value))
