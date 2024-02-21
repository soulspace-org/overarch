(ns org.soulspace.overarch.domain.analytics-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.analytics :refer :all]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.application.model-repository :as repo]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(def rel-unresolved-input
  #{{:el :system
     :id :test/system-a
     :name "System A"}

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

(def rel-unresolved-model (repo/build-model rel-unresolved-input))

(def view-ref-unresolved-input
  #{{:el :context-view
     :id :test/missing-elements
     :title "View referencing missing elements"
     :ct [{:ref :foo/bar}
          {:ref :foo/baz}]}})
(def view-ref-unresolved-model (repo/build-model view-ref-unresolved-input))

(deftest check-relations-test
  (testing "check-relations"
    (is (= 3 (count (check-relations rel-unresolved-model))))))

(deftest check-views-test
  (testing "check-views"
    (is (= 2 (count (check-views view-ref-unresolved-model))))))
