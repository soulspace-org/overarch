(ns user
  (:require [clojure.string :as str]))
;;;;
;;;; Development functions
;;;;

;;;
;;; Portal data viewer
;;;
(comment
  (require '[portal.api :as portal])
  (def p (portal/open {:launcher :vs-code}))
  ;(def p (portal/open))
  (add-tap #'portal/submit)

  (tap> "hello")
  (portal/clear)
  (tap> :world)
  (tap> {:firstname "Donald"
         :lastname "Duck"
         :address {:street "Entenstr. 123"
                   :city "Entenhausen"}})

  (remove-tap #'portal/submit)

  (portal/close)
  (portal/docs))

;;;
;;; Criterium
;;; 
(comment
  (require '[criterium.core :as criterium])
  (criterium/bench (Thread/sleep 1000))
  (criterium/quick-bench (Thread/sleep 1000))
  )

;;;
;;; Expound spec tool
;;;
(comment
  (require '[expound.alpha :as expound])
  )

;;;
;;; Clj->Java decompiler
;;;
(comment
  (require '[clj-java-decompiler.core :refer [decompile disassemble]])
  (decompile (fn [] (println "Hello, World!")))
  (disassemble (fn [] (println "Hello, World!")))
  ;
  )

(def topic-docs
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