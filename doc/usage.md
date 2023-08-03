Overarch Usage
==============

Overview
--------

The usage of Overarch is twofold. On the one hand, it is an open data format
to describe the functional requirements, architecture, and design
of software systems. On the other hand it is a tool to transform the description into diagrams or other representations.

Overarch can be used as a CLI tool to convert specified models and diagrams
into different formats, e.g. the rendering of diagrams in PlantUML or the
conversion of the data to JSON.


Modelling
---------

Overarch supports modelling the functional requirements, the architecture and the design of the system under description.

Modelling a system with overarch should provide a value for the project and
this guides the selection of model elements and supported abstractions and views.

The model contains all the elements relevant in the architecture of the system.
Models are specified in the Extensible Data Notation (EDN).

### EDN Basics
The Extensible Data Notation EDN is a data notation with a rich set of
literals for scalar and composite data types. It is also a subset of the
Clojure language textual format. Therefore Clojure plugins/extensions for
editors or IDEs provide syntax checking/highlighting and code completion.

Compared to JSON which supports literals for numbers, strings, vectors and
maps, EDN provides a richer set of data literals, e.g. integer and floating
point numbers, big integers and decimals, strings, symbols, keywords,
UUIDs and instants of time.
It also provides literals for list, vectors (arrays), sets and maps.

The following literals are used in Overarch models and views.

#### Strings
Strings are used e.g. as names and descriptions of model elements and for the
title of views.

```clojure
"This is a string"

"This is
a multiline
string"
```

#### Keywords
Keywords are used as keys in the maps for model elements and views. They are also
used as identifiers for model elements and views.

Keywords start with a colon (':'), have an optional namespace followed by a
slash ('/') and a mandatory name, e.g. :namespace/name.

Keywords should be prefixed with a namespace to avoid collisions with keywords for
other models, which is especially relevant for identifiers or for custom keys
in the model elements and views.

```clojure
:keyword
:namespaced/keyword
```

#### Sets
Sets are unordered collections of elements without duplicates. They are used as
top level collections for the model elements and views. They are also used
as a container for the children of model elements.

```clojure
#{"a" "b" "c"}
```

#### Maps
Maps are associative collections of key/value pairs. They are used to describe
the attributes of model elements and views.

```clojure
{:firstname "John" :lastname "Doe" :age 42}
```

#### Vectors
Vectors are ordered collections of elements which may contain an element
multiple times. They are used for the elements as content of a view because
the ordering of the elements may be relevant for the rendering of the view
(e.g. in PlantUML).

```clojure
[1 2 3 4]
["John" "Doe"]
```

As you can see in the example models, all collection literals can be nested.

