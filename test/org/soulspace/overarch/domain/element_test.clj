(ns org.soulspace.overarch.domain.element-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :refer :all]))

(deftest element?-test
  (testing "element?"
    (are [x y] (= x (fns/truthy? (element? y)))
      true {:el :person}
      false {}
      false {:type :person})))

(deftest identifiable?-test
  (testing "identifiable?"
    (are [x y] (= x (fns/truthy? (identifiable? y)))
      true {:id :abc}
      true {:id :a/abc}
      false {}
      false {:type :person})))

(deftest named?-test
  (testing "named?"
    (are [x y] (= x (fns/truthy? (named? y)))
      true {:name "abc"}
      false {}
      false {:type :person})))

(deftest namespaced?-test
  (testing "namespaced?"
    (are [x y] (= x (fns/truthy? (namespaced? y)))
      true {:id :a/bc}
      false {}
      false {:id :abc})))

(deftest relational?-test
  (testing "relational?"
    (are [x y] (= x (fns/truthy? (relational? y)))
      true {:from :abc :to :bcd}
      true {:from :a/abc :to :a/bcd}
      false {}
      false {:type :person})))

(deftest external?-test
  (testing "external?"
    (are [x y] (= x (fns/truthy? (external? y)))
      true {:external true}
      false {:external false}
      false {}
      false {:type :person})))

(deftest model-element?-test
  (testing ""
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
      true {:el :transition}
      false {:el :ref})))

(deftest model-node?-test
  (testing ""
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
      true {:el :deep-history-state}

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
  (testing "reference?"
    (are [x y] (= x (fns/truthy? (reference? y)))
      true {:ref :abc}
      true {:ref :a/abc}
      false {}
      false {:type :person})))

(deftest boundary?-test
  (testing "boundary?"
    (are [x y] (= x (fns/truthy? (boundary? y)))
      true {:el :context-boundary}
      true {:el :enterprise-boundary}
      true {:el :system-boundary}
      true {:el :container-boundary}
      false {}
      false {:el :person})))

(deftest external?-test
  (testing "external?"
    (are [x y] (= x (fns/truthy? (external? y)))
      false {:el :person}
      false {:el :person :external false}
      true {:el :person :external true})))

(deftest internal?-test
  (testing "internal?"
    (are [x y] (= x (fns/truthy? (internal? y)))
      true {:el :person}
      true {:el :person :external false}
      false {:el :person :external true})))



