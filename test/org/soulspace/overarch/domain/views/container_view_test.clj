(ns org.soulspace.overarch.domain.views.container-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.container-view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? {:el :container-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? {:el :container-view} y)))
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

(deftest include-content?-test
  (testing "include-content? true"
    (are [x y] (= x (boolean (include-content? {:el :container-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :system-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? {:el :container-view} y)))
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

(deftest as-boundary?-test
  (testing "as-boundary? true"
    (are [x y] (= x (boolean (as-boundary? y)))
      true {:el :system :ct #{{:el :container}}}))
  (testing "as-boundary? false"
    (are [x y] (= x (boolean (as-boundary? y)))
      false {:el :container :ct #{{:el :component}}}
      false {:el :system}
      false {:el :container}
      false {:el :component})))

(deftest render-relation-node?-test
  (testing "render-relation-node? true"
    (are [x y] (= x (boolean (render-relation-node? {:el :container-view} y)))
      true {:el :person}
      true {:el :system :external true}
      true {:el :container}
      true {:el :system :external false}))
  (testing "render-relation-node? false"
    (are [x y] (= x (boolean (render-relation-node? {:el :container-view} y)))
      false {:el :system :external false :ct #{{:el :container}}}
      false {:el :component})))

(def container-view1
  {:el :container-view
   :id :test/container-view1
   :title "Container View 1"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}
        {:ref :test/user1-uses-container1}
        {:ref :test/container1-calls-ext-system1}]})

(def container-view2
  {:el :container-view
   :id :test/container-view1
   :title "Container View 2"
   :ct [{:ref :test/user1}
        {:ref :test/ext-system1}
        {:ref :test/container1}
        {:ref :test/container-db1}
        {:ref :test/container-queue1}
        {:ref :test/user1-uses-container1}
        {:ref :test/container1-stores-in-container-db1}
        {:ref :test/container1-sends-to-container-queue1}
        {:ref :test/container1-calls-ext-system1}]})

(def container-view1-related
  {:el :container-view
   :id :test/container-view1-related
   :spec {:include :related}
   :title "Container View 1 related"
   :ct [{:ref :test/user1-uses-container1}
        {:ref :test/container1-calls-ext-system1}
        {:ref :test/container1-stores-in-container-db1}
        {:ref :test/container1-sends-to-container-queue1}]})

(def container-view1-relations
  {:el :container-view
   :id :test/container-view1-relations
   :spec {:include :relations}
   :title "Container View 1 relations"
   :ct [{:ref :test/user1}
        {:ref :test/system1}
        {:ref :test/ext-system1}]})

(def container-view2-relations
  {:el :container-view
   :id :test/container-view2-relations
   :spec {:include :relations}
   :title "Container View 2 relations"
   :ct [{:ref :test/user1}
        {:ref :test/container1}
        {:ref :test/container-db1}
        {:ref :test/container-queue1}
        {:ref :test/ext-system1}]})

(deftest referenced-model-nodes-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-model-nodes for container view"
      (are [x y] (= x y)
        3 (count (referenced-model-nodes c4-1 container-view1))
        5 (count (referenced-model-nodes c4-1 container-view2))
        0 (count (referenced-model-nodes c4-1 container-view1-related))
        3 (count (referenced-model-nodes c4-1 container-view1-relations))
        5 (count (referenced-model-nodes c4-1 container-view2-relations))))))

(deftest referenced-relations-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-relations for container view"
      (are [x y] (= x y)
        2 (count (referenced-relations c4-1 container-view1))
        4 (count (referenced-relations c4-1 container-view2))
        4 (count (referenced-relations c4-1 container-view1-related))
        0 (count (referenced-relations c4-1 container-view1-relations))
        0 (count (referenced-relations c4-1 container-view2-relations))))))

(deftest referenced-elements-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-elements for container view"
      (are [x y] (= x y)
        5 (count (referenced-elements c4-1 container-view1))
        9 (count (referenced-elements c4-1 container-view2))
        4 (count (referenced-elements c4-1 container-view1-related))
        3 (count (referenced-elements c4-1 container-view1-relations))
        5 (count (referenced-elements c4-1 container-view2-relations))))))

(deftest specified-model-nodes-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-model-nodes for container view"
      (are [x y] (= x y)
        3 (count (specified-model-nodes c4-1 container-view1))
        5 (count (specified-model-nodes c4-1 container-view2))
        5 (count (specified-model-nodes c4-1 container-view1-related))
        3 (count (specified-model-nodes c4-1 container-view1-relations))
        5 (count (specified-model-nodes c4-1 container-view2-relations))))))

(deftest specified-relations-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-relations for container view"
      (are [x y] (= x y)
        2 (count (specified-relations c4-1 container-view1))
        4 (count (specified-relations c4-1 container-view2))
        4 (count (specified-relations c4-1 container-view1-related))
        0 (count (specified-relations c4-1 container-view1-relations))
        0 (count (specified-relations c4-1 container-view2-relations))))))

(deftest specified-elements-test
  (let [c4-1 model-test/c4-model1]
    ; TODO check
    (testing "specified-elements for container view"
      (are [x y] (= x y)
        5 (count (specified-elements c4-1 container-view1))
        9 (count (specified-elements c4-1 container-view2))
        9 (count (specified-elements c4-1 container-view1-related))
        5 (count (specified-elements c4-1 container-view1-relations))
        9 (count (specified-elements c4-1 container-view2-relations))))))

(comment
  (def c4-1 model-test/c4-model1)
  (referenced-model-nodes c4-1 container-view1-related)

  (referenced-model-nodes c4-1 container-view1)
  (referenced-model-nodes c4-1 container-view2)
  (referenced-model-nodes c4-1 container-view1-related)
  (referenced-model-nodes c4-1 container-view1-relations)
  (referenced-model-nodes c4-1 container-view2-relations)

  (referenced-relations c4-1 container-view1)
  (referenced-relations c4-1 container-view2)
  (referenced-relations c4-1 container-view1-related)
  (referenced-relations c4-1 container-view1-relations)
  (referenced-relations c4-1 container-view2-relations)

  (referenced-elements c4-1 container-view1)
  (referenced-elements c4-1 container-view2)
  (referenced-elements c4-1 container-view1-related)
  (referenced-elements c4-1 container-view1-relations)
  (referenced-elements c4-1 container-view2-relations)

  (specified-model-nodes c4-1 container-view1)
  (specified-model-nodes c4-1 container-view2)
  (specified-model-nodes c4-1 container-view1-related)
  (specified-model-nodes c4-1 container-view1-relations)
  (specified-model-nodes c4-1 container-view2-relations)

  (specified-relations c4-1 container-view1)
  (specified-relations c4-1 container-view2)
  (specified-relations c4-1 container-view1-related)
  (specified-relations c4-1 container-view1-relations)
  (specified-relations c4-1 container-view2-relations) 

  (specified-elements c4-1 container-view1)
  (specified-elements c4-1 container-view2)
  (specified-elements c4-1 container-view1-related)
  (specified-elements c4-1 container-view1-relations)
  (specified-elements c4-1 container-view2-relations)

  (elements-to-render c4-1 container-view1)
  (elements-to-render c4-1 container-view1 (:ct container-view1))
  (elements-to-render c4-1 container-view1 (:ct (model/resolve-element c4-1 :test/system1)))

  )
