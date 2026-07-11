# Overarch Model

## Diagram
![Overarch Model](../overarch/model-view.png)

## Description
Shows all nodes and relations in the Overarch model

## Concepts
| Concept | Description |
|---|---|
| [Actor](../overarch/concepts/actor.md)| An actor (role) in a use case model. |
| [Aggregate](../overarch/concepts/aggregate.md)| A cluster of domain objects that can be treated as a single unit for consistency and invariants. Part of the solution space. |
| [Annotation](../overarch/concepts/annotation.md)| An annotation in the code model. |
| [Artifact](../overarch/concepts/artifact.md)| An artifact in the process model, e.g. a document or a docker container |
| [Bounded Context](../overarch/concepts/bounded-context.md)| A boundary within which a particular domain model is valid and consistent. Part of the solution space. |
| [Capability](../overarch/concepts/capability.md)| A capability in the process model, e.g. a business capability or a technical capability. |
| [Choice](../overarch/concepts/choice.md)| Selects one of several alternative paths |
| [Class](../overarch/concepts/class.md)| A class in the code model. |
| [Command](../overarch/concepts/command.md)| An instruction to perform a specific action or operation within the domain. Part of the solution space. |
| [Component](../overarch/concepts/component.md)| A (logical) building block of a container (e.g. a module or a layer) |
| [Concept](../overarch/concepts/concept.md)| Describes a thing or an idea. |
| [Container](../overarch/concepts/container.md)| a deployed process in the architecture (e.g. a service or an application) |
| [Container Instance](../overarch/concepts/container-instance.md)| Deployed instance of a container in the deployment model |
| [Decision](../overarch/concepts/decision.md)| A decision in the process model, e.g. an architectural decision or a business decision |
| [Deep History State](../overarch/concepts/deep-history-state.md)| Remembers the last active substate in a substate |
| [Domain](../overarch/concepts/domain.md)| A specific area of knowledge, activity or subject matter. Part of the problem space. |
| [Domain Event](../overarch/concepts/domain-event.md)| An event that represents a significant change in the state of the domain. Part of the solution space. |
| [End State](../overarch/concepts/end-state.md)| A final state of a state machine |
| [Entity](../overarch/concepts/entity.md)| An object that is defined by its identity, rather than its attributes. Part of the solution space. |
| [Enum](../overarch/concepts/enum.md)| An enumeration in the code model. |
| [Enum Value](../overarch/concepts/enum-value.md)| A value of an enumeration in the code model. |
| [Field](../overarch/concepts/field.md)| A field of a class, namespace or schema in the code model. |
| [Fork](../overarch/concepts/fork.md)| Splits the flow into multiple concurrent flows |
| [Function](../overarch/concepts/function.md)| A function in the code model. |
| [History State](../overarch/concepts/history-state.md)| Remembers the last active substate |
| [Information](../overarch/concepts/information.md)| Information in the process model. |
| [Interface](../overarch/concepts/interface.md)| A contract that defines a set of operations that a class must implement. |
| [Join](../overarch/concepts/join.md)| Joins multiple concurrent flows into one flow |
| [Knowledge](../overarch/concepts/knowledge.md)| The understanding and skills acquired through experience and education. |
| [Method](../overarch/concepts/method.md)| A method of a class or interface in the code model. |
| [Namespace](../overarch/concepts/namespace.md)| A namespace in the code model. |
| [Node](../overarch/concepts/node.md)| Part of the infrastructure in the deployment model |
| [Organization](../overarch/concepts/organization.md)| An organization (e.g. a company) in the organization model. |
| [Organizational Unit](../overarch/concepts/org-unit.md)| An organizational unit (e.g. a department) in the organization model. |
| [Package](../overarch/concepts/package.md)| A collection of related classes or modules. |
| [Parameter](../overarch/concepts/parameter.md)| A parameter of a method or function in the code model. |
| [Permission](../overarch/concepts/permission.md)| A permission, e.g. an access right. |
| [Person](../overarch/concepts/person.md)| A human actor or role (deprecated, use role). |
| [Policy](../overarch/concepts/policy.md)| A rule or guideline that governs the behavior or actions within the domain. Part of the solution space. |
| [Process](../overarch/concepts/process.md)| A process in the process model, e.g. a business process or a software development process. |
| [Protocol](../overarch/concepts/protocol.md)| A contract that defines a set of operations that a type must implement. |
| [Requirement](../overarch/concepts/requirement.md)| A Requirement in the process model, e.g. a functional or non-functional requirement |
| [Role](../overarch/concepts/role.md)| A role in the model, e.g. a person or a system. |
| [Schema](../overarch/concepts/schema.md)| Describes the structure of data, e.g. a database schema or a JSON schema. |
| [Start State](../overarch/concepts/start-state.md)| The initial state of a state machine |
| [State](../overarch/concepts/state.md)| A state in a state machine |
| [State Machine](../overarch/concepts/state-machine.md)| A collection of states and transitions |
| [Stereotype](../overarch/concepts/stereotype.md)| A stereotype in the code model. |
| [System](../overarch/concepts/system.md)| A system relevant in the architecture |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md)| A technical node in the architecture model |
| [Use Case](../overarch/concepts/use-case.md)| A use case in the use case model. |
| [Value Object](../overarch/concepts/value-object.md)| An object that is defined by its attributes, rather than its identity. Part of the solution space. |
| [Version](../overarch/concepts/version.md)| A version of an artifact in the process model |

