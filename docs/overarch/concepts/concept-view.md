# Concept Map of Overarch

## Diagram
![Concept Map of Overarch](../../overarch/concepts/concept-view.png)

## Description
Contains the relevant concepts for overarch.

## Concepts
| Concept | Description |
|---|---|
| [Actor](../../overarch/concepts/actor.md)| An actor (role) in a use case model. |
| [Aggregate](../../overarch/concepts/aggregate.md)| A cluster of domain objects that can be treated as a single unit for consistency and invariants. Part of the solution space. |
| [Annotation](../../overarch/concepts/annotation.md)| An annotation in the code model. |
| [Artifact](../../overarch/concepts/artifact.md)| An artifact in the process model, e.g. a document or a docker container |
| [Bounded Context](../../overarch/concepts/bounded-context.md)| A boundary within which a particular domain model is valid and consistent. Part of the solution space. |
| [Capability](../../overarch/concepts/capability.md)| A capability in the process model, e.g. a business capability or a technical capability. |
| [Choice](../../overarch/concepts/choice.md)| Selects one of several alternative paths |
| [Class](../../overarch/concepts/class.md)| A class in the code model. |
| [Command](../../overarch/concepts/command.md)| An instruction to perform a specific action or operation within the domain. Part of the solution space. |
| [Component](../../overarch/concepts/component.md)| A (logical) building block of a container (e.g. a module or a layer) |
| [Concept](../../overarch/concepts/concept.md)| Describes a thing or an idea. |
| [Container](../../overarch/concepts/container.md)| a deployed process in the architecture (e.g. a service or an application) |
| [Container Instance](../../overarch/concepts/container-instance.md)| Deployed instance of a container in the deployment model |
| [Decision](../../overarch/concepts/decision.md)| A decision in the process model, e.g. an architectural decision or a business decision |
| [Deep History State](../../overarch/concepts/deep-history-state.md)| Remembers the last active substate in a substate |
| [Domain](../../overarch/concepts/domain.md)| A specific area of knowledge, activity or subject matter. Part of the problem space. |
| [Domain Event](../../overarch/concepts/domain-event.md)| An event that represents a significant change in the state of the domain. Part of the solution space. |
| [End State](../../overarch/concepts/end-state.md)| A final state of a state machine |
| [Entity](../../overarch/concepts/entity.md)| An object that is defined by its identity, rather than its attributes. Part of the solution space. |
| [Enum](../../overarch/concepts/enum.md)| An enumeration in the code model. |
| [Enum Value](../../overarch/concepts/enum-value.md)| A value of an enumeration in the code model. |
| [Field](../../overarch/concepts/field.md)| A field of a class, namespace or schema in the code model. |
| [Fork](../../overarch/concepts/fork.md)| Splits the flow into multiple concurrent flows |
| [Function](../../overarch/concepts/function.md)| A function in the code model. |
| [History State](../../overarch/concepts/history-state.md)| Remembers the last active substate |
| [Information](../../overarch/concepts/information.md)| Information in the process model. |
| [Interface](../../overarch/concepts/interface.md)| A contract that defines a set of operations that a class must implement. |
| [Join](../../overarch/concepts/join.md)| Joins multiple concurrent flows into one flow |
| [Knowledge](../../overarch/concepts/knowledge.md)| The understanding and skills acquired through experience and education. |
| [Method](../../overarch/concepts/method.md)| A method of a class or interface in the code model. |
| [Namespace](../../overarch/concepts/namespace.md)| A namespace in the code model. |
| [Node](../../overarch/concepts/node.md)| Part of the infrastructure in the deployment model |
| [Organization](../../overarch/concepts/organization.md)| An organization (e.g. a company) in the organization model. |
| [Organizational Unit](../../overarch/concepts/org-unit.md)| An organizational unit (e.g. a department) in the organization model. |
| [Package](../../overarch/concepts/package.md)| A collection of related classes or modules. |
| [Parameter](../../overarch/concepts/parameter.md)| A parameter of a method or function in the code model. |
| [Permission](../../overarch/concepts/permission.md)| A permission, e.g. an access right. |
| [Person](../../overarch/concepts/person.md)| A human actor or role (deprecated, use role). |
| [Policy](../../overarch/concepts/policy.md)| A rule or guideline that governs the behavior or actions within the domain. Part of the solution space. |
| [Process](../../overarch/concepts/process.md)| A process in the process model, e.g. a business process or a software development process. |
| [Protocol](../../overarch/concepts/protocol.md)| A contract that defines a set of operations that a type must implement. |
| [Requirement](../../overarch/concepts/requirement.md)| A Requirement in the process model, e.g. a functional or non-functional requirement |
| [Role](../../overarch/concepts/role.md)| A role in the model, e.g. a person or a system. |
| [Schema](../../overarch/concepts/schema.md)| Describes the structure of data, e.g. a database schema or a JSON schema. |
| [Start State](../../overarch/concepts/start-state.md)| The initial state of a state machine |
| [State](../../overarch/concepts/state.md)| A state in a state machine |
| [State Machine](../../overarch/concepts/state-machine.md)| A collection of states and transitions |
| [Stereotype](../../overarch/concepts/stereotype.md)| A stereotype in the code model. |
| [System](../../overarch/concepts/system.md)| A system relevant in the architecture |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md)| A technical node in the architecture model |
| [Use Case](../../overarch/concepts/use-case.md)| A use case in the use case model. |
| [Value Object](../../overarch/concepts/value-object.md)| An object that is defined by its attributes, rather than its identity. Part of the solution space. |
| [Version](../../overarch/concepts/version.md)| A version of an artifact in the process model |

