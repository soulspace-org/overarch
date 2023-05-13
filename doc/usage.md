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


Model Elements
--------------

#### Persons
Users are internal or external actors of the system.

#### Systems

Systems can contain containers.

#### Containers
A container is a part of the system.
Containers can contain components.

#### Components
A component is unit of software, which lives in a container of the system.

#### Nodes
A node is a unit in a deployment view. Nodes represent parts of the infrastructure in which the containers of the system are deployed. 
They can contain other nodes, (external) sytems and containers.

#### Relations
Relations describe the interacions of the parts of a view.


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

![Component View rendered with PlantUML](/doc/banking_componentView.svg)

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

Exports
-------

### PlantUML

VS Code PlantUML Extension

### Structurizr


### JSON



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


