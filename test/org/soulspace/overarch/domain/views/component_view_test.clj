(ns org.soulspace.overarch.domain.views.component-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.component-view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest render-element?-test
  (testing "render-element?"
    (are [x y] (= x (fns/truthy? (render-model-node? {:el :component-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}
      true {:el :container-boundary}
      true {:el :component}
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

(deftest as-boundary?-test
  (testing "as-boundary?"
    (are [x y] (= x (fns/truthy? (as-boundary? y)))
      true {:el :system :ct #{{:el :container}}}
      true {:el :container :ct #{{:el :component}}}
      false {:el :system}
      false {:el :container}
      false {:el :component})))

