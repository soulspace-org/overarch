;;;;
;;;; Code adapted from comb (https://github.com/weavejester/comb).
;;;;
(ns org.soulspace.overarch.adapter.template.comb
  "Clojure templating library."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]
            [clojure.java.io :as io]
            [org.soulspace.overarch.application.template :as t]
            ; require overarch domain to make functions available for templates
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

(defn- read-source [source]
  (if (string? source)
    source
    (slurp source)))

(def delimiters ["<%" "%>"])

(def parser-regex
  (re-pattern
   (str "(?s)\\A"
        "(?:" "(.*?)"
        (first delimiters) "(.*?)" (last delimiters)
        ")?"
        "(.*)\\z")))

(defn emit-string [s]
  (print "(print " (pr-str s) ")"))

(defn emit-expr [expr]
  (if (.startsWith expr "=")
    (print "(print (str " (subs expr 1) "))")
    (print expr)))

(defn- parse-string [src]
  (with-out-str
    (print "(do ")
    (loop [src src]
      (let [[_ before expr after] (re-matches parser-regex src)]
        (if expr
          (do (emit-string before)
              (emit-expr expr)
              (recur after))
          (do (emit-string after)
              (print ")")))))))

(defn compile-fn
  [args src]
  (core/eval
   `(core/fn ~args
      (with-out-str
        ~(-> src read-source parse-string read-string)))))

(defmacro fn
  "Compile a template into a function that takes the supplied arguments. The
  template source may be a string, or an I/O source such as a File, Reader or
  InputStream."
  [args source]
  `(compile-fn '~args ~source))

(defn eval
  "Evaluate a template using the supplied bindings. The template source may
  be a string, or an I/O source such as a File, Reader or InputStream."
  ([source]
   (eval source {}))
  ([source bindings]
   (println "Current Namespace:" *ns*)
   (let [current-ns *ns*] 
     (println "Current Namespace:" *ns*)
     (binding [*ns* current-ns]
       (let [keys (map (comp symbol name) (keys bindings))
             func (compile-fn [{:keys (vec keys)}] source)]
         (func bindings))))))

(defmethod t/read-template :comb
  [rtype template]
  (read-source template)
  )

(defmethod t/apply-template :comb
  ([engine-key template]
   (eval template))
  ([engine-key template data]
   (eval template data)))

(comment
  (defn greet-code [x] (print "Hello" x "!"))

  (t/apply-template :comb "Hello" {})
  (t/apply-template :comb "Hello<% (dotimes [x 3] %> World<% ) %>!" {})
  (t/apply-template :comb "Hello <%= name %>!" {:name "World"})
  (t/apply-template :comb "Hello <% (:name bindings) %>!" {:name "World"})
  (t/apply-template :comb "<% (defn greet-template [x] (print \"Hello\" x))
                           (greet-template \"World\")%>" {})
  (t/apply-template :comb "<% (greet-code \"World\")%>" {})
  (t/apply-template :comb "<%= (el/element-name e) %>" {:e {:el :system
                                                           :id :foo/foo-bar}})
  (t/apply-template :comb "<%= (:id e) %>" {:e {:el :system
                                                :id :foo/foo-bar}})

  (t/apply-template :comb (io/as-file "templates/ns-test.cmb"))
  )
