(ns org.soulspace.overarch.domain.views.state-machine-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.state-machine-view :refer :all] 
            [org.soulspace.overarch.domain.model :as model]))

(def test-input
  #{})
(def test-model (model/build-model test-input))

(deftest as-boundary?-test
  (testing "as-boundary? true"
    ;(are [x y] (= x (boolean (as-boundary? y))))
    )
  (testing "as-boundary? false"
    (are [x y] (= x (boolean (as-boundary? test-model {:el :state-machine-view} y)))
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
      false {:el :container :ct #{{:el :component}}}
      false {:el :system}
      false {:el :container}
      false {:el :component})))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :state-machine-view} y)))
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :transition}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :state-machine-view} y)))
      false {:el :context-boundary}
      false {:el :person}
      false {:el :system}
      false {:el :actor}
      false {:el :use-case}
      false {:el :uses}
      false {:el :include}
      false {:el :extends}
      false {:el :generalizes}
      false {:el :enterprise-boundary}
      false {:el :system-boundary}
      false {:el :container}
      false {:el :node}
      false {:el :container-boundary}
      false {:el :component}
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
    (are [x y] (= x (boolean (include-content? test-model {:el :state-machine-view} y)))
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :transition}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? test-model {:el :state-machine-view} y)))
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
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
