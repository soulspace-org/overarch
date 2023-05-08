(defproject org.soulspace.clj/overarch "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/tools.cli "1.0.206"]
                 [com.cnuernber/charred "1.028"]
                 [hawk/hawk "0.2.11"]
                 [org.soulspace.clj/clj.java "0.9.1"]]

  :repl-options {:init-ns org.soulspace.overarch.core}

  :profiles {:dev {:dependencies [[djblue/portal "0.37.1"]
                                  [criterium/criterium "0.4.6"]
                                  [expound/expound "0.9.0"]]
                   :global-vars {*warn-on-reflection* true}}})
