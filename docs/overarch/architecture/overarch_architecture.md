
# Architecture Documentation for Overarch
An Open Architecture Knowledge Platform

## Table of Content
1. [Introduction and Goals](#introduction-and-goals)
1. [Architecture Constraints](#architecture-constraints)
1. [System Context and Scope](#system-context-and-scope)
1. [Solution Strategy](#solution-strategy)
1. [Building Block View](#building-blocks)
1. [Runtime View](#runtime-view)
1. [Deployment View](#deployment-view)
1. [Crosscutting Concepts](#crosscutting-concepts)
1. [Architectural Decisions](#architecture-decisions)
1. [Quality Requirements](#quality-requirements)
1. [Risk and Technical Dept](#risk-and-technical-dept)
1. [Glossary](#glossary)

## Introduction and Goals
<!-- PA-BEGIN(Introduction1) -->
Overarch is an open architecture knowledge platform.
It captures (architecture) knowledge in a graph based knowledge model.
This knowledge model can be queried to answer architectural questions.
<!-- PA-END(Introduction1) -->


### Goals
| Goal | Description |
|---|---|
| [Composeable Models](../../overarch/architecture/goal/composeable-models.md) | Models from different sources can be composed to form a larger model. |
| [Customizeable Artifact Generation](../../overarch/architecture/goal/extensible-target-artifacts.md) | The generation of artifacts from information in the model is fully customizeable. |
| [Extensible Models](../../overarch/architecture/goal/extensible-model-elements.md) | Model elements are open and can be extended with additional information. |
| [Knowledge Models](../../overarch/architecture/goal/knowledge-models.md) | An Overarch model shall capture architectural knowledge on different levels (e.g. enterprise, business, system, code, deployment). |
| [Open Models](../../overarch/architecture/goal/open-models.md) | The the models shall be open and accessible for other tools |
| [Queryable Models](../../overarch/architecture/goal/queryably-models.md) | Model can be queried to answer specific questions or to select a specific subset of the model. |

## Architecture Constraints


## System Context and Scope


### System Context View
![Context View of Overarch](../../overarch/architecture/context-view.png)

[Context View of Overarch](../../overarch/architecture/context-view.md)


### Scope
<!-- PA-BEGIN(Scope) -->
<!-- PA-END(Scope) -->


## Solution Strategy
<!-- PA-BEGIN(SolutionStrategy) -->
<!-- PA-END(SolutionStrategy) -->


## Building Blocks


### Container View
![Container View of Overarch](../../overarch/architecture/container-view.png)

[Container View of Overarch](../../overarch/architecture/container-view.md)


## Runtime View


## Deployment View


## Crosscutting Concepts
<!-- PA-BEGIN(CrosscuttingConcepts) -->
<!-- PA-END(CrosscuttingConcepts) -->


## Architecture Decisions


### Decisions
| Decision | Description |
|---|---|
| [Data Format for Models](../../overarch/architecture/decision/data-format.md) | Which external data format for the model? |
| [Implementation Architecture](../../overarch/architecture/decision/implementation-architecture.md) | What is the implementation architecture for Overarch? |
| [Implementation Language](../../overarch/architecture/decision/implementation-language.md) | What language should be used to implement Overarch? |
| [Internal Data Representation](../../overarch/architecture/decision/data-representation.md) | What is the internal representation of the model? |

## Quality Requirements



## Risk and Technical Dept




## Glossary

