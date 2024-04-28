(ns org.soulspace.overarch.domain.model-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.domain.model :refer :all]))

(def c4-input1
  "Simple test model for C4 Architecture"
  #{{:el :person
     :id :test/user1
     :name "User 1"}
    {:el :system
     :id :test/ext-system1
     :external true
     :name "External System 1"}
    {:el :system
     :id :test/system1
     :name "Test System"
     :ct #{{:el :container
            :id :test/container1
            :name "Test Container 1"
            :ct #{{:el :component
                   :id :test/component11
                   :name "Test Component 11"}
                  {:el :component
                   :id :test/component12
                   :name "Test Component 12"}}}
           {:el :container
            :id :test/container-db1
            :subtype :database
            :name "Test DB Container 1"}
           {:el :container
            :id :test/container-queue1
            :subtype :queue
            :name "Test Queue Container 1"}}}
    {:el :rel
     :id :test/user1-uses-system1
     :from :test/user1
     :to :test/system1
     :name "uses"}
    {:el :rel
     :id :test/system1-calls-ext-system1
     :from :test/system1
     :to :test/ext-system1
     :name "calls"}
    {:el :rel
     :id :test/user1-uses-container1
     :from :test/user1
     :to :test/container1
     :name "uses"}
    {:el :rel
     :id :test/container1-calls-ext-system1
     :from :test/container1
     :to :test/ext-system1
     :name "calls"}
    {:el :rel
     :id :test/container1-stores-in-container-db1
     :from :test/container1
     :to :test/container-db1
     :name "stores in"}
    {:el :rel
     :id :test/container1-sends-to-container-queue1
     :from :test/container1
     :to :test/container-queue1
     :name "sends to"}})


(def concept-input1
  #{{:el :concept
     :id :test/concept1
     :name "Concept 1"}
    {:el :concept
     :id :test/concept2
     :name "Concept 2"}
    {:el :concept
     :id :test/concept3
     :name "Concept 3"}
    {:el :rel
     :id :test/concept1-has-a-concept2
     :from :test/concept1
     :to :test/concept2
     :name "has a"}
    {:el :rel
     :id :test/concept1-is-a-concept3
     :from :test/concept1
     :to :test/concept3
     :name "is a"}})

(def c4-model1 (build-model c4-input1))
(def concept-model1 (build-model concept-input1))

(deftest id->element-test
  (testing "id->elements"
    (are [x y] (= x y)
      19 (count (:id->element c4-model1)) ; incl. 5 synthetic relations
      5 (count (:id->element  concept-model1)))))

(deftest id->parent-test
  (testing "id->parent"
    (are [x y] (= x y)
      5 (count (:id->parent c4-model1))
      0 (count (:id->parent concept-model1)))))

(deftest referrer-id->relations-test
  (testing "referrer-id->rels"
    (are [x y] (= x y)
      3 (count (:referrer-id->relations c4-model1))
      1 (count (:referrer-id->relations concept-model1))
      2 (count (:test/user1 (:referrer-id->relations c4-model1)))
      4 (count (:test/system1 (:referrer-id->relations c4-model1))) ; incl. 3 synthetic relations
      0 (count (:test/ext-system1 (:referrer-id->relations c4-model1)))
      2 (count (:test/concept1 (:referrer-id->relations concept-model1)))
      0 (count (:test/concept2 (:referrer-id->relations concept-model1)))
      0 (count (:test/concept3 (:referrer-id->relations concept-model1))))))

(deftest referred-id->relations-test
  (testing "referred-id->rels"
    (are [x y] (= x y)
      7 (count (:referred-id->relations c4-model1))
      2 (count (:referred-id->relations concept-model1))
      0 (count (:test/user1 (:referred-id->relations c4-model1)))
      1 (count (:test/system1 (:referred-id->relations c4-model1)))
      2 (count (:test/ext-system1 (:referred-id->relations c4-model1)))
      0 (count (:test/concept1 (:referred-id->relations concept-model1)))
      1 (count (:test/concept2 (:referred-id->relations concept-model1)))
      1 (count (:test/concept3 (:referred-id->relations concept-model1))))))

(comment
  :test/user1
  c4-model1
  (relations-of-nodes c4-model1 #{{:id :test/user1} {:id :test/system1} {:id :test/ext-system1}}))

(def ancestor-input
  #{{:el :system
     :id :a/system
     :ct #{{:el :container
            :id :a/container
            :ct #{{:el :component
                   :id :a/component}}}}}})
(def ancestor-model (build-model ancestor-input))

(deftest ancestor-nodes-test
  (testing "ancestor-nodes true"
    (are [x y] (= x (ancestor-nodes ancestor-model y))
      #{{:el :system
         :id :a/system
         :ct #{{:el :container
                :id :a/container
                :ct #{{:el :component
                       :id :a/component}}}}}
        {:el :container
         :id :a/container
         :ct #{{:el :component
                :id :a/component}}}}
      {:el :component
       :id :a/component}

      #{{:el :system
         :id :a/system
         :ct #{{:el :container
                :id :a/container
                :ct #{{:el :component
                       :id :a/component}}}}}}
      {:el :container
       :id :a/container
       :ct #{{:el :component
              :id :a/component}}})))

