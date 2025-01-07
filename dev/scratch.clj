
;;
;; Metadata Handling
;; Source: Slack/Tim Pradley
;;
#_(defn assoc-meta [value k v]
  "Adds `k` to value metadata if it can"
  (cond (instance? IReference value) (do (alter-meta! value assoc k v) value)
        (instance? IMeta value) (vary-meta value assoc k v)
        :else value))

#_(defn dissoc-meta [value k]
  "Removes `k` from the metadata of value if present"
  (cond (instance? IReference value) (do (alter-meta! value dissoc k) value)
        (instance? IMeta value) (vary-meta value dissoc k)
        :else value))


; root nodes
(not (= #{{:el :system,
           :id :org.soulspace.internal/system,
           :name "Internal System",
           :ct #{{:el :container,
                  :id :org.soulspace.internal.system/container2,
                  :name "Container2",
                  :tech "Java",
                  :tags #{"critical" "autoscaled"}}
                 {:el :container,
                  :id :org.soulspace.internal.system/container1,
                  :name "Container1",
                  :ct #{{:el :component,
                         :id :org.soulspace.internal.system.container1/component1,
                         :name "Component1"}}}
                 {:el :container,
                  :id :org.soulspace.internal.system/container1-ui,
                  :name "Container1 UI"}
                 {:el :container,
                  :id :org.soulspace.internal.system/container2-topic,
                  :subtype :queue, :name "Container2 Events",
                  :tech "Kafka"}
                 {:el :container,
                  :id :org.soulspace.internal.system/container1-db,
                  :subtype :database, :name "Container1 DB"}}}}
        #{{:el :container,
           :id :org.soulspace.internal.system/container1,
           :name "Container1",
           :ct #{{:el :component,
                  :id :org.soulspace.internal.system.container1/component1,
                  :name "Component1"}}}
          {:el :container,
           :id :org.soulspace.internal.system/container1-ui,
           :name "Container1 UI"}
          {:el :system, :id :org.soulspace.internal/system,
           :name "Internal System",
           :ct #{{:el :container,
                  :id :org.soulspace.internal.system/container1,
                  :name "Container1",
                  :ct #{{:el :component,
                         :id :org.soulspace.internal.system.container1/component1,
                         :name "Component1"}}}
                 {:el :container,
                  :id :org.soulspace.internal.system/container1-ui,
                  :name "Container1 UI"}
                 {:el :container,
                  :id :org.soulspace.internal.system/container1-db,
                  :subtype :database, :name "Container1 DB"}}}
          {:el :container,
           :id :org.soulspace.internal.system/container1-db,
           :subtype :database,
           :name "Container1 DB"}}))
