(ns org.soulspace.overarch.adapter.render.plantuml.uml-test
  (:require [clojure.test :refer :all]
             [org.soulspace.overarch.adapter.render.plantuml.uml :refer :all]))
  
  (deftest compile-test
    (testing "Compilation"
      (is (= 1 1))))