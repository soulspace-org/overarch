(ns org.soulspace.overarch.diagram
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
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
  {:azure/analysis-services           "azure/Analytics/AzureAnalysisServices"
   :azure/data-catalog                "azure/Analytics/AzureDataCatalog"
   :azure/data-explorer               "azure/Analytics/AzureDataExplorer"
   :azure/data-lake-analytics         "azure/Analytics/AzureDataLakeAnalytics"
   :azure/databricks                  "azure/Analytics/AzureDatabricks"
   :azure/event-hub                   "azure/Analytics/AzureEventHub"
   :azure/hdinsight                   "azure/Analytics/AzureHDInsight"
   :azure/stream-analytics            "azure/Analytics/AzureDataCatalog"
   :azure/app-service                 "azure/Compute/AzureAppService"
   :azure/batch                       "azure/Compute/AzureBatch"
   :azure/function                    "azure/Compute/AzureFunction"
   :azure/service-fabric              "azure/Compute/AzureServiceFabric"
   :azure/virtual-machine             "azure/Compute/AzureVirtualMachine"
   :azure/virtual-machine-scale-set   "azure/Compute/AzureVirtualMachineScaleSet"
   :azure/container-instance          "azure/Containers/AzureContainerInstance"
   :azure/container-registry          "azure/Containers/AzureContainerRegistry"
   :azure/kubernetes-service          "azure/Containers/AzureKubernetesService"
   :azure/service-fabric-mesh         "azure/Containers/AzureServiceFabricMesh"
   :azure/web-app-for-containers      "azure/Containers/AzureWebAppForContainers"
   :azure/azure                       "azure/General/Azure"
   :azure/automation                  "azure/Management/AzureAutomation"
   :azure/backup                      "azure/Management/AzureBackup"
   :azure/blueprints                  "azure/Management/AzureBluePrints"
   :azure/managed-application         "azure/Management/AzureManagedApplications"
   :azure/log-analytics               "azure/Management/AzureLogAnalytics"
   :azure/mmanagement-group           "azure/Management/AzureManagementGroups"
   :azure/monitor                     "azure/Management/AzureMonitor"
   :azure/policy                      "azure/Management/AzurePolicy"
   :azure/resource-group              "azure/Management/AzureResourceGroups"
   :azure/scheduler                   "azure/Management/AzureScheduler"
   :azure/site-recovery               "azure/Management/AzureSiteRecovery"
   :azure/subscription                "azure/Management/AzureSubscription"
   :azure/application-gateway         "azure/Networking/AzureApplicationGateway"
   :azure/ddos-protection             "azure/Networking/AzureAzureDDoSProtection"
   :azure/dns                         "azure/Networking/AzureDNS"
   :azure/express-route               "azure/Networking/AzureExpressRoute"
   :azure/front-door-service          "azure/Networking/AzureFrontDoorService"
   :azure/load-balancer               "azure/Networking/AzureLoadBalancer"
   :azure/traffic-manager             "azure/Networking/AzureTrafficManager"
   :azure/vpn-gateway                 "azure/Networking/AzureVPNGateway"
   :azure/virtual-network             "azure/Networking/AzureVirtualNetwork"
   :azure/virtual-wan                 "azure/Networking/AzureVirtualWAN"
   :azure/key-vault                   "azure/Security/AzureKeyVault"
   :azure/sentinel                    "azure/Storage/AzureSentinel"
   :azure/blob-storage                "azure/Storage/AzureBlobStorage"
   :azure/data-box                    "azure/Storage/AzureDataBox"
   :azure/data-lake-storage           "azure/Storage/AzureDataLakeStorage"
   :azure/file-storage                "azure/Storage/AzureFileStorage"
   :azure/managed-disks               "azure/Storage/AzureManagedDisks"
   :azure/net-app-files               "azure/Storage/AzureNetAppFiles"
   :azure/queue-storage               "azure/Storage/AzureQueueStorage"
   :azure/stor-simple                 "azure/Storage/AzureStorSimple"
   :azure/storage                     "azure/Storage/AzureStorage"}
  )

(defmulti render-diagram
  "Renders a diagram"
  exp/export-format)