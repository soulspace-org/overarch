(ns org.soulspace.overarch.application.template-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.application.template :refer :all]))

(deftest artifact-filename-test
  (testing "artifact-filename without element"
    (are [x y] (= x (apply artifact-filename y))
      "hello.txt" [{:base-name "hello" :extension "txt"}]
      "hello_service.clj" [{:base-name "hello" :suffix "_service" :extension "clj"}]))
  (testing "artifact-filename with element"
    (are [x y] (= x (apply artifact-filename y))
      "hello.txt" [{:extension "txt"} {:name "hello"}]
      "hello_service.clj" [{:suffix "_service" :extension "clj"} {:name "hello"}]
      )))

(deftest artifact-path-test
  (testing "artifact-path without element"
    (are [x y] (= x (apply artifact-path y))
      "hello/" [{:base-namespace "hello"}]
      "src/hello/" [{:subdir "src" :base-namespace "hello"}]))
  (testing "artifact-path with element"
    (are [x y] (= x (apply artifact-path y))
      "hello/" [{} {:id :hello/huhu}]
      "src/hello/" [{:subdir "src"} {:id :hello/huhu}]
      )))

