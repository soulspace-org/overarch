(ns org.soulspace.overarch.domain.element-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :refer :all]))

;;;
;;; Tests for element predicates
;;;
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

(deftest technical-architecture-node?-test
  (testing "technical-architecture-node? true"
    (are [x y] (= x (fns/truthy? (technical-architecture-node? y)))
      true {:el :system}
      true {:el :container}
      true {:el :component}))

  (testing "technical-architecture-node? false"
    (are [x y] (= x (fns/truthy? (technical-architecture-node? y)))
      false {:el :person}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel}
      false {:el :request}
      false {:el :response}
      false {:el :publish}
      false {:el :subscribe}
      false {:el :send}
      false {:el :dataflow}
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
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest architecture-node?-test
  (testing "architecture-node? true"
    (are [x y] (= x (fns/truthy? (architecture-node? y)))
      true {:el :person}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :system}
      true {:el :container}
      true {:el :component}))

  (testing "architecture-node? false"
    (are [x y] (= x (fns/truthy? (architecture-node? y)))
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel}
      false {:el :request}
      false {:el :response}
      false {:el :publish}
      false {:el :subscribe}
      false {:el :send}
      false {:el :dataflow}
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
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest architecture-relation?-test
  (testing "architecture-relation? true"
    (are [x y] (= x (fns/truthy? (architecture-relation? y)))
      true {:el :rel :from :a :to :b}
      true {:el :request :from :a :to :b}
      true {:el :response :from :a :to :b}
      true {:el :publish :from :a :to :b}
      true {:el :subscribe :from :a :to :b}
      true {:el :send :from :a :to :b}
      true {:el :dataflow :from :a :to :b}))

  (testing "architecture-relation? false"
    (are [x y] (= x (fns/truthy? (architecture-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest deployment-node?-test
  (testing "deployment-node? true"
    (are [x y] (= x (fns/truthy? (deployment-node? y)))
      true {:el :node}
      true {:el :system}
      true {:el :container}
      true {:el :component}))

  (testing "deployment-node? false"
    (are [x y] (= x (fns/truthy? (deployment-node? y)))
      false {:el :person}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest deployment-relation?-test
  (testing "deployment-relation? true"
    (are [x y] (= x (fns/truthy? (deployment-relation? y)))
      true {:el :link :from :a :to :b}))

  (testing "deployment-relation? false"
    (are [x y] (= x (fns/truthy? (deployment-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest usecase-node?-test
  (testing "usecase-node? true"
    (are [x y] (= x (fns/truthy? (usecase-node? y)))
      true {:el :context-boundary}
      true {:el :use-case}
      true {:el :actor}
      true {:el :person}
      true {:el :system}
      true {:el :container}))

  (testing "usecase-node? false"
    (are [x y] (= x (fns/truthy? (usecase-node? y)))
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest usecase-relation?-test
  (testing "usecase-relation? true"
    (are [x y] (= x (fns/truthy? (usecase-relation? y)))
      true {:el :uses :from :a :to :b}
      true {:el :include :from :a :to :b}
      true {:el :extends :from :a :to :b}
      true {:el :generalizes :from :a :to :b}))

  (testing "usecase-relation? false"
    (are [x y] (= x (fns/truthy? (usecase-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :link :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest statemachine-node?-test
  (testing "statemachine-node? true"
    (are [x y] (= x (fns/truthy? (statemachine-node? y)))
      true {:el :state-machine}
      true {:el :start-state}
      true {:el :state}
      true {:el :end-state}
      true {:el :fork}
      true {:el :join}
      true {:el :choice}
      true {:el :history-state}
      true {:el :deep-history-state}))

  (testing "statemachine-node? false"
    (are [x y] (= x (fns/truthy? (statemachine-node? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :context-boundary}
      false {:el :enterprise-boundary}
      false {:el :node}
      false {:el :use-case}
      false {:el :actor}
      false {:el :concept}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest statemachine-relation?-test
  (testing "statemachine-relation? true"
    (are [x y] (= x (fns/truthy? (statemachine-relation? y)))
      true {:el :transition :from :a :to :b}))

  (testing "statemachine-relation? false"
    (are [x y] (= x (fns/truthy? (statemachine-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :link :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest class-model-node?-test
  (testing "class-model-node? true"
    (are [x y] (= x (fns/truthy? (class-model-node? y)))
      true {:el :package}
      true {:el :namespace}
      true {:el :class}
      true {:el :interface}
      true {:el :enum}
      true {:el :field}
      true {:el :method}
      true {:el :stereotype}
      true {:el :annotation}
      true {:el :protocol}))

  (testing "class-model-node? false"
    (are [x y] (= x (fns/truthy? (class-model-node? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :context-boundary}
      false {:el :enterprise-boundary}
      false {:el :node}
      false {:el :use-case}
      false {:el :actor}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :concept}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest class-model-relation?-test
  (testing "class-model-relation? true"
    (are [x y] (= x (fns/truthy? (class-model-relation? y)))
      true {:el :inheritance :from :a :to :b}
      true {:el :implementation :from :a :to :b}
      true {:el :composition :from :a :to :b}
      true {:el :aggregation :from :a :to :b}
      true {:el :association :from :a :to :b}
      true {:el :dependency :from :a :to :b}))

  (testing "class-model-relation? false"
    (are [x y] (= x (fns/truthy? (class-model-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :link :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest concept-model-node?-test
  (testing "concept-model-node? true"
    (are [x y] (= x (fns/truthy? (concept-model-node? y)))
      true {:el :concept}
      true {:el :person}
      true {:el :system}
      true {:el :container}
      true {:el :context-boundary}
      true {:el :enterprise-boundary}))

  (testing "concept-model-node? false"
    (are [x y] (= x (fns/truthy? (concept-model-node? y)))
      false {:el :component}
      false {:el :node}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest concept-model-relation?-test
  (testing "concept-model-relation? true"
    (are [x y] (= x (fns/truthy? (concept-model-relation? y)))
      true {:el :has :from :a :to :b}
      true {:el :is-a :from :a :to :b}
      true {:el :rel :from :a :to :b}))

  (testing "concept-model-relation? false"
    (are [x y] (= x (fns/truthy? (concept-model-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :link :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
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
      false {:el :rel :from :a :to :b}
      false {:el :request :from :a :to :b}
      false {:el :response :from :a :to :b}
      false {:el :publish :from :a :to :b}
      false {:el :subscribe :from :a :to :b}
      false {:el :send :from :a :to :b}
      false {:el :dataflow :from :a :to :b}
      false {:el :link :from :a :to :b}
      false {:el :uses :from :a :to :b}
      false {:el :include :from :a :to :b}
      false {:el :extends :from :a :to :b}
      false {:el :generalizes :from :a :to :b}
      false {:el :inheritance :from :a :to :b}
      false {:el :implementation :from :a :to :b}
      false {:el :composition :from :a :to :b}
      false {:el :aggregation :from :a :to :b}
      false {:el :association :from :a :to :b}
      false {:el :dependency :from :a :to :b}
      false {:el :transition :from :a :to :b}
      false {:el :has :from :a :to :b}
      false {:el :is-a :from :a :to :b}
      false {:el :ref})))

(deftest model-relation?-test
  (testing "model-relation? true"
    (are [x y] (= x (fns/truthy? (model-relation? y)))
      true {:el :rel :from :a :to :b}
      true {:el :request :from :a :to :b}
      true {:el :response :from :a :to :b}
      true {:el :publish :from :a :to :b}
      true {:el :subscribe :from :a :to :b}
      true {:el :send :from :a :to :b}
      true {:el :dataflow :from :a :to :b}
      true {:el :link :from :a :to :b}
      true {:el :uses :from :a :to :b}
      true {:el :include :from :a :to :b}
      true {:el :extends :from :a :to :b}
      true {:el :generalizes :from :a :to :b}
      true {:el :inheritance :from :a :to :b}
      true {:el :implementation :from :a :to :b}
      true {:el :composition :from :a :to :b}
      true {:el :aggregation :from :a :to :b}
      true {:el :association :from :a :to :b}
      true {:el :dependency :from :a :to :b}
      true {:el :transition :from :a :to :b}
      true {:el :has :from :a :to :b}
      true {:el :is-a :from :a :to :b}))

  (testing "model-relation? false"
    (are [x y] (= x (fns/truthy? (model-relation? y)))
      false {:el :person}
      false {:el :system}
      false {:el :container}
      false {:el :component}
      false {:el :enterprise-boundary}
      false {:el :context-boundary}
      false {:el :node}
      false {:el :concept}
      false {:el :use-case}
      false {:el :actor}
      false {:el :package}
      false {:el :namespace}
      false {:el :class}
      false {:el :interface}
      false {:el :enum}
      false {:el :field}
      false {:el :method}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :protocol}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :ref})))

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
      true {:el :rel :from :a :to :b}
      true {:el :request :from :a :to :b}
      true {:el :response :from :a :to :b}
      true {:el :publish :from :a :to :b}
      true {:el :subscribe :from :a :to :b}
      true {:el :send :from :a :to :b}
      true {:el :dataflow :from :a :to :b}
      true {:el :link :from :a :to :b}
      true {:el :uses :from :a :to :b}
      true {:el :include :from :a :to :b}
      true {:el :extends :from :a :to :b}
      true {:el :generalizes :from :a :to :b}
      true {:el :inheritance :from :a :to :b}
      true {:el :implementation :from :a :to :b}
      true {:el :composition :from :a :to :b}
      true {:el :aggregation :from :a :to :b}
      true {:el :association :from :a :to :b}
      true {:el :dependency :from :a :to :b}
      true {:el :transition :from :a :to :b}
      true {:el :has :from :a :to :b}
      true {:el :is-a :from :a :to :b}))

  (testing "model-element? false"
    (are [x y] (= x (fns/truthy? (model-element? y)))
      false {:el :fluffy}
      false {:el :concept-view}
      false {:el :context-view}
      false {:el :container-view}
      false {:el :component-view}
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

(deftest node-of?-test
  (testing "node-of? true"
    (are [x y] (= x (fns/truthy? (apply node-of? y)))
      true [:person {:el :person}]
      true [:system {:el :system}]
      true [:container {:el :container}]
      true [:context-boundary {:el :context-boundary}]
      true [:enterprise-boundary {:el :enterprise-boundary}]))
  (testing "node-of? false"
    (are [x y] (= x (fns/truthy? (apply node-of? y)))
      false [:bla {:el :bla}]
      false [:rel {:el :rel :from :a :to :b}]
      false [:system-boundary {:el :system-boundary}]
      false [:container-boundary {:el :container-boundary}]
      false [:person {:el :system}])))

(deftest relation-of?-test
  (testing "relation-of? true"
    (are [x y] (= x (fns/truthy? (apply relation-of? y)))
      true [:rel {:el :rel :from :a :to :b}]
      true [:request {:el :request :from :a :to :b}]
      true [:response {:el :response :from :a :to :b}]
      true [:transition {:el :transition :from :a :to :b}]))
  (testing "relation-of? false"
    (are [x y] (= x (fns/truthy? (apply relation-of? y)))
      false [:bla {:el :bla}]
      false [:person {:el :person}]
      false [:system {:el :system}]
      false [:container {:el :container}]
      false [:context-boundary {:el :context-boundary}]
      false [:enterprise-boundary {:el :enterprise-boundary}]
      false [:system-boundary {:el :system-boundary}]
      false [:container-boundary {:el :container-boundary}]
      false [:person {:el :system}])))

(deftest view?-test
  (testing "view? true"
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
      true {:el :glossary-view :id :glossary-view}))
  (testing "view? false"
    (are [x y] (= x (fns/truthy? (view? y)))
      false {:el :abcd-view :id :abcd-view})))

(deftest hierarchical-view?-test
  (testing "hierarchical-view? true"
    (are [x y] (= x (fns/truthy? (hierarchical-view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :class-view :id :class-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :glossary-view :id :glossary-view}))
  (testing "hierarchical-view? false"
    (are [x y] (= x (fns/truthy? (hierarchical-view? y)))
      false {:el :dynamic-view :id :dynamic-view}
      false {:el :use-case-view :id :use-case-view}
      false {:el :concept-view :id :concept-view}
      false {:el :abcd-view :id :abcd-view})))

;;;
;;; Tests for element functions
;;;
(deftest generate-node-id-test
  (testing "generate-node-id"
    (are [x y] (= x (generate-node-id y {:el :class :id :test/class1 :name "TestClass1"}))
      :test/class1-name-field {:el :field :name "name"}
      :test/class1-getname-method {:el :method :name "getName"}))) 

(deftest element-namespace-test
  (testing "element-namespace"
    (are [x y] (= x (element-namespace y))
      "org.soulspace" {:id :org.soulspace/foo}
      nil {:id :foo}
      nil {:el :foo/bar})))

(def elements
  #{{:el :person
     :id :org.soulspace.external/person
     :external true
     :name "External Person"}
    {:el :person
     :id :org.soulspace.internal/person
     :name "Internal Person"}
    {:el :system
     :id :org.soulspace.external/system1
     :external true
     :name "External System 1"}})

(deftest elements-by-namespace-test
  (testing "elements-by-namespace"
    (is (= {"org.soulspace.external"
            [{:el :system
              :id :org.soulspace.external/system1
              :external true
              :name "External System 1"}
             {:el :person
              :id :org.soulspace.external/person
              :external true
              :name "External Person"}]
            "org.soulspace.internal"
            [{:el :person
              :id :org.soulspace.internal/person
              :name "Internal Person"}]}
           (elements-by-namespace elements)))))

(def elements-to-filter
  #{{:el :person
     :id :org.soulspace.external/person
     :external true
     :name "External Person"}
    {:el :person
     :id :org.soulspace.internal/person
     :name "Internal Person"}
    {:el :system
     :id :org.soulspace.external/system1
     :external true
     :name "External System 1"}
    {:el :system
     :id :org.soulspace.external/system2
     :external true
     :name "External System 2"}
    {:el :system
     :id :org.soulspace.internal/system
     :name "Internal System"}
    {:el :container
     :id :org.soulspace.internal.system/container1
     :name "Container1"
     :tech "Clojure"
     :tags #{"autoscaled"}}
    {:el :container
     :id :org.soulspace.internal.system/container1-ui
     :name "Container1 UI"
     :tech "ClojureScript"}
    {:el :container
     :id :org.soulspace.internal.system/container1-db
     :subtype :database
     :name "Container1 DB"
     :tech "Datomic"}
    {:el :container
     :id :org.soulspace.internal.system/container2
     :name "Container2"
     :tech "Java"
     :tags #{"critical" "autoscaled"}}
    {:el :container
     :id :org.soulspace.internal.system/container2-topic
     :subtype :queue
     :name "Container2 Events"
     :tech "Kafka"}

    {:el :rel
     :id :org.soulspace.external/person-uses-system1
     :from :org.soulspace.external/person
     :to :org.soulspace.external/system1
     :name "uses"}
    {:el :rel
     :id :org.soulspace.internal/person-uses-system
     :from :org.soulspace.internal/person
     :to :org.soulspace.internal/system
     :name "uses"}
    {:el :rel
     :id :org.soulspace.internal/person
     :from :org.soulspace.internal/person
     :to :org.soulspace.internal.system/container1-ui
     :name "uses"}
    {:el :publish
     :id :org.soulspace.internal.system/container2-publishes-to-container2-topic
     :from :org.soulspace.internal.system/container2
     :to :org.soulspace.internal.system/container2-topic
     :name "publishes to"}
    {:el :subscribe
     :id :org.soulspace.internal.system/container1-consumes-container2-topic
     :from :org.soulspace.internal.system/container1
     :to :org.soulspace.internal.system/container2-topic
     :name "consumes"}
    })

(deftest filter-xf-test
  (testing "filter-xf"
    (are [x y] (= x (into #{} (filter-xf y) elements-to-filter))
      #{{:el :person
         :id :org.soulspace.external/person
         :external true
         :name "External Person"}}
      {:type :person :external true}

      #{{:el :person
         :id :org.soulspace.internal/person
         :name "Internal Person"}}
      {:type :person :external false}

      #{{:el :rel
         :id :org.soulspace.external/person-uses-system1
         :from :org.soulspace.external/person
         :to :org.soulspace.external/system1
         :name "uses"}}
      {:type :rel :namespace "org.soulspace.external"}

      #{{:el :container
         :id :org.soulspace.internal.system/container2
         :name "Container2"
         :tech "Java"
         :tags #{"critical" "autoscaled"}}}
      {:tag "critical"}

      #{{:el :container
         :id :org.soulspace.internal.system/container2
         :name "Container2"
         :tech "Java"
         :tags #{"critical" "autoscaled"}}
        {:el :container
         :id :org.soulspace.internal.system/container1
         :name "Container1"
         :tech "Clojure"
         :tags #{"autoscaled"}}}
      {:tag "autoscaled"}

      #{{:el :container
         :id :org.soulspace.internal.system/container1-db
         :subtype :database
         :name "Container1 DB"
         :tech "Datomic"}}
      {:tech "Datomic"}

      ;
      )))


(comment
  (into []
      (filter-xf {:namespace "org.soulspace.external"})
      elements-to-filter)
  (into []
        (filter-xf {:namespaces #{"org.soulspace.external" "org.soulspace.internal"}})
        elements-to-filter)
  (into []
      (filter-xf {:namespace-prefix "org.soulspace"})
      elements-to-filter)
  (into []
      (filter-xf {:type :person})
      elements-to-filter)
  (into []
      (filter-xf {:type :container})
      elements-to-filter)
  (into []
      (filter-xf {:types #{:system :container}})
      elements-to-filter)
  (into []
      (filter-xf {:type :container :subtype :database})
      elements-to-filter)
  (into []
      (filter-xf {:type :container :subtypes #{:database :queue}})
      elements-to-filter)
  (into []
      (filter-xf {:external true})
      elements-to-filter)
  (into []
      (filter-xf {:tech "Clojure"})
      elements-to-filter)
  (into []
      (filter-xf {:tag "autoscaled"})
      elements-to-filter)
  (into []
      (filter-xf {:tag "critical"})
      elements-to-filter)
  (into []
      (filter-xf {:tags #{"critical" "autoscaled"}})
      elements-to-filter)
  (into []
      (filter-xf {:type :rel})
      elements-to-filter)

  )