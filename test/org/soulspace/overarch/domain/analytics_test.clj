(ns org.soulspace.overarch.domain.analytics-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.analytics :refer :all]
            [org.soulspace.overarch.domain.model :as model]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(def unresolved-rel-input
  #{{:el :system
     :id :test/system-a
     :name "System A"}
    {:el :system
     :id :test/system-b
     :name "System B"}

    {:el :send
     :id :test/system-a-sends-to-system-c
     :from :test/system-a
     :to :test/missing-system-c
     :name "sends data to"}
    {:el :send
     :id :test/system-c-sends-to-system-d
     :from :test/missing-system-c
     :to :test/missing-system-d
     :name "sends data to"}})

(def unresolved-rel-model (model/build-registry unresolved-rel-input))

(deftest check-relations-test
  (testing "check-relations"
    (is (= 3 (count (check-relations unresolved-rel-model))))))

