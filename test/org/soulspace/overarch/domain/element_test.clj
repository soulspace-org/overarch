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
      false {:el :ref})))

(deftest architecture-relation?-test
  (testing "architecture-relation? true"
    (are [x y] (= x (fns/truthy? (architecture-relation? y)))
      true {:el :rel}
      true {:el :request}
      true {:el :response}
      true {:el :publish}
      true {:el :subscribe}
      true {:el :send}
      true {:el :dataflow}))

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
      false {:el :ref})))

(deftest deployment-relation?-test
  (testing "deployment-relation? true"
    (are [x y] (= x (fns/truthy? (deployment-relation? y)))
      true {:el :link}))

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
      false {:el :ref})))

(deftest usecase-node?-test
  (testing "usecase-node? true"
    (are [x y] (= x (fns/truthy? (usecase-node? y)))
      true {:el :context-boundary}
      true {:el :use-case}
      true {:el :actor}
      true {:el :person}
      true {:el :system}))

  (testing "usecase-node? false"
    (are [x y] (= x (fns/truthy? (usecase-node? y)))
      false {:el :container}
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
      false {:el :ref})))

(deftest usecase-relation?-test
  (testing "usecase-relation? true"
    (are [x y] (= x (fns/truthy? (usecase-relation? y)))
      true {:el :uses}
      true {:el :include}
      true {:el :extends}
      true {:el :generalizes}))

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
      false {:el :rel}
      false {:el :request}
      false {:el :response}
      false {:el :publish}
      false {:el :subscribe}
      false {:el :send}
      false {:el :dataflow}
      false {:el :link}
      false {:el :transition}
      false {:el :inheritance}
      false {:el :implementation}
      false {:el :composition}
      false {:el :aggregation}
      false {:el :association}
      false {:el :dependency}
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
      false {:el :ref})))

(deftest statemachine-relation?-test
  (testing "statemachine-relation? true"
    (are [x y] (= x (fns/truthy? (statemachine-relation? y)))
      true {:el :transition}))

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
      false {:el :rel}
      false {:el :request}
      false {:el :response}
      false {:el :publish}
      false {:el :subscribe}
      false {:el :send}
      false {:el :dataflow}
      false {:el :link}
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
      false {:el :ref})))

;; TODO add class model and concept model tests

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
      false {:el :request}
      false {:el :response}
      false {:el :publish}
      false {:el :subscribe}
      false {:el :send}
      false {:el :dataflow}
      false {:el :link}
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

(deftest model-relation?-test
  (testing "model-relation? true"
    (are [x y] (= x (fns/truthy? (model-relation? y)))
      true {:el :rel}
      true {:el :request}
      true {:el :response}
      true {:el :publish}
      true {:el :subscribe}
      true {:el :send}
      true {:el :dataflow}
      true {:el :link}
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
      true {:el :rel}
      true {:el :request}
      true {:el :response}
      true {:el :publish}
      true {:el :subscribe}
      true {:el :send}
      true {:el :dataflow}
      true {:el :link}
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
       true [:context-boundary{:el :context-boundary}]
       true [:enterprise-boundary {:el :enterprise-boundary}]
      ))
  (testing "node-of? false"
    (are [x y] (= x (fns/truthy? (apply node-of? y)))
      false [:bla {:el :bla}]
      false [:rel {:el :rel}]
      false [:system-boundary {:el :system-boundary}]
      false [:container-boundary {:el :container-boundary}]
      false [:person {:el :system}])))

(deftest relation-of?-test
  (testing "relation-of? true"
    (are [x y] (= x (fns/truthy? (apply relation-of? y)))
      true [:rel {:el :rel}]
      true [:request {:el :request}]
      true [:response {:el :response}]
      true [:transition {:el :transition}]))
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
