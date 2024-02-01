(ns org.soulspace.overarch.adapter.ui.cli-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.adapter.ui.cli :refer :all]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))