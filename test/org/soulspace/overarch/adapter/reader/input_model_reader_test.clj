(ns org.soulspace.overarch.adapter.reader.input-model-reader-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.adapter.reader.input-model-reader :refer :all]
            ))

(deftest input-child?-test
  (testing "input-child? true"
    (is (= true (input-child? {:el :component :id :a/component}
                                       {:el :container
                                        :id :a/container
                                        :ct #{{:el :component
                                               :id :a/component}}}))))
  (testing "child? false"
    (are [x y] (= x (input-child? y
                                           {:el :container
                                            :id :a/container
                                            :ct #{{:el :component
                                                   :id :a/component}}}))
      false {:el :container
             :id :a/container
             :ct #{{:el :component
                    :id :a/component}}}
      false nil)))

(comment
  (input-child? nil nil)
  (input-child? {:el :component :id :a/component} nil)
  (input-child? nil {:el :component :id :a/component}))

