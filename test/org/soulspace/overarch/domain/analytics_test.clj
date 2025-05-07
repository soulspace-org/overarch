(ns org.soulspace.overarch.domain.analytics-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.analytics :refer :all]
            [org.soulspace.overarch.adapter.reader.model-reader :as reader]))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(def opts {:input-model-format :overarch-input})

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
(def rel-unresolved-model (reader/build-model opts rel-unresolved-input))

(def view-ref-unresolved-input
  #{{:el :context-view
     :id :test/missing-elements
     :title "View referencing missing elements"
     :ct [{:ref :foo/bar}
          {:ref :foo/baz}]}})
(def view-ref-unresolved-model (reader/build-model opts view-ref-unresolved-input))

(deftest check-relations-test
  (testing "check-relations"
    (is (= 3 (count (check-relations rel-unresolved-model))))))

(deftest check-views-test
  (testing "check-views"
    (is (= 2 (count (check-views view-ref-unresolved-model))))))
