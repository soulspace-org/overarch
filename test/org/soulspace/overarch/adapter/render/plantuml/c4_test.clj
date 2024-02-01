(ns org.soulspace.overarch.adapter.render.plantuml.c4-test
  (:require [clojure.test :refer :all]
             [org.soulspace.overarch.adapter.render.plantuml.c4 :refer :all]))
  
  (deftest compile-test
    (testing "Compilation"
      (is (= 1 1))))