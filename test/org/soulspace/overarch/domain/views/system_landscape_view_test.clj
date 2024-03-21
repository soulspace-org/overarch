(ns org.soulspace.overarch.domain.views.system-landscape-view-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.view :refer :all]
            [org.soulspace.overarch.domain.views.system-landscape-view :refer :all] 
            [org.soulspace.overarch.domain.model-test :as model-test]))

(deftest render-model-element?-test
  (testing "render-model-element? true"
    (are [x y] (= x (boolean (render-model-element? {:el :system-landscape-view} y)))
      true {:el :person}
      true {:el :system}
      true {:el :enterprise-boundary}
      true {:el :context-boundary}))
  (testing "render-model-element? false"
    (are [x y] (= x (boolean (render-model-element? {:el :system-landscape-view} y)))
      false {:el :system-boundary}
      false {:el :container-boundary}
      false {:el :container}
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
      false {:el :protocol}
      false {:el :concept})))