## Generalizations
| From | Name | To | Description |
|---|---|---|---|
| [History State](../overarch/concepts/history-state.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Join](../overarch/concepts/join.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Choice](../overarch/concepts/choice.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Component](../overarch/concepts/component.md) | is a | [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) |  |
| [End State](../overarch/concepts/end-state.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Deep History State](../overarch/concepts/deep-history-state.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Container](../overarch/concepts/container.md) | is a | [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) |  |
| [Start State](../overarch/concepts/start-state.md) | is a | [State](../overarch/concepts/state.md) |  |
| [Fork](../overarch/concepts/fork.md) | is a | [State](../overarch/concepts/state.md) |  |
| [System](../overarch/concepts/system.md) | is a | [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) |  |
| [System](../overarch/concepts/system.md) | is an | [Actor](../overarch/concepts/actor.md) | a system can be a primary or supporting actor of a use case |
| [Person](../overarch/concepts/person.md) | is an | [Actor](../overarch/concepts/actor.md) | a person/user role can be a primary actor of a use case |

## Features
| From | Name | To | Description |
|---|---|---|---|
| [Enum](../overarch/concepts/enum.md) | contains | [Enum Value](../overarch/concepts/enum-value.md) |  |
| [Interface](../overarch/concepts/interface.md) | contains | [Method](../overarch/concepts/method.md) |  |
| [Class](../overarch/concepts/class.md) | contains | [Field](../overarch/concepts/field.md) |  |
| [Package](../overarch/concepts/package.md) | contains | [Class](../overarch/concepts/class.md) |  |
| [Namespace](../overarch/concepts/namespace.md) | contains | [Field](../overarch/concepts/field.md) |  |
| [Method](../overarch/concepts/method.md) | contains | [Parameter](../overarch/concepts/parameter.md) |  |
| [Package](../overarch/concepts/package.md) | contains | [Package](../overarch/concepts/package.md) |  |
| [Protocol](../overarch/concepts/protocol.md) | contains | [Function](../overarch/concepts/function.md) |  |
| [Package](../overarch/concepts/package.md) | contains | [Enum](../overarch/concepts/enum.md) |  |
| [System](../overarch/concepts/system.md) | contains | [Container](../overarch/concepts/container.md) |  |
| [Organization](../overarch/concepts/organization.md) | contains | [Organizational Unit](../overarch/concepts/org-unit.md) | organizational unit of the organization |
| [Organizational Unit](../overarch/concepts/org-unit.md) | contains | [Organizational Unit](../overarch/concepts/org-unit.md) | sub-units of the organizational unit |
| [Package](../overarch/concepts/package.md) | contains | [Interface](../overarch/concepts/interface.md) |  |
| [Namespace](../overarch/concepts/namespace.md) | contains | [Function](../overarch/concepts/function.md) |  |
| [Component](../overarch/concepts/component.md) | contains | [Namespace](../overarch/concepts/namespace.md) |  |
| [State Machine](../overarch/concepts/state-machine.md) | contains | [State](../overarch/concepts/state.md) |  |
| [Node](../overarch/concepts/node.md) | contains | [Node](../overarch/concepts/node.md) |  |
| [Container](../overarch/concepts/container.md) | contains | [Component](../overarch/concepts/component.md) |  |
| [Namespace](../overarch/concepts/namespace.md) | contains | [Protocol](../overarch/concepts/protocol.md) |  |
| [Class](../overarch/concepts/class.md) | contains | [Method](../overarch/concepts/method.md) |  |
| [Function](../overarch/concepts/function.md) | contains | [Parameter](../overarch/concepts/parameter.md) |  |
| [Schema](../overarch/concepts/schema.md) | contains | [Field](../overarch/concepts/field.md) |  |
| [Package](../overarch/concepts/package.md) | contains | [Annotation](../overarch/concepts/annotation.md) |  |
| [Component](../overarch/concepts/component.md) | contains | [Package](../overarch/concepts/package.md) |  |
| [State](../overarch/concepts/state.md) | contains substate | [State](../overarch/concepts/state.md) |  |

## Roles
| Person/Role | Description |
|---|---|
| [Modeller](../overarch/roles/modeller.md)| Models the architecture of a system and specifies views of the model. |
| [Template Programmer](../overarch/roles/template-programmer.md)| Creates templates for artifact generation. |

## Requirements
| Requirement | Description |
|---|---|
| [Namespaced IDs for Overarch elements](../overarch/architecture/requirement/namespaced-ids.md)| For models to be composeable, the IDs for elements of Overarch models must be namespaced to avoid clashes across different models. |

## Decisions
| Decision | Description |
|---|---|
| [Data Format for Models](../overarch/architecture/decision/data-format.md)| Extensible Data Notation (EDN) is used as the data format for models and other customization/configuration data |
| [Implementation Architecture](../overarch/architecture/decision/implementation-architecture.md)| Clean Architecture is used to structure the implementation |
| [Implementation Language](../overarch/architecture/decision/implementation-language.md)| Clojure is used as sole implementation language |
| [Internal Data Representation](../overarch/architecture/decision/data-representation.md)| Model elements are stored in a graph of nodes and relations |

