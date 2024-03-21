(ns org.soulspace.overarch.domain.views.component-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.component-view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? {:el :component-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}
      true {:el :container-boundary}
      true {:el :component}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? {:el :component-view} y)))
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
    (are [x y] (= x (boolean (include-content? {:el :component-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :system-boundary}
      true {:el :container-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? {:el :component-view} y)))
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

(deftest as-boundary?-test
  (testing "as-boundary? true"
    (are [x y] (= x (boolean (as-boundary? y)))
      true {:el :system :ct #{{:el :container}}}
      true {:el :container :ct #{{:el :component}}}))
  (testing "as-boundary? false"
    (are [x y] (= x (boolean (as-boundary? y)))
      false {:el :system}
      false {:el :container}
      false {:el :component})))

(deftest render-relation-node?-test
  (testing "render-relation-node? true"
    (are [x y] (= x (boolean (render-relation-node? {:el :component-view} y)))
      true {:el :person}
      true {:el :system :external true}
      true {:el :system :external false}
      true {:el :container :external true}
      true {:el :container :external false}
      true {:el :component}))
  (testing "render-relation-node? false"
    (are [x y] (= x (boolean (render-relation-node? {:el :component-view} y)))
      false {:el :system :external false :ct #{{:el :container}}}
      false {:el :container :external false :ct #{{:el :component}}})))
