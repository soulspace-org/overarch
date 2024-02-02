(ns org.soulspace.overarch.util.functions-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :refer :all]))


(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(deftest truthy?-test
  (testing "truthy? false"
    (are [x y] (= x (truthy? y))
      false false
      false nil))
    (testing "truthy? true"
    (are [x y] (= x (truthy? y))
      true true
      true "true"
      true "false"
      true 0
      true 1
      true '())))