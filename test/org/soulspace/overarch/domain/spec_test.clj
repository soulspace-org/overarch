(ns org.soulspace.overarch.domain.spec-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.spec :refer :all]
            [org.soulspace.overarch.domain.model-test :as mt]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(deftest check-input-model-test
  (testing "check-input-model valid"
    (is (= mt/c4-input1 (check-input-model mt/c4-input1)))
    (is (= mt/concept-input1 (check-input-model mt/concept-input1)))))