(deftest ancestor-node?-test
  (testing "ancestor-node? true"
    (are [x y] (= x (ancestor-node? ancestor-model
                                    {:el :component
                                     :id :a/component}
                                    y))
      true {:el :system
            :id :a/system
            :ct #{{:el :container
                   :id :a/container
                   :ct #{{:el :component
                          :id :a/component}}}}}

      true {:el :container
            :id :a/container
            :ct #{{:el :component
                   :id :a/component}}}))
  (testing "ancestor-node? true"
    (are [x y] (= x (ancestor-node? ancestor-model
                                    {:el :component
                                     :id :a/component}
                                    y))
                  false {:el :component
                         :id :a/component})))

(def filter-input
  #{{:el :person
     :id :org.soulspace.external/person
     :external true
     :name "External Person"}
    {:el :person
     :id :org.soulspace.internal/person
     :name "Internal Person"}
    {:el :system
     :id :org.soulspace.external/system1
     :external true
     :name "External System 1"}
    {:el :system
     :id :org.soulspace.external/system2
     :external true
     :name "External System 2"}
    {:el :system
     :id :org.soulspace.internal/system
     :name "Internal System"
     :ct [{:el :container
           :id :org.soulspace.internal.system/container1
           :name "Container1"
           :tech "Clojure"
           :tags #{"autoscaled"}
           :ct #{{:el :component
                  :id :org.soulspace.internal.system.container1/component1
                  :name "Component1"}}}
          {:el :container
           :id :org.soulspace.internal.system/container1-ui
           :name "Container1 UI"
           :tech "ClojureScript"}
          {:el :container
           :id :org.soulspace.internal.system/container1-db
           :subtype :database
           :name "Container1 DB"
           :tech "Datomic"}
          {:el :container
           :id :org.soulspace.internal.system/container2
           :name "Container2"
           :tech "Java"
           :tags #{"critical" "autoscaled"}}
          {:el :container
           :id :org.soulspace.internal.system/container2-topic
           :subtype :queue
           :name "Container2 Events"
           :tech "Kafka"}]}

    {:el :rel
     :id :org.soulspace.external/person-uses-system1
     :from :org.soulspace.external/person
     :to :org.soulspace.external/system1
     :name "uses"}
    {:el :rel
     :id :org.soulspace.internal/person-uses-system
     :from :org.soulspace.internal/person
     :to :org.soulspace.internal/system
     :name "uses"}
    {:el :rel
     :id :org.soulspace.internal/person
     :from :org.soulspace.internal/person
     :to :org.soulspace.internal.system/container1-ui
     :name "uses"}
    {:el :publish
     :id :org.soulspace.internal.system/container2-publishes-to-container2-topic
     :from :org.soulspace.internal.system/container2
     :to :org.soulspace.internal.system/container2-topic
     :name "publishes to"}
    {:el :subscribe
     :id :org.soulspace.internal.system/container1-consumes-container2-topic
     :from :org.soulspace.internal.system/container1
     :to :org.soulspace.internal.system/container2-topic
     :name "consumes"}})

(def filter-model1 (build-model filter-input))

