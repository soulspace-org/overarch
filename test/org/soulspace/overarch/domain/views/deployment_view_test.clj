(ns org.soulspace.overarch.domain.views.deployment-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.deployment-view :refer :all] 
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.adapter.reader.model-reader :as reader]))

(def opts {:input-model-format :overarch-input})

(def test-input
  #{})
(def test-model (reader/build-model opts test-input))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :deployment-view} y)))
      true {:el :artifact}
      true {:el :container}
      true {:el :node}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :deployment-view} y)))
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :person}
      false {:el :system}
      false {:el :system-boundary}
      false {:el :container-boundary}
      false {:el :component}
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
    (are [x y] (= x (boolean (include-content? test-model {:el :deployment-view} y)))
      true {:el :artifact}
      true {:el :container}
      true {:el :node}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? test-model {:el :deployment-view} y)))
      false {:el :container-boundary}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :person}
      false {:el :system}
      false {:el :system-boundary}
      false {:el :component}
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


