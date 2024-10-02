(ns org.soulspace.overarch.adapter.template.markdown-api-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.adapter.template.markdown-api :refer :all]))

(deftest element-link-test
  (testing "element-link with element"
    (are [x y] (= x (element-link y))
      "[Test Role](test/roles/test-role.md)" {:el :person
                                              :id :test.roles/test-role
                                              :name "Test Role"})))

(deftest relative-element-link-test
  (testing "relative-element-link with current and new element"
    (are [x y] (= x (apply relative-element-link y))
      "[Test Role](../../test/roles/test-role.md)" [{:el :use-case
                                                     :id :test.use-cases/test-use-case
                                                     :name "Test Use Case"}
                                                    {:el :person
                                                     :id :test.roles/test-role
                                                     :name "Test Role"}])))
