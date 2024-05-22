(defproject org.soulspace.clj/overarch "0.18.0-SNAPSHOT"
  :description "Overarch provides a data model for the holistic description of a software system, opening multiple use cases on the model data."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.11.3"]
                 [org.clojure/data.json "2.5.0"]
                 [org.clojure/tools.cli "1.1.230"]
                 [com.cnuernber/charred "1.034"]
                 [com.nextjournal/beholder "1.0.2"]
                 [expound/expound "0.9.0"]
                 [org.slf4j/slf4j-nop "2.0.13"]
                 [org.soulspace.clj/clj.java "0.9.1"]
                 [org.soulspace.clj/cmp.markdown "0.4.1"]]

  :repl-options {:init-ns org.soulspace.overarch.adapter.ui.cli}

  :plugins [[com.github.liquidz/antq "RELEASE"]]
   ;; optional - you can add antq options here:
  :antq {}

;  :profiles {:dev {:dependencies [[djblue/portal "0.55.1"]
;                                  [criterium/criterium "0.4.6"]
;                                  [com.clojure-goes-fast/clj-java-decompiler "0.3.4"]
;                                  ; [expound/expound "0.9.0"]
;                                  ]
;                   :global-vars {*warn-on-reflection* true}}}

  :uberjar-name "overarch.jar"
  :main org.soulspace.overarch.adapter.ui.cli

  :scm {:name "git" :url "https://github.com/soulspace-org/overarch"}
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]])
