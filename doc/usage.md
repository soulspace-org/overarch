# Overarch Usage Overview

The usage of Overarch is twofold. On the one hand, Overarch provides an open
data format to describe the concepts, functional requirements, architecture,
and design of software systems. On the other hand it is a tool to transform the
description into diagrams, reports or other representations.

Overarch can be used as a CLI tool to convert specified models and diagrams
into various formats, e.g. the rendering of diagrams in PlantUML or the
conversion of the data to JSON. It can also be used to query the model by
criteria and generate artifacts based on the result with templates.

![Use Cases of Overarch](/doc/images/overarch/use-cases/overarch-use-case-view.svg)


# Rationale for Overarch
Modelling and vizualizing a software system or a system landscape is useful for
thinking, communication, documentation and onboarding collegues. The systems
can be modelled with different level of details and vizualized with different
views. 

To model a system you can either use a modelling tool or a diagramming tool.
Most modelling tools are commercial and lock in your models or not comprehensive.
Modelling is also often decoupled from development.

Most diagramming tools don't use a model or don't separate the model information
from the layout. The scope of most diagramming tools is limited to diagrams only.
With diagramming tools like Excalidraw or Gliffy it's quite easy to draw some
diagrams for a system. But changing existing diagrams can be cumbersome when
diagram elements have to be placed and moved manually. This also limits the
scope and size of the diagrams that can be created and maintained.

Some problems most of these tools often face is versioning and collaboration.
By using a text based representation of the model and the views, a development
workflow with version control, e.g. git can be applied to modelling. 

Some tools like PlantUML or Structurizr provide a DSL to model a system or to
describe diagrams. While PlantUML operates at the diagram level and is
basically a text based diagramming tool with auto layout, structurizr is a
modelling tool which also features a graphical editor. But to use the model in
a different context, the DSL has to be parsed.

Overarch addresses these issues by providing a text based data format for the
specification of the model and the views of the model. By using plain data
literals like sets and maps, no DSL parser is needed to instanciate the model.


Overarch is free, open and extensible
* provides model information as plain data
* models are readable, discoverable, composable, reusable and extensible
* model queries, vizualization, artifact generation

# Command Line Interface

```
Overarch CLI
   
   Reads your model and view specifications and renders or exports
   into the specified formats.

   For more information see https://github.com/soulspace-org/overarch

Usage: java -jar overarch.jar [options].

Options:

  -m, --model-dir PATH                   models     Models directory or path
  -r, --render-format FORMAT                        Render format (all, graphviz, markdown, plantuml)
  -R, --render-dir DIRNAME               export     Render directory
      --[no-]render-format-subdirs       true       Use subdir per render format
  -x, --export-format FORMAT                        Export format (json, structurizr)
  -X, --export-dir DIRNAME               export     Export directory
  -w, --watch                            false      Watch model dir for changes and trigger action
  -s, --select-elements CRITERIA                    Select and print model elements by criteria
  -S, --select-references CRITERIA                  Select model elements by criteria and print as references
      --select-views CRITERIA                       Select and print views by criteria
      --select-view-references CRITERIA             Select views by criteria and print as references
  -T, --template-dir DIRNAME             templates  Template directory
  -g, --generation-config FILE                      Generation configuration
  -G, --generation-dir DIRNAME           generated  Generation artifact directory
  -B, --backup-dir DIRNAME               backup     Generation backup directory
      --scope NAMESPACE                             Sets the internal scope by namespace prefix
      --[no-]model-warnings              true       Returns warnings for the loaded model
      --[no-]model-info                  false      Returns infos for the loaded model
      --plantuml-list-sprites            false      Lists the loaded PlantUML sprites
  -h, --help                                        Print help
      --debug                            false      Print debug messages
 ```

## CLI Examples
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


# Editor/IDE Integration
## Visual Studio Code

With the addition of a few extensions, Visual Studio Code is a lightweight
modelling environment.
Install the following extensions in VS Code:
![Calva Extension](/doc/images/vscode_calva_ext.png)
![PlantUML Extension](/doc/images/vscode_plantuml_ext.png)
![Graphviz Preview Extension](/doc/images/vscode_graphviz_preview_ext.png)
![Graphviz Language Extension](/doc/images/vscode_graphviz_language_ext.png)

With this setup you get an editor for the EDN files with code completion,
syntax check and syntax highlighting.

![Model editing](/doc/images/overarch_vscode_model.png)

You also get integrated previews of the rendered PlantUML diagrams and the
ability to generate image files in different formats (e.g. PNG, SVG, PDF, ...)
directly from within Visual Studio Code.

## IntelliJ


# Modelling
Overarch supports modelling the functional requirements, the architecture and
the design of the system under description.

Modelling a system with overarch should provide a value for the project and
this guides the selection of model elements and supported abstractions and
views.

