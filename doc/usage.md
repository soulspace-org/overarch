# Overarch Usage Overview

The usage of Overarch is twofold. On the one hand, it is an open data format
to describe the functional requirements, architecture, and design
of software systems. On the other hand it is a tool to transform the
description into diagrams or other representations.

Overarch can be used as a CLI tool to convert specified models and diagrams
into different formats, e.g. the rendering of diagrams in PlantUML or the
conversion of the data to JSON.

![Use Cases of Overarch](/doc/images/overarch_overarchUseCaseView.svg)

# Modelling

Overarch supports modelling the functional requirements, the architecture and
the design of the system under description.

Modelling a system with overarch should provide a value for the project and
this guides the selection of model elements and supported abstractions and
views.

The model contains all the elements relevant in the architecture of the system.
Models are specified in the
[Extensible Data Notation (EDN)](https://github.com/edn-format/edn).

## EDN Basics
The Extensible Data Notation EDN is a data notation with a rich set of
literals for scalar and composite data types. It is also a subset of the
Clojure language textual format. Therefore Clojure plugins/extensions for
editors or IDEs provide syntax checking/highlighting and code completion.

Compared to JSON, which only supports literals for numbers, strings, vectors
(arrays) and maps, EDN provides a richer set of data literals, e.g. integer and
floating point numbers, big integers and decimals, strings, symbols, keywords,
UUIDs and instants of time. It also provides literals for list, vectors, sets
and maps.

The following literals are used in Overarch models and views.

### Strings
Strings are used e.g. as names and descriptions of model elements and for the
title of views.

```clojure
"This is a string"

"This is
a multiline
string"
```

### Keywords
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

### Sets
Sets are unordered collections of elements without duplicates. They are used as
top level collections for the model elements and views. They are also used
as a container for the children of model elements.

```clojure
#{"a" "b" "c"}
```

### Maps
Maps are associative collections of key/value pairs. They are used to describe
the attributes of model elements and views.

```clojure
{:firstname "John" :lastname "Doe" :age 42}
```

### Vectors
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

## Examples
The model and diagram descriptions of the C4 model banking example can be
found in models/banking folder:
 * [model.edn](/models/banking/model.edn)
 * [views.edn](/models/banking/views.edn)

If you have a Clojure environment in some editor or IDE, just use it.
If not, try Visual Studio Code with the Calva and PlantUML extensions.
With this setup you get an editor for the EDN files with code completion,
syntax check and syntax highlighting.

![Model editing](/doc/images/overarch_vscode_model.png)


# Models

You can split your model into separate EDN files, which might be reasonable for
big systems. Overarch can recursively read all models from a directory or
search path so you are quite free in structuring your model files.

The top level element in each model EDN file is a **set** which contains the top
level model elements. Model elements are denoted as maps in the EDN file.

All model elements have at least two keys, **:el** for the type of the
element and **:id** for the identifier. The identifiers should be namespaced
keywords, so that different models can be composed without collisions of the
identifiers.

## Model Nodes
Model Nodes describe the elements of the different kind of models for the system.

### Common Keys of Model Nodes
key       | type               | values             | description 
----------|--------------------|--------------------|------------
:el       | keyword            | see model elements | type of the model node
:id       | keyword            | namespaced id      | id of the model node
:name     | string             | short name         | name of the model node
:desc     | string             |                    | description of the model node
:tags     | set of strings     | e.g. #{"critical"} | some tags which can be used in element selection
:ct       | set of maps        | model nodes        | the children of the model node

## Model Relations
Relations describe the connections and interactions of the nodes.

### Common Keys of Relations
key       | type               | values              | description 
----------|---------|---------------------|------------
:el       | keyword            | e.g. :rel, :request | type of the relation
:id       | keyword            | namespaced id       | id of the relation
:from     | keyword            | namespaced id       | id of the referrer node
:to       | keyword            | namespaced id       | id of the referred node
:name     | string             |                     | name of the relation
:desc     | string             |                     | description of the relation
:tags     | set of strings     | e.g.                | some tags which can be used in element selection

## References (:ref)
References refer to a model element with the given id. They are primarily used
to refer to the model elements to include in views. They can also be used to
refer to model elements in other model elements, e.g. to split a huge hierarchical
systems into multiple EDN files.

References can have other keys, which are merged with the referred element in
context of the reference. For example you can mark an internal system as external
in the context of a specific view by adding `:external true` to the reference.


## Architecture and Deployment Model
Overarch supports elements for C4 architecture and deployment models.

### Logical Data Model for the Architecture and Deployment Model Elements
![Architecture and Deployment Model Elements](/doc/images/overarch_architectureModelElementsOverview.svg)


### Additional Keys for Architecture Model Nodes
key       | type    | values             | description 
----------|---------|--------------------|------------
:subtype  | keyword | :database, :queue  | specific role of the model node
:external | boolean | true, false        | default is false
:tech     | string  |                    | technology of the model node


### Person (:person)
Persons are internal or external actors of the system.

```
{:el :person
 :id :banking/personal-customer
 :name "Personal Banking Customer"
 :desc "A customer of the bank, with personal banking accounts."}
```

### System (:system)
A System is the top level element of the C4 model an can contain a set of
containers. Systems can be internal or external to the project context.
The structure of internal systems is modelled with containers.

### Container (:container)
A container is a part of a system. It represents a process of the system
(e.g. an executable or a service). Containers are composed of a set of
components.

### Component (:component)
A component is unit of software, which lives in a container of the system.

### Node (:node)
A node is a unit in a deployment view. Nodes represent parts of the
infrastructure in which the containers of the system are deployed. They can
contain a set of other nodes or containers.

### Relations
Relations describe the connections and interactions of the parts of a view.

kind        | sync/async  | dependency  | description
------------|-------------|-------------|------------
:request    | sync        | true        | synchrounous request
:response   | sync        | false       | response to a synchronous request
:send       | async       | true        | asynchronous point-to-point message
:publish    | async       | true        | asynchronous broadcast message (via broker, topic, queue)
:subscribe  | async       | true        | subscribtion to an asynchronous broadcast message (via broker, topic, queue)
:dataflow   | unspecified | unspecified | flow of data independent of the call semantic
:rel        | unspecified | unspecified | unclassified relation


### Additional Keys for Architecture Model Relations
key       | type    | values             | description 
----------|---------|--------------------|------------
:tech     | string  | e.g. "REST"        | technology of the relation

### Boundaries
Boundaries group related elements and are normally rendered as a dashed box in a view. There are currently 4 types of boundaries, two of them implicit.

The implicit boundaries are the system boundary and the container boundary.
They are not modelled explicitly but are rendered for referenced systems and containers in specific views. A system boundary is rendered, when an internal system with containers as content is referenced in a container view or component view. Likewise a container boundary is rendered for a referenced container in a component view.

The explicit boundaries are enterprise boundary and context boundary.
These are explicitly modelled.
An enterprise boundary `{:el :enterprise-boundary}` can be used to group
systems by enterprise or company.
A context boundary `{:el :context-boundary}` can be used to group containers
or components by some common context, especially by domain contexts in the
sense of domain driven design.

### Example
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

## Concept Model

A concept model captures relevant concepts of the domain(s) of the system.
The concepts could be part of the ubiquous language of the systems domain.

A concept model can contain the concepts of the domain and the high level elements
of the architecture model, e.g. the persons (actors), external systems and the
system itself with it's containers.

![Concept Model Elements](/doc/images/overarch_conceptModelElementsOverview.svg)

### Concepts (:concept)
A concept which is relevant for the domain of the system. The description should document the meaning of the concept.

### Concept Model Relations (:is-a, :has, :rel)
Concepts can be related with other concepts.

relation type | description
--------------|------------
:is-a         | the :from node is a specialization of the :to node
:has          | the :from node is a part or attribute of the :to node
:rel          | unclassified relation between the nodes

### Example
See [example concept model](/models/concept/model.edn).

## Use Case Model

A use case model captures the functionality a system is suposed to deliver.
High level use cases provide an overview of this functionality and may link
to business processes, domain stories and arcitectural elements.

As such they provide a pivot for the traceability from business processes into
the design of the system.

The elements of the use case model are mainly borrowed from the UML use case
model so prior knowledge of UML modelling applies here.

### Logical Data Model for the Use Case Elements
![Use Case Model Elements](/doc/images/overarch_useCaseModelElementsOverview.svg)

### Example
Example [Use Case Model](/models/usecase/model.edn)

### Use Cases (:use-case)

A use case describes the goal of an actor in the context of the system described. The goal can be a concrete user goal, a high level summary of user goals or a subfunction of a user goal. This is captured by the :level key.


key         | type    | values                           | description 
------------|---------|----------------------------------|------------
:level      | keyword | :summary :user-goal :subfunction | specific role of the element
:ext-points | string  |                                  | extension points of a use case

### Actors (:person, :system, :container, :actor)

Persons, systems and containers from the architecture model should be used as
actors in the use case model to provide a connection between the architecture
model and the use case model.

You can use the :actor element to model actors not present as persons or
systems in the architectural model, but this should be avoided if possible.
A reason for an :actor element might be the introduction of a time actor to
model the scheduling of use cases.

### Relations (:uses :include :extends :generalizes)

Relations connect actors to the use cases or use cases with other use cases.
Use case models support different kinds of relations.

kind         | description
-------------|------------
:uses        | a use case element uses another use case element (e.g. an actor uses a use case or a use case uses an external system)
:include     | a use case includes the functionality of another use cases
:extends     | a use case extends the functionality of another use case
:generalizes | 

## State Machine Model

A state model describes a state machine which can be used to model the states
a system component can be in and the transition from one state to the next state based on the events the system receives as input.

The elements of the class model are mainly borrowed from the UML class model
so prior knowledge of UML modelling applies here.

### Logical Data Model for the State Machine Elements
![State Machine Elements](/doc/images/overarch_stateMachineElementsOverview.svg)

### Example
Example [State Model](/models/state/model.edn)

### State Machine (:state-machine)
A state machine is the root element for a state machine view. It contains the set of states and transistions as value of the *:ct* key.

### States (:state, :start-state, :end-state)
A simple state machine has at least one start state, some normal states to model the different states a system can be in, and at least one end state.

A start state starts the state machine and an end state terminates the state machine.

States can be compound, they can have an internal state machine. This is modelled as a set of states and transitions in the *:ct* key, analog to the state machine itself.

### Transitions (:transition)
A transition connects two states and models the input that leads to the transition from the current state (:from) to the next state (:to).

### Forks and Joins (:fork-state, :join-state)
You can split a transition to trigger multiple new states with a fork state.
A fork has a single input transition and multiple output transitions.

To join multiple transitions after a fork a join state is used. A join has multiple input transitions and a single output transition. 


## Class Model

A class model captures the static structure of the code.

The abstraction level of a class model is not very high compared to the actual
implementation. Therfore modelling and updating a complete class model is not
of much value. But class models of the domain can be very valuable as a means
of communication between domain experts and developers to shape and document
the domain model for a bounded context.

The elements of the class model are mainly borrowed from the UML class model
so prior knowledge of UML modelling applies here.

### Logical Data Model for the Class Model Elements
![Class Model Elements](/doc/images/overarch_classModelElementsOverview.svg)

### Packages/Namespace (:package, :namespace)
Packages and namespaces provide a hierarchical structure for the organisation
of the elements of the class model.

Packages and namespaces are treated the same, so use what suits your system best.

### Interfaces/Protocols (:interface, :protocol)
Interfaces and protocols specify related methods. Interfaces also provide a
type for the static type system.

Interfaces and protocolls are treated the same, so use what suits your system best.

### Class (:class)
A class in object orientation is a typed element that encapsulates state and
behaviour. The state is modelled with fields, the behaviour with methods.

In functional programming, you can use classes to model the values of your
system.

### Enumeration (:enum)
An enumeration is a typed enumeration of values.

### Field (:field)
A field is part of the state of a class.

### Method (:method)
A method is part of the behaviour of a class or an interface.

### Function (:function)
A function is a first class element in functional programming.
It has input parameters and calculates results.

### Relations (:association :aggregation, :composition :inheritance :implementation :dependency)

# Model Element Selection By Criteria
Model elements can be selected based on criteria.
Criterias are given as a map where each key/value pair specifies a criterium
for the selection. An element is selected, if it matches all criteria in the
map (logical conjunction).

Criterias can also be given as a vector of criteria maps. An element is
selected, if it is selected by any of the critria maps (logial disjunction). 

## Keys
key                    | type            | example values          | description
-----------------------|-----------------|-------------------------|------------
:el                    | keyword         | :system                 | elements of the given type
:els                   | set of keywords | #{:system :person}      | elements with one of the given types
:namespace             | string          | "org.soulspace"         | elements with the given id namespace
:namespaces            | set of strings  | #{"org.soulspace"}      | elements with one of the given id namespaces
:namespace-prefix      | string          | "org"                   | elements with the given id namespace prefix
:from-namespace        | string          | "org.soulspace"         | relations with the given id namespace of the from reference
:from-namespaces       | set of strings  | #{"org.soulspace"}      | relations with one of the given id namespaces of the from reference
:from-namespace-prefix | string          | "org"                   | relations with the given id namespace prefix of the from reference
:to-namespace          | string          | "org.soulspace"         | relations with the given id namespace of the from reference
:to-namespaces         | set of strings  | #{"org.soulspace"}      | relations with one of the given id namespaces of the from reference
:to-namespace-prefix   | string          | "org"                   | relations with the given id namespace prefix of the from reference
:id?                   | boolean         | true, false             | elements for which the id check returns the given value
:id                    | keyword         | :org.soulspace/overarch | the element with the given id
:from                  | keyword         | :org.soulspace/overarch | relations with the given from id
:to                    | keyword         | :org.soulspace/overarch | relations with the given to id
:subtype?              | boolean         | true, false             | nodes for which the subtype check returns the given value
:subtype               | keyword         | :queue                  | nodes of the given subtype
:subtypes              | set of keywords | #{:queue :database}     | nodes of one of the given subtypes
:external?             | boolean         | true, false             | elements of the given external state
:name?                 | boolean         | true, false             | elements for which the name check returns the given value
:desc?                 | boolean         | true, false             | elements for which the description check returns the given value
:tech?                 | boolean         | true, false             | elements for which the technology check returns the given value
:tech                  | string          | "Clojure"               | elements of the given technology
:techs                 | set of strings  | #{"Clojure" "Java"}     | elements with one or more of the given technologies
:all-techs             | set of strings  | #{"Clojure" "Java"}     | elements with all of the given technologies
:tags?                 | boolean         | true, false             | elements for which the tags check returns the given value
:tag                   | string          | "critical"              | elements with the given tag
:tags                  | set of strings  | #{"Clojure" "Java"}     | elements with one or more of the given tags
:all-tags              | set of strings  | #{"Clojure" "Java"}     | elements with all of the given tags
:children?             | boolean         | true, false             | nodes for which the check for children returns the given value
:child?                | boolean         | true, false             | nodes for which the check for child returns the given value
:refers?               | boolean         | true, false             | nodes for which the check for refers returns the given value
:referred?             | boolean         | true, false             | nodes for which the check for referred returns the given value
:refers-to             | keyword         | :org.soulspace/overarch | nodes which refer to the element with the given id
:referred-by           | keyword         | :org.soulspace/overarch | nodes which are refered by the element with the given id

# Views
To show model elements in diagrams or in textual representations you can define
different kind of views. The kind of view defines the visual rendering of the
elements and the kind of elements and relations that are shown.

In a specific view you can reference the model elements you want to include in
this view. A Model element can be included in as many views as you want, but the
element has to match the expected kinds of model elements to be shown. 
For example, a system landscape view renders person and system elements but no
use cases or state machines, even if they are referenced in the view. Please consult
the models for the model and view elements.

## Logical Data Model for the View Elements
![View Elements](/doc/images/overarch_viewElementsOverview.svg)

## Architecture and Deployment Views (C4 Model)

Overarch supports the description of all C4 core and supplementary views
except from code views, which ideally should be generated from the code
if needed. The core C4 views form a hierarchy of views.

See [c4model.com](https://c4model.com) for the rationale and detailed
information about the C4 Model.

The views can reference elements from the model as their content. The
content of a view should be a list instead of a set because the order
of elements may be relevant in the rendering of a view. 

### Keys

key       | type    | values                   | description 
----------|---------|--------------------------|------------
:el       | keyword | see views                | type of the view
:id       | keyword | namespaced id            | used for export file name
:title    | string  |                          | rendered title
:spec     | map     | see view specs           | rendering customization (e.g. styling)
:ct       | list    | model refs (or elements) | view specific keys possible

### System Context Views (:context-view)
Shows the system in the context of the actors and other systems it is
interacting with. Contains users, external systems and the system to be
described.

![System Context View rendered with PlantUML](/doc/images/banking_systemContextView.svg)

### Container Views (:container-view)
Shows the containers (e.g. processes, deployment units of the system) and
the interactions between them and the outside world. Contains the elements
of the system context diagram and the containers of the system to be described.
The system to be described is rendered as a system boundary in the container
diagram.

![Container View rendered with PlantUML](/doc/images/banking_containerView.svg)

### Component Views (:component-view)
Shows the components and their interactions inside of a container and with
outside systems and actors.

![Component View rendered with PlantUML](/doc/images/banking_apiComponentView.svg)

### C4 Code Views
A C4 code view is not supported, the level of abstraction for implementation details
is usually not high enough to justify modelling implemeted code. Also the speed of
change in the code is most likely to high and renders a code model obsolete fast.
If you want to visualize existing code, you can use the features of your IDE to generate
a diagram of it.

On the other hand it can be useful to create a view of code not yet implemented.
UML class view can be used to model a domain and communicate a design. See UML views for that. 

### System Landscape Views (:system-landscape-view)
The system landscape view shows a high level picture, a broader view of the system landscape and the interactions of the systems.

![System Landscape View rendered with PlantUML](/doc/images/banking_systemLandscapeView.svg)

### Deployment Views (:deployment-view)
The deployment view shows the infrastucture and deployment of the containers of the system.

![Deployment View rendered with PlantUML](/doc/images/banking_deploymentView.svg)

### Dynamic Views (:dynamic-view)
Shows the order of interactions. The relations get numerated in the given order and the nuber is rendered in the diagram.

## UML Views
Overarch supports selected UML views to show aspects of a system that are not covered by the C4
Model.

### Use Case View
A use case view is used to show the actors of the system under design and their
goals using this system.

![Use Case View rendered with PlantUML](/doc/images/banking_useCaseView.svg)

### State Machine Views
A state machine view is used to show the different states a component can be in. It also shows the
transitions between these states based on the input events, the component receives. 

### Class Views
A class view is used to show the design of parts of the software. You can use it e.g. to model a
domain and to communicate the model with domain experts.

## Conceptual Views
Overarch also supports conceptual views as part of the documentation of the
system. Conceptual views can be used in early stages of the development project,
when the requirements and the architecture are not yet fixed, to get an overview
of the system to be designed. They can also be used to document the relevant
concept ofthe domain of the system for discussion, onboarding and learning.
Concepts should also be part of the glossary, as well as actors, systems and
the applications and containers developed for the system.

### Concept View
The concept view is a graphical view. It shows the concepts as a concept map
with the relations between the concepts.

### Glossary View
The glossary view is a textual view. It shows a sorted list of elements with
their type and their descriptions.

## View Specs
Views can be customized with the `:spec` key. View specs may include general
directives for a view or directives for specific renderers (e.g. PlantUML).

key           | type    | values                    | description 
--------------|---------|---------------------------|------------
:include      | keyword | :referenced-only :related | specify automatic includes (work in progress)
:layout       | keyword | :top-down, :left-right    | rendering direction
:linetype     | keyword | :orthogonal, :polygonal   | different line types for relations
:sketch       | boolean | true, false               | visual clue for sketches
:styles       | set     | see Styling               | visual customization of elements
:theme        | keyword | id of the theme           | theme containing styles

### Includes
With the `:include` key elements can be automatically included in a view. 
The default behaviour is `:referenced-only` which only includes the referenced
elements.

With the value `:related` all elements participating in the referenced
relations will be automatically included in addidtion to the referenced
elements.

### Styling
Overarch supports custom styles for elements. For an example see
[views.edn](/models/test/views.edn).

#### Keys

key           | type    | values                   | description 
--------------|---------|--------------------------|------------
:id           | keyword | namespaced id            | used to reference styles
:for          | keyword | :rel, :element           | element type to be styled
:line-style   | keyword | :dashed, :dotted, :bold  | line style for relations
:line-color   | hex rgb | #0000FF for bright blue  | line color for relations
:border-color | hex rgb | #FF0000 for bright red   | border color for nodes
:text-color   | hex rgb | #003300 for dark green   | text color for names and descriptions
:legend-text  | string  |                          | meaningful text to show in legend


# Rendering

## PlantUML
The specified views C4 architecture and UML viewscan be exported to PlantUML
diagrams. These can be rendered into different formats (e.g. SVG, PNG, PDF)
with PlantUML.

You can specify PlantUML specific directives with the **:plantuml** key of a
view spec.

```
   :spec {:plantuml {:sprite-libs [:azure]}}
```

### Keys
key              | type    | values                   | description 
-----------------|---------|--------------------------|------------
:node-separation | integer | 50 (for 50 pixels)       | separation between nodes
:rank-separation | integer | 250 (for 250 pixels)     | separation between ranks
:sprite-libs     | vector  | sprite-lib keywords      | used to render sprites for techs, order defines precedence of the libs

### Sprite Support
Overarch supports PlantUML sprites to show a visual cue of the technology in
the elements of a diagram.
It does so by matching the value of the **:tech** key of a model element to
the list of sprites. You can also directly add a **:sprite** key to the
reference of a model element in a view. The explicit **:sprite** value takes
precedence over the **:tech** value.

The list of sprites contains sprites of the PlantUML standard library, e.g.
sprites for AWS and Azure. The mapping files from tech name to sprite
reside in [resources/plantuml](/resources/plantuml). 

Currently the following keys for sprite libs are supported:
 * :awslib14
 * :azure
 * :cloudinsight
 * :cloudogu
 * :devicons
 * :devicons2
 * :font-awesome-5
 * :logos

The command line interface supports the option `--plantuml-list-sprites`
which prints the (long) list of sprite mappings. 

### Rendering PlantUML diagrams in VS Code
The Visual Studio Code PlantUML Extension allows previewing and exporting these
diagrams right from the IDE.

![PlantUML preview](/doc/images/overarch_plantuml_preview.png)

PlantUML plugins also exists for major IDEs and build tools (e.g. IntelliJ, Eclipse, Maven, Leiningen).

* [PlantUML Plugin for Leiningen](https://github.com/vbauer/lein-plantuml)


## GraphViz
The concept view can be exported as a concept map to a GraphViz *.dot file.

### Rendering GraphViz diagrams
For GraphViz there are a few Visual Studio Code  extensions available that allow
previews of the generated Graphviz files. 

The images can be created with the *dot* executable, which resides in the bin
directory of the GraphViz installation.

You can specify Graphviz directives with the **:graphviz** key in a view spec.
Currently only the configuration of the
[layout engine](https://graphviz.org/docs/layouts/) is supported.

### Keys

key              | type    | values                   | description 
-----------------|---------|--------------------------|------------
:engine          | keyword | e.g. :dot, :neato, :sfdp | the graphviz layout engine to use

## Markdown
Markdown is used to render textual representations of the views.
You can use converters to generate other formats like HTML or PDF from markdown.

You can specify Markdown directives with the **:markdown** key in a
view spec.

### Keys

key              | type    | values                   | description 
-----------------|---------|--------------------------|------------
:references      | boolean | true, false              | render references for nodes


# Exports

## JSON
The model and view descriptions can be exported to JSON to make their content
available to languages for which no EDN implementation exists.
The export converts each EDN file in the model directory to JSON.

## Structurizr (*experimental*)
Structurizr is a tool set created by Simon Brown.
The Structurizr export creates a workspace with the loaded model and views.

As Structurizr currently only supports the C4 architecture model and views,
only these elements will be included in the Structurizr workspace.

# Command Line Interface

```
Overarch CLI
   
   Reads your model and view specifications and renders or exports
   into the specified formats.

   For more information see https://github.com/soulspace-org/overarch

Usage: java -jar overarch.jar [options].

Options:

  -m, --model-dir PATH                models     Models directory or path
  -r, --render-format FORMAT                     Render format (all, graphviz, markdown, plantuml)
  -R, --render-dir DIRNAME            export     Render directory
  -x, --export-format FORMAT                     Export format (json, structurizr)
  -X, --export-dir DIRNAME            export     Export directory
  -w, --watch                                    Watch model dir for changes and trigger action
  -s, --select-elements CRITERIA                 Select and print model elements by criteria
  -S, --select-references CRITERIA               Select model elements by criteria and print as references
  -t, --template-dir DIRNAME          templates  Template directory
  -g, --generator-config FILE                    Generator configuration
  -G, --generator-dir DIRNAME         generated  Generator artifact directory
  -B, --generator-backup-dir DIRNAME  backup     Generator backup directory
      --model-warnings                           Returns warnings for the loaded model
      --model-info                               Returns infos for the loaded model
      --plantuml-list-sprites                    Lists the loaded PlantUML sprites
  -h, --help                                     Print help
      --debug                                    Print debug messages
 ```

## Examples
To render all views for all models, use
```
> java -jar ./target/overarch.jar -r all
```
or
```
> java -jar ./target/overarch.jar -r all --debug
```

To render all views for all models with a directory watch to trigger rerendering on changes, use
```
> java -jar ./target/overarch.jar -r all -w --debug
```

To export the models to JSON, use
```
> java -jar ./target/overarch.jar -x json
```

To query the model for all containers, use
```
> java -jar ./target/overarch.jar -s '{:el :container}'
```
or
```
> java -jar ./target/overarch.jar -S '{:el :container}'
```
