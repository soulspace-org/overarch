;;;;
;;;; Template Engine implementation based on Comb templates.
;;;; Some code adapted from comb (https://github.com/weavejester/comb).
;;;;
(ns org.soulspace.overarch.adapter.template.comb
  "Template Engine implementation based on Comb templates."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]
            [clojure.java.io :as io]
            [sci.core :as sci]
            [org.soulspace.overarch.application.template :as t]
            ; require to expose namespaces in templates
            [clojure.set :as set]
            [clojure.string :as str]
            [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]
            [org.soulspace.overarch.adapter.template.template-api :as ta]
            [org.soulspace.overarch.adapter.template.graphviz-api :as gv]
            [org.soulspace.overarch.adapter.template.markdown-api :as md]
            ))

;;;
;;; Parse Comb Templates
;;;
(binding [*read-eval* false])

(def delimiters ["<%" "%>"])

(def parser-regex
  (re-pattern
   (str "(?s)\\A"
        "(?:" "(.*?)"
        (first delimiters) "(.*?)" (last delimiters)
        ")?"
        "(.*)\\z")))

(defn emit-string
  [s]
  (print "(print " (pr-str s) ")"))

(defn emit-expr
  [^String expr]
  (if (.startsWith expr "=")
    (print "(print (str " (subs expr 1) "))")
    (print expr)))

(defn- parse-string
  [src]
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

;;;
;;; Comb templates evaluated with core.eval
;;;
(defn compile-fn
  [args parsed]
  (core/eval
   `(core/fn ~args
      (with-out-str
        ~(read-string parsed)))))

(defmacro fn
  "Compile a template into a function that takes the supplied arguments. The
  template source may be a string, or an I/O source such as a File, Reader or
  InputStream."
  [args parsed]
  `(compile-fn '~args ~parsed))

(defn eval
  "Evaluate a template using the supplied bindings. The template source may
  be a string, or an I/O source such as a File, Reader or InputStream."
  ([parsed bindings]
   (let [current-ns *ns*]
     (binding [*ns* current-ns]
       (let [keys (map (comp symbol name) (keys bindings))
             func (compile-fn [{:keys (vec keys)}] parsed)]
         (func bindings))))))

(defmethod t/parse-template :comb
  ([engine-key template]
   (-> template
       (t/read-source)
       (parse-string))))

(defmethod t/apply-template :comb
  ([engine-key parsed-template data]
   (eval parsed-template data)))

(comment ; Comb
  (defn greet-code [x] (print "Hello" x "!"))

  (core/eval (read-string "el/technical-architecture-node-types"))

  (parse-string "Hello<% (dotimes [x 3] %> World<% ) %>!")
  (core/eval (read-string (parse-string (slurp "templates/ns-test.cmb"))))

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

;;;
;;; Comb templates evaluated with the Small Clojure Interpreter (SCI)
;;;
(defn sci-opts
  []
  (let [model-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.model-api)
        model-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.model-api model-ns)
        view-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.view-api)
        view-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.view-api view-ns)
        template-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.template-api)
        template-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.template-api template-ns)
        markdown-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.markdown-api)
        markdown-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.markdown-api markdown-ns)
        graphviz-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.graphviz-api)
        graphviz-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.graphviz-api graphviz-ns)
        set-ns (sci/create-ns 'clojure.set)
        set-sci-ns (sci/copy-ns clojure.set set-ns)
        string-ns (sci/create-ns 'clojure.string)
        string-sci-ns (sci/copy-ns clojure.string string-ns)]
    {:namespaces {'clojure.set set-sci-ns
                  'clojure.string string-sci-ns
                  'org.soulspace.overarch.adapter.template.model-api model-sci-ns
                  'org.soulspace.overarch.adapter.template.view-api view-sci-ns
                  'org.soulspace.overarch.adapter.template.template-api template-sci-ns
                  'org.soulspace.overarch.adapter.template.markdown-api markdown-sci-ns
                  'org.soulspace.overarch.adapter.template.graphviz-api graphviz-sci-ns}
     :ns-aliases '{set clojure.set
                   str clojure.string
                   m org.soulspace.overarch.adapter.template.model-api
                   v org.soulspace.overarch.adapter.template.view-api
                   t org.soulspace.overarch.adapter.template.template-api
                   md org.soulspace.overarch.adapter.template.markdown-api
                   gv org.soulspace.overarch.adapter.template.graphviz-api}}))

(def sci-ctx (sci/init sci-opts))

(defn eval-sci
  "Evaluate a `parsed-template` using the supplied `data` bindings."
  ([parsed-template data]
   (let [e (:e data)
         ctx (:ctx data)
         model (:model data)
         view (:view data)
         protected-areas (:protected-areas data)
         opts (update-in (sci-opts) [:namespaces] merge {'user {'ctx ctx
                                                                'model model
                                                                'protected-areas protected-areas
                                                                'e e
                                                                'view view}})]
     (try
       (sci/with-out-str (sci/eval-string parsed-template opts))
       (catch Exception e
         (println "Exception while generating for template" (:template ctx))
         (println (ex-message e))
         (println (ex-data e))
         (when (:debug ctx)
           (println "Parsed Template: ")
           (println parsed-template))
         ;(println (ex-cause e))
         )))))

(defmethod t/parse-template :combsci
  ([engine-key template]
   (-> template
       (t/read-source)
       (parse-string))))

(defmethod t/apply-template :combsci
  ([engine-key parsed-template data]
   (eval-sci parsed-template data)))

(comment ; Comb SCI
  (sci/eval-string* sci-ctx "\"Hello\"")
  (sci/eval-string* sci-ctx "\"Hello<% (dotimes [x 3] %> World<% ) %>!\"")
  (sci/eval-string (parse-string "Hello<% (dotimes [x 3] %> World<% ) %>!") (sci-opts))
  (sci/eval-string* sci-ctx "(do (print  \"Hello\" ) (dotimes [x 3] (print  \" World\" ) ) (print  \"!\" ))")
  (t/apply-template :combsci "Hello")
  (t/apply-template :combsci "*ns*")
  (t/apply-template :combsci "\"Hello<% (dotimes [x 3] %> World<% ) %>!\"" {})
  (t/apply-template :combsci "<% (defn greet-template [x] (print \"Hello\" x))
                           (greet-template \"World\")%>" {})
  (t/apply-template :combsci "<%= (str/join \";\" [1 2 3])%>")
  (t/apply-template :combsci "<%= (m/element-name e) %>"
                    {:e {:el :system
                         :id :foo/foo-bar}})
  (t/apply-template :combsci (io/as-file "dev/templates/ns-test.cmb") {:e {:el :system
                                                                           :id :foo/foo-bar}})
  ;
  )
