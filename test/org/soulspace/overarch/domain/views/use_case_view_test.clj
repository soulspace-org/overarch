(ns org.soulspace.overarch.domain.views.use-case-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest use-case-view-element?-test
  (testing "use-case-view-element?"
    (are [x y] (= x (fns/truthy? (use-case-view-element? y)))
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :actor}
      true {:el :use-case}
      true {:el :uses}
      true {:el :include}
      true {:el :extends}
      true {:el :generalizes}
      false {:el :enterprise-boundary}
      false {:el :system-boundary}
      false {:el :container}
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

