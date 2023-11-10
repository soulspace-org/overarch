(ns org.soulspace.overarch.domain.model-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :refer :all]))


(def c4-model
  #{{:el :person
     :id :test/user1
     :name "User 1"}
    {:el :system
     :id :test/system1
     :name "Test System"
     :ct #{{:el :container
            :id :test/container1
            :name "Test Container 1"
            :ct #{{:el :component
                   :id :test/component11
                   :name "Test Component 11"}}}}}
    {:el :rel
     :id :test/user1-uses-system1
     :from :test/user1
     :to :test/system1
     :name "uses"}})

(def concept-model
  #{{:el :concept
     :id :test/concept1
     :name "Concept 1"}
    {:el :concept
     :id :test/concept2
     :name "Concept 2"}
    {:el :concept
     :id :test/concept3
     :name "Concept 3"}
    {:el :rel
     :id :test/concept1-has-a-concept2
     :from :test/concept1
     :to :test/concept2
     :name "has a"}
    {:el :rel
     :id :test/concept1-is-a-concept3
     :from :test/concept1
     :to :test/concept3
     :name "is a"}
    })

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

(deftest reference?-test
  (testing "reference?"
    (are [x y] (= x (fns/truthy? (reference? y)))
      true {:ref :abc}
      true {:ref :a/abc}
      false {}
      false {:type :person})))

(deftest build-id->elements-test
      (testing "build-id->elements"
        (are [x y] (= x y)
          5 (count (build-id->elements c4-model))
          5 (count (build-id->elements concept-model))
          )))

(deftest build-id->parent-test
  (testing "build-id->parent"
    (are [x y] (= x y)
      2 (count (build-id->parent c4-model))
      0 (count (build-id->parent concept-model)))))


