(ns org.soulspace.overarch.diagram
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.core :as core]
            [org.soulspace.overarch.export :as exp]))

; general, multimethod?
(def diagram-type->element-predicate
  "Map from diagram type to content-level predicate."
  {:context-diagram          core/context-level?
   :container-diagram        core/container-level?
   :component-diagram        core/component-level?
   :code-diagram             core/code-level?
   :system-landscape-diagram core/system-landscape-level?
   :dynamic-diagram          core/dynamic-level?
   :deployment-diagram       core/deployment-level?})

; general
(def element->boundary
  "Maps model types to boundary types depending on the diagram type."
  {[:container-diagram :system]          :system-boundary
   [:component-diagram :system]          :system-boundary
   [:component-diagram :container]       :container-boundary})

;;;
;;; Context based content filtering
;;;

; general
(defn render-predicate
  "Returns true if the element is should be rendered for this diagram type.
   Checks both sides of a relation."
  [diagram-type]
  (let [element-predicate (diagram-type->element-predicate diagram-type)]
    (fn [e]
      (or (and (= :rel (:el e))
               (element-predicate (core/get-model-element (:from e)))
               (element-predicate (core/get-model-element (:to e))))
          (element-predicate e)))))

; general
(defn as-boundary?
  "Returns the boundary element, if the element should be rendered
   as a boundary for this diagram type, false otherwise."
  [diagram-type e]
  (and
   ; has children
   (seq (:ct e))
   ; has a boundary mapping for this diagram-type
   (element->boundary [diagram-type (:el e)])))

;;;
;;; Rendering functions
;;;

; general
(defn element-to-render
  "Returns the model element to be rendered in the context of the diagram."
  [diagram-type e]
  (let [boundary (as-boundary? diagram-type e)]
    (if boundary
    ; e has a boundary type and has children, render as boundary
      (assoc e :el boundary)
    ; render e as normal model element
      e)))