## Use Cases
| Use Case | Description |
|---|---|
| [create templates](../overarch/use-case/create-templates.md)| Create templates to generate artifacts from model selections. |
| [define views](../overarch/use-case/define-views.md)| Textually define views of the system in the Overarch view model. |
| [export JSON](../overarch/use-case/export-json.md)| Export the model and views to JSON. |
| [export Structurizr](../overarch/use-case/export-structurizr.md)| Export the architecture model and views as a Structurizr workspace. |
| [export model](../overarch/use-case/export-model.md)| Export the model in different formats. |
| [generate artifacts ](../overarch/use-case/generate-artifacts.md)| Generate artifacts with templates from model selections. |
| [model system](../overarch/use-case/model-system.md)| Textually model the system in the Overarch data model. |
| [query model](../overarch/use-case/query-model.md)| Query the model with selection criteria. |
| [render views](../overarch/use-case/render-views.md)| Render the views of the system. |

## Extends
| From | Name | To | Description |
|---|---|---|---|
| [export JSON](../overarch/use-case/export-json.md) |  | [export model](../overarch/use-case/export-model.md) |  |
| [export Structurizr](../overarch/use-case/export-structurizr.md) |  | [export model](../overarch/use-case/export-model.md) |  |

## Uses
| From | Name | To | Description |
|---|---|---|---|
| [Modeller](../overarch/roles/modeller.md) |  | [generate artifacts ](../overarch/use-case/generate-artifacts.md) |  |
| [create templates](../overarch/use-case/create-templates.md) |  | [Editor/IDE](../overarch/architecture/editor.md) |  |
| [Modeller](../overarch/roles/modeller.md) |  | [model system](../overarch/use-case/model-system.md) |  |
| [define views](../overarch/use-case/define-views.md) |  | [Editor/IDE](../overarch/architecture/editor.md) |  |
| [Modeller](../overarch/roles/modeller.md) |  | [query model](../overarch/use-case/query-model.md) |  |
| [Modeller](../overarch/roles/modeller.md) |  | [define views](../overarch/use-case/define-views.md) |  |
| [Modeller](../overarch/roles/modeller.md) |  | [render views](../overarch/use-case/render-views.md) |  |
| [Template Programmer](../overarch/roles/template-programmer.md) |  | [create templates](../overarch/use-case/create-templates.md) |  |
| [Modeller](../overarch/roles/modeller.md) |  | [export model](../overarch/use-case/export-model.md) |  |
| [model system](../overarch/use-case/model-system.md) |  | [Editor/IDE](../overarch/architecture/editor.md) |  |

## Systems
| System | Description |
|---|---|
| [Build Pipeline](../overarch/architecture/build-pipeline.md)| Generates and publishes artifacts (e.g. build script/pipeline) |
| [Diagram Generators](../overarch/architecture/diagram-generator.md)| generates diagrams from text (e.g. Graphviz, PlantUML, Mermaid) |
| [Editor/IDE](../overarch/architecture/editor.md)| Tool for describing the architecture model and the views. |
| [Overarch](../overarch/architecture/overarch.md)| An Open Architecture Knowledge Platform |
| [Repository](../overarch/architecture/vc-repository.md)| Version controlled repository (e.g. git) |

## Containers
| Container | Description |
|---|---|
| [Overarch CLI](../overarch/architecture/overarch-cli.md)| CLI tool for the generation of value from the knowledge. |

## Components
| Component | Description |
|---|---|
| [adapter.exports.json](../overarch/adapter/exports/json.md)| Contains the implementation of the JSON export. |
| [adapter.exports.structurizr](../overarch/adapter/exports/structurizr.md)| Contains the implementation of the Structurizr export. |
| [adapter.reader.model-reader](../overarch/adapter/reader/model-reader.md)| Contains the functions for reading and building models and setting the repository state. The multimethods should be implemented by specific readers. |
| [adapter.render.graphviz](../overarch/adapter/render/graphviz.md)| Contains the implementation of the Graphviz rendering. |
| [adapter.render.markdown](../overarch/adapter/render/markdown.md)| Contains the implementation of the Markdown rendering. |
| [adapter.render.plantuml](../overarch/adapter/render/plantuml.md)| Contains the implementation of the PlantUML rendering. |
| [adapter.template.comb](../overarch/adapter/template/comb.md)| Contains the implementation of a template engine for Comb templates. |
| [adapter.ui.cli](../overarch/adapter/ui/cli.md)| Parses the command line options and dispatches functions on them. |
| [application.export](../overarch/application/export.md)| Contains the export multimethods to be implemented by the concrete export adapter implementations. |
| [application.model-repository](../overarch/application/model-repository.md)| Contains a stateful representation of the model and accessor functions to the model state. |
| [application.reader](../overarch/application/reader.md)| Contains the reader multimethods to be implemented by the concrete reader adapter implementations. |
| [application.render](../overarch/application/render.md)| Contains the render multimethods to be implemented by the concrete render adapter implementations. |
| [application.template](../overarch/application/template.md)| Contains the functions for template based artifact generation and multimethods to be implemented by the concrete template engine adapter implementations. |
| [domain.model](../overarch/domain/model.md)| Contains the model specification and functions. |
| [domain.view](../overarch/domain/view.md)| Contains the view specification and functions. |
| [util.functions](../overarch/util/functions.md)| Contains common functions. |
| [util.io](../overarch/util/io.md)| Contains I/O related functions. |

