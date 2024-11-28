(ns org.soulspace.overarch.domain.views.component-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.component-view :refer :all]
            [org.soulspace.overarch.domain.model :as model]))

(def test-input
  #{{:el :enterprise-boundary
     :id :test/enterprise-boundary1
     :ct #{{:el :system
            :id :test/system1
            :ct #{{:el :context-boundary
                   :id :test/context-boundary1
                   :ct #{{:el :container
                          :id :test/container1
                          :ct #{{:el :component
                                 :id :test/component1}}}}}}}}}
   {:el :system
    :id :test/system2}})

(def test-model (model/build-model test-input))

(deftest as-boundary?-test
  (testing "as-boundary? true"
    (are [x y] (= x (boolean (as-boundary? test-model {:el :component-view} y)))
      true {:el :enterprise-boundary
            :id :test/enterprise-boundary1}
      true {:el :system
            :id :test/system1}
      true {:el :context-boundary
            :id :test/context-boundary1}
      true {:el :container
            :id :test/container1}))
  (testing "as-boundary? false"
    (are [x y] (= x (boolean (as-boundary? test-model {:el :component-view} y)))
      false {:el :system
             :id :test/system2}
      ;false {:el :container}
      ;false {:el :component}
      ;false {:el :system :external true :ct #{{:el :container}}}
      ;false {:el :container :external true :ct #{{:el :component}}}
      )))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :component-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :system-boundary}
      true {:el :container}
      true {:el :container-boundary}
      true {:el :component}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? test-model {:el :component-view} y)))
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
    (are [x y] (= x (boolean (include-content? test-model {:el :component-view} y)))
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :system-boundary}
      true {:el :container-boundary}))
  (testing "include-content? false"
    (are [x y] (= x (boolean (include-content? test-model {:el :component-view} y)))
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
