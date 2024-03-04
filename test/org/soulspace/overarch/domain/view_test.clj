(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(def styles-input
  #{{:el :context-view
     :id :test/styles-context-view
     :spec {:styles #{{:id :test/dashed-rel
                       :el :rel
                       :text-color "#000000"
                       :line-color "#000000"
                       :line-style :dashed
                       :tech "async"
                       :legend-text "asynchronous"}
                      {:id :test/dotted-rel
                       :el :rel
                       :line-style :dotted
                       :legend-text "dependency"}
                      {:id :test/bold-rel
                       :el :rel
                       :line-style :bold
                       :line-color "#007700"
                       :text-color "#000077"
                       :legend-text "synchronous"}
                      {:id :test/critical
                       :el :element
                       :bg-color "#CC0000"
                       :border-color "#330000"
                       :text-color "#000000"
                       :legend-text "critical system"}}}
     :title "Style Test"
     :ct [{:el :system
           :id :test/sys1
           :name "Sys1"
           :style :test/critical}
          {:el :system
           :id :test/sys2
           :name "Sys2"
           :external true}
          {:el :rel
           :from :test/sys1
           :to :test/sys2
           :name "calls"
           :style :test/dashed-rel}]}
    
    {:el :theme
     :id :test/test-theme1
     :styles #{{:id :test/dashed-rel
                :el :rel
                :text-color "#000000"
                :line-color "#000000"
                :line-style :dashed
                :tech "async"
                :legend-text "asynchronous"}
               {:id :test/dotted-rel
                :el :rel
                :line-style :dotted
                :legend-text "dependency"}
               {:id :test/bold-rel
                :el :rel
                :line-style :bold
                :line-color "#007700"
                :text-color "#000077"
                :legend-text "synchronous"}
               {:id :test/critical
                :el :element
                :bg-color "#CC0000"
                :border-color "#330000"
                :text-color "#000000"
                :legend-text "critical system"}}}

    {:el :context-view
     :id :test/theme-context-view
     :spec {:themes [:test/test-theme1]}
     :title "Theme Test"
     :ct [{:el :system
           :id :test/sys1
           :name "Sys1"
           :style :test/critical}
          {:el :system
           :id :test/sys2
           :name "Sys2"
           :external true}
          {:el :rel
           :from :test/sys1
           :to :test/sys2
           :name "calls"
           :style :test/dashed-rel}]}})

(def styles-model (model/build-model styles-input))

(deftest styles-spec-test
  ; TODO 
  (testing "styles-spec-test"
    (is (= 1 1)))) 

(comment
  (:views styles-model)
  (:themes styles-model)
  (styles-spec styles-model ((:id->element styles-model) :test/styles-context-view))
  (styles-spec styles-model ((:id->element styles-model) :test/theme-context-view))
  )
