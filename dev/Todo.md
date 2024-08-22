# ToDos

* generate correct markdown for hierarchical views
* add styling to graphviz and plantuml uml rendering
* separate clean architecture model from domain driven design model
  * use components for features (vertical slicing)
  * use packages, classes and interfaces for clean architecture
  * eventually move to separate repository

* add contributing and code of conduct

* rainbow coloured orthogonal relations
* custom views rendered by templates
* custom elements {:el :my/xy} included in the graph

* model config via edn?

## Documentation
* readme
  * move rationale to docs (partially done)
    * modelling tool
      * free, extensible, composable, reusable

* restructure docs
  * rationale
  * prerequisites
  * installation  
  * modelling
  * model queries
  * views
  * templates
  * IDE integration
  * commandline interface
  * tutorial
  * best practices

## Model Structure
* Answers to the following questions on different levels
  * What? (capabilities, use case goals, data? ...)
  * Why? (purpose, description?)
  * Who? (organizations, roles, use case actors)
  * With what/whom? (collaborations, supporting actors/systems, interfaces)
  * How? (processes, use cases, scenarios/stories, sequences, activities, state machines, data?)
  * When? (events, triggers, timings, policies, state machines)
  * Where (locations, regions, deployment/infrastructure)
  * How good? (NFRs, quality metrics)
  * How long? (retentions, ...)