For more information see the [EDN specification](https://github.com/edn-format/edn).

### Examples
The model and diagram descriptions of the C4 model banking example can be
found in models/banking folder:
 * [model.edn](/models/banking/model.edn)
 * [views.edn](/models/banking/views.edn)

If you have a Clojure environment in some editor or IDE, just use it.
If not, try Visual Studio Code with the Calva and PlantUML extensions.
With this setup you get an editor for the EDN files with code completion,
syntax check and syntax highlighting.

![Model editing](/doc/images/overarch_vscode_model.png)


Models
------

The top level element in a model EDN file is a set which contains the top level
model elements. Model elements are denoted as maps in the EDN file.
All model elements have at least two keys, **:el** for the type of the
element and **:id** for the identifier. The identifiers should be namespaced
keywords, so that different models can be composed without collisions of the
identifiers.

### Common Keys

key       | type    | values             | description 
----------|---------|--------------------|------------
:el       | keyword | see model elements | type of the model element
:id       | keyword | namespaced id      | id of the element
:name     | string  |                    | name of the element
:desc     | string  |                    | description of the element
:ct       | set     | model elements     | the children of the model element



### Architecture and Deployment Model Elements
Overarch supports elements for C4 architecture and deployment models.

#### Logical Data Model for the Architecture and Deployment Model Elements
![Architecture and Deployment Model Elements](/doc/images/overarch_architectureModelElementsOverview.svg)



#### Additional Keys for Architecture Model Elements

key       | type    | values             | description 
----------|---------|--------------------|------------
:subtype  | keyword | :database, :queue  | specific role of the element
:external | boolean | true false         | default is false
:tech     | string  |                    | technology of the element


#### Person (:person)
Persons are internal or external actors of the system.

```
{:el :person
 :id :banking/personal-customer
 :name "Personal Banking Customer"
 :desc "A customer of the bank, with personal banking accounts."}
```

#### System (:system)
A System is the top level element of the C4 model an can contain a set of
containers. Systems can be internal or external to the project context.
The structure of internal systems is modelled with containers.

#### Container (:container)
A container is a part of a system. It represents a process of the system
(e.g. an executable or a service). Containers are composed of a set of
components.

#### Component (:component)
A component is unit of software, which lives in a container of the system.

#### Node (:node)
A node is a unit in a deployment view. Nodes represent parts of the
infrastructure in which the containers of the system are deployed. They can
contain a set of other nodes or containers.

#### Relation (:rel)
Relations describe the connections and interactions of the parts of a view.

#### Reference (:ref)
References refer to a model element with the given id. They are primarily used
to refer to the model elements to include in views. They can also be used to
refer to model elements in other model elements, e.g. to split a huge hierarchical
systems into multiple EDN files.

References can have other keys, which are merged with the referred element in
context of the reference. For example you can mark an internal system as external
in the context of a specific view by adding `:external true` to the reference.

#### Boundaries
Boundaries group related elements and are normally rendered as a dashed box in a view. There are currently 4 types of boundaries, two of them implicit.

The implicit boundaries are the system boundary and the container boundary.
They are not modelled explicitly but are rendered for referenced systems and containers in specific views. A system boundary is rendered, when an internal system with containers as content is referenced in a container view or component view. Likewise a container boundary is rendered for a referenced container in a component view.

The explicit boundaries are enterprise boundary and context boundary.
These are explicitly modelled. An enterprise boundary {:el :enterprise-boundary}
can be used to group systems by enterprise or company. A context boundary
{:el :context-boundary} can be used to group containers or components by some
common context, especially by domain contexts in the sense of domain
driven design.

#### Example
Example (exerpt from the [banking model](/models/banking/model.edn) containing context and container
level elements):

```clojure
#{{:el :person
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
  :name "Sends e-mail to"}} 
 ```

### Concept Model Elements

A concept model captures relevant concepts of the domain(s) of the system.
The concepts could be part of the ubiquous language of the systems domain.

A concept model can contain the concepts of the domain and the high level elements
of the architecture model, e.g. the persons (actors), external systems and the
system itself with it's containers.

### Use Case Model Elements

A use case model captures the functionality a system is suposed to deliver.
High level use cases provide an overview of this functionality and may link
to business processes, domain stories and arcitectural elements.

As such they provide a pivot for the traceability from business processes into
the design of the system.

#### Logical Data Model for the Use Case Elements
![Use Case Model Elements](/doc/images/overarch_useCaseModelElementsOverview.svg)

#### Example
Example [Use Case Model](/models/usecase/model.edn)

#### Use Cases (:use-case)

A use case describes the goal of an actor in the context of the system described. The goal can be a concrete user goal, a high level summary of user goals or a subfunction of a user goal. This is captured by the :level key.


key         | type    | values                           | description 
------------|---------|----------------------------------|------------
:level      | keyword | :summary :user-goal :subfunction | specific role of the element
:ext-points | string  |                                  | eextension points of a use case

#### Actors (:person, :system, :actor)

Persons and systems from the architecture model should be used as actors in the
use case model to provide a connection between the architecture model and the use case model.

You can use the :actor element to model actors not present as persons or systems in the architectural model, but this should be avoided if possible.
A reason for an :actor element might be the introduction of a time actor to
model the scheduling of use cases.

#### Relations (:uses :include :extends :generalizes)

Relations connect actors to the use cases or use cases with other use cases.
Use case models support different kinds of relations.

### State Machine Model Elements

A state model describes a state machine which can be used to model the states
a system component can be in and the transition from one state to the next state based on the events the system receives as input.

#### Logical Data Model for the State Machine Elements
![State Machine Elements](/doc/images/overarch_stateMachineElementsOverview.svg)

#### Example
Example [State Model](/models/state/model.edn)

#### State Machine (:state-machine)
A state machine is the root element for a state machine view. It contains the set of states and transistions as value of the *:ct* key.

#### States (:state, :start-state, :end-state)
A simple state machine has at least one start state, some normal states to model the different states a system can be in, and at least one end state.

A start state starts the state machine and an end state terminates the state machine.

States can be compound, they can have an internal state machine. This is modelled as a set of states and transitions in the *:ct* key, analog to the state machine itself.

#### Transitions (:transition)
A transition connects two states and models the input that leads to the transition from the current state (:from) to the next state (:to).


#### Forks and Joins (:fork-state, :join-state)
You can split a transition to trigger multiple new states with a fork state.
A fork has a single input transition and multiple output transitions.

To join multiple transitions after a fork a join state is used. A join has multiple input transitions and a single output transition. 


### Class Model Elements

A class model captures the static structure of the code.

The abstraction level of a class model is not very high compared to the actual
implementation. Therfore modelling and updating a complete class model is not
of much value. But class models of the domain can be very valuable as a means of communication between domain experts and developers to shape and document
the domain model for a bounded context.

#### Logical Data Model for the Class Model Elements
![Class Model Elements](/doc/images/overarch_classModelElementsOverview.svg)

#### Packages/Namespace (:package, :namespace)
Packages and namespaces provide a hierarchical structure for the organisation
of the elements of the class model.

Use what suits your system best.


#### Interfaces/Protocols (:interface, :protocol)
Interfaces and protocols specify related methods. Interfaces also provide a
type for the static type system.
Use what suits your system best.

#### Class (:class)
A class in object orientation is a typed element that encapsulates state and
behaviour. The state is modelled with fields, the behaviour with methods.

In functional programming, you can use classes to model the values of your
system.

#### Enumeration (:enum)
An enumeration is a typed enumeration of values.

#### Field (:field)
A field is part of the state of a class.

#### Method (:method)
A method is part of the behaviour of a class or an interface.

#### Function (:function)

#### Relations (:association :aggregation, :composition :inheritance :implementation :dependency)


Views
-----
To show model elements in diagrams or in textual representations you can define
different kind of views. The kind of view defines the visual rendering of the
elements and the kind of elements and relations that are shown.

In a specific view you can reference the model elements you want to include in
this view. A Model element can be included in as many views as you want, but the
element has to match the expected kinds of model elements to be shown. 
For example, a system landscape view renders person and system elements but no
use cases or state machines, even if they are referenced in the view. Please consult
the models for the model and view elements.

#### Logical Data Model for the View Elements
![View Elements](/doc/images/overarch_viewElementsOverview.svg)


### Architecture and Deployment Views (C4 Model)

Overarch supports the description of all C4 core and supplementary views
except from code views, which ideally should be generated from the code
if needed. The core C4 views form a hierarchy of views.

See [c4model.com](https://c4model.com) for the rationale and detailed
information about the C4 Model.

The views can reference elements from the model as their content. The
content of a view should be a list instead of a set because the order
of elements is relevant in a view. 



#### Keys

key       | type    | values                   | description 
----------|---------|--------------------------|------------
:el       | keyword | see views                | type of the view
:id       | keyword | namespaced id            | used for export file name
:title    | string  |                          | rendered title
:spec     | map     |                          | rendering customization (e.g. styling)
:ct       | list    | model refs (or elements) | view specific keys possible


#### System Context Views (:context-view)
Shows the system in the context of the actors and other systems it is
interacting with. Contains users, external systems and the system to be
described.

![System Context View rendered with PlantUML](/doc/images/banking_systemContextView.svg)

#### Container Views (:container-view)
Shows the containers (e.g. processes, deployment units of the system) and
the interactions between them and the outside world. Contains the elements
of the system context diagram and the containers of the system to be described.
The system to be described is rendered as a system boundary in the container
diagram.

![Container View rendered with PlantUML](/doc/images/banking_containerView.svg)

#### Component Views (:component-view)
Shows the components and their interactions inside of a container and with
outside systems and actors.

![Component View rendered with PlantUML](/doc/images/banking_apiComponentView.svg)

#### C4 Code Views
A C4 code view is not supported, the level of abstraction for implementation details
is usually not high enough to justify modelling implemeted code. Also the speed of
change in the code is most likely to high and renders a code model obsolete fast.
If you want to visualize existing code, you can use the features of your IDE to generate
a diagram of it.

On the other hand it can be useful to create a view of code not yet implemented.
UML class view can be used to model a domain and communicate a design. See UML views for that. 

#### System Landscape Views (:system-landscape-view)
The system landscape view shows a high level picture, a broader view of the system landscape and the interactions of the systems.

![System Landscape View rendered with PlantUML](/doc/images/banking_systemLandscapeView.svg)

#### Deployment Views (:deployment-view)
The deployment view shows the infrastucture and deployment of the containers of the system.

![Deployment View rendered with PlantUML](/doc/images/banking_deploymentView.svg)

#### Dynamic Views (:dynamic-view)
Shows the order of interactions. The relations get numerated in the given order and the nuber is rendered in the diagram.

### UML Views
Overarch supports selected UML views to show aspects of a system that are not covered by the C4
Model.

#### Use Case View
A use case view is used to show the actors of the system under design and their
goals using this system.

![Use Case View rendered with PlantUML](/doc/images/banking_useCaseView.svg)

#### State Machine Views
A state machine view is used to show the different states a component can be in. It also shows the
transitions between these states based on the input events, the component receives. 

#### Class Views
A class view is used to show the design of parts of the software. You can use it e.g. to model a
domain and to communicate the model with domain experts.

### Textual Views
Overarch also supports textual views as part of the documentation of the system. Textual views are
used to leverage the information present in the model.

#### Glossary View
The glossary view is a sorted list of elements with their type and their descriptions.

### Styling
Overarch supports custom styles for elements. For an example see [views.edn](/models/test/views.edn).

#### Keys

key           | type    | values                   | description 
--------------|---------|--------------------------|------------
:id           | keyword | namespaced id            | used to reference styles
:el           | keyword | :rel, :element           | element type to be styled
:line-style   | keyword | :dashed, :dotted, :bold  | for relations
:line-color   | hex rgb | #0000FF for bright blue  | 
:border-color | hex rgb | #FF0000 for bright red   |
:text-color   | hex rgb | #003300 for dark green   |
:legend-text  | string  |                          | meaningful text to show in legend


Exports
-------

### JSON
The model and view descriptions can be exported to JSON to make their content
available to languages for which no EDN implementation exists.
The export converts each EDN file to JSON.

### PlantUML
The the specified views can be exported to PlantUML C4 diagrams.
These can be rendered into different formats (e.g. SVG, PNG, PDF) with PlantUML.

You can specify PlantUML specific directives with the **:plantuml** key of a
view spec.

```
   :spec {:plantuml {:sprite-libs [:azure]}}
```

### Markdown

#### Sprite Support
Overarch supports PlantUML sprites to show a visual cue of the technology in
the elements of a diagram.
It does so by matching the value of the **:tech** key of a model element to
the list of sprites. You can also directly add a **:sprite** key to the
reference of a model element in a view. The explicit **:sprite** value takes
precedence over the **:tech** value.

The list of sprites contains sprites of the PlantUML standard library, e.g.
sprites for AWS and Azure. The mapping files from tech name to sprite
reside in [resources/plantuml](/resources/plantuml). 

The command line interface supports the option `--plantuml-list-sprites`
which prints the (long) list of sprite mappings. 


#### Rendering PlantUML diagrams
The Visual Studio Code PlantUML Extension allows previewing and exporting these
diagrams right from the IDE.

![PlantUML preview](/doc/images/overarch_plantuml_preview.png)

PlantUML plugins also exists for major IDEs and build tools (e.g. IntelliJ, Eclipse, Maven, Leiningen).

* [PlantUML Plugin for Leiningen](https://github.com/vbauer/lein-plantuml)

### Structurizr (*experimental*)
Structurizr is a tool set created by Simon Brown.
The structurizr export creates a workspace with the loaded model and views.


Command Line Interface
----------------------

Usage:

```
java -jar overarch.jar [options]
```

Overarch currently supports these options

```
Options:

  Option                       Default   Description

  -m, --model-dir DIRNAME      models    Model directory
  -e, --export-dir DIRNAME     export    Export directory
  -w, --watch                            Watch model dir for changes and trigger export
  -f, --format FORMAT          plantuml  Export format (json, plantuml, structurizr)
      --model-info                       Returns infos for the loaded model
      --plantuml-list-sprites            Lists the loaded PlantUML sprites
      --debug                            Print debug messages
  -h, --help                             Print help
 ```


