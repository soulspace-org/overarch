(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.adapter.reader.model-reader :as reader]))

(def opts {:input-model-format :overarch-input})

(def styles-input
  #{{:el :system
     :id :test/sys1
     :name "Sys1"
     :style :test/critical}
    {:el :system
     :id :test/sys2
     :name "Sys2"
     :external true}
    {:el :request
     :id :test/sys1-calls-sys2
     :from :test/sys1
     :to :test/sys2
     :name "calls"}
    {:el :context-view
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
     :ct [{:ref :test/sys1}
          {:ref :test/sys2}
          {:ref :test/sys1-calls-sys2 :style :test/dashed-rel}]}

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
     :ct [{:ref :test/sys1}
          {:ref :test/sys2}
          {:ref :test/sys1-calls-sys2 :style :test/dashed-rel}]}})

(def styles-model (reader/build-model opts styles-input))

(deftest styles-spec-test
  ; TODO 
  (testing "styles-spec-test"
    (is (= 1 1))))

(def c4-model1 (reader/build-model
                opts
                #{{:el :person
                   :id :test/user1
                   :name "User 1"}
                  {:el :system
                   :id :test/ext-system1
                   :external true
                   :name "External System 1"}
                  {:el :system
                   :id :test/system1
                   :name "Test System"
                   :ct #{{:el :container
                          :id :test/container1
                          :name "Test Container 1"
                          :ct #{{:el :component
                                 :id :test/component11
                                 :name "Test Component 11"}
                                {:el :component
                                 :id :test/component12
                                 :name "Test Component 12"}}}
                         {:el :container
                          :id :test/container-db1
                          :subtype :database
                          :name "Test DB Container 1"}
                         {:el :container
                          :id :test/container-queue1
                          :subtype :queue
                          :name "Test Queue Container 1"}}}
                  {:el :request
                   :id :test/user1-uses-system1
                   :from :test/user1
                   :to :test/system1
                   :name "uses"}
                  {:el :request
                   :id :test/system1-calls-ext-system1
                   :from :test/system1
                   :to :test/ext-system1
                   :name "calls"}
                  {:el :request
                   :id :test/user1-uses-container1
                   :from :test/user1
                   :to :test/container1
                   :name "uses"}
                  {:el :request
                   :id :test/container1-calls-ext-system1
                   :from :test/container1
                   :to :test/ext-system1
                   :name "calls"}
                  {:el :request
                   :id :test/container1-stores-in-container-db1
                   :from :test/container1
                   :to :test/container-db1
                   :name "stores in"}
                  {:el :publish
                   :id :test/container1-sends-to-container-queue1
                   :from :test/container1
                   :to :test/container-queue1
                   :name "sends to"}}))

(deftest layout-spec-test
  (testing "Layout in :spec"
    (is (= :left-right (layout-spec {:el :context-view
                                     :id :test/c4-context-view
                                     :title "Context View"
                                     :spec {:layout :left-right}
                                     :ct []}))))
  (testing "Layout in view"
    (is (= :left-right (layout-spec {:el :context-view
                                     :id :test/c4-context-view
                                     :title "Context View"
                                     :layout :left-right
                                     :ct []})))))

(deftest include-spec-test
  (testing "Include in :spec"
    (is (= :related (include-spec {:el :context-view
                                     :id :test/c4-context-view
                                     :title "Context View"
                                     :spec {:include :related}
                                     :ct []}))))
  (testing "Include in view"
    (is (= :related (include-spec {:el :context-view
                                     :id :test/c4-context-view
                                     :title "Context View"
                                     :include :related
                                     :ct []})))))

