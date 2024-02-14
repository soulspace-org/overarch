![overarch - Image © 2019 Ludger Solbach](/doc/images/overarch.jpg)

Overarch
========

A data driven description of software architecture based on UML and the C4
model.

Describe your model as data and specify/generate representations (e.g.
diagrams) for your model. All core and supplementary C4 diagrams except code
diagrams are supported. Also UML use case, state machine and class diagrams
are supported.

Overarch is not so much about how to model your architecture (see
[C4 Model](https://c4model.com) for that), but about making these models
composable and reusable.

[![Clojars Project](https://img.shields.io/clojars/v/org.soulspace.clj/overarch.svg)](https://clojars.org/org.soulspace.clj/overarch)
[![cljdoc badge](https://cljdoc.org/badge/org.soulspace.clj/overarch)](https://cljdoc.org/d/org.soulspace.clj/overarch)
![GitHub](https://img.shields.io/github/license/soulspace-org/overarch)



Features
--------

* models and views as data
  * C4 models and views
  * UML use case, state machine and class models and views
  * Concept models, concept maps and glossaries
  * separation of models and views
  * hierarchical models and element references
  * view specific customization of model elements
  * extensible format
* exports
  * JSON if you need to process models with languages without EDN support
  * PlantUML
    * all C4 views (except code view)
    * use case, state machine and class diagrams
    * styling and sprite support
  * GraphViz
    * Concept maps
  * Markdown
    * Glossary, textual representations of graphical views
  * Structurizr *experimental*
* watch model directory for changes


Rationale
---------

UML and C4 models are great to vizualize an architecture on different levels
of detail with the various diagrams types. The value lies in an expressive
description and visualization of an architecture with different views.

But the models used for diagram generation with the existing tools are not
models in the sense of generality. Especially if you describe your model in
PlantUML files, these descriptions are mere textfiles.

The textfiles don't compose and you can't do anything else with these
descriptions other than render them with PlantUML. The parsing process is
opaque and you don't have access to the data of the model.

Also the model is complected with the diagrams, as layout and rendering
information is part of the model description and vice versa. The model should
capture the essence of the architecture and not its representation.

If the model is described as plain *data* in an open format, it can be
transformed into a graphical representation, e.g. into PlantUML textfiles, via
the specification of views on the model.

In Overarch the model data is separated from information about these representations. Models can be composed with these views and with other models. By doing so, the model may also be used in other ways, e.g. the generation of documentation, code or infrastructure.

Even if the model is specified as data, the format should be a text file (EDN,
JSON) to be easily edited with text editors by the whole team and to be
committed to version control, instead of being in some propriatory binary
format.

The native format is the Extensible Data Notation (EDN) with representations
in other formats like JSON. EDN is a textual format for data, which is human
readable. It is also directly readable into data structures in clojure or java
code.

The data format is open for extension. E.g. it copes with additional attributes or element types in the data structures.

The model should describe the architecture (the structure) of your system(s).
The elements are based on UML and the C4 model and are a hierarchical composition of the elements of the architecture.

Model references are used to refer to model elements from other models and
representations (e.g. diagrams). To allow references to elements and  relations, they must be given an id.

Model references may be enhanced with additional attributes that are specific
to the usage context (e.g. a style attribute in the context of a diagram)


Examples
--------

This is an example of the specification of a model and some diagrams based on
the Internet Banking System example of Simon Brown at [C4 Model](https://c4model.com).

The complete model and diagram specifications can be found under
[models/banking](/models/banking).

Further information about modelling with *Overarch* can be found in [Usage](doc/usage.md).

### Example of a model definition

```clojure
#{; actors
  {:el :person
  :id :banking/personal-customer
  :name "Personal Banking Customer"
  :desc "A customer of the bank, with personal banking accounts."}
 ; system under design
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
 ; external systems
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

 ; Context view relations 
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
  :name "Sends e-mail to"}} 
 ```

### Example of a views specification

```clojure
#{{:el :context-view
  :id :banking/system-context-view
  :title "System Context View of the Internet Banking System"
  :ct [; model elements
       {:ref :banking/personal-customer}
       {:ref :banking/email-system}
       {:ref :banking/mainframe-banking-system}
       {:ref :banking/internet-banking-system}
       
       ; relations
       {:ref :banking/personal-customer-uses-internet-banking-system :direction :down}
       {:ref :banking/internet-banking-system-uses-email-system :direction :right}
       {:ref :banking/internet-banking-system-using-mainframe-banking-system}
       {:ref :banking/email-system-sends-mail-to-personal-customer :direction :up}]}

 {:el :container-view
  :id :banking/container-view
  :title "Container View of the Internet Banking System"
  :ct [; model elements
       {:ref :banking/personal-customer}
       {:ref :banking/internet-banking-system}
       {:ref :banking/email-system}
       {:ref :banking/mainframe-banking-system}

       ; relations
       {:ref :banking/email-system-sends-mail-to-personal-customer :direction :up}
       {:ref :banking/personal-customer-uses-web-app}
       {:ref :banking/personal-customer-uses-single-page-app}
       {:ref :banking/personal-customer-uses-mobile-app}

       {:ref :banking/web-app-deliveres-single-page-app :direction :right}
       {:ref :banking/single-page-app-calls-api-application}
       {:ref :banking/mobile-app-calls-api-application}
       {:ref :banking/api-application-uses-database :direction :left}
       {:ref :banking/api-application-uses-email-system :direction :right}
       {:ref :banking/api-application-uses-mainframe-banking-system}
       ]}
}
 ```

### PlantUML export of the System Context View
```plantuml
@startuml banking_systemContextView
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

title System Context View of the Internet Banking System

Person(banking_personalCustomer, "Personal Banking Customer", $descr="A customer of the bank, with personal banking accounts.")
System_Ext(banking_emailSystem, "E-mail System", $descr="The internal Microsoft Exchange email system.")
System_Ext(banking_mainframeBankingSystem, "Mainframe Banking System", $descr="Stores all the core banking information about customers, accounts, transactions, etc.")
System(banking_internetBankingSystem, "Internet Banking System", $descr="Allows customers to view information about their bank accounts and make payments.")

Rel_Down(banking_personalCustomer, banking_internetBankingSystem, "Views account balances and makes payments using")
Rel_Right(banking_internetBankingSystem, banking_emailSystem, "Sends e-mail using")
Rel(banking_internetBankingSystem, banking_mainframeBankingSystem, "Gets account information from, and makes payments using")
Rel_Up(banking_emailSystem, banking_personalCustomer, "Sends e-mail to")

SHOW_LEGEND()
@enduml
```

### System Context View rendered with PlantUML
![System Context View rendered with PlantUML](/doc/images/banking_systemContextView.svg)

### Container View rendered with PlantUML
![Container View rendered with PlantUML](/doc/images/banking_containerView.svg)


Build
-----
Overarch is written in [Clojure](https://clojure.org) and gets built with
[leiningen](https://leiningen.org/). To build it, you need to have Java 11 or higher
and leiningen installed.

In the cloned overarch repository, run

```
lein uberjar
```

to build a JAR file with all dependencies. This JAR file is created in the *target* folder and is named *overarch.jar*


Installation
------------

### Visual Studio Code

If you have a clojure environment in some editor or IDE, just use it. Maybe a PlantUML plugin exists for this environment too.

If not, try Visual Studio Code with the **Calva** and **PlantUML** extensions.

![Calva Extension](/doc/images/vscode_calva_ext.png)
![PlantUML Extension](/doc/images/vscode_plantuml_ext.png)

With this setup you get an editor for the EDN files with code completion,
syntax check and syntax highlighting.

![Model editing](/doc/images/overarch_vscode_model.png)

You also get integrated previews of the exported PlantUML diagrams and the
ability to generate image files in different formats (e.g. PNG, SVG, PDF, ...)
directly from within Visual Studio Code.

![PlantUML preview](/doc/images/overarch_plantuml_preview.png)

PlantUML also needs an installation of [graphviz](https://graphviz.org/download/).
Please read the installation instructions in the PlantUML extension on how to
install graphviz for your operating system.

To get support for icons (PlantUML sprites) from the PlantUML standard library, a recent **plantuml.jar** is highly recommended. Please download it from [PlantUML Releases](https://github.com/plantuml/plantuml/releases) and reference it in the PlantUML extension settings.

![PlantUML Extension Settings](/doc/images/vscode_plantuml_ext_settings.png)


Usage
-----

Use a folder for all the data (e.g. models, view specifications) of a project.
Add EDN files for the model and the view specifications. All the EDN files in the folder will be loaded.

### Command Line Interface
Start the the *Overarch* CLI tool with java.

```
java -jar overarch.jar [options]
```

*Overarch* currently supports these options

```
Overarch CLI Exporter
   
  Reads your model and view specifications and renders or exports
  into the specified formats.

Usage:
  java -jar overarch.jar [options].

Options:
  -m, --model-dir PATH         models  Model directory or path
  -r, --render-format FORMAT           Render format (all, graphviz, markdown, plantuml)
  -R, --render-dir DIRNAME     export  Export directory
  -x, --export-format FORMAT           Export format (json, structurizr)
  -X, --export-dir DIRNAME     export  Export directory
  -w, --watch                          Watch model dir for changes and trigger action
      --model-warnings                 Returns warnings for the loaded model (default true)
      --model-info                     Returns infos for the loaded model (default false)
      --plantuml-list-sprites          Lists the loaded PlantUML sprites
  -h, --help                           Print help
      --debug                          Print debug messages
```

If you use Visual Studio Code as described above, you can start *Overarch* in watch mode from a terminal inside VS Code. Every time you save some changes in the EDN files, the views will be updated and previews can be rendered with the PlantUML extension.

Documentation
-------------

See [Usage](doc/usage.md) for additional information on modelling and usage of the *Overarch* CLI tool.

See [Design](doc/design.md) for information about the design of *Overarch*.


Plans
-----

Here are my current plans to enhance overarch in the next releases.

* enhanced conveniance in view specifications
  * automatic includes of elements e.g.
    * include relations for referenced elements
    * includes based on selection criteria
* provide relations between elements of the different models to model
  traceability information (e.g. from use cases to the containers implementing
  them or from domain concepts to domain model elements)
* markdown export for the textual information in views also rendered as diagrams
* theme support for diagrams


Copyright
---------

© 2023 Ludger Solbach


License
-------

Eclipse Public License 1.0 (EPL1.0)