Overarch currently supports the following kinds of models
* [Concept Models](#concept-model)
  to model the concepts used for the system and their relations
* [Use-Case Models](#use-case-model)
  to model the scope of the system with the supported use cases
* [Architecture Models](#architecture-model)
  to model the logical architecture of the system with different levels of detail
* [Code Models](#code-model)
  to model the design of the system, e.g. the domain model, and the structure
  of the implementation
* [State-Machine Models](#state-machine-model)
  to model relevant states and transitions of the system
* [Deployment Models](#deployment-model)
  to model the physical architecture of the system
* [Organization Models](#organization-model)
  to model organizational structures, collaborations and responsibilities for
  parts of the system

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

Keywords start with a colon (`:`), have an optional namespace followed by a
slash (`/`) and a mandatory name, e.g. `:namespace/name`.

Keywords should be prefixed with a namespace to avoid collisions with keywords
for other models, which is especially relevant for identifiers or for custom
keys in the model elements and views. Namespaces may have different parts,
separated by a period (`.`), e.g. `:org.soulspace/overarch`.

```clojure
:keyword
:namespaced/keyword
:my.namespaced/keyword
```

*Unprefixed keywords and the namespace 'overarch' for map keys are reserved for
 overarch. Please use your own prefix if you want to add custom information to
 the maps in the model.*

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
key       | type               | values                 | description 
----------|--------------------|----------------------  |------------
:el       | keyword            | see model elements     | type of the model node
:id       | keyword            | namespaced id          | id of the model node
:name     | string             | short name             | name of the model node
:desc     | string             | short description      | description of the model element, to be rendered in diagrams
:doc      | multiline string   | longer documentation   | documentation of the model element, not to be rendered in diagrams but textual output
:maturity | keyword            | :proposed, :deprecated | the maturity of the model element
:tags     | set of strings     | e.g. #{"critical"}     | some tags which can be used in element selection
:ct       | set of maps        | model nodes            | the children of the model node

## Model Relations
Relations describe the connections and interactions of the nodes.

### Common Keys of Relations
key       | type               | values                 | description 
----------|--------------------|------------------------|-------------
:el       | keyword            | e.g. :rel, :request    | type of the relation
:id       | keyword            | namespaced id          | id of the relation
:from     | keyword            | namespaced id          | id of the referrer node
:to       | keyword            | namespaced id          | id of the referred node
:name     | string             |                        | name of the relation
:desc     | string             |                        | description of the relation
:doc      | multiline string   | longer documentation   | documentation of the model element, not to be rendered in diagrams but textual output
:maturity | keyword            | :proposed, :deprecated | the maturity of the model element
:tags     | set of strings     | e.g. #{"critical"}     | some tags which can be used in element selection

### Generic relations (:contained-in :implements)
The `:contained-in` relation is used to model hierarchical composition.
Hierarchical composition specified with the `:ct` key in the EDN models is
converted to :contained-in relations when the model is loaded.
`contained-in` relations can also be used explicitly in the EDN model to
help structuring the model in separate files.

An `:implements` relation defines the implementation of some type of node by
another type of node, e.g. the implementation of a use case by a container.
The `:implements` relations can be used to establish traceability between
different models or node types in Overarch.

## References (:ref)
References refer to a model element with the given id. They are primarily used
to refer to the model elements to include in views. They can also be used to
refer to model elements in other model elements, e.g. to split a huge hierarchical
systems into multiple EDN files.

References can have other keys, which are merged with the referred element in
context of the reference. For example you can mark an internal system as external
in the context of a specific view by adding `:external true` to the reference.

## Boundaries
Boundaries group related elements and are normally rendered as a dashed box in
a view. There are currently 4 types of boundaries, two of them implicit.

The implicit boundaries are the system boundary and the container boundary.
They are not modelled explicitly but are rendered for referenced systems and
containers in specific views. A system boundary is rendered, when an internal
system with containers as content is referenced in a container view or
component view. Likewise a container boundary is rendered for a referenced
container in a component view.

The explicit boundaries are enterprise boundary and context boundary.
These are explicitly modelled.
An enterprise boundary `{:el :enterprise-boundary}` can be used to group
systems by enterprise or company.
A context boundary `{:el :context-boundary}` can be used to group concepts,
containers or components by some common context, especially by domain contexts
in the sense of domain driven design.


## Architecture Model
Overarch supports elements for C4 architecture models.

### Logical Data Model for the Architecture Model Elements
![Architecture and Deployment Model Elements](/doc/images/overarch/data-model/architecture-model-elements.svg)


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

### Relations
Relations describe the connections and interactions of the parts of a view.

kind        | sync/async  | dependency  | description
------------|-------------|-------------|------------
:request    | sync        | true        | synchrounous request
:response   | sync        | false       | response to a synchronous request
:send       | async       | true        | asynchronous point-to-point message
:publish    | async       | true        | asynchronous broadcast message (via broker, topic, queue), forms a dependency to the broker/queue
:subscribe  | async       | true        | subscribtion to an asynchronous broadcast message (via broker, topic, queue), forms a dependency to the broker/queue
:dataflow   | unspecified | unspecified | flow of data independent of the call semantic
:rel        | unspecified | unspecified | unclassified relation


### Additional Keys for Architecture Model Relations
key       | type    | values             | description 
----------|---------|--------------------|------------
:tech     | string  | e.g. "REST"        | technology of the relation

### Example
Example (exerpt from the [banking model](/models/banking/model.edn) containing
context and container level elements):

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

## Deployment Model
Overarch also supports elements for C4 deployment models.

### Logical Data Model for the Deployment Model Elements
![Deployment Model Elements](/doc/images/overarch/data-model/deployment-model-elements.svg)

### Node (:node)
A node is a unit in a deployment view. Nodes represent parts of the
infrastructure in which the containers of the system are deployed. They can
contain a set of other nodes or containers.

### Deployment model relations
relation type | description
--------------|------------
:link         | A link between two nodes of the deployment model, e.g. two virtual networks
:deployed-to  | A deployment relation between a container and a node in the deployment model
:rel

## Concept Model
A concept model captures relevant concepts of the domain(s) of the system. The
concepts could be part of the ubiquous language of the systems domain.

A concept model can contain the concepts of the domain and the high level
elements of the architecture model, e.g. the persons (actors), external systems
and the system itself with it's containers.

### Logical Data Model for the Concept Model Elements
![Concept Model Elements](/doc/images/overarch/data-model/concept-model-elements.svg)

### Concepts (:concept)
A concept which is relevant for the domain of the system. The description
should document the meaning of the concept.

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

### Logical Data Model for the Use Case Model Elements
![Use Case Model Elements](/doc/images/overarch/data-model/use-case-model-elements.svg)

### Example
Example [Use Case Model](/models/usecase/model.edn)

### Use Cases (:use-case)
A use case describes the goal of an actor in the context of the system
described. The goal can be a concrete user goal, a high level summary of user
goals or a subfunction of a user goal. This is captured by the :level key.


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
a system component can be in and the transition from one state to the next
state based on the events the system receives as input.

The elements of the class model are mainly borrowed from the UML class model
so prior knowledge of UML modelling applies here.

### Logical Data Model for the State Machine Model Elements
![State Machine Elements](/doc/images/overarch/data-model/state-machine-model-elements.svg)

### Example
Example [State Model](/models/state/model.edn)

### State Machine (:state-machine)
A state machine is the root element for a state machine view. It contains the
set of states and transistions as value of the *:ct* key.

### States (:state, :start-state, :end-state)
A simple state machine has at least one start state, some normal states to
model the different states a system can be in, and at least one end state.

A start state starts the state machine and an end state terminates the state
machine.

States can be compound, they can have an internal state machine. This is
modelled as a set of states and transitions in the *:ct* key, analog to the
state machine itself.

### Transitions (:transition)
A transition connects two states and models the input that leads to the
transition from the current state (:from) to the next state (:to).

### Forks and Joins (:fork-state, :join-state)
You can split a transition to trigger multiple new states with a fork state.
A fork has a single input transition and multiple output transitions.

To join multiple transitions after a fork a join state is used. A join has
multiple input transitions and a single output transition. 


## Code Model
A code model captures the static structure of the code.

The abstraction level of a code model is not very high compared to the actual
implementation. Therfore modelling and updating a complete code model is not
of much value. But code models of the domain can be very valuable as a means
of communication between domain experts and developers to shape and document
the domain model for a bounded context.

The elements of the code model are mainly borrowed from the UML class model
so prior knowledge of UML modelling applies here.

### Logical Data Model for the Code Model Elements
![Code Model Elements](/doc/images/overarch/data-model/code-model-elements.svg)

### Packages/Namespace (:package, :namespace)
Packages and namespaces provide a hierarchical structure for the organisation
of the elements of the code model.

Packages and namespaces are treated the same, so use what suits your system best.

### Interfaces/Protocols (:interface, :protocol)
Interfaces and protocols specify related methods. Interfaces also provide a
type for the static type system.

Interfaces and protocols are treated the same, so use what suits your system
and inplementation language best.

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
The relations connect the nodes of the code model. They are essentially the relation types of the UML.

## Organization Model
The organization model captures the organizational structure of an organization.

### Logical Data Model for the Organization Model Elements
![Organization Model Elements](/doc/images/overarch/data-model/organization-model-elements.svg)

### Organization Structure Nodes (:organization :org-unit)
An organization contains organizational units (e.g. branches and departments)
and organizational units can cointain other organizational units.
You can enhance the org-unit node with attributes specific for your organization
(e.g. support mailboxes) and use these attributes in the templates to generate
documentation or other artifacts.

### Relations (:responsible-for, :collaborates-with :role-in)
The responsible-for relation captures the responsibility of an organizational
unit for architecture or deployment nodes (e.g. a system or an Azure subscription).

## Process Model
The process model captures the structure of capabilities, processes and their resources.

### Logical Data Model for the Process Model Elements

### Nodes (:capability :information :knowledge :process :artifact :requirement :decision)

### Relations (:role-in :required-for :input-of :output-of)

# Model Element Selection By Criteria
Model elements can be selected based on criteria.
Criterias are given as a map where each key/value pair specifies a criterium
for the selection. An element is selected, if it matches all criteria in the
map (logical conjunction).

Criterias can also be given as a vector of criteria maps. An element is
selected, if it is selected by any of the critria maps (logical disjunction). 

## Keys
key                    | type            | example values            | description
-----------------------|-----------------|---------------------------|------------
:key?                  | vector          | [:tech true]                 | elements for which the check for the key returns the value (useful for custom keys)
:key                   | vector          | [:tech "Clojure"]            | elements for which the lookup of the key returns the value (useful for custom keys)
:el                    | keyword         | :system                      | elements of the given ``:el`` type
:els                   | set of keywords | #{:system :person}           | elements with one of the given ``:el`` types
:!els                  | set of keywords | #{:system :person}           | elements with none of the given ``:el`` types
:namespace             | string          | "org.soulspace"              | elements with the given ``:id`` namespace
:!namespace            | string          | "org.soulspace"              | elements not with the given ``:id`` namespace
:namespaces            | set of strings  | #{"org.soulspace"}           | elements with one of the given ``:id`` namespaces
:namespace-prefix      | string          | "org"                        | elements with the given ``:id`` namespace prefix
:from-namespace        | string          | "org.soulspace"              | relations with the given id namespace of the ```:from`` reference
:from-namespaces       | set of strings  | #{"org.soulspace"}           | relations with one of the given id namespaces of the ``:from`` reference
:from-namespace-prefix | string          | "org"                        | relations with the given id namespace prefix of the ``:from`` reference
:to-namespace          | string          | "org.soulspace"              | relations with the given id namespace of the ``:to`` reference
:to-namespaces         | set of strings  | #{"org.soulspace"}           | relations with one of the given id namespaces of the ``:to`` reference
:to-namespace-prefix   | string          | "org"                        | relations with the given id namespace prefix of the ``:to`` reference
:id?                   | boolean         | true, false                  | elements for which the ``:id`` check returns the given value
:id                    | keyword         | :org.soulspace/overarch      | the element with the given ``:id``
:!id                   | keyword         | :org.soulspace/overarch      | elements not with the given ``:id``
:from                  | keyword         | :org.soulspace/overarch      | relations with the given ``:from`` id
:!from                 | keyword         | :org.soulspace/overarch      | relations not with the given ``:from`` id
:to                    | keyword         | :org.soulspace/overarch      | relations with the given ```:to`` id
:!to                   | keyword         | :org.soulspace/overarch      | relations not with the given ``:to`` id
:subtype?              | boolean         | true, false                  | nodes for which the ``:subtype`` check returns the given boolean value
:subtype               | keyword         | :queue                       | nodes of the given ``:subtype``
:subtypes              | set of keywords | #{:queue :database}          | nodes of one of the given ``:subtype``s
:!subtypes             | set of keywords | #{:queue :database}          | nodes of none of the given ``:subtype``s
:maturity?             | boolean         | true, false                  | elements for which the ``:maturity`` check returns the given boolean value
:maturity              | keyword         | :proposed, :deprecated       | elements of the given ``:maturity``
:maturities            | set of keywords | #{:implemented  :deprecated} | elements of the given maturities
:!maturities           | set of keywords | #{:implemented  :deprecated} | elements not of the given maturities
:external?             | boolean         | true, false                  | elements of the given external state
:name?                 | boolean         | true, false                  | elements for which the ``:name`` check returns the given value
:name                  | string/regex    | "Overarch CLI"               | elements for which the ``:name`` matches the given value
:desc?                 | boolean         | true, false                  | elements for which the ``:desc`` check returns the given value
:desc                  | string/regex    | "CLI" "(?i).*CLI.*"          | elements for which the ``:desc`` matches the given value
:doc?                  | boolean         | true, false                  | elements for which the ``:doc`` check returns the given value
:doc                   | string/regex    | "CLI" "(?i).*CLI.*"          | elements for which the ``:doc`` matches the given value
:tech?                 | boolean         | true, false                  | elements for which the ``:tech`` check returns the given value
:tech                  | string          | "Clojure"                    | elements of the given ``:tech``
:techs                 | set of strings  | #{"Clojure" "Java"}          | elements with one or more of the given ``:tech``s
:!techs                | set of strings  | #{"Clojure" "Java"}          | elements with none of the given ``:tech``s
:all-techs             | set of strings  | #{"Clojure" "Java"}          | elements with all of the given ``:tech``s
:tags?                 | boolean         | true, false                  | elements for which the ``:tags`` check returns the given value
:tag                   | string          | "critical"                   | elements with the given tag
:tags                  | set of strings  | #{"Clojure" "Java"}          | elements with one or more of the given ``:tags``
:!tags                 | set of strings  | #{"Clojure" "Java"}          | elements with none of the given ``:tags``
:all-tags              | set of strings  | #{"Clojure" "Java"}          | elements with all of the given ``:tags``
:refers?               | boolean         | true, false                  | nodes for which the check for refers returns the given value
:referred?             | boolean         | true, false                  | nodes for which the check for referred returns the given value
:refers-to             | keyword         | :org.soulspace/overarch      | nodes which refer to the node with the given id
:!refers-to            | keyword         | :org.soulspace/overarch      | nodes which not refer to the node with the given id
:referred-by           | keyword         | :org.soulspace/overarch      | nodes which are referred by the node with the
:!referred-by          | keyword         | :org.soulspace/overarch      | nodes which are not referred by the node with the given id
:child?                | boolean         | true, false                  | nodes for which the check for child returns the given value
:child-of              | keyword         | :org.soulspace/overarch      | nodes which are children of the node with the given id
:!child-of             | keyword         | :org.soulspace/overarch      | nodes which are not children of the node with the given id
:descendant-of         | keyword         | :org.soulspace/overarch      | nodes which are descendants of the node with the given id
:!descendant-of        | keyword         | :org.soulspace/overarch      | nodes which are not descendants of the node with the given id
:parent?               | boolean         | true, false                  | nodes for which the check for children returns the given value
:parent-of             | keyword         | :org.soulspace/overarch      | node which is the parent of the node with the given id
:!parent-of            | keyword         | :org.soulspace/overarch      | nodes which are not the parent of the node with the given id
:ancestor-of           | keyword         | :org.soulspace/overarch      | nodes which are ancestors of the node with the given id
:!ancestor-of          | keyword         | :org.soulspace/overarch      | nodes which are not ancestors of the node with the given id

# Views
To show model elements in diagrams or in textual representations you can define
different kind of views. The kind of view defines the visual rendering of the
elements and the kind of elements and relations that are shown.

Overarch supports different types of views and renderings. For architecture and
deployment models, C4 diagrams can be rendered. Use case models, state machine
models and code models can be rendered as UML diagrams. Structure diagams can
be used to show the hierarchical structure of organisations, systems or
deployments.

Overarch supports the description of all C4 core and supplementary diagrams
as views. The core C4 views form a hierarchy of views. See
[c4model.com](https://c4model.com) for the rationale and detailed information
about the C4 model and diagrams.

Overarch also supports conceptual views as part of the documentation of the
system. Conceptual views can be used in early stages of the development project,
when the requirements and the architecture are not yet fixed, to get an overview
of the system to be designed. They can also be used to document the relevant
concept ofthe domain of the system for discussion, onboarding and learning.
Concepts should also be part of the glossary, as well as actors, systems and
the applications and containers developed for the system.

## Logical Data Model for the View Model Elements
![View Model Elements](/doc/images/overarch/data-model/view-model-elements.svg)

## View Content Specification
In a specific view you can reference the model elements you want to include in
this view. A Model element can be included in as many views as you want, but the
element has to match the expected kinds of model elements to be shown.
For example, a system landscape view renders person and system elements but no
use cases or state machines, even if they are referenced in the view. Please consult
the models for the model and view elements.

Model elements can be referenced directly via a `:ref`. They can also be
selected via model criteria. Either References for selected nodes or nodes for
selected references can be included automatically.

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

## System Context View (:context-view)
Shows the system in the context of the actors and other systems it is
interacting with. Contains users, external systems and the system to be
described.

![System Context View rendered with PlantUML](/doc/images/banking/system-context-view.svg)

## Container View (:container-view)
Shows the containers (e.g. processes, deployment units of the system) and
the interactions between them and the outside world. Contains the elements
of the system context diagram and the containers of the system to be described.
The system to be described is rendered as a system boundary in the container
diagram. External systems are rendered as boxes by default, but can be expanded
to show their containers by adding `:expand-external true` to the view spec.

![Container View rendered with PlantUML](/doc/images/banking/container-view.svg)

## Component View (:component-view)
Shows the components and their interactions inside of a container and with
outside systems and actors. External systems are rendered as boxes by default,
but can be expanded to show their containers and components by adding
`:expand-external true` to the view spec.


![Component View rendered with PlantUML](/doc/images/banking/api-component-view.svg)

## Code View (:code-view)
A code view is used to show the design of parts of the software. You can use it
e.g. to model a domain and to communicate the model with domain experts.

Normally it is not neccessary to model the whole code base, the level of
abstraction for implementation details is usually not high enough to justify
modelling implemeted code. Also the speed of change in the code is most likely
to high and renders a code model obsolete fast.
If you want to visualize existing code, you can use the features of your IDE to
generate a diagram of it. Maybe you can generate a code model from the existing
code base.

On the other hand it can be useful to create a view of code not yet implemented.
An UML class view can be used to model a domain or communicate a design
pattern.

The data models shown here in the documentation are examples of code-views.

## System Structure View (:deployment-structure-view)
The system structure view shows hierarchical composition of a system.

### System Landscape Views (:system-landscape-view)
The system landscape view shows a high level picture, a broader view of the
system landscape and the interactions of the systems.

![System Landscape View rendered with PlantUML](/doc/images/banking/system-landscape-view.svg)

### Dynamic Views (:dynamic-view)
Shows the order of interactions. The relations get numerated in the given order
and the nuber is rendered in the diagram. The `:index` attribute can be used on
relations or refs to relations to set the number for a relation in the view.

If the relations are describing interactions specific for the diagram instead
of general architectural relations (e.g. interfaces) of the model elements,
it is ok to to specify the relations in the content of the view.

## Deployment View (:deployment-view)
The deployment view shows the infrastucture and deployment of the containers of
the system.

![Deployment View rendered with PlantUML](/doc/images/banking/deployment-view.svg)

## Deployment Structure View (:deployment-structure-view)
The deployment structure view shows hierarchical composition of the
infrastructure and deployments of the system.

## State Machine Views (:state-machine-view)
A state machine view is used to show the different states a component can be in.
It also shows the transitions between these states based on the input events
the component receives. 

## Use Case View (:use-case-view)
A use case view is used to show the actors of the system under design and their
goals using this system.

![Use Case View rendered with PlantUML](/doc/images/banking/use-case-view.svg)

## Concept View (:concept-view)
The concept view is a graphical view. It shows the concepts as a concept map
with the relations between the concepts.

## Organization Structure View (:organization-structure-view)
The organization structure view shows hierarchical composition of a organization.

## Glossary View (:glossary)
The glossary view is a textual view. It shows a sorted list of elements with
their type and their descriptions.

## View Content Selection and Customization
Views can be customized with the `:spec` key. View specs may include general
directives for a view or directives for specific renderers (e.g. PlantUML).

key              | type          | example values            | description 
-----------------|---------------|---------------------------|------------
:include         | keyword       | :relations :related       | specify automatic includes (work in progress)
:selection       | map or vector | {:namespace "banking"}    | select the content by criteria (see [Model Element Selection](#model-element-selection-by-criteria))
:layout          | keyword       | :top-down, :left-right    | rendering direction
:linetype        | keyword       | :orthogonal, :polygonal   | different line types for relations
:sketch          | boolean       | true, false               | visual clue for sketches
:styles          | set           | see Styling               | visual customization of elements
:themes          | keyword       | id of the theme           | theme containing styles
:expand-external | boolean       | true, false               | show the content of external systems in container/component views (default false)

### Selection
With the `:selection` key a criteria map or a vector of criterias can be specified.
The matching elements will be included in the view. This feature can be used to
create 'dynamic' views that always contain the latest model content matching
the criteria. See section [Model Element Selection by Criteria](#model-element-selection-by-criteria)
for details and [banking views](/models/banking/views.edn) for examples.

### Includes
With the `:include` key elements can be automatically included in a view. 
The default behaviour is `:referenced-only` which only includes the referenced
elements.

With the value `:relations` all relations to the referenced elements will be
 automatically included.

With the value `:related` all elements participating in the referenced
relations will be automatically included in addidtion to the referenced
elements.

### Preference Rules for View Content
Criteria based selection, direct element references and includes can be
combined in a view. First the selection is merged with the references in such
a way, that key overrides and additions on references are preserved. Then the
included elements are calculated and merged. This merge also preserves the key
overrides and additions made on the references.

Therefore you can select the content with the `:selection` and `:include` keys
and customize the rendering with direct references in the `:ct` vector of the
view.

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
Views can be rendered into different formats via the Overarch CLI tool.
Views can also be rendered via templates, which allows full control over the
output and allows new output formats without changes in Overarch itself (see
[Template Based Artifact Generation](#template-based-artifact-generation)).

This section describes the rendering of views via the Overarch CLI tool.

## PlantUML
The specified views C4 architecture and UML views can be rendered to PlantUML
diagram specification (*.puml files). These can be rendered into different
formats (e.g. SVG, PNG, PDF) with PlantUML.

You can specify PlantUML specific directives with the **:plantuml** key of a
view spec.

```
   :spec {:plantuml {:sprite-libs [:azure :devicons]}}
```

### Keys
key              | type    | example values           | description 
-----------------|---------|--------------------------|------------
:node-separation | integer | 50 (for 50 pixels)       | separation between nodes
:rank-separation | integer | 250 (for 250 pixels)     | separation between ranks
:sprite-libs     | vector  | sprite-lib keywords      | used to render sprites for techs, order defines precedence of the libs
:skinparams      | map     | {"monochrome" "true"}    | render generic skinparams (as `skinparam <key> <value>`)

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

PlantUML plugins also exists for major IDEs and build tools (e.g. IntelliJ,
Eclipse, Maven, Leiningen).

* [PlantUML Plugin for Leiningen](https://github.com/vbauer/lein-plantuml)

## GraphViz
The concept view can be exported as a concept map to a GraphViz *.dot file.

### Rendering GraphViz diagrams
For GraphViz there are a few Visual Studio Code  extensions available that
allow previews of the generated Graphviz files. 

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
:diagram         | map     | {:format "png"}          | render diagram image


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


# Template Based Artifact Generation
Overarch can generate artifacts for model elements and views via templates.
The use cases of the tempates range from reports up to automatic code generation.
Overarch supports forward engineering protected areas for manually written
content in generated artifacts

## Generation Configuration
You can configure the generation of artifacts with an EDN file.
The configuration contains a vector of generation context maps.
A generation context map specifies a selection of model elements, a template
to use, how the template should be applied, and where the resulting artifact
should be created.

key               | type     | values              | default  | description
------------------|----------|---------------------|----------|-------------
:selection        | CRITERIA | {:el :system}       |          | Criteria to select model elements
:view-selection   | CRITERIA | {:el :context-view} |          | Criteria to select views
:template         | PATH     | "report/node.cmb"   |          | Path to the template relative to the template dir
:engine           | :keyword | :comb               | :combsci | The template engine to use (currently just :comb and :combsci)
:encoding         | string   | "UTF-8"             | "UTF-8"  | The encoding of the result artifact
:per-element      | boolean  | true/false          | false    | Apply the template for each element of the selection
:per-namespace    | boolean  | true/false          | false    | Apply the template for each namespace of the selection
:subdir           | string   | "report"            |          | Subdirectory for generated artifact under the generator directory
:namespace-prefix | string   | "src"               |          | Prefix to the namespace to use as path element
:base-namespace   | string   |                     |          | Base namespace to use as path element
:namespace-suffix | string   | "impl"              |          | Suffix to the namespace to use as path element
:prefix           | string   | "Abstract"          |          | Prefix for the filename
:base-name        | string   |                     |          | Base of the filename
:suffix           | string   | "Impl"              |          | Suffix for the filename
:extension        | string   | "md" "clj" "java"   |          | Extension for the filename
:filename         | string   | "README.md"         |          | Specific filename to use
:id-as-namespace  | boolean  | true/false          | false    | Use the element id as the namespace for path generation
:id-as-name       | boolean  | true/false          | false    | Use the name part of the element id as the name for path generation
:protected-areas  | string   | "PA"                |          | Marker for protected areas in the template/artifact
:debug            | boolean  | true/false          | false    | Print parsed template on error

You can add additional (namespaced) keys to the generation context map, which
are available in via the `ctx` symbol in the template.

The model elements, to which a template is applied, are selected via criteria
with the selection key.
A template can be applied to the collection of selected elements or to each
element of the collection.

Templates can also be applied on views selected by criteria with the
:view-selection key. The view selection also returns a collection of views,
even if there is only one view selected. So use the :per-element key to enable
the generation on a view level.

### Example Templates
Example templates can be found in the [templates](/templates) folder.
The provided templates are usable as is, but they also provide a starting point
for organization or project specific templates.

As the model is extensible via custom keys and this information is provided for
the templates, custom templates can make use of all those custom information in
the model. For example, if containers have a custom key :my-org/repository-url,
you can use this information in the documentation templates to provide a link
to the repository of each container.

### Example Generation Config File
```clojure
[;; Report for all systems in the banking namespace
 {:selection {:namespace "banking" :el :system} ; selection criteria for the model elements
 :template "node-report.md.cmb"  ; relative path of the template to apply
 :title "Banking Systems Report" ; title of the report
 :engine :comb                   ; the template engine to use (currently only :comb)
 :encoding "UTF-8"               ; artifact encoding
 :per-element false              ; apply the template for each element of the selection or on the selection as a whole
 :subdir "reports"               ; subdirectory for generated artifact
 ; :namespace-prefix ""          ; prefix for the namespace of the generated artifact
 :base-namespace "systems"       ; base namespace of the generated artifact
 ; :namespace-suffix ""          ; suffix for the namespace of the generated artifact
 ; :prefix ""                    ; prefix for the name of the generated artifact
 :base-name "systems-report"     ; base name of the generated artifact
 ; :suffix ""                    ; suffix for the name of the generated artifact
 :extension "md"                 ; extension of the generated artifact
 :id-as-namespace false          ; use the name as the namespace of the artifact
 ; :protected-area "PA"          ; protected area prefix
 }

;; Report for the REST interfaces in the model
{:selection {:el :model-relation :techs #{"REST"}} ; selection criteria for the model elements
 :template "rel-report.md.cmb"   ; relative path of the template to apply
 :title "REST Interface Report"  ; title of the report
 :engine :comb                   ; the template engine to use (currently only :comb)
 :encoding "UTF-8"               ; artifact encoding
 :per-element false              ; apply the template for each element of the selection or on the selection as a whole
 :subdir "reports"               ; subdirectory for generated artifact
; :namespace-prefix ""           ; prefix for the namespace of the generated artifact
 :base-namespace "interfaces"    ; base namespace of the generated artifact
; :namespace-suffix ""           ; suffix for the namespace of the generated artifact
; :prefix ""                     ; prefix for the name of the generated artifact
 :base-name "systems-report"     ; base name of the generated artifact
; :suffix ""                     ; suffix for the name of the generated artifact
 :extension "md"                 ; extension of the generated artifact
 :id-as-namespace false          ; use the name as the namespace of the artifact
; :protected-area "PA"           ; protected area prefix
}]
```

## Overarch CLI
The relevant CLI options for template based artifact generation are
```
  -m, --model-dir PATH           models     Models directory or path
  -T, --template-dir DIRNAME     templates  Template directory
  -g, --generation-config FILE              Generation configuration
  -G, --generation-dir DIRNAME   generated  Generation artifact directory
  -B, --backup-dir DIRNAME       backup     Generation backup directory
```

Example using a config file in the current directory and default directories
```
java -jar overarch.jar -g gencfg.edn
```

## Comb Template Engine
Overarch uses the [Comb](https://github.com/weavejester/comb)
template syntax by James Reeves.

Comb is a simple templating system for Clojure. You can use Comb to embed
fragments of Clojure code into a text file.

### Syntax
Clojure fragments in a template are demarkated with `<%` and `%>`.
You can embed clojure code as an expression, where the result of the
execution is included in the resulting artifact. You can also embed the clojure
code as a control structure, where the result of the execution of the control
structure is not included in the resulting artifact, only the template text or
other expressions inside of the control structure.

#### Expressions
```clojure
1 + 2 = <%= (+ 1 2) %>
```

Result:
```
1 + 2 = 3
```

#### Control structures
```clojure
foo<%
(dotimes [x 3]
%> bar<%
)
%>
```
Result:
```
foo bar bar bar
```

### API for Templates
In the comb templates you can use most of the functions of the clojure.core
namespace. Additionally the functions of clojure.set and clojure.string are
provided under the aliases `set` and `str`, e.g. `set/union` or `str/join`.

Overarch also provides functions to query and navigate the model under the
`m` alias, e.g. `m/resolve-element`.

### Security Considerations
Comb templates evaluated with `:comb` are compiled and can contain arbitrary
clojure code, which gets evaluated in the context of the overarch process.
Be aware of this fact and review templates accordingly, especially when using
templates from external sources.

When the comp templates are evaluated by the `:combsci` engine, they are
interpreted with Babashka SCI, the small clojure interpreter. This has many
advantages:
* sandboxed evaluation
* control over the the available clojure namespaces and symbols
* independent from the method of providing and starting overarch

As the template code is interpreted with SCI and not compiled, the generation
might be a bit slower then with using compiled templates. But the advantages
outweight the performance penalty by far. 

## Protected Areas
Protected areas are used to protect manually inserted text in generated
artifacts. For example, when generating source code from a code model element,
maybe only the signature of the function may be generated. The body of the
function may have to be inserted by a programmer.

When regenerating the source code artifact, you don't want the manually
inserted code to be deleted or overridden, but preserved.

Given this class node from a model
```clojure
{:el :class
 :id :model/calc
 :name "Calc"
 :ct [{:el method
       :name "square"
       :type "double"
       :visibility :public
       :ct [{:el :field
             :name "x"
             :type "double"}
           ]}
     ]}
```
and a template like
```clojure
public class <%= (:name e) %> {

  <% (doseq [m (:ct e)] %>
  public <%= (:type m) %> <%= (:name m) %>(<%
  (doseq [p (:ct m)] %> (:type m) %> <%= (:name m) %>, <%)%>) {
    // PA-BEGIN(square-impl)
    <%= (:square-impl protected-areas)%>
    // PA-END(square-impl)
  }
  <%)%>
}
```

On the first generation pass, the generated file will look like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    // PA-END(square-impl)
  }
}
```
After manually inserting the implementation, the artifact looks like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    return x * x;
    // PA-END(square-impl)
  }
}
```
On regeneration, the content of the protected area is parsed by the generator before applying the template and reinserted by the template.

So after regeneration the artifact still looks like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    return x * x;
    // PA-END(square-impl)
  }
}
```

# Best Practices
Here are some best practices or guidelines from my practical use of Overarch.

The biggest advice is: **Be consistent in your modelling!**

Consistency is the key to features like selection based views and artifact
generation. It also helps you to navigate and maintain huge models.

# Modelling
## Model Structure And Ids
* Split big models into multiple folders and files. This helps to keep the
  model files small and comprehensible.
* The namespace of the element id should reflect the folder structure of the
  model.
* When refactoring ids, use the find & replace functionality of your editor/IDE
  to replace every occurence in all the model files at once, so the referential
  integrity of the model keeps intact.
* Use refs or `:contained-in` relations to create the hierarchical structure
  across different folders and files.
  * with `:contained-in` relations you don't have to touch the parent when
    adding new children
  * The hierarchies modelled with the `:ct` attribute is converted to
    `:contained-in` relations with the attribute `:synthetic` set to `true`
    when the model is read.


## Relations
* Use specific relation types instead of general `:rel`, if possible.
* The id of a relation should start with the id of the referrer node (the `:from`
  id) followed by a verb based on the relation type and the name part of the id
  of the referred node (the `:to` id), so
  * the naming scheme is consistent
  * you know in which file you have to look for the relation

## Views
* Use selection criteria and includes to specify the content of views.
* Use refs in a view to customize elements for the specific view, e.g to
  override directions for relations.
* By creating a standard set of views with consistent naming, it's easier to
  generate a consistent documentation via templates.

# Templates
* Use the provided templates as a base for customizations.
* Use just the namespace of the element for path generation, elaborate path
  configurations (with namespace-prefix/-suffix) makes link generation
  difficult.