## Synchronous Requests
| From | Name | To | Technology | Description |
|---|---|---|---|---|
| [adapter.render.markdown](../overarch/adapter/render/markdown.md) |  queries | [domain.view](../overarch/domain/view.md) |  | view |
| [application.template](../overarch/application/template.md) | accesses | [domain.view](../overarch/domain/view.md) |  | view |
| [Build Pipeline](../overarch/architecture/build-pipeline.md) | calls | [Overarch CLI](../overarch/architecture/overarch-cli.md) |  | generate artifacts |
| [adapter.ui.cli](../overarch/adapter/ui/cli.md) | calls | [application.export](../overarch/application/export.md) |  | export functions |
| [adapter.render.plantuml](../overarch/adapter/render/plantuml.md) | calls | [util.io](../overarch/util/io.md) |  | loads sprite mappings |
| [Build Pipeline](../overarch/architecture/build-pipeline.md) | calls | [Overarch](../overarch/architecture/overarch.md) |  | generate artifacts |
| [adapter.ui.cli](../overarch/adapter/ui/cli.md) | calls | [application.template](../overarch/application/template.md) |  | template functions |
| [adapter.render.graphviz](../overarch/adapter/render/graphviz.md) | calls | [domain.view](../overarch/domain/view.md) |  | view queries and rendering functions |
| [Build Pipeline](../overarch/architecture/build-pipeline.md) | calls | [Diagram Generators](../overarch/architecture/diagram-generator.md) |  | generate diagrams |
| [adapter.ui.cli](../overarch/adapter/ui/cli.md) | calls | [application.render](../overarch/application/render.md) |  | render functions |
| [adapter.exports.json](../overarch/adapter/exports/json.md) | calls | [util.io](../overarch/util/io.md) |  | writes JSON |
| [adapter.ui.cli](../overarch/adapter/ui/cli.md) | loads | [application.model-repository](../overarch/application/model-repository.md) |  | model |
| [Modeller](../overarch/roles/modeller.md) | models in | [Editor/IDE](../overarch/architecture/editor.md) |  | creates models and views |
| [Template Programmer](../overarch/roles/template-programmer.md) | programs in | [Editor/IDE](../overarch/architecture/editor.md) |  | creates templates |
| [adapter.exports.structurizr](../overarch/adapter/exports/structurizr.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [adapter.render.markdown](../overarch/adapter/render/markdown.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [adapter.render.plantuml](../overarch/adapter/render/plantuml.md) | queries | [domain.view](../overarch/domain/view.md) |  | view |
| [application.template](../overarch/application/template.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [adapter.render.graphviz](../overarch/adapter/render/graphviz.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [domain.view](../overarch/domain/view.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [adapter.render.plantuml](../overarch/adapter/render/plantuml.md) | queries | [domain.model](../overarch/domain/model.md) |  | model |
| [Modeller](../overarch/roles/modeller.md) | uses | [Overarch](../overarch/architecture/overarch.md) |  | for diagram generation and model transformation. |

## Dataflows
| From | Name | To | Technology | Description |
|---|---|---|---|---|
| [Overarch CLI](../overarch/architecture/overarch-cli.md) | generated artifacts | [Repository](../overarch/architecture/vc-repository.md) |  | e.g. markdown, dot/puml files |
| [Overarch](../overarch/architecture/overarch.md) | generated artifacts | [Repository](../overarch/architecture/vc-repository.md) |  | e.g. markdown, dot/puml files |
| [Repository](../overarch/architecture/vc-repository.md) | generated diagram files | [Diagram Generators](../overarch/architecture/diagram-generator.md) |  |  |
| [Diagram Generators](../overarch/architecture/diagram-generator.md) | generated images | [Repository](../overarch/architecture/vc-repository.md) |  |  |
| [Repository](../overarch/architecture/vc-repository.md) | models, templates | [Overarch CLI](../overarch/architecture/overarch-cli.md) |  |  |
| [Editor/IDE](../overarch/architecture/editor.md) | models, templates | [Repository](../overarch/architecture/vc-repository.md) |  |  |
| [Repository](../overarch/architecture/vc-repository.md) | models, templates | [Overarch](../overarch/architecture/overarch.md) |  |  |

## Classes
| Class | Description |
|---|---|
| [abstract-node](../overarch/data-model/abstract-node.md)| A node which may have an abstract field. |
| [actor](../overarch/data-model/actor.md)| An actor (role) in a use case model. The actor can be human or technical, e.g. a system or time. If the architecture model contains persons or systems acting with the use cases, you can replace the actors with these elements. |
| [aggregate](../overarch/data-model/aggregate.md)| A cluster of domain objects that can be treated as a single unit for consistency and invariants. Part of the solution space. |
| [aggregation](../overarch/data-model/aggregation.md)| A aggregation relationship between two classes in the code model. |
| [architecture-model-element](../overarch/data-model/architecture-model-element.md)| An element of the architecture model. |
| [architecture-model-node](../overarch/data-model/architecture-model-node.md)| A node in the architecture model. |
| [architecture-model-relation](../overarch/data-model/architecture-model-relation.md)| A relation in the architecture model. |
| [architecture-view](../overarch/data-model/architecture-view.md)| An architectural view. |
| [artifact](../overarch/data-model/artifact.md)| An artifact in the process model |
| [artifact-of](../overarch/data-model/artifact-of.md)| A relation between artifacts and other process or deployment model nodes. |
| [association](../overarch/data-model/association.md)| A association relationship between two classes in the code model. |
| [associative-relation](../overarch/data-model/associative-relation.md)| A relation that may provide associative access to the referred element. |
| [boundary-node](../overarch/data-model/boundary-node.md)| A grouping of elements belonging together in a context. |
| [bounded-context](../overarch/data-model/bounded-context.md)| A boundary within which a particular domain model is valid and consistent. Part of the solution space. |
| [capability](../overarch/data-model/capability.md)| A capability in the process model |
| [choice](../overarch/data-model/choice.md)| A choice of transitions in a state machine. A choice has a single incoming transition and multiple outgoing transitions with the result of the condition of the choice. |
| [class](../overarch/data-model/class.md)| A class in the code model. |
| [code-model-element](../overarch/data-model/code-model-element.md)| An element in a code model. |
| [code-model-node](../overarch/data-model/code-model-node.md)| A node in the code model. |
| [code-model-relation](../overarch/data-model/code-model-relation.md)| A relation in the code model. |
| [code-view](../overarch/data-model/code-view.md)| The code view is a static design view. It shows the code structure of some components of the system. |
| [collaborates-with](../overarch/data-model/collaborates-with.md)| A relation between two organisational model nodes. |
| [command](../overarch/data-model/command.md)| An instruction to perform a specific action or operation within the domain. |
| [component](../overarch/data-model/component.md)| A compontent is a part of a container and describes a (logical) building block of a container (e.g. a module or a layer). |
| [component-view](../overarch/data-model/component-view.md)| The component view is a static architectural view. It shows the component structure of a container and the interactions between these components and with it's environment consisting of users and external systems. |
| [composition](../overarch/data-model/composition.md)| A composition relationship between two classes in the code model. |
| [concept](../overarch/data-model/concept.md)| A concept in the (ubiquous) language of the system. |
| [concept-model-element](../overarch/data-model/concept-model-element.md)| An element in the concept model. |
| [concept-model-node](../overarch/data-model/concept-model-node.md)| A node in the concept model. |
| [concept-model-relation](../overarch/data-model/concept-model-relation.md)| A relation in the concept model. |
| [concept-view](../overarch/data-model/concept-view.md)| The concept view is a graphical view. It shows the concepts as a concept map with the relations between the concepts. |
| [constraint](../overarch/data-model/constraint.md)| A constraint in the process model |
| [constraint-for](../overarch/data-model/constraint-for.md)| A relation between constraint and other elements. |
| [contained-in](../overarch/data-model/contained-in.md)| A composition relation from child to parent. |
| [container](../overarch/data-model/container.md)| A container is a part of a system and describes a deployed process in the architecture (e.g. a service or an application). A container is a compound element which contains the components of the implementation. A container can be used in the architecture model, the deployment model and the use case model. |
| [container-view](../overarch/data-model/container-view.md)| The container view is a static architectural view. It shows the process structure of the system under description and the interactions between these containers and with it's environment consisting of users and external systems. |
| [context-boundary](../overarch/data-model/context-boundary.md)| A boundary of a bounded context. |
| [context-view](../overarch/data-model/context-view.md)| The (system) context view is a static architectural view. It shows the system under description and the interactions with it's environment consisting of users and external systems. |
| [control](../overarch/data-model/control.md)| A control in the process model |
| [control-for](../overarch/data-model/control-for.md)| A relation between control and other elements. |
| [dataflow](../overarch/data-model/dataflow.md)| A flow of data between two elements of the architecture. |
| [decision](../overarch/data-model/decision.md)| An decision in the process model |
| [deep-history-state](../overarch/data-model/deep-history-state.md)| A state with a deep history. |
| [dependency](../overarch/data-model/dependency.md)| A dependency relationship between two elements in the code model. |
| [deployed-to](../overarch/data-model/deployed-to.md)| A deployment relation between a container or an artifact and a node in the deployment model. The container or artifact is deployed to the node. |
| [deployment-model-element](../overarch/data-model/deployment-model-element.md)| An element in the deployment model. |
| [deployment-model-node](../overarch/data-model/deployment-model-node.md)| A node in the deployment model. |
| [deployment-model-relation](../overarch/data-model/deployment-model-relation.md)| A relation in the deployment model. |
| [deployment-structure-view](../overarch/data-model/deployment-structure-view.md)| The deployment structure view is a graphical view. It shows the hierarchy of the infrastructure and deployments as an organigram. |
| [deployment-view](../overarch/data-model/deployment-view.md)| The deployment view is a static architectural view. It shows the deployment of a system with the infrastructure modelled as nodes and the containers of the system deployed in these nodes. |
| [domain](../overarch/data-model/domain.md)| A specific area of knowledge, activity or subject matter. Part of the problem space. |
| [domain-event](../overarch/data-model/domain-event.md)| A record of a significant event that happened in the domain that domain experts would recognize and care about. A fact in the past. |
| [domain-model-element](../overarch/data-model/domain-model-element.md)| An element in the domain model. |
| [domain-model-node](../overarch/data-model/domain-model-node.md)| A node in the domain model. |
| [domain-model-relation](../overarch/data-model/domain-model-relation.md)| A relation in the domain model. |
| [dynamic-view](../overarch/data-model/dynamic-view.md)| The dynamic view is an architectural and behavioural view. It shows the order of some interactions between elements of the architecture. |
| [element](../overarch/data-model/element.md)| An element of data. |
| [end-state](../overarch/data-model/end-state.md)| A end state in a state machine. |
| [enterprise-boundary](../overarch/data-model/enterprise-boundary.md)| A boundary of an enterprise or a company. |
| [entity](../overarch/data-model/entity.md)| An object that is defined by its identity rather than its attributes. Part of the solution space. |
| [enum](../overarch/data-model/enum.md)| An enumeration of typed related values in the code model. |
| [enum-value](../overarch/data-model/enum-value.md)| A value of an enumeration in the code model. |
| [extends](../overarch/data-model/extends.md)| An extends relationship between two use cases. |
| [field](../overarch/data-model/field.md)| A field in a code model element. |
| [fork](../overarch/data-model/fork.md)| A fork of transitions in a state machine. A fork has a single incoming transition and multiple outgoing transitions. |
| [function](../overarch/data-model/function.md)| A function in the code model. |
| [generalizes](../overarch/data-model/generalizes.md)| A generalizes relationship between two use case elements. |
| [glossary-view](../overarch/data-model/glossary-view.md)| The glossary view is a textual view. It shows the main elements and concepts of the system under description alphabetically sorted. |
| [goal](../overarch/data-model/goal.md)| An goal in the process model |
| [goal-for](../overarch/data-model/goal-for.md)| A relation between goal and other elements. |
| [granted-for](../overarch/data-model/granted-for.md)| A relationship between a permission and a role. |
| [has](../overarch/data-model/has.md)| A composition or association relationship between the concepts. |
| [history-state](../overarch/data-model/history-state.md)| A state with history. |
| [implementation](../overarch/data-model/implementation.md)| An implementation relationship between a code and an interface/protocol in the class model. |
| [implements](../overarch/data-model/implements.md)| A trace relation from an implementation to its definition. |
| [include](../overarch/data-model/include.md)| A include relationship between two use cases. |
| [information](../overarch/data-model/information.md)| An information in the process model |
| [inheritance](../overarch/data-model/inheritance.md)| An inheritance relationship between two classes in the code model. |
| [input-of](../overarch/data-model/input-of.md)| A relation between artifacts, information or knowledge and processes. |
| [instance-of](../overarch/data-model/instance-of.md)| An element is an instance of another element. |
| [interface](../overarch/data-model/interface.md)| An interface in the code model. An interface defines related methods. |
| [is-a](../overarch/data-model/is-a.md)| A specialization relationship between the concepts. |
| [join](../overarch/data-model/join.md)| A join of transitions in a state machine. A join has multiple incoming transitions and a single outgoing transition. |
| [knowledge](../overarch/data-model/knowledge.md)| A process in the process model |
| [link](../overarch/data-model/link.md)| A link between two nodes of the deployment model. |
| [method](../overarch/data-model/method.md)| A method in a code model element. |
| [model](../overarch/data-model/model.md)| A representation of a domain that captures the essential aspects. Part of the solution space. |
| [model-element](../overarch/data-model/model-element.md)| An element which describes the relation of elements. |
| [model-node](../overarch/data-model/model-node.md)| An element which is a node in the model. |
| [model-relation](../overarch/data-model/model-relation.md)| An element which is a relation in the and describes the relationship of two model nodes. |
| [model-view](../overarch/data-model/model-view.md)| The model view is a graphical view. It shows all selected elements as a graph. |
| [namespace](../overarch/data-model/namespace.md)| A namespace in the code model. Namespaces provide a hierarchical structure for the organisation of the elements of the class model. |
| [node](../overarch/data-model/node.md)| An element of the deployment model of the system under description. A node is a compound element which contains other nodes or containers referenced from the architecture model. |
| [optional-node](../overarch/data-model/optional-node.md)| A node which may have an optional field. |
| [org-unit](../overarch/data-model/org-unit.md)| An organizational unit (e.g. a department) in the organization model. |
| [organization](../overarch/data-model/organization.md)| An organization (e.g. a company) in the organization model. |
| [organization-model-element](../overarch/data-model/organization-model-element.md)| An element in the organization model |
| [organization-model-node](../overarch/data-model/organization-model-node.md)| A node in the organization model |
| [organization-model-relation](../overarch/data-model/organization-model-relation.md)| A relation in the organization model |
| [organization-structure-view](../overarch/data-model/organization-structure-view.md)| The organization structure view is a graphical view. It shows the hierarchy of the organization and its parts as an organigram. |
| [output-of](../overarch/data-model/output-of.md)| A relation between artifacts, information or knowledge and processes. |
| [package](../overarch/data-model/package.md)| A package in the code model. Packages provide a hierarchical structure for the organisation of the elements of the class model. |
| [parameter](../overarch/data-model/parameter.md)| A parameter in a code model method or function. |
| [permission](../overarch/data-model/permission.md)| A permission, entitlement or access right for some action e.g. in a system. |
| [permission-of](../overarch/data-model/permission-of.md)| A relationship between a permission and an architecture node, e.g. a system. |
| [person](../overarch/data-model/person.md)| A human actor or role working with the system under description. A person can be used in the architecture model and the use case model. |
| [policy](../overarch/data-model/policy.md)| A rule or guideline that governs the behavior or actions within the domain. |
| [process](../overarch/data-model/process.md)| A process in the process model |
| [process-model-element](../overarch/data-model/process-model-element.md)| An element in the process model |
| [process-model-node](../overarch/data-model/process-model-node.md)| A node in the process model |
| [process-model-relation](../overarch/data-model/process-model-relation.md)| A relation in the process model |
| [protocol](../overarch/data-model/protocol.md)| A protocol in the code model. A protocol defines related functions. |
| [publish](../overarch/data-model/publish.md)| Publishing of asynchronous events between two elements of the architecture (receiver should be a broker or topic). |
| [ref](../overarch/data-model/ref.md)| A reference to an element. |
| [regulation](../overarch/data-model/regulation.md)| A regulation in the process model |
| [regulation-for](../overarch/data-model/regulation-for.md)| A relation between regulation and other elements. |
| [rel](../overarch/data-model/rel.md)| A generic relation between the concepts. |
| [request](../overarch/data-model/request.md)| A synchronous request between two elements of the architecture. |
| [required-for](../overarch/data-model/required-for.md)| A relation between other nodes and capabilities. |
| [requirement](../overarch/data-model/requirement.md)| A requirement in the process model |
| [response](../overarch/data-model/response.md)| A response to a synchronous request between two elements of the architecture. |
| [responsible-for](../overarch/data-model/responsible-for.md)| A relation between organisational model nodes and architecture or deployment model elements. |
| [role](../overarch/data-model/role.md)| A human actor or role working with the system under description. A person can be used in the architecture model and the use case model. |
| [role-in](../overarch/data-model/role-in.md)| A relation between persons and organization units or processes. |
| [role-model-element](../overarch/data-model/role-model-element.md)| An element of the role model. |
| [role-model-node](../overarch/data-model/role-model-node.md)| A node in the role model. |
| [role-model-relation](../overarch/data-model/role-model-relation.md)| A relation in the role model. |
| [schema](../overarch/data-model/schema.md)| A schema in the code model. A schema defines the shape of data. |
| [scoped-node](../overarch/data-model/scoped-node.md)| A node which may have a scope field. |
| [send](../overarch/data-model/send.md)| An asynchronous message or command between two elements of the architecture (point-to-point). |
| [spec](../overarch/data-model/rendering-spec.md)| A specification of the rendering options for a view. |
| [start-state](../overarch/data-model/start-state.md)| A start state in a state machine. |
| [state](../overarch/data-model/state.md)| A state in a state machine. |
| [state-machine](../overarch/data-model/state-machine.md)| A state machine as root element of the state machine model. A state machine encapsulates a set of states of a system and the transitions between these states. |
| [state-machine-model-element](../overarch/data-model/state-machine-model-element.md)| An element in a state machine model. |
| [state-machine-model-node](../overarch/data-model/state-machine-model-node.md)| A node in the state machine model. |
| [state-machine-model-relation](../overarch/data-model/state-machine-model-relation.md)| A relation in the state machine model. |
| [state-machine-view](../overarch/data-model/state-machine-view.md)| The state machine view is a behavioural design view. It shows states and transitions of a state machine. |
| [subscribe](../overarch/data-model/subscribe.md)| Subscription of asynchronous events between two elements of the architecture (sender should be a broker or topic). |
| [system](../overarch/data-model/system.md)| A system relevant in the architecture. A system can be an external system, which is modelled as a black box or an internal system, a system under description, which is modelled as a compound element with all the containers of the system. A system can be used in the architecture model, the deployment model (external systems) and the use case model (external systems). |
| [system-landscape-view](../overarch/data-model/system-landscape-view.md)| The system landscape view is a static architectural view. It shows the broader context of the system under description. It contains the system under descriptions, it's direct users and interacting external systems and maybe additional systems and users which play a role in the broader context of the system under description. |
| [system-structure-view](../overarch/data-model/system-structure-view.md)| The system structure view is a graphical view. It shows the hierarchy of the system and its parts as an organigram. |
| [technical-architecture-model-node](../overarch/data-model/technical-architecture-model-node.md)| A technical node in the architecture model. |
| [technical-architecture-model-relation](../overarch/data-model/technical-architecture-model-relation.md)| A technical relation in the architecture model. |
| [technical-element](../overarch/data-model/technical-element.md)| An element which is implemented in the given technologies. |
| [test](../overarch/data-model/test.md)| A test in the process model |
| [test-for](../overarch/data-model/test-for.md)| A relation between a test and other elements. |
| [transition](../overarch/data-model/transition.md)| A transition from on state to another in effect of an event in the state machine. |
| [type-hierarchy-relation](../overarch/data-model/type-hierarchy-relation.md)| A relation that establishes a type hierarchy. |
| [typed-node](../overarch/data-model/typed-node.md)| A node which may have a type field. |
| [use-case](../overarch/data-model/use-case.md)| A use case in the use case model. |
| [use-case-model-element](../overarch/data-model/use-case-model-element.md)| An element in a use case model. |
| [use-case-model-node](../overarch/data-model/use-case-model-node.md)| A node in the use case model of overarch. |
| [use-case-model-relation](../overarch/data-model/use-case-model-relation.md)| A relation in the use case model of overarch. |
| [use-case-view](../overarch/data-model/use-case-view.md)| The use case view is a behavioural view of the (functional) requirements. It shows the actors of the system and the use cases to provide an overview of the functionality of the system under description. |
| [uses](../overarch/data-model/uses.md)| A use relationship between an actor and a use case (or vice versa). |
| [value-object](../overarch/data-model/value-object.md)| An object that is defined by its attributes rather than its identity. Part of the solution space. |
| [version](../overarch/data-model/version.md)| A version of an artifact in the process model |
| [version-of](../overarch/data-model/version-of.md)| A relation between versions and artifacts. |
| [view](../overarch/data-model/view.md)| An element for describing a view. |
| [visibility-node](../overarch/data-model/visibility-node.md)| A node which may have a visibility field. |

## Other Relationships
| From | Name | To | Description |
|---|---|---|---|
| [Bounded Context](../overarch/concepts/bounded-context.md) | contains | [Aggregate](../overarch/concepts/aggregate.md) | aggregates contained in the bounded context |
| [Container](../overarch/concepts/container.md) | deployed on | [Node](../overarch/concepts/node.md) |  |
| [Use Case](../overarch/concepts/use-case.md) | extends | [Use Case](../overarch/concepts/use-case.md) | describes the extension of the functionality of the referred use case |
| [Class](../overarch/concepts/class.md) | implementation | [Interface](../overarch/concepts/interface.md) |  |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | implements | [Decision](../overarch/concepts/decision.md) |  |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | implements | [Requirement](../overarch/concepts/requirement.md) |  |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | implements | [Process](../overarch/concepts/process.md) |  |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | implements | [Use Case](../overarch/concepts/use-case.md) |  |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | implements | [State Machine](../overarch/concepts/state-machine.md) |  |
| [Use Case](../overarch/concepts/use-case.md) | includes | [Use Case](../overarch/concepts/use-case.md) | describes the inclusion of the functionality of the referred use case |
| [Class](../overarch/concepts/class.md) | inheritance | [Class](../overarch/concepts/class.md) |  |
| [Artifact](../overarch/concepts/artifact.md) | input of | [Process](../overarch/concepts/process.md) | artifact required by the process |
| [Node](../overarch/concepts/node.md) | link | [Node](../overarch/concepts/node.md) |  |
| [Artifact](../overarch/concepts/artifact.md) | output of | [Process](../overarch/concepts/process.md) | artifact produced by the process |
| [Information](../overarch/concepts/information.md) | output of | [Process](../overarch/concepts/process.md) | information produced by the process |
| [Person](../overarch/concepts/person.md) | request | [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) |  |
| [Knowledge](../overarch/concepts/knowledge.md) | required | [Person](../overarch/concepts/person.md) | knowledge required for a role |
| [Process](../overarch/concepts/process.md) | required for | [Capability](../overarch/concepts/capability.md) | process required for the capability |
| [Knowledge](../overarch/concepts/knowledge.md) | required for | [Capability](../overarch/concepts/capability.md) | knowledge required for the capability |
| [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | required for | [Capability](../overarch/concepts/capability.md) | technical component required for the capability |
| [Information](../overarch/concepts/information.md) | required for | [Process](../overarch/concepts/process.md) | information required to fullfill the process |
| [Information](../overarch/concepts/information.md) | required for | [Knowledge](../overarch/concepts/knowledge.md) | information required to gain knowledge |
| [Organizational Unit](../overarch/concepts/org-unit.md) | responsible for | [Capability](../overarch/concepts/capability.md) | organizational unit responsible for the capability |
| [Organizational Unit](../overarch/concepts/org-unit.md) | responsible for | [Technical Architecture Node](../overarch/concepts/technical-architecture-node.md) | organizational unit responsible for the technical architecture node (system, container, component) |
| [Person](../overarch/concepts/person.md) | role in | [Organizational Unit](../overarch/concepts/org-unit.md) | role in the organizational unit |
| [Person](../overarch/concepts/person.md) | role in | [Process](../overarch/concepts/process.md) | personal role in a process |
| [Modeller](../overarch/roles/modeller.md) | starts | [adapter.ui.cli](../overarch/adapter/ui/cli.md) | with the provided options. |
| [State](../overarch/concepts/state.md) | transition | [State](../overarch/concepts/state.md) | describes the transition fom one state to another state triggered by an event |
| [Domain Event](../overarch/concepts/domain-event.md) | triggers | [Policy](../overarch/concepts/policy.md) | policy triggered by the domain event |
| [Policy](../overarch/concepts/policy.md) | triggers | [Command](../overarch/concepts/command.md) | command triggered by the policy |
| [Actor](../overarch/concepts/actor.md) | uses | [Use Case](../overarch/concepts/use-case.md) | describes the goal or usage of the use case by an actor |
| [Version](../overarch/concepts/version.md) | version of | [Artifact](../overarch/concepts/artifact.md) | version of an artifact |

## Navigation
[List of views in namespace](./views-in-namespace.md)

[List of all Views](../views.md)


(generated by [Overarch](https://github.com/soulspace-org/overarch) with template docs/view.md.cmb)

