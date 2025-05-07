(ns org.soulspace.overarch.domain.views.context-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.context-view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.adapter.reader.model-reader :as reader]))

(def opts {:input-model-format :overarch-input})

(def test-input
  #{})
(def test-model (reader/build-model opts test-input))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :context-view} y)))
      true {:el :person}
      true {:el :system}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :context-view} y)))
      false {:el :system-boundary}
      false {:el :container-boundary}
      false {:el :container}
      false {:el :component}
      false {:el :node}
      false {:el :actor}
      false {:el :use-case}
      false {:el :uses}
      false {:el :include}
      false {:el :extends}
      false {:el :generalizes}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :transition}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :package}
      false {:el :class}
      false {:el :field}
      false {:el :method}
      false {:el :interface}
      false {:el :enum}
      false {:el :inheritance}
      false {:el :implementation}
      false {:el :composition}
      false {:el :aggregation}
      false {:el :association}
      false {:el :dependency}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :namespace}
      false {:el :function}
      false {:el :protocol}
      false {:el :concept})))

(deftest include-content?-test
  (testing "include-content? true"
    (are [x y] (= x (boolean (include-content? test-model {:el :context-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? test-model {:el :context-view} y)))
      false {:el :system-boundary}
      false {:el :container-boundary}
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :node}
      false {:el :actor}
      false {:el :use-case}
      false {:el :uses}
      false {:el :include}
      false {:el :extends}
      false {:el :generalizes}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :transition}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :package}
      false {:el :class}
      false {:el :field}
      false {:el :method}
      false {:el :interface}
      false {:el :enum}
      false {:el :inheritance}
      false {:el :implementation}
      false {:el :composition}
      false {:el :aggregation}
      false {:el :association}
      false {:el :dependency}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :namespace}
      false {:el :function}
      false {:el :protocol}
      false {:el :concept})))

(def context-view1
  {:el :context-view
   :id :test/context-view1
   :title "Context View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}
        {:ref :test/user1-uses-system1}
        {:ref :test/system1-calls-ext-system1}]})

(def context-view1-related
  {:el :context-view
   :id :test/context-view1-related
   :spec {:include :related}
   :title "Context View 1"
   :ct [{:ref :test/user1-uses-system1}
        {:ref :test/system1-calls-ext-system1}]})

(def context-view1-relations
  {:el :context-view
   :id :test/context-view1-relations
   :spec {:include :relations}
   :title "Context View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}]})

(deftest referenced-elements-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-elements for context view"
      (are [x y] (= x y)
        5 (count (referenced-elements c4-1 context-view1))
        2 (count (referenced-elements c4-1 context-view1-related))
        3 (count (referenced-elements c4-1 context-view1-relations))))))

(comment
  (def c4-1 model-test/c4-model1)
  (referenced-elements c4-1 context-view1)

  (elements-to-render c4-1 context-view1)
  (elements-to-render c4-1 context-view1 (:ct context-view1))

)
