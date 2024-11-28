(ns org.soulspace.overarch.domain.views.container-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.container-view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]))

(def test-input
  #{{:el :system
     :id :test/system1
     :ct #{{:el :container
            :id :test/container1
            :ct #{{:el :component
                   :id :test/component1}}}}}
    {:el :system
     :id :test/system2}})

(def test-model (model/build-model test-input))

(deftest as-boundary?-test
  (testing "as-boundary? true"
    (are [x y] (= x (boolean (as-boundary? test-model {:el :container-view} y)))
      true {:el :system
            :id :test/system1
            :ct #{{:el :container}}}))
  (testing "as-boundary? false"
    (are [x y] (= x (boolean (as-boundary? test-model {:el :container-view} y)))
      false {:el :container
             :id :test/container1
             :ct #{{:el :component}}}
      false {:el :system
             :id :test/system2}
      ;false {:el :container}
      ;false {:el :component}
      )))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :container-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :container-view} y)))
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
    (are [x y] (= x (boolean (include-content? test-model {:el :container-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :system-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? test-model {:el :container-view} y)))
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

(deftest referenced-elements-test
  (let [c4-1 model-test/c4-model1]
    (testing "referenced-elements for container view"
      (are [x y] (= x y)
        5 (count (referenced-elements c4-1 container-view1))
        9 (count (referenced-elements c4-1 container-view2))
        4 (count (referenced-elements c4-1 container-view1-related))
        3 (count (referenced-elements c4-1 container-view1-relations))
        5 (count (referenced-elements c4-1 container-view2-relations))))))

(comment
  (def c4-1 model-test/c4-model1)
  (referenced-elements c4-1 container-view1)
  (referenced-elements c4-1 container-view2)
  (referenced-elements c4-1 container-view1-related)
  (referenced-elements c4-1 container-view1-relations)
  (referenced-elements c4-1 container-view2-relations)

  (elements-to-render c4-1 container-view1)
  (elements-to-render c4-1 container-view1 (:ct container-view1))
  (elements-to-render c4-1 container-view1 (:ct (model/resolve-element c4-1 :test/system1)))

  )
