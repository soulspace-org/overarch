(ns scratch
  (:require [clojure.set :as set]))


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
