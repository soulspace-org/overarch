(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(def context-view1
  {:el :context-view
   :id :test/context-view1
   :title "Context View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/user1-uses-system1}
        {:ref :test/system1-calls-ext-system1}]})

(def container-view1
  {:el :container-view
   :id :test/container-view1
   :title "Container View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/user1-uses-container1}
        {:ref :test/container1-calls-ext-system1}]})

(def concept-view1
  {:el :concept-view
   :id :test/concept-view1
   :title "Concept View 1"
   :ct [{:ref :test/concept1}
        {:ref :test/concept2}
        {:ref :test/concept3}
        {:ref :test/concept1-has-a-concept2}
        {:ref :test/concept1-is-a-concept3}]})

(def concept-view1-related
  {:el :concept-view
   :id :test/concept-view1 
   :title "Concept View 1"
   :spec {:include :related}
   :ct [{:ref :test/concept1-has-a-concept2}
        {:ref :test/concept1-is-a-concept3}]})

(def concept-view1-relations
  {:el :concept-view
   :id :test/concept-view1
   :title "Concept View 1"
   :spec {:include :relations}
   :ct [{:ref :test/concept1}
        {:ref :test/concept2}
        {:ref :test/concept3}]})

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
      false {:el :protocol}
      false {:el :concept})))


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
      false {:el :protocol}
      false {:el :concept})))

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
      false {:el :protocol}
      false {:el :concept})))

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
      false {:el :protocol}
      false {:el :concept})))


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

(deftest concept-view-element?-test
  (testing "concept-view-element?"
    (are [x y] (= x (fns/truthy? (concept-view-element? y)))
      true {:el :concept}
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

(deftest glossary-view-element?-test
  (testing "glossary-view-element?"
    (are [x y] (= x (fns/truthy? (glossary-view-element? y)))
      true {:el :concept}
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


(deftest as-boundary?-test
  (testing "as-boundary?"
    (are [x y] (= x (fns/truthy? (apply as-boundary? y)))
      true [:container-view {:el :system :ct #{{:el :container}}}]
      true [:component-view {:el :system :ct #{{:el :container}}}]
      true [:component-view {:el :container :ct #{{:el :component}}}]

      false [:container-view {:el :system}]
      false [:container-view {:el :container}]
      false [:container-view {:el :container :ct #{{:el :component}}}]
      false [:component-view {:el :system}]
      false [:component-view {:el :container}]
      false [:component-view {:el :component}]
      )))

(deftest referenced-elements-test
  (let [concept1 (model/build-registry model-test/concept-model1)]
    (testing "referenced-elements"
      (are [x y] (= x y)
        5 (count (referenced-elements concept1 concept-view1))
        2 (count (referenced-elements concept1 concept-view1-related))
        3 (count (referenced-elements concept1 concept-view1-relations))))))

(deftest specified-elements-test
  (let [concept1 (model/build-registry model-test/concept-model1)]
    (testing "specified-elements"
      (are [x y] (= x y)
        5 (count (specified-elements concept1 concept-view1))
        5 (count (specified-elements concept1 concept-view1-related))
        5 (count (specified-elements concept1 concept-view1-relations))))))

(comment
  (as-boundary? :container-view {:el :system :ct #{{:el :container}}})
  (def c4-1 (model/build-registry model-test/c4-model1))
  (referenced-model-nodes c4-1 context-view1)
  (referenced-relations c4-1 context-view1)
  (referenced-elements c4-1 context-view1)
  (specified-model-nodes c4-1 context-view1)
  (specified-relations c4-1 context-view1)
  (specified-elements c4-1 context-view1)

  (elements-to-render c4-1 context-view1)
  (elements-to-render c4-1 context-view1 (:ct context-view1))

  (elements-to-render c4-1 container-view1)
  (elements-to-render c4-1 container-view1 (:ct container-view1))
  (elements-to-render c4-1 container-view1 (:ct (model/resolve-ref c4-1 :test/system1)))

  (def concept1 (model/build-registry model-test/concept-model1))
  (referenced-model-nodes concept1 concept-view1)
  (referenced-model-nodes concept1 concept-view1-related)
  (referenced-model-nodes concept1 concept-view1-relations)
  (referenced-relations concept1 concept-view1)
  (referenced-relations concept1 concept-view1-related)
  (referenced-relations concept1 concept-view1-relations)
  (referenced-elements concept1 concept-view1)
  (referenced-elements concept1 concept-view1-related)
  (referenced-elements concept1 concept-view1-relations)
  (specified-model-nodes concept1 concept-view1)
  (specified-model-nodes concept1 concept-view1-related)
  (specified-model-nodes concept1 concept-view1-relations)
  (specified-relations concept1 concept-view1)
  (specified-relations concept1 concept-view1-related)
  (specified-relations concept1 concept-view1-relations)
  (specified-elements concept1 concept-view1)
  (specified-elements concept1 concept-view1-related)
  (specified-elements concept1 concept-view1-relations)

  )
