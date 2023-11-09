(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]))

(deftest view?-test
  (testing "view?"
    (are [x y] (= x (fns/truthy? (view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :dynamic-view :id :dynamic-view}
      true {:el :use-case-view :id :use-case-view}
      true {:el :class-view :id :class-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :context-view :id :context-view}
      true {:el :glossary-view :id :glossary-view}
      false {:el :abcd-view :id :abcd-view})))
  
(deftest hierarchical-view?-test
  (testing "hierarchical-view?"
    (are [x y] (= x (fns/truthy? (hierarchical-view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :class-view :id :class-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :glossary-view :id :glossary-view}
      false {:el :dynamic-view :id :dynamic-view}
      false {:el :use-case-view :id :use-case-view}
      false {:el :concept-view :id :concept-view}
      false {:el :abcd-view :id :abcd-view})))

(deftest context-view-element?-test
  (testing "context-view-element?"
    (are [x y] (= x (fns/truthy? (context-view-element? y)))
      true {:el :person}
      true {:el :system}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
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
      false {:el :protocol})))


(deftest container-view-element?-test
  (testing "container-view-element?"
    (are [x y] (= x (fns/truthy? (container-view-element? y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}
      false {:el :container-boundary}
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
      false {:el :protocol})))

(deftest component-view-element?-test
  (testing "component-view-element?"
    (are [x y] (= x (fns/truthy? (component-view-element? y)))
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
      false {:el :protocol})))

(deftest deployment-view-element?-test
  (testing "deployment-view-element?"
    (are [x y] (= x (fns/truthy? (deployment-view-element? y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}
      true {:el :node}
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
      false {:el :protocol})))


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
      false {:el :protocol})))

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
      true {:el :context-boundary}
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
      false {:el :protocol})))
