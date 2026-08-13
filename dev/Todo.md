# ToDos

## Modelling
* Answers to the following questions on different levels
  * What? (capabilities, use case goals, data? ...)
  * Why? (purpose, description?)
  * Who? (organizations, roles, use case actors)
  * With what/whom? (collaborations, supporting actors/systems, interfaces, artifacts)
  * How? (processes, use cases, scenarios/stories, sequences, activities, state machines, data?)
  * When? (events, triggers, timings, policies, state machines)
  * Where (locations, regions, deployment/infrastructure)
  * How good? (NFRs, quality metrics)
  * How long? (retentions, ...)
  * How configured? (parameters, environments)

* model/view config via edn?

## Export/Import
* canonical model export
* model merge with element merge
* model extract by selection

## Simplification/Extensibility
* remove model types from element/model source
  * node/relation distinction for element types neccessary?
* views specs should specify the selection criteria for model elements

## Views
* add styling to graphviz and plantuml uml rendering
* update PlantUML sprites

* custom views rendered by templates
* custom elements {:el :my/xy} included in the graph
* rainbow coloured orthogonal relations?

## Templates
* check generation for :artifact-of, :version-of and :instance-of relationships
* add templates for plantuml C4 and UML
* check templates for mermaid
  * treeview/treemap
  * class/state diagrams?
  * architecture diagram
  * block diagram
  * event modeling diagram
  * wardley maps (needs a wardley model)

## UI
* add option to ignore unresolved nodes in processing

## Documentation
* readme
  * move rationale to docs (partially done)
    * modelling tool
      * free, extensible, composable, reusable

* add code of conduct
