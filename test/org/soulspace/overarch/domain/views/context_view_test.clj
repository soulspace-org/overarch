(ns org.soulspace.overarch.domain.views.context-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.context-view :refer :all] 
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (fns/truthy? (render-model-element? {:el :context-view} y)))
      true {:el :person}
      true {:el :system}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}))
  (testing "render-model-element? false"
    (are [x y] (= x (fns/truthy? (render-model-element? {:el :context-view} y)))
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

(deftest include-content?-test
  (testing "include-content? true"
    (are [x y] (= x (fns/truthy? (include-content? {:el :context-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (fns/truthy? (include-content? {:el :context-view} y)))
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

(def context-view1
  {:el :context-view
   :id :test/context-view1
   :title "Context View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}
        {:ref :test/user1-uses-system1}
        {:ref :test/system1-calls-ext-system1}]})

(def context-view1-related
  {:el :context-view
   :id :test/context-view1-related
   :spec {:include :related}
   :title "Context View 1"
   :ct [{:ref :test/user1-uses-system1}
        {:ref :test/system1-calls-ext-system1}]})

(def context-view1-relations
  {:el :context-view
   :id :test/context-view1-relations
   :spec {:include :relations}
   :title "Context View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}]})

(deftest referenced-model-nodes-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-model-nodes for context view"
      (are [x y] (= x y)
        3 (count (referenced-model-nodes c4-1 context-view1))
        0 (count (referenced-model-nodes c4-1 context-view1-related))
        3 (count (referenced-model-nodes c4-1 context-view1-relations))))))

(deftest referenced-relations-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-relations for context view"
      (are [x y] (= x y)
        2 (count (referenced-relations c4-1 context-view1))
        2 (count (referenced-relations c4-1 context-view1-related))
        0 (count (referenced-relations c4-1 context-view1-relations))))))

(deftest referenced-elements-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-elements for context view"
      (are [x y] (= x y)
        5 (count (referenced-elements c4-1 context-view1))
        2 (count (referenced-elements c4-1 context-view1-related))
        3 (count (referenced-elements c4-1 context-view1-relations))))))

(deftest specified-model-nodes-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-model-nodes for context view"
      (are [x y] (= x y)
        3 (count (specified-model-nodes c4-1 context-view1))
        3 (count (specified-model-nodes c4-1 context-view1-related))
        3 (count (specified-model-nodes c4-1 context-view1-relations))))))

(deftest specified-relations-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-relations for context view"
      (are [x y] (= x y)
        2 (count (specified-relations c4-1 context-view1))
        2 (count (specified-relations c4-1 context-view1-related))
        2 (count (specified-relations c4-1 context-view1-relations))))))

(deftest specified-elements-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-elements for context view"
      (are [x y] (= x y)
        5 (count (specified-elements c4-1 context-view1))
        5 (count (specified-elements c4-1 context-view1-related))
        5 (count (specified-elements c4-1 context-view1-relations))))))

(comment
  (def c4-1 model-test/c4-model1)
  (referenced-model-nodes c4-1 context-view1)
  (referenced-model-nodes c4-1 context-view1-related)
  (referenced-model-nodes c4-1 context-view1-relations)

  (referenced-relations c4-1 context-view1)
  (referenced-elements c4-1 context-view1)
  (specified-model-nodes c4-1 context-view1)
  (specified-relations c4-1 context-view1)
  (specified-elements c4-1 context-view1)

  (elements-to-render c4-1 context-view1)
  (elements-to-render c4-1 context-view1 (:ct context-view1))

)