; general
(defn elements-to-render
  "Returns the list of elements to render from the diagram
   or the given collection of elements, depending on the type
   of the diagram."
  ([diagram]
   (elements-to-render diagram (:ct diagram)))
  ([diagram coll]
   (let [diagram-type (:el diagram)]
     (->> coll
          (map core/resolve-ref)
          (filter (render-predicate diagram-type))
          (map #(element-to-render diagram-type %))))))

; general
(defn relation-to-render
  "Returns the relation to be rendered in the context of the diagram."
  [diagram rel]
  ; TODO promote relations to higher levels?
  )

(defn collect-technologies
  "Returns a map of id to element for the elements of the coll."
  ([coll]
   (collect-technologies #{} coll))
  ([m coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (:tech e)
         (recur (collect-technologies (set/union m #{(:tech e)}) (:ct e)) (rest coll))
         (recur (collect-technologies m (:ct e)) (rest coll))))
     m)))

; general
(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

(defn element-name
  "Returns the name of the element."
  [e]
  (if (:name e)
    (:name e)
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

; general?
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)))


(def azure-icons
  {:azure/analysis-services           {:path "Analytics"
                                       :name "AzureAnalysisServices"}
   :azure/data-catalog                {:path "Analytics"
                                       :name "AzureDataCatalog"}
   :azure/data-explorer               {:path "Analytics"
                                       :name "AzureDataExplorer"}
   :azure/data-lake-analytics         {:path "Analytics"
                                       :name "AzureDataLakeAnalytics"}
   :azure/databricks                  {:path "Analytics"
                                       :name "AzureDatabricks"}
   :azure/event-hub                   {:path "Analytics"
                                       :name "AzureEventHub"}
   :azure/hdinsight                   {:path "Analytics"
                                       :name "AzureHDInsight"}
   :azure/stream-analytics            {:path "Analytics"
                                       :name "AzureDataCatalog"}
   :azure/app-service                 {:path "Compute"
                                       :name "AzureAppService"}
   :azure/batch                       {:path "Compute"
                                       :name "AzureBatch"}
   :azure/function                    {:path "Compute"
                                       :name "AzureFunction"}
   :azure/service-fabric              {:path "Compute"
                                       :name "AzureServiceFabric"}
   :azure/virtual-machine             {:path "Compute"
                                       :name "AzureVirtualMachine"}
   :azure/virtual-machine-scale-set   {:path "Compute"
                                       :name "AzureVirtualMachineScaleSet"}
   :azure/container-instance          {:path "Containers"
                                       :name "AzureContainerInstance"}
   :azure/container-registry          {:path "Containers"
                                       :name "AzureContainerRegistry"}
   :azure/kubernetes-service          {:path "Containers"
                                       :name "AzureKubernetesService"}
   :azure/service-fabric-mesh         {:path "Containers"
                                       :name "AzureServiceFabricMesh"}
   :azure/web-app-for-containers      {:path "Containers"
                                       :name "AzureWebAppForContainers"}
   :azure/azure                       {:path "General"
                                       :name "Azure"}
   :azure/automation                  {:path "Management"
                                       :name "AzureAutomation"}
   :azure/backup                      {:path "Management"
                                       :name "AzureBackup"}
   :azure/blueprints                  {:path "Management"
                                       :name "AzureBluePrints"}
   :azure/managed-application         {:path "Management"
                                       :name "AzureManagedApplications"}
   :azure/log-analytics               {:path "Management"
                                       :name "AzureLogAnalytics"}
   :azure/mmanagement-group           {:path "Management"
                                       :name "AzureManagementGroups"}
   :azure/monitor                     {:path "Management"
                                       :name "AzureMonitor"}
   :azure/policy                      {:path "Management"
                                       :name "AzurePolicy"}
   :azure/resource-group              {:path "Management"
                                       :name "AzureResourceGroups"}
   :azure/scheduler                   {:path "Management"
                                       :name "AzureScheduler"}
   :azure/site-recovery               {:path "Management"
                                       :name "AzureSiteRecovery"}
   :azure/subscription                {:path "Management"
                                       :name "AzureSubscription"}
   :azure/application-gateway         {:path "Networking"
                                       :name "AzureApplicationGateway"}
   :azure/ddos-protection             {:path "Networking"
                                       :name "AzureAzureDDoSProtection"}
   :azure/dns                         {:path "Networking"
                                       :name "AzureDNS"}
   :azure/express-route               {:path "Networking"
                                       :name "AzureExpressRoute"}
   :azure/front-door-service          {:path "Networking"
                                       :name "AzureFrontDoorService"}
   :azure/load-balancer               {:path "Networking"
                                       :name "AzureLoadBalancer"}
   :azure/traffic-manager             {:path "Networking"
                                       :name "AzureTrafficManager"}
   :azure/vpn-gateway                 {:path "Networking"
                                       :name "AzureVPNGateway"}
   :azure/virtual-network             {:path "Networking"
                                       :name "AzureVirtualNetwork"}
   :azure/virtual-wan                 {:path "Networking"
                                       :name "AzureVirtualWAN"}
   :azure/key-vault                   {:path "Security"
                                       :name "AzureKeyVault"}
   :azure/sentinel                    {:path "Security"
                                       :name "AzureSentinel"}
   :azure/blob-storage                {:path "Storage"
                                       :name "AzureBlobStorage"}
   :azure/data-box                    {:path "Storage"
                                       :name "AzureDataBox"}
   :azure/data-lake-storage           {:path "Storage"
                                       :name "AzureDataLakeStorage"}
   :azure/file-storage                {:path "Storage"
                                       :name "AzureFileStorage"}
   :azure/managed-disks               {:path "Storage"
                                       :name "AzureManagedDisks"}
   :azure/net-app-files               {:path "Storage"
                                       :name "AzureNetAppFiles"}
   :azure/queue-storage               {:path "Storage"
                                       :name "AzureQueueStorage"}
   :azure/stor-simple                 {:path "Storage"
                                       :name "AzureStorSimple"}
   :azure/storage                     {:path "Storage"
                                       :name "AzureStorage"}}
  )

(defmulti render-diagram
  "Renders a diagram"
  exp/export-format)