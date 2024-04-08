(ns org.soulspace.overarch.application.template-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.application.template :refer :all]))

(deftest artifact-filename-test
  (testing "artifact-filename-test without element"
    (are [x y] (= x (apply artifact-filename y))
      "hello.txt" [{:base-name "hello" :extension "txt"}])))
