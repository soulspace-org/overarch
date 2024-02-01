(ns org.soulspace.overarch.domain.spec-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.spec :refer :all]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))