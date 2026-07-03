(defproject org.soulspace.clj/overarch "0.42.0"
  :description "Overarch provides a data model for the holistic description of a software system, opening multiple use cases on the model data."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.12.5"]
                 [org.clojure/tools.cli "1.4.256"]
                 [org.clojars.quoll/tiara "0.5.2"]
                 [com.cnuernber/charred "1.039"]
                 [com.nextjournal/beholder "1.0.3"]
                 [expound/expound "0.9.0"]
                 [org.babashka/sci "0.13.53"]
                 [zprint/zprint "1.3.0"]
                 [org.slf4j/slf4j-nop "2.0.18"]
                 [org.soulspace.clj/clj.java "0.9.1"]
                 [org.soulspace.clj/cmp.markdown "0.4.1"]]

  :repl-options {:init-ns org.soulspace.overarch.adapter.ui.cli}

  :uberjar-name "overarch.jar"
  :main org.soulspace.overarch.adapter.ui.cli

  :scm {:name "git" :url "https://github.com/soulspace-org/overarch"}
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]])
