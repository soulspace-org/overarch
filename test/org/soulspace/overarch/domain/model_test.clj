(ns org.soulspace.overarch.domain.model-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :refer :all]))


(deftest model-predicates-test
  (testing "element?"
    (are [x y] (= x (fns/truthy? (element? y)))
      true {:el :person}
      false {}
      false {:type :person}))

  (testing "identifiable?"
    (are [x y] (= x (fns/truthy? (identifiable? y)))
      true {:id :abc}
      true {:id :a/abc}
      false {}
      false {:type :person}))

  (testing "named?"
    (are [x y] (= x (fns/truthy? (named? y)))
      true {:name "abc"}
      false {}
      false {:type :person}))

  (testing "relational?"
    (are [x y] (= x (fns/truthy? (relational? y)))
      true {:from :abc :to :bcd}
      true {:from :a/abc :to :a/bcd}
      false {}
      false {:type :person}))

  (testing "technical?"
    (are [x y] (= x (fns/truthy? (technical? y)))
      true {:tech "abc"}
      false {}
      false {:type :person}))
  
  (testing "reference?"
    (are [x y] (= x (fns/truthy? (reference? y)))
      true {:ref :abc}
      true {:ref :a/abc}
      false {}
      false {:type :person}))
  )