## Generalizations
| From | Name | To | Description |
|---|---|---|---|
| [History State](../../overarch/concepts/history-state.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Join](../../overarch/concepts/join.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Choice](../../overarch/concepts/choice.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Component](../../overarch/concepts/component.md) | is a | [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) |  |
| [End State](../../overarch/concepts/end-state.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Deep History State](../../overarch/concepts/deep-history-state.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Container](../../overarch/concepts/container.md) | is a | [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) |  |
| [Start State](../../overarch/concepts/start-state.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [Fork](../../overarch/concepts/fork.md) | is a | [State](../../overarch/concepts/state.md) |  |
| [System](../../overarch/concepts/system.md) | is a | [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) |  |
| [System](../../overarch/concepts/system.md) | is an | [Actor](../../overarch/concepts/actor.md) | a system can be a primary or supporting actor of a use case |
| [Person](../../overarch/concepts/person.md) | is an | [Actor](../../overarch/concepts/actor.md) | a person/user role can be a primary actor of a use case |

## Features
| From | Name | To | Description |
|---|---|---|---|
| [Enum](../../overarch/concepts/enum.md) | contains | [Enum Value](../../overarch/concepts/enum-value.md) |  |
| [Interface](../../overarch/concepts/interface.md) | contains | [Method](../../overarch/concepts/method.md) |  |
| [Class](../../overarch/concepts/class.md) | contains | [Field](../../overarch/concepts/field.md) |  |
| [Package](../../overarch/concepts/package.md) | contains | [Class](../../overarch/concepts/class.md) |  |
| [Namespace](../../overarch/concepts/namespace.md) | contains | [Field](../../overarch/concepts/field.md) |  |
| [Method](../../overarch/concepts/method.md) | contains | [Parameter](../../overarch/concepts/parameter.md) |  |
| [Package](../../overarch/concepts/package.md) | contains | [Package](../../overarch/concepts/package.md) |  |
| [Protocol](../../overarch/concepts/protocol.md) | contains | [Function](../../overarch/concepts/function.md) |  |
| [Package](../../overarch/concepts/package.md) | contains | [Enum](../../overarch/concepts/enum.md) |  |
| [System](../../overarch/concepts/system.md) | contains | [Container](../../overarch/concepts/container.md) |  |
| [Organization](../../overarch/concepts/organization.md) | contains | [Organizational Unit](../../overarch/concepts/org-unit.md) | organizational unit of the organization |
| [Organizational Unit](../../overarch/concepts/org-unit.md) | contains | [Organizational Unit](../../overarch/concepts/org-unit.md) | sub-units of the organizational unit |
| [Package](../../overarch/concepts/package.md) | contains | [Interface](../../overarch/concepts/interface.md) |  |
| [Namespace](../../overarch/concepts/namespace.md) | contains | [Function](../../overarch/concepts/function.md) |  |
| [Component](../../overarch/concepts/component.md) | contains | [Namespace](../../overarch/concepts/namespace.md) |  |
| [State Machine](../../overarch/concepts/state-machine.md) | contains | [State](../../overarch/concepts/state.md) |  |
| [Node](../../overarch/concepts/node.md) | contains | [Node](../../overarch/concepts/node.md) |  |
| [Container](../../overarch/concepts/container.md) | contains | [Component](../../overarch/concepts/component.md) |  |
| [Namespace](../../overarch/concepts/namespace.md) | contains | [Protocol](../../overarch/concepts/protocol.md) |  |
| [Class](../../overarch/concepts/class.md) | contains | [Method](../../overarch/concepts/method.md) |  |
| [Function](../../overarch/concepts/function.md) | contains | [Parameter](../../overarch/concepts/parameter.md) |  |
| [Schema](../../overarch/concepts/schema.md) | contains | [Field](../../overarch/concepts/field.md) |  |
| [Package](../../overarch/concepts/package.md) | contains | [Annotation](../../overarch/concepts/annotation.md) |  |
| [Component](../../overarch/concepts/component.md) | contains | [Package](../../overarch/concepts/package.md) |  |
| [State](../../overarch/concepts/state.md) | contains substate | [State](../../overarch/concepts/state.md) |  |

## Other Relationships
| From | Name | To | Description |
|---|---|---|---|
| [Bounded Context](../../overarch/concepts/bounded-context.md) | contains | [Aggregate](../../overarch/concepts/aggregate.md) | aggregates contained in the bounded context |
| [Container](../../overarch/concepts/container.md) | deployed on | [Node](../../overarch/concepts/node.md) |  |
| [Use Case](../../overarch/concepts/use-case.md) | extends | [Use Case](../../overarch/concepts/use-case.md) | describes the extension of the functionality of the referred use case |
| [Class](../../overarch/concepts/class.md) | implementation | [Interface](../../overarch/concepts/interface.md) |  |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | implements | [Decision](../../overarch/concepts/decision.md) |  |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | implements | [Requirement](../../overarch/concepts/requirement.md) |  |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | implements | [Process](../../overarch/concepts/process.md) |  |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | implements | [Use Case](../../overarch/concepts/use-case.md) |  |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | implements | [State Machine](../../overarch/concepts/state-machine.md) |  |
| [Use Case](../../overarch/concepts/use-case.md) | includes | [Use Case](../../overarch/concepts/use-case.md) | describes the inclusion of the functionality of the referred use case |
| [Class](../../overarch/concepts/class.md) | inheritance | [Class](../../overarch/concepts/class.md) |  |
| [Artifact](../../overarch/concepts/artifact.md) | input of | [Process](../../overarch/concepts/process.md) | artifact required by the process |
| [Node](../../overarch/concepts/node.md) | link | [Node](../../overarch/concepts/node.md) |  |
| [Artifact](../../overarch/concepts/artifact.md) | output of | [Process](../../overarch/concepts/process.md) | artifact produced by the process |
| [Information](../../overarch/concepts/information.md) | output of | [Process](../../overarch/concepts/process.md) | information produced by the process |
| [Person](../../overarch/concepts/person.md) | request | [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) |  |
| [Knowledge](../../overarch/concepts/knowledge.md) | required | [Person](../../overarch/concepts/person.md) | knowledge required for a role |
| [Process](../../overarch/concepts/process.md) | required for | [Capability](../../overarch/concepts/capability.md) | process required for the capability |
| [Knowledge](../../overarch/concepts/knowledge.md) | required for | [Capability](../../overarch/concepts/capability.md) | knowledge required for the capability |
| [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | required for | [Capability](../../overarch/concepts/capability.md) | technical component required for the capability |
| [Information](../../overarch/concepts/information.md) | required for | [Process](../../overarch/concepts/process.md) | information required to fullfill the process |
| [Information](../../overarch/concepts/information.md) | required for | [Knowledge](../../overarch/concepts/knowledge.md) | information required to gain knowledge |
| [Organizational Unit](../../overarch/concepts/org-unit.md) | responsible for | [Capability](../../overarch/concepts/capability.md) | organizational unit responsible for the capability |
| [Organizational Unit](../../overarch/concepts/org-unit.md) | responsible for | [Technical Architecture Node](../../overarch/concepts/technical-architecture-node.md) | organizational unit responsible for the technical architecture node (system, container, component) |
| [Person](../../overarch/concepts/person.md) | role in | [Organizational Unit](../../overarch/concepts/org-unit.md) | role in the organizational unit |
| [Person](../../overarch/concepts/person.md) | role in | [Process](../../overarch/concepts/process.md) | personal role in a process |
| [State](../../overarch/concepts/state.md) | transition | [State](../../overarch/concepts/state.md) | describes the transition fom one state to another state triggered by an event |
| [Domain Event](../../overarch/concepts/domain-event.md) | triggers | [Policy](../../overarch/concepts/policy.md) | policy triggered by the domain event |
| [Policy](../../overarch/concepts/policy.md) | triggers | [Command](../../overarch/concepts/command.md) | command triggered by the policy |
| [Actor](../../overarch/concepts/actor.md) | uses | [Use Case](../../overarch/concepts/use-case.md) | describes the goal or usage of the use case by an actor |
| [Version](../../overarch/concepts/version.md) | version of | [Artifact](../../overarch/concepts/artifact.md) | version of an artifact |

## Navigation
[List of views in namespace](./views-in-namespace.md)

[List of all Views](../../views.md)


(generated by [Overarch](https://github.com/soulspace-org/overarch) with template docs/view.md.cmb)

