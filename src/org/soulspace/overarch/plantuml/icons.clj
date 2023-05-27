(ns org.soulspace.overarch.plantuml.icons
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [charred.api :as csv]
            [org.soulspace.clj.java.file :as file]
            ))

(def azure-icons
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
   "Azure HDInsight"                   {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureHDInsight"}
   "Azure Stream Analytics"            {:lib "azure"
                                        :path "Analytics"
                                        :name "AzureStreamAnalytics"}
   "Azure App Service"                 {:lib "azure"
                                        :path "Compute"
                                        :name "AzureAppService"}
   "Azure Batch"                       {:lib "azure"
                                        :path "Compute"
                                        :name "AzureBatch"}
   "Azure Function"                    {:lib "azure"
                                        :path "Compute"
                                        :name "AzureFunction"}
   "Azure Service Fabric"              {:lib "azure"
                                        :path "Compute"
                                        :name "AzureServiceFabric"}
   "Azure Virtual Machine"             {:lib "azure"
                                        :path "Compute"
                                        :name "AzureVirtualMachine"}
   "Azure Virtual Machine Scale Set"   {:lib "azure"
                                        :path "Compute"
                                        :name "AzureVirtualMachineScaleSet"}
   "Azure Container Instance"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureContainerInstance"}
   "Azure Container Registry"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureContainerRegistry"}
   "Azure Kubernetes Service"          {:lib "azure"
                                        :path "Containers"
                                        :name "AzureKubernetesService"}
   "Azure Service Fabric Mesh"         {:lib "azure"
                                        :path "Containers"
                                        :name "AzureServiceFabricMesh"}
   "Azure Web App For Containers"      {:lib "azure"
                                        :path "Containers"
                                        :name "AzureWebAppForContainers"}
   "Azure"                             {:lib "azure"
                                        :path "General"
                                        :name "Azure"}
   "Azure Automation"                  {:lib "azure"
                                        :path "Management"
                                        :name "AzureAutomation"}
   "Azure Backup"                      {:lib "azure"
                                        :path "Management"
                                        :name "AzureBackup"}
   "Azure Blue Prints"                 {:lib "azure"
                                        :path "Management"
                                        :name "AzureBluePrints"}
   "Azure Managed Applications"        {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagedApplications"}
   "Azure Log Analytics"               {:lib "azure"
                                        :path "Management"
                                        :name "AzureLogAnalytics"}
   "Azure Management Group"            {:lib "azure"
                                        :path "Management"
                                        :name "AzureManagementGroups"}
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
   "Azure Application Gateway"         {:lib "azure"
                                        :path "Networking"
                                        :name "AzureApplicationGateway"}
   "Azure DDoS Protection"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureAzureDDoSProtection"}
   "Azure DNS"                         {:lib "azure"
                                        :path "Networking"
                                        :name "AzureDNS"}
   "Azure Express Route"               {:lib "azure"
                                        :path "Networking"
                                        :name "AzureExpressRoute"}
   "Azure Front Door Service"          {:lib "azure"
                                        :path "Networking"
                                        :name "AzureFrontDoorService"}
   "Azure Load Balancer"               {:lib "azure"
                                        :path "Networking"
                                        :name "AzureLoadBalancer"}
   "Azure Traffic Manager"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureTrafficManager"}
   "Azure VPN Gateway"                 {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVPNGateway"}
   "Azure Virtual Network"             {:lib "azure"
                                        :path "Networking"
                                        :name "AzureVirtualNetwork"}
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

(comment
  (count "/home/soulman/devel/tmp/plantuml-stdlib")
  (write-csv "dev/stdlib.csv" (into [] (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib")))
  
  (count (plantuml-imports "/home/soulman/devel/tmp/plantuml-stdlib/"))
  )