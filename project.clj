(defproject org.soulspace.clj/overarch "0.40.0-SNAPSHOT"
  :description "Overarch provides a data model for the holistic description of a software system, opening multiple use cases on the model data."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.12.1"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.clojars.quoll/tiara "0.4.0"]
                 [com.cnuernber/charred "1.037"]
                 [com.nextjournal/beholder "1.0.2"]
                 [expound/expound "0.9.0"]
                 [org.babashka/sci "0.10.47"]
                 [zprint/zprint "1.3.0"]
                 [org.slf4j/slf4j-nop "2.0.17"]
                 [org.soulspace.clj/clj.java "0.9.1"]
                 [org.soulspace.clj/cmp.markdown "0.4.1"]]

  :repl-options {:init-ns org.soulspace.overarch.adapter.ui.cli}

  :uberjar-name "overarch.jar"
  :main org.soulspace.overarch.adapter.ui.cli

  :scm {:name "git" :url "https://github.com/soulspace-org/overarch"}
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]])
