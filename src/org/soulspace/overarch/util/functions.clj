(ns org.soulspace.overarch.util.functions)

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


;;
;; Test helper
;;

(defn truthy?
  "Returns true, if the argument `e` is truthy."
  [e]
  (if e true false))

