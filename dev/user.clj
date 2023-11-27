
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
  )

