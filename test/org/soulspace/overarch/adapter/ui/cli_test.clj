(ns org.soulspace.overarch.adapter.ui.cli-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [org.soulspace.overarch.adapter.ui.cli :refer :all]))

(defn err-output
  "Returns the output of `f` on stderr and stdout as map."
  [f]
  (let [err (java.io.StringWriter.)
        out (java.io.StringWriter.)]
    (binding [*err* err
              *out* out]
      (f))
    {:err (str err) :out (str out)}))

(deftest compile-test
  (testing "Compilation"
    (is (= 1 1))))

(deftest deprecated-render-format-test
  (testing "warning is printed to stderr, if the render format is set"
    (let [{:keys [err out]} (err-output #(warn-deprecated-render-format {:render-format :plantuml}))]
      (is (str/includes? err "deprecated"))
      (is (str/includes? err "templates"))
      (is (= "" out))))
  (testing "no warning without a render format"
    (let [{:keys [err out]} (err-output #(warn-deprecated-render-format {:generation-config "templates/gencfg.edn"}))]
      (is (= "" err))
      (is (= "" out)))))
