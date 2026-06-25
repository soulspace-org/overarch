;;;;
;;;; Template Engine implementation based on Comb templates.
;;;; Some code adapted from comb (https://github.com/weavejester/comb).
;;;;
(ns org.soulspace.overarch.adapter.template.comb
  "Template Engine implementation based on Comb templates."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [sci.core :as sci]
            [org.soulspace.overarch.application.template :as t]
            ; required to expose namespaces in templates
            [clojure.set :as set]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.overarch.adapter.template.model-api :as m]
            [org.soulspace.overarch.adapter.template.view-api :as v]))

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
;;; Comb templates evaluated with the Small Clojure Interpreter (SCI)
;;;

; FIXME we need the options to get the template directory for loading templates from other templates
(defn load-fn
  "Load a template namespace into the SCI context."
  [{:keys [namespace]}]
  (let [template-dir "templates/"
        path (str/replace (name namespace) #"\." "/")
        filename (str template-dir path ".clj")
        source (slurp filename)
        result {:file filename
                :source source}]
    ;(println "Loading namespace" namespace "from file:" filename)
    ;(println "Source:" source)
    result))

(def memoized-load-fn (memoize load-fn))

(defn sci-opts
  []
  (let [model-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.model-api)
        model-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.model-api model-ns)
        view-ns (sci/create-ns 'org.soulspace.overarch.adapter.template.view-api)
        view-sci-ns (sci/copy-ns org.soulspace.overarch.adapter.template.view-api view-ns)
        sstr-ns (sci/create-ns 'org.soulspace.clj.string)
        sstr-sci-ns (sci/copy-ns org.soulspace.clj.string sstr-ns)
        set-ns (sci/create-ns 'clojure.set)
        set-sci-ns (sci/copy-ns clojure.set set-ns)
        string-ns (sci/create-ns 'clojure.string)
        string-sci-ns (sci/copy-ns clojure.string string-ns)]
    {:namespaces {'clojure.set set-sci-ns
                  'clojure.string string-sci-ns
                  'org.soulspace.overarch.adapter.template.model-api model-sci-ns
                  'org.soulspace.overarch.adapter.template.view-api view-sci-ns
                  'org.soulspace.clj.string sstr-sci-ns}
     :ns-aliases '{set clojure.set
                   str clojure.string
                   m org.soulspace.overarch.adapter.template.model-api
                   v org.soulspace.overarch.adapter.template.view-api
                   sstr org.soulspace.clj.string}
     :load-fn memoized-load-fn}))

(def sci-ctx (sci/init sci-opts))

;; TODO check sci/parse-next with a sci context to parse the template once into a
;;      clojure form and then evaluate the form with sci/eval-form instead of
;;      parsing the template into a string of clojure code and evaluating that
;;      string with sci/eval-string.
;;      This would allow to catch parsing errors in the template and also be more
;;      efficient as the template is only parsed once.
;;      The context would also cache required namespaces.

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
     (sci/with-out-str (sci/eval-string parsed-template opts)))))

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
