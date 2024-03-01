(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(comment
  )
