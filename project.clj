(defproject org.soulspace.clj/overarch "0.19.0-SNAPSHOT"
  :description "Overarch provides a data model for the holistic description of a software system, opening multiple use cases on the model data."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.11.3"]
                 [org.clojure/data.json "2.5.0"]
                 [org.clojure/tools.cli "1.1.230"]
                 [com.cnuernber/charred "1.034"]
                 [com.nextjournal/beholder "1.0.2"]
                 [expound/expound "0.9.0"]
                 [org.babashka/sci "0.8.41"]
                 [org.slf4j/slf4j-nop "2.0.13"]
                 [org.soulspace.clj/clj.java "0.9.1"]
                 [org.soulspace.clj/cmp.markdown "0.4.1"]]

  :repl-options {:init-ns org.soulspace.overarch.adapter.ui.cli}

  :uberjar-name "overarch.jar"
  :main org.soulspace.overarch.adapter.ui.cli

  :scm {:name "git" :url "https://github.com/soulspace-org/overarch"}
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]])
