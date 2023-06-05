(ns org.soulspace.overarch.plantuml.sprites
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [charred.api :as csv]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.clj.string :as sstr]))

(comment
  ; include icon/sprite sets, if icons are used, e.g. 
  "!define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"
  "!define FONTAWESOME https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"
  "!include DEVICONS/angular.puml
   !include DEVICONS/java.puml
   !include DEVICONS/msql_server.puml
   !include FONTAWESOME/users.puml
   ")

(def sprite-libraries
  "Definition of sprite libraries."
  {:azure {:name "azure"
           :local-prefix "azure"
           :local-imports ["AzureCommon"
                           "AzureC4Integration"]
           :remote-prefix "AZURE"
           :remote-url "https://raw.githubusercontent.com/azure/"
           :remote-imports ["AzureCommon"
                            "AzureC4Integration"]}
   :aws {:name "awslib"
         :local-prefix "awslib14"
         :local-imports ["AWSCommon"
                         "AWSC4Integration"]
         :remote-prefix "AWS"
         :remote-url "https://raw.githubusercontent.com/awslib/"
         :remote-imports ["AWSCommon"
                          "AWSC4Integration"]}
   :devicons    {:name "devicons"
                 :local-prefix "devicons"
                 :remote-prefix "DEVICONS"
                 :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons"}
   :fontawesome {:name "fontawesome"
                 :local-prefix "fontawesome"
                 :remote-prefix "FONTAWESOME"
                 :remote-url "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5"}})

(def tech->sprite
  "Map of technology names to sprite infos."
  {"Azure Batch AI"                    {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "AzureBatchAI"}
   "Azure Bot Service"                 {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "AzureBotService"}
   "Azure Cognitive Services"          {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "AzureCognitiveServices"}
   "Azure Machine Learning Service"    {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "AzureMachineLearningService"}
   "Azure Machine Learning Studio"     {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "AzureMachineLearningStudio"}
   "Microsoft Genomics"                {:lib "azure"
                                        :path "AIMachineLearning"
                                        :name "MicrosoftGenomics"}
   "Azure Analysis Services"           {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureAnalysisServices"}
   "Azure Data Catalog"                {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureDataCatalog"}
   "Azure Data Explorer"               {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureDataExplorer"}
   "Azure Data Lake Analytics"         {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureDataLakeAnalytics"}
   "Azure Databricks"                  {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureDatabricks"}
   "Azure Event Hub"                   {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureEventHub"}
   "Azure Event Hub Cluster"           {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureEventHubCluster"}
   "Azure HDInsight Cluster"           {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureHDInsightCluster"}
   "Azure Log Analytics Workspace"     {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureLogAnalyticsWorkspace"}
   "Azure PowerBI Embedded"            {:lib "azure"
                                        :path "Analytics"
                                        :name "AzurePowerBIEmbedded"}
   "Azure Stream Analytics"            {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureStreamAnalytics"}
   "PowerBI"                           {:lib "azure"
                                        :path "Analytics"
                                        :name "PowerBI"}
   "Azure App Service"                 {:lib "azure"
                                        :path "Compute"
                                        :name "AzureAppService"}
   "Azure App Service Plan"            {:lib "azure"
                                        :path "Compute"
                                        :name "AzureAppServicePlan"}
   "Azure App Service Plan Linux"      {:lib "azure"
                                        :path "Compute"
                                        :name "AzureAppServicePlanLinux"}
   "Azure Availability Set"            {:lib "azure"
                                        :path "Compute"
                                        :name "AzureAvailabilitySet"}
   "Azure Batch"                       {:lib "azure"
                                        :path "Compute"
                                        :name "AzureBatch"}
   "Azure Batch Account"               {:lib "azure"
                                        :path "Compute"
                                        :name "AzureBatchAccount"}
   "Azure Cloud Service"               {:lib "azure"
                                        :path "Compute"
                                        :name "AzureCloudServices"}
   "Azure Container Service"           {:lib "azure"
                                        :path "Compute"
                                        :name "AzureContainerService"}
   "Azure Function"                    {:lib "azure"
                                        :path "Compute"
                                        :name "AzureFunction"}
   "Azure Mesh Application"            {:lib "azure"
                                        :path "Compute"
                                        :name "AzureMeshApplication"}
   "Azure Server Farm"                 {:lib "azure"
                                        :path "Compute"
                                        :name "AzureServerFarm"}
   "Azure Service Fabric"              {:lib "azure"
                                        :path "Compute"
                                        :name "AzureServiceFabric"}
   "Azure Spring Cloud"                {:lib "azure"
                                        :path "Compute"
                                        :name "AzureSpringCloud"}
   "Azure Virtual Machine"             {:lib "azure"
                                        :path "Compute"
                                        :name "AzureVirtualMachine"}
   "Azure Virtual Machine Scale Set"   {:lib "azure"
                                        :path "Compute"
                                        :name "AzureVirtualMachineScaleSet"}
   "Azure Container App"               {:lib "azure"
                                        :path "Containers"
                                        :name "AzureContainerApp"}
   "Azure Container Instance"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureContainerInstance"}
   "Azure Container Registry"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureContainerRegistry"}
   "Azure Kubernetes Service"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureKubernetesService"}
   "Azure Service Environment"         {:lib "azure"
                                        :path "Containers"
                                        :name "AzureServiceEnvironment"}
   "Azure Application Insights"        {:lib "azure"
                                        :path "Devops"
                                        :name "AzureApplicationInsights"}
   "Azure Artifacts"                   {:lib "azure"
                                        :path "Devops"
                                        :name "AzureArtifacts"}
   "Azure Boards"                      {:lib "azure"
                                        :path "Devops"
                                        :name "AzureBoards"}
   "Azure DevOps"                      {:lib "azure"
                                        :path "Devops"
                                        :name "AzureDevOps"}
   "Azure DevOps Organisation"         {:lib "azure"
                                        :path "Devops"
                                        :name "AzureDevOpsOrganisation"}
   "Azure Dev Test Labs"               {:lib "azure"
                                        :path "Devops"
                                        :name "AzureDevTestLabs"}
   "Azure Lab Services"                {:lib "azure"
                                        :path "Devops"
                                        :name "AzureLabServices"}
   "Azure Pipelines"                   {:lib "azure"
                                        :path "Devops"
                                        :name "AzurePipelines"}
   "Azure Repos"                       {:lib "azure"
                                        :path "Devops"
                                        :name "AzureRepos"}
   "Azure Test Plans"                   {:lib "azure"
                                        :path "Devops"
                                        :name "AzureTestPlans"}
   "Azure"                             {:lib "azure"
                                        :path "General"
                                        :name "Azure"}
   "App Configuration"                 {:lib "azure"
                                        :path "Management"
                                        :name "AppConfiguration"}
   "Azure Arc"                         {:lib "azure"
                                        :path "Management"
                                        :name "AzureArc"}
   "Azure Arc Machine"                 {:lib "azure"
                                        :path "Management"
                                        :name "AzureArcMachine"}
   "Azure Automation"                  {:lib "azure"
                                        :path "Management"
                                        :name "AzureAutomation"}
   "Azure Backup"                      {:lib "azure"
                                        :path "Management"
                                        :name "AzureBackup"}
   "Azure Blue Prints"                 {:lib "azure"
                                        :path "Management"
                                        :name "AzureBluePrints"}
   "Azure Compliance"                  {:lib "azure"
                                        :path "Management"
                                        :name "AzureCompliance"}
   "Azure Cost Alert"                  {:lib "azure"
                                        :path "Management"
                                        :name "AzureCostAlert"}
   "Azure Cost Analysis"               {:lib "azure"
                                        :path "Management"
                                        :name "AzureCostAnalysis"}
   "Azure Cost Budget"                 {:lib "azure"
                                        :path "Management"
                                        :name "AzureCostBudget"}
   "Azure Cost Management"             {:lib "azure"
                                        :path "Management"
                                        :name "AzureCostManagement"}
   "Azure Cost Management And Billing" {:lib "azure"
                                        :path "Management"
                                        :name "AzureCostManagementAndBilling"}
   "Azure Geo Recovery"                {:lib "azure"
                                        :path "Management"
                                        :name "AzureGeoRecovery"}
   "Azure Lighthouse"                  {:lib "azure"
                                        :path "Management"
                                        :name "AzureLighthose"}
   "Azure Log Analytics"               {:lib "azure"
                                        :path "Management"
                                        :name "AzureLogAnalytics"}
   "Azure Managed Application Center"  {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagedApplicationCenter"}
   "Azure Managed Applications"        {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagedApplications"}
   "Azure Management Group"            {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagementGroups"}
   "Azure Management Portal"           {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagementPortal"}
   "Azure Metrics"                     {:lib "azure"
                                        :path "Management"
                                        :name "AzureMetrics"}
   "Azure Monitor"                     {:lib "azure"
                                        :path "Management"
                                        :name "AzureMonitor"}
   "Azure Policy"                      {:lib "azure"
                                        :path "Management"
                                        :name "AzurePolicy"}
   "Azure Resource Group"              {:lib "azure"
                                        :path "Management"
                                        :name "AzureResourceGroups"}
   "Azure Scheduler"                   {:lib "azure"
                                        :path "Management"
                                        :name "AzureScheduler"}
   "Azure Site Recovery"               {:lib "azure"
                                        :path "Management"
                                        :name "AzureSiteRecovery"}
   "Azure Subscription"                {:lib "azure"
                                        :path "Management"
                                        :name "AzureSubscription"}
   "Azure Tag"                         {:lib "azure"
                                        :path "Management"
                                        :name "AzureTag"}
   "Azure Application Gateway"         {:lib "azure"
                                        :path "Networking"
                                        :name "AzureApplicationGateway"}
   "Azure Bastion"                     {:lib "azure"
                                        :path "Networking"
                                        :name "AzureBastion"}
   "Azure DDoS Protection"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureAzureDDoSProtection"}
   "Azure DNS"                         {:lib "azure"
                                        :path "Networking"
                                        :name "AzureDNS"}
   "Azure Express Route"               {:lib "azure"
                                        :path "Networking"
                                        :name "AzureExpressRoute"}
   "Azure Firewall"                    {:lib "azure"
                                        :path "Networking"
                                        :name "AzureFirewall"}
   "Azure Front Door Service"          {:lib "azure"
                                        :path "Networking"
                                        :name "AzureFrontDoorService"}
   "Azure IPAdress Space"              {:lib "azure"
                                        :path "Networking"
                                        :name "AzureIPAdressSpace"}
   "Azure Load Balancer"               {:lib "azure"
                                        :path "Networking"
                                        :name "AzureLoadBalancer"}
   "Azure NSG"                         {:lib "azure"
                                        :path "Networking"
                                        :name "AzureNSG"}
   "Azure Network Interface"           {:lib "azure"
                                        :path "Networking"
                                        :name "AzureNetworkInterface"}
   "Azure Private Link"                {:lib "azure"
                                        :path "Networking"
                                        :name "AzurePrivateLink"}
   "Azure Private Link Hub"            {:lib "azure"
                                        :path "Networking"
                                        :name "AzurePrivateLinkHub"}
   "Azure Private Link Service"        {:lib "azure"
                                        :path "Networking"
                                        :name "AzurePrivateLinkService"}
   "Azure Public IP Address"           {:lib "azure"
                                        :path "Networking"
                                        :name "AzurePublicIPAddress"}
   "Azure Subnet"                      {:lib "azure"
                                        :path "Networking"
                                        :name "AzureSubnet"}
   "Azure Traffic Manager"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureTrafficManager"}
   "Azure VPN Gateway"                 {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVPNGateway"}
   "Azure Virtual Network"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVirtualNetwork"}
   "Azure Virtual Network Peering"     {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVirtualNetworkPeering"}
   "Azure Virtual WAN"                 {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVirtualWAN"}
   "Azure Key Vault"                   {:lib "azure"
                                        :path "Security"
                                        :name "AzureKeyVault"}
   "Azure Sentinel"                    {:lib "azure"
                                        :path "Security"
                                        :name "AzureSentinel"}
   "Azure Blob Storage"                {:lib "azure"
                                        :path "Storage"
                                        :name "AzureBlobStorage"}
   "Azure Data Box"                    {:lib "azure"
                                        :path "Storage"
                                        :name "AzureDataBox"}
   "Azure Data Lake Storage"           {:lib "azure"
                                        :path "Storage"
                                        :name "AzureDataLakeStorage"}
   "Azure File Storage"                {:lib "azure"
                                        :path "Storage"
                                        :name "AzureFileStorage"}
   "Azure Managed Disks"               {:lib "azure"
                                        :path "Storage"
                                        :name "AzureManagedDisks"}
   "Azure Net App Files"               {:lib "azure"
                                        :path "Storage"
                                        :name "AzureNetAppFiles"}
   "Azure Queue Storage"               {:lib "azure"
                                        :path "Storage"
                                        :name "AzureQueueStorage"}
   "Azure Stor Simple"                 {:lib "azure"
                                        :path "Storage"
                                        :name "AzureStorSimple"}
   "Azure Storage"                     {:lib "azure"
                                        :path "Storage"
                                        :name "AzureStorage"}})

(defn capitalize-parts
  "Returns a version of the string with capitalized parts, separated by `separator`"
  [s separator]
  (->> (str/split s (re-pattern separator))
       (map str/capitalize)
       (str/join separator)))

; convert names correctly
(defn tech-name
  "Create a tech name from the sprite name s."
  [s]
  (-> s
      (sstr/camel-case-to-hyphen)
      (str/replace "_" "-")
      (str/replace "-" " ")
      (capitalize-parts " ")
      ))

(defn sprite?
  "Returns true if the icon-map contains an icon for the given technology."
  [tech]
  (tech->sprite tech))

(defn plantuml-imports
  "Returns a collection of vectors of all the PlantUML '*.puml' files in
   the given directory `dir` containing the path relative to the `dir`
   and the base name of the import file."
  [dir]
  (->> dir
       (file/all-files-by-extension "puml")
       (map (partial file/relative-path dir))
       (map (juxt file/parent-path file/base-name))
       ;(map file/base-name)
       ))

(defn write-csv
  "Writes the collection `coll` in CSV format to `file`."
  [file coll]
  (with-open [wrt (io/writer file)]
    (csv/write-csv wrt coll)))

(defn sprite-entry
  "Prepares sprite entry info from collection PlantUML imports."
  [x]
  (let [path-entries (str/split (first x) #"/")
        sprite-lib (first path-entries)
        sprite-path (str/join "/" (rest path-entries))
        sprite-name (last x)
        sprite-key (tech-name (last x))]
    {:key sprite-key
     :lib sprite-lib
     :path sprite-path
     :name sprite-name}))

; find max length of sprite keys and pad/indent maps
; use text instead of println with *out* binding
(defn write-sprite-map
  "Writes the collection `coll` in CSV format to `file`."
  [file coll]
  (let [max-length (reduce max 0 (map count coll))]
    (with-open [wrt (io/writer file)]
      (binding [*out* wrt]
        (println "{")
        (doseq [entry coll] 
          (println (str "  \"" (:key entry) "\""
                        (str/join (repeat (- max-length (count (:key entry))))) " "
                        " {:lib " (:lib entry)
                        " :path " (:path entry)
                        " :name " (:name entry) "}")
        (println "}")))))))

; use (io/resource ) or load sprite mapping from options config dir 
(defn load-tech-sprite-mapping
  ""
  [options]
  (if (:config-dir options)
    (println "TODO load from config dir")
    (edn/read-string (slurp (io/resource "azure.edn")))))

(comment
  (count "/home/soulman/devel/tmp/plantuml-stdlib")
  (write-csv "dev/stdlib.csv" (into [] (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib")))

  (count (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))

  (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))
  (write-sprite-map "spritemap.edn" (map sprite-entry (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/")))
  )