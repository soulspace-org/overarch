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
  (relations-of-nodes c4-model1 #{{:id :test/user1} {:id :test/system1} {:id :test/ext-system1}})
  )

(def elements-to-filter
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
     :name "Internal System"}
    {:el :container
     :id :org.soulspace.internal.system/container1
     :name "Container1"
     :tech "Clojure"
     :tags #{"autoscaled"}}
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
     :tech "Kafka"}

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

(deftest filter-xf-test
  (testing "filter-xf with single criteria map"
    (are [x y] (= x (into #{} (filter-xf y) elements-to-filter))
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
         :tags #{"autoscaled"}}}
      {:tag "autoscaled"}

      #{{:el :container
         :id :org.soulspace.internal.system/container1-db
         :subtype :database
         :name "Container1 DB"
         :tech "Datomic"}}
      {:tech "Datomic"}))
  
  (testing "filter-xf with vector of criteria"
    (are [x y] (= x (into #{} (filter-xf y) elements-to-filter))

      #{{:el :system :id :org.soulspace.external/system2 :external true :name "External System 2"}
        {:el :system :id :org.soulspace.external/system1 :external true :name "External System 1"}
        {:el :person :id :org.soulspace.external/person :external true :name "External Person"}
        {:el :person :id :org.soulspace.internal/person :name "Internal Person"}}
      [{:external? true} {:el :person}]

      ;
      )))

(comment
  (into []
        (filter-xf {:namespace "org.soulspace.external"})
        elements-to-filter)
  (into []
        (filter-xf {:namespaces #{"org.soulspace.external" "org.soulspace.internal"}})
        elements-to-filter)
  (into []
        (filter-xf {:namespace-prefix "org.soulspace"})
        elements-to-filter)
  (into []
        (filter-xf {:el :person})
        elements-to-filter)
  (into []
        (filter-xf [{:external true} {:el :person}])
        elements-to-filter)
  (into []
        (filter-xf {:el :container})
        elements-to-filter)
  (into []
        (filter-xf {:els #{:system :container}})
        elements-to-filter)
  (into []
        (filter-xf {:el :container :subtype :database})
        elements-to-filter)
  (into []
        (filter-xf {:el :container :subtypes #{:database :queue}})
        elements-to-filter)
  (into []
        (filter-xf {:external true})
        elements-to-filter)
  (into []
        (filter-xf {:tech "Clojure"})
        elements-to-filter)
  (into []
        (filter-xf {:tag "autoscaled"})
        elements-to-filter)
  (into []
        (filter-xf {:tag "critical"})
        elements-to-filter)
  (into []
        (filter-xf {:tags #{"critical" "autoscaled"}})
        elements-to-filter)
  (into []
        (filter-xf {:el :rel})
        elements-to-filter))
