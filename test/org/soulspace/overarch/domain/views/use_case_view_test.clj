(ns org.soulspace.overarch.domain.views.use-case-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.use-case-view :refer :all] 
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.application.model-repository :as repo]))

(def test-input
  #{})
(def test-model (repo/build-model test-input))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :use-case-view} y)))
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :container}
      true {:el :actor}
      true {:el :use-case}
      true {:el :uses}
      true {:el :include}
      true {:el :extends}
      true {:el :generalizes}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :use-case-view} y)))
      false {:el :enterprise-boundary}
      false {:el :system-boundary}
      false {:el :node}
      false {:el :container-boundary}
      false {:el :component}
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

