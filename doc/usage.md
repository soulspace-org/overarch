Overarch Usage
==============

Overview
--------
Overarch can be used as a CLI tool to convert specified models and diagrams into different formats,
e.g. the rendering of diagrams in PlantUML or the conversion of the data to JSON.

Modelling
---------
If you have a clojure environment in some editor or IDE, just use it.
If not, try Visual Studio Code with the Calva and PlantUML extensions.
With this setup you get an editor for the EDN files with code completion,
syntax check and syntax highlighting.

![Model editing](/doc/overarch_vscode_model.png)

### Examples
The model and diagram descriptions of the C4 model banking example can be found in models/banking folder:
 * [model.edn](/models/banking/model.edn)
 * [diagrams.edn](/models/banking/diagrams.edn)


Models
------

THe model contains all the elements relevant in the architecture of the system.
Models are specified in the Extensible Data Notation (EDN).
EDN is a data notation like JSON but with richer data type support.
Unlike JSON, EDN also supports sets in addition to lists and maps.
EDN also supports more primitive types like UUIDs, instants of time, symbols and keywords.

Especially keywords are useful as keys in a map or as identifiers.
Keywords start with a colon (':'), have an optional namespace followed by a slash ('/')
and a mandatory name, e.g. :namespace/name.

The top level element in a model EDN file is a set. It contains the top level model elements.
Model elements are denoted as maps in the EDN file.
All model elements have at least two attributes, :el for the type of the element
and :id for the identifier. Ids should be namespaced keywords, so that different models
can be composed without collisions of the identifiers.


### Model Elements

#### Persons
Users are internal or external actors of the system.

#### Systems
A System is the top level element of the C4 model an can contain a set of containers.

#### Containers
A container is a part of a system. It represents a process of the system (e.g. an executable or a service)
Containers are composed of a set of components.

#### Components
A component is unit of software, which lives in a container of the system.

#### Nodes
A node is a unit in a deployment view. Nodes represent parts of the infrastructure in which
the containers of the system are deployed. They can contain a set of other nodes or containers.

#### Relations
Relations describe the interacions of the parts of a view.


Example (exerpt from the banking model containing context and container level elements):

```clojure
[{:el :person
  :id :banking/personal-customer
  :name "Personal Banking Customer"
  :desc "A customer of the bank, with personal banking accounts."}
 {:el :system
  :id :banking/internet-banking-system
  :name "Internet Banking System"
  :desc "Allows customers to view information about their bank accounts and make payments."
  :ct #{{:el :container
        :id :banking/web-app
        :name "Web Application"
        :desc "Deliveres the static content and the internet banking single page application."
        :tech "Clojure and Luminus"}
       {:el :container
        :id :banking/single-page-app
        :name "Single-Page Application"
        :desc "Provides all of the internet banking functionality to customers via their web browser."
        :tech "ClojureScript and Re-Frame"}
       {:el :container
        :id :banking/mobile-app
        :name "Mobile App"
        :desc "Provides a limited subset of the internet banking functionality to customers via their mobile device."
        :tech "ClojureScript and Reagent"}
       {:el :container
        :id :banking/api-application
        :name "API Application"
        :desc "Provides internet banking functionality via a JSON/HTTPS API."
        :tech "Clojure and Liberator"}
       {:el :container
        :subtype :database
        :id :banking/database
        :name "Database"
        :desc "Stores the user registration information, hashed authentication credentials, access logs, etc."
        :tech "Datomic"}}}
 {:el :system
  :id :banking/mainframe-banking-system
  :external true
  :name "Mainframe Banking System"
  :desc "Stores all the core banking information about customers, accounts, transactions, etc."}
 {:el :system
  :id :banking/email-system
  :external true
  :name "E-mail System"
  :desc "The internal Microsoft Exchange email system."}

 ; Context level relations 
 {:el :rel
  :id :banking/personal-customer-uses-internet-banking-system
  :from :banking/personal-customer
  :to :banking/internet-banking-system
  :name "Views account balances and makes payments using"}
 {:el :rel
  :id :banking/internet-banking-system-uses-email-system
  :from :banking/internet-banking-system
  :to :banking/email-system
  :name "Sends e-mail using"}
 {:el :rel
  :id :banking/internet-banking-system-using-mainframe-banking-system
  :from :banking/internet-banking-system
  :to :banking/mainframe-banking-system
  :name "Gets account information from, and makes payments using"}
 {:el :rel
  :id :banking/email-system-sends-mail-to-personal-customer
  :from :banking/email-system
  :to :banking/personal-customer
  :name "Sends e-mail to"}] 
 ```


Diagrams
--------

### C4 Diagrams
Overarch supports the description of all C4 core and sublementary diagrams except from code diagrams, which ideally should be generated from the code if needed.

The core C4 diagrams form a hierarchy.

#### System Context Diagrams
Shows the system in the context of the actors and other systems it is interacting with. Contains users, external systems and the system to be described.

![System Context View rendered with PlantUML](/doc/banking_systemContextView.svg)

#### Container Diagrams
Shows the containers (e.g. processes, deployment units of the system) and the interactions between them and the outside world. Contains the elements of the system context diagram and the containers of the system to be described. The system to be described is rendered as a system boundary in the container diagram.

![Container View rendered with PlantUML](/doc/banking_containerView.svg)

#### Component Diagrams
Shows the components and their interactions inside of a container.

![Component View rendered with PlantUML](/doc/banking_apiComponentView.svg)

#### Code Diagrams
Not supported

#### System Landscape Diagrams
Shows a broader system landscape and the interactions of the systems.

![System Landscape View rendered with PlantUML](/doc/banking_systemLandscapeView.svg)

#### Deployment Diagrams
Shows the infrastucture and deployment of the containers of the system.

![Deployment View rendered with PlantUML](/doc/banking_deploymentView.svg)

#### Dynamic Diagrams
Shows the order of some interactions.

### Styling



Exports
-------

### JSON
The model and diagram descriptions can be exported to JSON to make their content
available to languages for which no EDN implementation exists.

### PlantUML
The the specified views can be exported to PlantUML C4 diagrams.
These can be rendered into different formats (e.g. SVG, PNG, PDF) with PlantUML.

The Visual Studio Code PlantUML Extension allows previewing and exporting these
diagrams right from the IDE.

![PlantUML preview](/doc/overarch_plantuml_preview.png)

PlantUML plugins also exists for major IDEs and build tools (e.g. IntelliJ, Eclipse, Maven, Leiningen).


* [PlantUML Plugin for Leiningen](https://github.com/vbauer/lein-plantuml)

### Structurizr




Command Line Interface
----------------------

Usage:

```
java -jar overarch.jar
```

Overarch currently supports these options

```
Options:

  -m, --model-dir DIRNAME   models    Model directory
  -e, --export-dir DIRNAME  export    Export directory
  -f, --format FORMAT       plantuml  Export format (json, plantuml)
  -h, --help                          Print help
      --debug                         Print debug messages
```



Integration
-----------