(deftest referenced-elements-test
  (testing "referenced-elements Refs Only"
    (is (= 5 (count (referenced-elements c4-model1
                                         {:el :context-view
                                          :id :test/c4-context-view-refs-only
                                          :title "Context View Refs Only"
                                          :ct [{:ref :test/system1-calls-ext-system1}
                                               {:ref :test/user1}
                                               {:ref :test/user1-uses-system1}
                                               {:ref :test/ext-system1}
                                               {:ref :test/system1}]})))))
  (testing "referenced-elements Selection Only"
    (is (= 0 (count (referenced-elements c4-model1
                                         {:el :context-view
                                          :id :test/c4-context-view-selection-only
                                          :title "Context View Selection Only"
                                          :spec {:selection {:namespace "test"}}
                                          :ct []}))))
    ;
    ))

(deftest selected-elements-test
  (testing "selected-elements Refs Only"
    (is (= 0 (count (selected-elements c4-model1
                                       {:el :context-view
                                        :id :test/c4-context-view-refs-only
                                        :title "Context View Refs Only"
                                        :ct [{:ref :test/system1-calls-ext-system1}
                                             {:ref :test/user1}
                                             {:ref :test/user1-uses-system1}
                                             {:ref :test/ext-system1}
                                             {:ref :test/system1}]})))))
  (testing "Selection Only"
    (is (= 5 (count (selected-elements c4-model1
                                    {:el :context-view
                                     :id :test/c4-context-view-selection-only
                                     :title "Context View Selection Only"
                                     :spec {:selection {:namespace "test"}}
                                     :ct []}))))
    (is (= 10 (count (selected-elements c4-model1
                                   {:el :container-view
                                    :id :test/c4-container-view-selection-only
                                    :title "Context View Selection Only"
                                    :spec {:selection {:namespace "test"}}
                                    :ct []}))))
    (is (= 8 (count (selected-elements c4-model1
                                        {:el :component-view
                                         :id :test/c4-component-view-selection-only
                                         :title "Context View Selection Only"
                                         :spec {:selection {:namespace "test"}}
                                         :ct []}))))
    (is (= 2 (count (selected-elements c4-model1
                                       {:el :context-view
                                        :id :test/c4-context-view-selection-only
                                        :title "Context View Selection Only"
                                        :spec {:selection {:el :system}}
                                        :ct []}))))
    ;
    ))

(deftest view-elements-test
  (testing "Refs Only"
    (is (= 5 (count (view-elements c4-model1
                                   {:el :context-view
                                    :id :test/c4-context-view-refs-only
                                    :title "Context View Refs Only"
                                    :ct [{:ref :test/system1-calls-ext-system1}
                                         {:ref :test/user1}
                                         {:ref :test/user1-uses-system1}
                                         {:ref :test/ext-system1}
                                         {:ref :test/system1}]})))))
  (testing "Selection Only"
    (is (= 5 (count (view-elements c4-model1
                                    {:el :context-view
                                     :id :test/c4-context-view-selection-only
                                     :title "Context View Selection Only"
                                     :spec {:selection {:namespace "test"}}
                                     :ct []}))))
    (is (= 2 (count (view-elements c4-model1
                                   {:el :context-view
                                    :id :test/c4-context-view-selection-only
                                    :title "Context View Selection Only"
                                    :spec {:selection {:el :system}}
                                    :ct []})))))
  (testing "Refs and Selection"
    ;
    )
  (testing "Refs and Related"
    (is (= 5 (count (view-elements c4-model1
                                   {:el :context-view
                                    :id :test/c4-context-view-refs-only
                                    :spec {:include :related}
                                    :title "Context View Refs Only"
                                    :ct [{:ref :test/user1-uses-system1}
                                         {:ref :test/system1-calls-ext-system1}]}))))
    ;
    )
  (testing "Refs and Relations"
    (is (= 5 (count (view-elements c4-model1
                                   {:el :context-view
                                    :id :test/c4-context-view-refs-only
                                    :spec {:include :relations}
                                    :title "Context View Refs Only"
                                    :ct [{:ref :test/user1}
                                         {:ref :test/ext-system1}
                                         {:ref :test/system1}]}))))
    ;
    )
  (testing "Selection and Related"
    ;
    )
  (testing "Selection and Relations"
    ;
    )
  ;
  )

