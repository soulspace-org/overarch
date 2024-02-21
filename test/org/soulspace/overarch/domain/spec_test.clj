(ns org.soulspace.overarch.domain.spec-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.spec :refer :all]
            [org.soulspace.overarch.domain.model-test :as mt]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(deftest check-test
  (testing "check valid"
    (is (= mt/c4-input1 (check mt/c4-input1)))
    (is (= mt/concept-input1 (check mt/concept-input1)))))
