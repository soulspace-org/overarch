(ns org.soulspace.overarch.adapter.exports.edn-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.adapter.exports.edn :refer :all]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))