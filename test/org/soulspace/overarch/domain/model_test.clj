(ns org.soulspace.overarch.domain.model-test
  (:require [clojure.test :refer :all]
            [org.soulspace.overarch.util.functions :as fns]
            [org.soulspace.overarch.domain.element :as e]
            [org.soulspace.overarch.domain.model :refer :all]))

(def c4-model1
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


(def concept-model1
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

(def c4-registry1 (build-registry c4-model1))
(def concept-registry1 (build-registry concept-model1))

(deftest id->element-test
  (testing "build-id->elements"
    (are [x y] (= x y)
      14 (count (traverse e/identifiable? id->element c4-model1))
      5 (count (traverse e/identifiable? id->element concept-model1)))))

(deftest build-id->parent-test
  (testing "build-id->parent"
    (are [x y] (= x y)
      5 (count (build-id->parent c4-model1))
      0 (count (build-id->parent concept-model1)))))

(deftest referrer-id->rel-test
  (testing "build-referrer-id->rels"
    (are [x y] (= x y)
      3 (count (traverse e/relation? referrer-id->rel c4-model1))
      1 (count (traverse e/relation? referrer-id->rel concept-model1))
      2 (count (:test/user1 (traverse e/relation? referrer-id->rel c4-model1)))
      1 (count (:test/system1 (traverse e/relation? referrer-id->rel c4-model1)))
      0 (count (:test/ext-system1 (traverse e/relation? referrer-id->rel c4-model1)))
      2 (count (:test/concept1 (traverse e/relation? referrer-id->rel concept-model1)))
      0 (count (:test/concept2 (traverse e/relation? referrer-id->rel concept-model1)))
      0 (count (:test/concept3 (traverse e/relation? referrer-id->rel concept-model1)))
      )))

(deftest referred-id->rel-test
  (testing "build-referrer-id->rels"
    (are [x y] (= x y)
      5 (count (traverse e/relation? referred-id->rel c4-model1))
      2 (count (traverse e/relation? referred-id->rel concept-model1))
      0 (count (:test/user1 (traverse e/relation? referred-id->rel c4-model1)))
      1 (count (:test/system1 (traverse e/relation? referred-id->rel c4-model1)))
      2 (count (:test/ext-system1 (traverse e/relation? referred-id->rel c4-model1)))
      0 (count (:test/concept1 (traverse e/relation? referred-id->rel concept-model1)))
      1 (count (:test/concept2 (traverse e/relation? referred-id->rel concept-model1)))
      1 (count (:test/concept3 (traverse e/relation? referred-id->rel concept-model1)))
      )))

(comment
  :test/user1
  (type (traverse e/relation? referrer-id->rel c4-model1))
  (traverse e/relation? referrer-id->rel concept-model1)
  (relations-of-nodes c4-registry1 #{{:id :test/user1} {:id :test/system1} {:id :test/ext-system1}})
  )