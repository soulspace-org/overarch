(ns org.soulspace.overarch.domain.views.class-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.class-view :refer :all]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? {:el :class-view} y)))
      true {:el :package}
      true {:el :class}
      true {:el :field}
      true {:el :method}
      true {:el :interface}
      true {:el :enum}
      true {:el :inheritance}
      true {:el :implementation}
      true {:el :composition}
      true {:el :aggregation}
      true {:el :association}
      true {:el :dependency}
      true {:el :stereotype}
      true {:el :annotation}
      true {:el :namespace}
      true {:el :function}
      true {:el :protocol}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? {:el :class-view} y)))
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
      false {:el :concept})))

