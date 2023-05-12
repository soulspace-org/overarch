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


Model Elements
--------------

#### Persons

Users, internal or external actors for the system.

#### Systems

Can containers

#### Containers

Can components

#### Components

#### Nodes

Can contain other nodes, (external) sytems and containers.

#### Relations



Diagrams
--------

### C4 Diagrams

#### System Context Diagrams

#### Container Diagrams

#### Component Diagrams

#### Code Diagrams

#### System Landscape Diagrams

#### Deployment Diagrams

#### Dynamic Diagrams


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