(deftest filter-xf-test
  (testing "filter-xf with single criteria map and element based criteria"
    (are [x y] (= x (into #{} (filter-xf filter-model1 y) (model-elements filter-model1)))
      #{{:el :person
         :id :org.soulspace.external/person
         :external true
         :name "External Person"}}
      {:el :person :external? true}

      #{{:el :person
         :id :org.soulspace.internal/person
         :name "Internal Person"}}
      {:el :person :external? false}

      #{{:el :rel
         :id :org.soulspace.external/person-uses-system1
         :from :org.soulspace.external/person
         :to :org.soulspace.external/system1
         :name "uses"}}
      {:el :rel :namespace "org.soulspace.external"}
      
      #{{:el :container
       :id :org.soulspace.internal.system/container1-ui
       :name "Container1 UI"
       :tech "ClojureScript"}}
      {:name "Container1 UI"}

      #{{:el :container
         :id :org.soulspace.internal.system/container2
         :name "Container2"
         :tech "Java"
         :tags #{"critical" "autoscaled"}}}
      {:tag "critical"}

      #{{:el :container
         :id :org.soulspace.internal.system/container2
         :name "Container2"
         :tech "Java"
         :tags #{"critical" "autoscaled"}}
        {:el :container
         :id :org.soulspace.internal.system/container1
         :name "Container1"
         :tech "Clojure"
         :tags #{"autoscaled"}
         :ct #{{:el :component
                :id :org.soulspace.internal.system.container1/component1
                :name "Component1"}}}}
      {:tag "autoscaled"}

      #{{:el :container
         :id :org.soulspace.internal.system/container1-db
         :subtype :database
         :name "Container1 DB"
         :tech "Datomic"}}
      {:tech "Datomic"}

      #{{:el :container
         :id :org.soulspace.internal.system/container1-db
         :subtype :database
         :name "Container1 DB"
         :tech "Datomic"}}
      {:key [:tech "Datomic"]}))
  
  (testing "filter-xf with single criteria map and model based criteria"
    (are [x y] (= x (into #{} (filter-xf filter-model1 y) (model-elements filter-model1)))
      #{{:el :system
         :id :org.soulspace.external/system1
         :external true
         :name "External System 1"}
        {:el :system
         :id :org.soulspace.internal/system
         :name "Internal System"
         :ct [{:el :container
               :id :org.soulspace.internal.system/container1
               :name "Container1"
               :tech "Clojure"
               :tags #{"autoscaled"}
               :ct #{{:el :component
                      :id :org.soulspace.internal.system.container1/component1
                      :name "Component1"}}}
              {:el :container
               :id :org.soulspace.internal.system/container1-ui
               :name "Container1 UI"
               :tech "ClojureScript"}
              {:el :container
               :id :org.soulspace.internal.system/container1-db
               :subtype :database
               :name "Container1 DB"
               :tech "Datomic"}
              {:el :container
               :id :org.soulspace.internal.system/container2
               :name "Container2"
               :tech "Java"
               :tags #{"critical" "autoscaled"}}
              {:el :container
               :id :org.soulspace.internal.system/container2-topic
               :subtype :queue
               :name "Container2 Events"
               :tech "Kafka"}]}}
      {:el :system :referred? true}

      #{{:el :person
         :id :org.soulspace.external/person
         :external true
         :name "External Person"}
        {:el :person
         :id :org.soulspace.internal/person
         :name "Internal Person"}}
      {:el :person :refers? true}

      #{{:el :system
         :id :org.soulspace.external/system1
         :external true
         :name "External System 1"}}
      {:el :system :referred-by :org.soulspace.external/person}

      #{{:el :person
         :id :org.soulspace.external/person
         :external true
         :name "External Person"}}
      {:el :person :refers-to :org.soulspace.external/system1}

      #{{:el :system
         :id :org.soulspace.internal/system
         :name "Internal System"
         :ct [{:el :container
               :id :org.soulspace.internal.system/container1
               :name "Container1"
               :tech "Clojure"
               :tags #{"autoscaled"}
               :ct #{{:el :component
                      :id :org.soulspace.internal.system.container1/component1
                      :name "Component1"}}}
              {:el :container
               :id :org.soulspace.internal.system/container1-ui
               :name "Container1 UI"
               :tech "ClojureScript"}
              {:el :container
               :id :org.soulspace.internal.system/container1-db
               :subtype :database
               :name "Container1 DB"
               :tech "Datomic"}
              {:el :container
               :id :org.soulspace.internal.system/container2
               :name "Container2"
               :tech "Java"
               :tags #{"critical" "autoscaled"}}
              {:el :container
               :id :org.soulspace.internal.system/container2-topic
               :subtype :queue
               :name "Container2 Events"
               :tech "Kafka"}]}}
      {:ancestor-of :org.soulspace.internal.system/container1}

      #{{:el :component
         :id :org.soulspace.internal.system.container1/component1
         :name "Component1"}}
      {:descendant-of :org.soulspace.internal.system/container1}))

  (testing "filter-xf with vector of criteria"
    (are [x y] (= x (into #{} (filter-xf filter-model1 y) (model-elements filter-model1)))

      #{{:el :system :id :org.soulspace.external/system2 :external true :name "External System 2"}
        {:el :system :id :org.soulspace.external/system1 :external true :name "External System 1"}
        {:el :person :id :org.soulspace.external/person :external true :name "External Person"}
        {:el :person :id :org.soulspace.internal/person :name "Internal Person"}}
      [{:external? true} {:el :person}]
      ;
      )))

(comment
  (into []
        (filter-xf filter-model1 {:namespace "org.soulspace.external"})
        filter-input)
  (into []
        (filter-xf filter-model1 {:namespaces #{"org.soulspace.external" "org.soulspace.internal"}})
        filter-input)
  (into []
        (filter-xf filter-model1 {:namespace-prefix "org.soulspace"})
        filter-input)
  (into []
        (filter-xf filter-model1 {:el :person})
        filter-input)
  (into []
        (filter-xf filter-model1 [{:external true} {:el :person}])
        filter-input)
  (into []
        (filter-xf filter-model1 {:el :container})
        filter-input)
  (into []
        (filter-xf filter-model1 {:els #{:system :container}})
        filter-input)
  (into []
        (filter-xf filter-model1 {:el :container :subtype :database})
        filter-input)
  (into []
        (filter-xf filter-model1 {:el :container :subtypes #{:database :queue}})
        filter-input)
  (into []
        (filter-xf filter-model1 {:external true})
        filter-input)
  (into []
        (filter-xf filter-model1 {:tech "Clojure"})
        filter-input)
  (into []
        (filter-xf filter-model1 {:tag "autoscaled"})
        filter-input)
  (into []
        (filter-xf filter-model1 {:tag "critical"})
        filter-input)
  (into []
        (filter-xf filter-model1 {:tags #{"critical" "autoscaled"}})
        filter-input)
  (into []
        (filter-xf filter-model1 {:el :rel})
        filter-input)
  ;
  )
