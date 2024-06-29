(ns org.soulspace.overarch.adapter.template.comb-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.application.template :as t]
            [org.soulspace.overarch.adapter.template.comb :as cmb]))

(deftest eval-test
  (testing "Comb templates evaluted with clojure.eval"
    (is (= (cmb/eval (t/parse-template :comb "foo") {}) "foo"))
    (is (= (cmb/eval (t/parse-template :comb "<%= 10 %>") {}) "10"))
    (is (= (cmb/eval (t/parse-template :comb "<%= x %>") {:x "foo"}) "foo"))
    (is (= (cmb/eval (t/parse-template :comb "<%=x%>") {:x "foo"}) "foo"))
    (is (= (cmb/eval (t/parse-template :comb "<% (doseq [x xs] %>foo<%= x %> <% ) %>") {:xs [1 2 3]})
           "foo1 foo2 foo3 "))))

(declare x)
(deftest fn-test
  (is (= ((cmb/fn [x] (t/parse-template :comb "foo<%= x %>")) "bar")
         "foobar")))

(deftest evalsci-test
  (testing "Comb templates evaluted with SCI"
    (is (= (cmb/eval-sci (t/parse-template :combsci "foo") {}) "foo"))))
