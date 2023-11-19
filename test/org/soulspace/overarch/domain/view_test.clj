(ns org.soulspace.overarch.domain.view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.model :as model]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest view?-test
  (testing "view?"
    (are [x y] (= x (fns/truthy? (view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :dynamic-view :id :dynamic-view}
      true {:el :use-case-view :id :use-case-view}
      true {:el :class-view :id :class-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :context-view :id :context-view}
      true {:el :glossary-view :id :glossary-view}
      false {:el :abcd-view :id :abcd-view})))
  
(deftest hierarchical-view?-test
  (testing "hierarchical-view?"
    (are [x y] (= x (fns/truthy? (hierarchical-view? y)))
      true {:el :system-landscape-view :id :system-landscape-view}
      true {:el :context-view :id :context-view}
      true {:el :container-view :id :container-view}
      true {:el :component-view :id :component-view}
      true {:el :deployment-view :id :deployment-view}
      true {:el :class-view :id :class-view}
      true {:el :state-machine-view :id :state-machine-view}
      true {:el :glossary-view :id :glossary-view}
      false {:el :dynamic-view :id :dynamic-view}
      false {:el :use-case-view :id :use-case-view}
      false {:el :concept-view :id :concept-view}
      false {:el :abcd-view :id :abcd-view})))

(comment
  )
