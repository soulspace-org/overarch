(ns org.soulspace.overarch.domain.views.concept-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.concept-view :refer :all] 
            [org.soulspace.overarch.domain.model-test :as model-test]
            [org.soulspace.overarch.domain.model :as model]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (fns/truthy? (render-model-element? {:el :concept-view} y)))
      true {:el :concept}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}
      true {:el :person}
      true {:el :system}
      true {:el :container}))
  
  (testing "render-model-element? false" 
    (are [x y] (= x (fns/truthy? (render-model-element? {:el :concept-view} y)))
      false {:el :system-boundary}
      false {:el :container-boundary}
      false {:el :component}
      false {:el :node}
      false {:el :actor}
      false {:el :use-case}
      false {:el :uses}
      false {:el :include}
      false {:el :extends}
      false {:el :generalizes}
      false {:el :state-machine}
      false {:el :start-state}
      false {:el :state}
      false {:el :end-state}
      false {:el :transition}
      false {:el :fork}
      false {:el :join}
      false {:el :choice}
      false {:el :history-state}
      false {:el :deep-history-state}
      false {:el :package}
      false {:el :class}
      false {:el :field}
      false {:el :method}
      false {:el :interface}
      false {:el :enum}
      false {:el :inheritance}
      false {:el :implementation}
      false {:el :composition}
      false {:el :aggregation}
      false {:el :association}
      false {:el :dependency}
      false {:el :stereotype}
      false {:el :annotation}
      false {:el :namespace}
      false {:el :function}
      false {:el :protocol})))

(def concept-view1
  {:el :concept-view
   :id :test/concept-view1
   :title "Concept View 1"
   :ct [{:ref :test/concept1}
        {:ref :test/concept2}
        {:ref :test/concept3}
        {:ref :test/concept1-has-a-concept2}
        {:ref :test/concept1-is-a-concept3}]})

(def concept-view1-related
  {:el :concept-view
   :id :test/concept-view1-related
   :title "Concept View 1"
   :spec {:include :related}
   :ct [{:ref :test/concept1-has-a-concept2}
        {:ref :test/concept1-is-a-concept3}]})

(def concept-view1-relations
  {:el :concept-view
   :id :test/concept-view1-relations
   :title "Concept View 1"
   :spec {:include :relations}
   :ct [{:ref :test/concept1}
        {:ref :test/concept2}
        {:ref :test/concept3}]})

(deftest referenced-model-nodes-test
  (let [concept1 model-test/concept-model1]
    (testing "referenced-model-nodes for concept view"
      (are [x y] (= x y)
        3 (count (referenced-model-nodes concept1 concept-view1))
        0 (count (referenced-model-nodes concept1 concept-view1-related))
        3 (count (referenced-model-nodes concept1 concept-view1-relations))))))

(deftest referenced-relations-test
  (let [concept1 model-test/concept-model1]
    (testing "referenced-relations for concept view"
      (are [x y] (= x y)
        2 (count (referenced-relations concept1 concept-view1))
        2 (count (referenced-relations concept1 concept-view1-related))
        0 (count (referenced-relations concept1 concept-view1-relations))))))

(deftest referenced-elements-test
  (let [concept1 model-test/concept-model1]
    (testing "referenced-elements for concept view"
      (are [x y] (= x y)
        5 (count (referenced-elements concept1 concept-view1))
        2 (count (referenced-elements concept1 concept-view1-related))
        3 (count (referenced-elements concept1 concept-view1-relations))))))

(deftest specified-model-nodes-test
  (let [concept1 model-test/concept-model1]
    (testing "specified-model-nodes for concept view"
      (are [x y] (= x y)
        3 (count (specified-model-nodes concept1 concept-view1))
        3 (count (specified-model-nodes concept1 concept-view1-related))
        3 (count (specified-model-nodes concept1 concept-view1-relations))))))

(deftest specified-relations-test
  (let [concept1 model-test/concept-model1]
    (testing "specified-relations for concept view"
      (are [x y] (= x y)
        2 (count (specified-relations concept1 concept-view1))
        2 (count (specified-relations concept1 concept-view1-related))
        2 (count (specified-relations concept1 concept-view1-relations))))))

(deftest specified-elements-test
  (let [concept1 model-test/concept-model1]
    (testing "specified-elements for concept view"
      (are [x y] (= x y)
        5 (count (specified-elements concept1 concept-view1))
        5 (count (specified-elements concept1 concept-view1-related))
        5 (count (specified-elements concept1 concept-view1-relations))))))

(comment
  (def concept1 model-test/concept-model1)
  (referenced-model-nodes concept1 concept-view1)
  (referenced-model-nodes concept1 concept-view1-related)
  (referenced-model-nodes concept1 concept-view1-relations)
  (referenced-relations concept1 concept-view1)
  (referenced-relations concept1 concept-view1-related)
  (referenced-relations concept1 concept-view1-relations)
  (referenced-elements concept1 concept-view1)
  (referenced-elements concept1 concept-view1-related)
  (referenced-elements concept1 concept-view1-relations)
  (specified-model-nodes concept1 concept-view1)
  (specified-model-nodes concept1 concept-view1-related)
  (specified-model-nodes concept1 concept-view1-relations)
  (specified-relations concept1 concept-view1)
  (specified-relations concept1 concept-view1-related)
  (specified-relations concept1 concept-view1-relations)
  (specified-elements concept1 concept-view1)
  (specified-elements concept1 concept-view1-related)
  (specified-elements concept1 concept-view1-relations))
