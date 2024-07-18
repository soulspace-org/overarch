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
  to model the design of the system, e.g. the domain model, and the structure of
  the implementation
* [State-Machine Models](#state-machine-model)
  to model relevant states and transitions of the system
* [Deployment Models](#deployment-model)
  to model the physical architecture of the system

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

Keywords should be prefixed with a namespace to avoid collisions with keywords for
other models, which is especially relevant for identifiers or for custom keys
in the model elements and views. Namespaces may have different parts, separated
by a period (`.`), e.g. `:org.soulspace/overarch`.

```clojure
:keyword
:namespaced/keyword
:my.namespaced/keyword
```

*Unprefixed keywords and the namespace 'overarch' for map keys are reserved for overarch.
 Please use your own prefix if you want to add custom information to the maps in the model.*

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

## References (:ref)
References refer to a model element with the given id. They are primarily used
to refer to the model elements to include in views. They can also be used to
refer to model elements in other model elements, e.g. to split a huge hierarchical
systems into multiple EDN files.

References can have other keys, which are merged with the referred element in
context of the reference. For example you can mark an internal system as external
in the context of a specific view by adding `:external true` to the reference.

## Boundaries
Boundaries group related elements and are normally rendered as a dashed box in a view. There are currently 4 types of boundaries, two of them implicit.

The implicit boundaries are the system boundary and the container boundary.
They are not modelled explicitly but are rendered for referenced systems and containers in specific views. A system boundary is rendered, when an internal system with containers as content is referenced in a container view or component view. Likewise a container boundary is rendered for a referenced container in a component view.

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
:publish    | async       | true        | asynchronous broadcast message (via broker, topic, queue)
:subscribe  | async       | true        | subscribtion to an asynchronous broadcast message (via broker, topic, queue)
:dataflow   | unspecified | unspecified | flow of data independent of the call semantic
:rel        | unspecified | unspecified | unclassified relation


### Additional Keys for Architecture Model Relations
key       | type    | values             | description 
----------|---------|--------------------|------------
:tech     | string  | e.g. "REST"        | technology of the relation

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

A concept model captures relevant concepts of the domain(s) of the system.
The concepts could be part of the ubiquous language of the systems domain.

A concept model can contain the concepts of the domain and the high level elements
of the architecture model, e.g. the persons (actors), external systems and the
system itself with it's containers.

### Logical Data Model for the Concept Model Elements
![Concept Model Elements](/doc/images/overarch/data-model/concept-model-elements.svg)

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

### Logical Data Model for the Use Case Model Elements
![Use Case Model Elements](/doc/images/overarch/data-model/use-case-model-elements.svg)

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

### Logical Data Model for the State Machine Model Elements
![State Machine Elements](/doc/images/overarch/data-model/state-machine-model-elements.svg)

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
![Class Model Elements](/doc/images/overarch/data-model/code-model-elements.svg)

### Packages/Namespace (:package, :namespace)
Packages and namespaces provide a hierarchical structure for the organisation
of the elements of the code model.

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

