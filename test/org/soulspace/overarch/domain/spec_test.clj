(ns org.soulspace.overarch.domain.spec-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.spec :refer :all]
            [org.soulspace.overarch.domain.model-test :as mt]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(deftest check-test
  (testing "check valid"
    (is (= mt/c4-model1 (check mt/c4-model1)))
    (is (= mt/concept-model1 (check mt/concept-model1)))))
