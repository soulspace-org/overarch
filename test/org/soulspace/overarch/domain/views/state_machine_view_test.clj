(ns org.soulspace.overarch.domain.views.state-machine-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest state-machine-view-element?-test
  (testing "state-machine-view-element?"
    (are [x y] (= x (fns/truthy? (state-machine-view-element? y)))
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :transition}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}
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

