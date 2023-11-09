(ns org.soulspace.overarch.model-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.model :refer :all]))

(defn truthy?
  "Returns true, if the argument ``e`` is truthy."
  [e]
  (if e true false))

(deftest model-predicates-test
  (testing "element?"
    (are [x y] (= x (truthy? y))
      true (element? {:el :person})
      false (element? {})
      false (element? {:type :person})))

  (testing "identifiable?"
    (are [x y] (= x (truthy? y))
      true (identifiable? {:id :abc})
      true (identifiable? {:id :a/abc})
      false (identifiable? {})
      false (identifiable? {:type :person})))

  (testing "named?"
    (are [x y] (= x (truthy? y))
      true (named? {:name "abc"})
      false (named? {})
      false (named? {:type :person})))

  (testing "relational?"
    (are [x y] (= x (truthy? y))
      true (relational? {:from :abc :to :bcd})
      true (relational? {:from :a/abc :to :a/bcd})
      false (relational? {})
      false (relational? {:type :person})))

  (testing "technical?"
    (are [x y] (= x (truthy? y))
      true (technical? {:tech "abc"})
      false (technical? {})
      false (technical? {:type :person})))
  
  (testing "reference?"
    (are [x y] (= x (truthy? y))
      true (reference? {:ref :abc})
      true (reference? {:ref :a/abc})
      false (reference? {})
      false (reference? {:type :person})))
  )

