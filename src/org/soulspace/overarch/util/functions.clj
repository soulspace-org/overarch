(ns org.soulspace.overarch.util.functions)


(defn keyword-set
  "Converts the `coll` into a set of keywords."
  [coll]
  (->> coll
       (map keyword)
       (into #{})))

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