(deftest root-elements-test
  (testing "root-elements"
    (is (= #{{:el :rel :id :test/system1-calls-ext-system1 :from :test/system1 :to :test/ext-system1 :name "calls"}
             {:el :person :id :test/user1 :name "User 1"}
             {:el :rel :id :test/user1-uses-system1, :from :test/user1, :to :test/system1, :name "uses"}
             {:el :system :id :test/ext-system1 :external true :name "External System 1"}
             {:el :system :id :test/system1 :name "Test System"
              :ct #{{:el :container :id :test/container1 :name "Test Container 1"
                     :ct #{{:el :component :id :test/component12 :name "Test Component 12"}
                           {:el :component :id :test/component11 :name "Test Component 11"}}}
                    {:el :container :id :test/container-db1 :subtype :database :name "Test DB Container 1"}
                    {:el :container :id :test/container-queue1 :subtype :queue :name "Test Queue Container 1"}}}}
           (root-elements c4-model1 #{{:el :rel :id :test/system1-calls-ext-system1 :from :test/system1 :to :test/ext-system1 :name "calls"}
                                      {:el :person :id :test/user1 :name "User 1"}
                                      {:el :rel :id :test/user1-uses-system1, :from :test/user1, :to :test/system1, :name "uses"}
                                      {:el :system :id :test/ext-system1 :external true :name "External System 1"}
                                      {:el :system :id :test/system1 :name "Test System"
                                       :ct #{{:el :container :id :test/container1 :name "Test Container 1"
                                              :ct #{{:el :component :id :test/component12 :name "Test Component 12"}
                                                    {:el :component :id :test/component11 :name "Test Component 11"}}}
                                             {:el :container :id :test/container-db1 :subtype :database :name "Test DB Container 1"}
                                             {:el :container :id :test/container-queue1 :subtype :queue :name "Test Queue Container 1"}}}})))
    (is (= #{{:el :system :id :test/system1 :name "Test System"
            :ct #{{:el :container :id :test/container1 :name "Test Container 1"
                   :ct #{{:el :component :id :test/component12 :name "Test Component 12"}
                         {:el :component :id :test/component11 :name "Test Component 11"}}}
                  {:el :container :id :test/container-db1 :subtype :database :name "Test DB Container 1"}
                  {:el :container :id :test/container-queue1 :subtype :queue :name "Test Queue Container 1"}}}}
           (root-elements c4-model1 #{{:el :system :id :test/system1 :name "Test System"
                                       :ct #{{:el :container :id :test/container1 :name "Test Container 1"
                                              :ct #{{:el :component :id :test/component12 :name "Test Component 12"}
                                                    {:el :component :id :test/component11 :name "Test Component 11"}}}
                                             {:el :container :id :test/container-db1 :subtype :database :name "Test DB Container 1"}
                                             {:el :container :id :test/container-queue1 :subtype :queue :name "Test Queue Container 1"}}}
                                      {:el :container :id :test/container1 :name "Test Container 1"
                                       :ct #{{:el :component :id :test/component12 :name "Test Component 12"}
                                             {:el :component :id :test/component11 :name "Test Component 11"}}}
                                      {:el :container :id :test/container-db1 :subtype :database :name "Test DB Container 1"}
                                      {:el :container :id :test/container-queue1 :subtype :queue :name "Test Queue Container 1"}
                                      {:el :component :id :test/component12 :name "Test Component 12"}
                                      {:el :component :id :test/component11 :name "Test Component 11"}})))))


(comment
  (:views styles-model)
  (:themes styles-model)
  (styles-spec styles-model ((:id->element styles-model) :test/styles-context-view))
  (styles-spec styles-model ((:id->element styles-model) :test/theme-context-view))
  (into #{} (model/filter-xf c4-model1 {:el :system}) (concat (model/nodes c4-model1) (model/relations c4-model1))))
