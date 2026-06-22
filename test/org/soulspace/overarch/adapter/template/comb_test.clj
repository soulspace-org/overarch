(ns org.soulspace.overarch.adapter.template.comb-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.application.template :as t]
            [org.soulspace.overarch.adapter.template.comb :as cmb]))

(deftest evalsci-test
  (testing "Comb templates evaluted with SCI"
    (is (= (cmb/eval-sci (t/parse-template {:engine :combsci} "foo") {}) "foo"))))
