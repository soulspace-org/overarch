(ns org.soulspace.overarch.domain.element-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :refer :all]))

(deftest element?-test
  (testing "element? true"
    (are [x y] (= x (fns/truthy? (element? y)))
      true {:el :person}))
  
  (testing "element? false"
    (are [x y] (= x (fns/truthy? (element? y)))
      false {}
      false {:type :person})))

(deftest identifiable?-test
  (testing "identifiable? true"
    (are [x y] (= x (fns/truthy? (identifiable? y)))
      true {:id :abc}
      true {:id :a/abc}))

  (testing "identifiable? false"
    (are [x y] (= x (fns/truthy? (identifiable? y)))
      false {}
      false {:type :person})))

(deftest named?-test
  (testing "named? true"
    (are [x y] (= x (fns/truthy? (named? y)))
      true {:name "abc"}))

  (testing "named? false"
    (are [x y] (= x (fns/truthy? (named? y)))
      false {}
      false {:type :person})))

(deftest namespaced?-test
  (testing "namespaced? true"
    (are [x y] (= x (fns/truthy? (namespaced? y)))
      true {:id :a/bc}))

  (testing "namespaced? false"
    (are [x y] (= x (fns/truthy? (namespaced? y)))
      false {}
      false {:id :abc})))

(deftest relational?-test
  (testing "relational? true"
    (are [x y] (= x (fns/truthy? (relational? y)))
      true {:from :abc :to :bcd}
      true {:from :a/abc :to :a/bcd}))

  (testing "relational? false"
    (are [x y] (= x (fns/truthy? (relational? y)))
      false {}
      false {:type :person})))

(deftest model-element?-test
  (testing "model-element? true"
    (are [x y] (= x (fns/truthy? (model-element? y)))
      true {:el :person}
      true {:el :system}
      true {:el :container}
      true {:el :component}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :node}
      true {:el :concept}
      true {:el :use-case}
      true {:el :actor}
      true {:el :package}
      true {:el :namespace}
      true {:el :class}
      true {:el :interface}
      true {:el :enum}
      true {:el :field}
      true {:el :method}
      true {:el :stereotype}
      true {:el :annotation}
      true {:el :protocol}
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}
      true {:el :rel}
      true {:el :uses}
      true {:el :include}
      true {:el :extends}
      true {:el :generalizes}
      true {:el :inheritance}
      true {:el :implementation}
      true {:el :composition}
      true {:el :aggregation}
      true {:el :association}
      true {:el :dependency}
      true {:el :transition}))

  (testing "model-element? false"
    (are [x y] (= x (fns/truthy? (model-element? y)))
      false {:el :ref})))

(deftest model-node?-test
  (testing "model-node? true"
    (are [x y] (= x (fns/truthy? (model-node? y)))
      true {:el :person}
      true {:el :system}
      true {:el :container}
      true {:el :component}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :node}
      true {:el :concept}
      true {:el :use-case}
      true {:el :actor}
      true {:el :package}
      true {:el :namespace}
      true {:el :class}
      true {:el :interface}
      true {:el :enum}
      true {:el :field}
      true {:el :method}
      true {:el :stereotype}
      true {:el :annotation}
      true {:el :protocol}
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}))

  (testing "model-node? false"
    (are [x y] (= x (fns/truthy? (model-node? y)))
      false {:el :rel}
      false {:el :uses}
      false {:el :include}
      false {:el :extends}
      false {:el :generalizes}
      false {:el :inheritance}
      false {:el :implementation}
      false {:el :composition}
      false {:el :aggregation}
      false {:el :association}
      false {:el :dependency}
      false {:el :transition}
      false {:el :ref})))

(deftest reference?-test
  (testing "reference? true"
    (are [x y] (= x (fns/truthy? (reference? y)))
      true {:ref :abc}
      true {:ref :a/abc}))

  (testing "reference? false"
    (are [x y] (= x (fns/truthy? (reference? y)))
      false {}
      false {:type :person})))

(deftest boundary?-test
  (testing "boundary? true"
    (are [x y] (= x (fns/truthy? (boundary? y)))
      true {:el :context-boundary}
      true {:el :enterprise-boundary}
      true {:el :system-boundary}
      true {:el :container-boundary}))

  (testing "boundary? false"
    (are [x y] (= x (fns/truthy? (boundary? y)))
      false {}
      false {:el :person})))

(deftest external?-test
  (testing "external? true"
    (are [x y] (= x (fns/truthy? (external? y)))
      true {:external true}
      true {:el :person :external true}))

  (testing "external? false"
    (are [x y] (= x (fns/truthy? (external? y)))
      false {:external false}
      false {:el :person :external false}
      false {}
      false {:type :person})))

(deftest internal?-test
  (testing "internal? true"
    (are [x y] (= x (fns/truthy? (internal? y)))
      true {:el :person}
      true {:el :person :external false}))
  
  (testing "internal? false"
    (are [x y] (= x (fns/truthy? (internal? y)))
      true {:el :person}
      true {:el :person :external false}
      false {:el :person :external true})))
