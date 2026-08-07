(ns org.soulspace.overarch.domain.element-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.element :refer :all]))

;;;
;;; Tests for element predicates
;;;
(deftest element?-test
  (testing "element? true"
    (are [x y] (= x (boolean (element? y)))
      true {:el :person}))

  (testing "element? false"
    (are [x y] (= x (boolean (element? y)))
      false {}
      false {:type :person})))

(deftest identifiable?-test
  (testing "identifiable? true"
    (are [x y] (= x (boolean (identifiable? y)))
      true {:id :abc}
      true {:id :a/abc}))

  (testing "identifiable? false"
    (are [x y] (= x (boolean (identifiable? y)))
      false {}
      false {:type :person})))

(deftest named?-test
  (testing "named? true"
    (are [x y] (= x (boolean (named? y)))
      true {:name "abc"}))

  (testing "named? false"
    (are [x y] (= x (boolean (named? y)))
      false {}
      false {:type :person})))

(deftest namespaced?-test
  (testing "namespaced? true"
    (are [x y] (= x (boolean (namespaced? y)))
      true {:id :a/bc}))

  (testing "namespaced? false"
    (are [x y] (= x (boolean (namespaced? y)))
      false {}
      false {:id :abc})))

(deftest relational?-test
  (testing "relational? true"
    (are [x y] (= x (boolean (relational? y)))
      true {:from :abc :to :bcd}
      true {:from :a/abc :to :a/bcd}))

  (testing "relational? false"
    (are [x y] (= x (boolean (relational? y)))
      false {}
      false {:type :person})))

(deftest external?-test
  (testing "external? true"
    (are [x y] (= x (boolean (external? y)))
      true {:external true}
      true {:el :person :external true}))

  (testing "external? false"
    (are [x y] (= x (boolean (external? y)))
      false {:external false}
      false {:el :person :external false}
      false {}
      false {:type :person})))

(deftest internal?-test
  (testing "internal? true"
    (are [x y] (= x (boolean (internal? y)))
      true {:el :person}
      true {:el :person :external false}))

  (testing "internal? false"
    (are [x y] (= x (boolean (internal? y)))
      true {:el :person}
      true {:el :person :external false}
      false {:el :person :external true})))

(deftest reference?-test
  (testing "reference? true"
    (are [x y] (= x (boolean (reference? y)))
      true {:ref :abc}
      true {:ref :a/abc}))

  (testing "reference? false"
    (are [x y] (= x (boolean (reference? y)))
      false {}
      false {:type :person})))

(deftest boundary?-test
  (testing "boundary? true"
    (are [x y] (= x (boolean (boundary? y)))
      true {:el :context-boundary}
      true {:el :enterprise-boundary}
      true {:el :system-boundary}
      true {:el :container-boundary}))

  (testing "boundary? false"
    (are [x y] (= x (boolean (boundary? y)))
      false {}
      false {:el :person})))

(deftest node-of?-test
  (testing "node-of? true"
    (are [x y] (= x (boolean (apply node-of? y)))
      true [:person {:el :person}]
      true [:system {:el :system}]
      true [:container {:el :container}]
      true [:context-boundary {:el :context-boundary}]
      true [:enterprise-boundary {:el :enterprise-boundary}]))
  (testing "node-of? false"
    (are [x y] (= x (boolean (apply node-of? y)))
      false [:bla {:el :bla}]
      false [:rel {:el :rel :from :a :to :b}]
      false [:system-boundary {:el :system-boundary}]
      false [:container-boundary {:el :container-boundary}]
      false [:person {:el :system}])))

(deftest relation-of?-test
  (testing "relation-of? true"
    (are [x y] (= x (boolean (apply relation-of? y)))
      true [:rel {:el :rel :from :a :to :b}]
      true [:request {:el :request :from :a :to :b}]
      true [:response {:el :response :from :a :to :b}]
      true [:transition {:el :transition :from :a :to :b}]))
  (testing "relation-of? false"
    (are [x y] (= x (boolean (apply relation-of? y)))
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
    (are [x y] (= x (boolean (view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :dynamic-view :id :dynamic-view}
      true {:el :use-case-view :id :use-case-view}
      true {:el :code-view :id :code-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :context-view :id :context-view}
      true {:el :glossary-view :id :glossary-view}))
  (testing "view? false"
    (are [x y] (= x (boolean (view? y)))
      false {:el :abcd-view :id :abcd-view})))

(deftest hierarchical-view?-test
  (testing "hierarchical-view? true"
    (are [x y] (= x (boolean (hierarchical-view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :code-view :id :code-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :glossary-view :id :glossary-view}))
  (testing "hierarchical-view? false"
    (are [x y] (= x (boolean (hierarchical-view? y)))
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
      "" {:id :foo}
      "" {:el :foo/bar})))

(def element-input
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
           (elements-by-namespace element-input)))))

(deftest union-by-id-test
  (testing "union-by-id"
    (is (= #{{:id :x/b, :el :a}
             {:id :x/c :el :m}
             {:id :x/a, :el :a, :dir :up}}
           (union-by-id #{{:id :x/a :el :a :dir :down}
                          {:id :x/b :el :a}}
                        #{{:id :x/a :el :a :dir :up}
                          {:id :x/c :el :m}})))))

(deftest difference-by-id-test
  (testing "difference-by-id"
    (is (= #{{:id :x/b :el :a} {:id :x/d :el :a :dir :left}}
           (difference-by-id #{{:id :x/a :el :a :dir :down}
                               {:id :x/b :el :a}
                               {:id :x/c :el :m}
                               {:id :x/d :el :a :dir :left}}
                             #{{:id :x/a :el :a :dir :up}} #{{:id :x/c :el :m}})))))

