;;;;
;;;; Code adapted from comb (https://github.com/weavejester/comb).
;;;;
(ns org.soulspace.overarch.adapter.template.comb
  "Clojure templating library."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [sci.core :as sci]
            [org.soulspace.overarch.application.template :as t]
            ; require overarch domain to make functions available for templates
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]))

; (def *read-eval* false)

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

(defn emit-expr [^String expr]
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
   (let [current-ns *ns*] 
     (binding [*ns* current-ns]
       (let [keys (map (comp symbol name) (keys bindings))
             func (compile-fn [{:keys (vec keys)}] source)]
         (func bindings))))))

(defmethod t/read-template :comb
  [rtype template]
  (read-source template))

(defmethod t/apply-template :comb
  ([engine-key template]
   (eval template))
  ([engine-key template data]
   (eval template data)))

(comment
  (defn greet-code [x] (print "Hello" x "!"))

  (core/eval (read-string "el/technical-architecture-node-types"))

  (parse-string "Hello<% (dotimes [x 3] %> World<% ) %>!")
  (core/eval (read-string (parse-string (slurp "templates/ns-test.cmb"))))
  (core/eval (read-string (parse-string "<%= el/technical-architecture-node-types %>")))

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
  (t/apply-template :comb (io/as-file "dev/templates/ns-test.cmb") {:e {:el :system
                                                                        :id :foo/foo-bar}})
  ;
  )

(defn- wrap-str
  [s]
  (str "\"" s "\""))

(def sci-opts {:namespaces {'clojure.core {'print print}
                            'clojure.string {'join str/join}
                            'org.soulspace.overarch.domain.element {'element-name el/element-name}}
               :ns-aliases '{el org.soulspace.overarch.domain.element
                             str clojure.string}})
(def ctx (sci/init sci-opts))

(defn eval-sci
  "Evaluate a template using the supplied bindings. The template source may
  be a string, or an I/O source such as a File, Reader or InputStream."
  ([source]
   (eval-sci source {}))
  ([source bindings]
   (let [parsed (-> source
                    (read-source)
                    (parse-string)
                    ;(wrap-str)
                    )
         _ (println parsed)
         keys (map (comp symbol name) (keys bindings))
         _ (println keys)
         ;opts {:bindings data}
         ]
     (sci/eval-string parsed
                      sci-opts))))

(defmethod t/read-template :combsci
  [rtype template]
  (read-source template))

(defmethod t/apply-template :combsci
  ([engine-key template]
   (eval-sci template))
  ([engine-key template data]
   (eval-sci template data)))

(comment
  ;
  (parse-string "Hello<% (dotimes [x 3] %> World<% ) %>!")
  (clojure.core/eval "Hello")
  (sci/eval-string* ctx "\"Hello\"")
  (sci/eval-string* ctx "\"Hello<% (dotimes [x 3] %> World<% ) %>!\"")
  (sci/eval-string (parse-string "Hello<% (dotimes [x 3] %> World<% ) %>!") sci-opts)
  (sci/eval-string* ctx "(do (print  \"Hello\" ) (dotimes [x 3] (print  \" World\" ) ) (print  \"!\" ))")
  (t/apply-template :combsci "Hello")
  (t/apply-template :combsci "*ns*")
  (t/apply-template :combsci "\"Hello<% (dotimes [x 3] %> World<% ) %>!\"" {})
  (t/apply-template :combsci "<% (defn greet-template [x] (print \"Hello\" x))
                           (greet-template \"World\")%>" {})
  (t/apply-template :combsci "<%= (str/join \";\" [1 2 3])%>")
  (t/apply-template :combsci "<%= (el/element-name e) %>"
                    {:e {:el :system
                         :id :foo/foo-bar}})
  (t/apply-template :combsci (io/as-file "dev/templates/ns-test.cmb") {:e {:el :system
                                                                           :id :foo/foo-bar}})
  )
