Overarch provides a data model for the holistic description of software systems and enterprises with their system landscapes, opening multiple use cases on the model data.
The data model is graph model based on clojure data structures. The data model is stored as EDN files. The meta model is also an overarch data model residing at `models/overarch/data-model/model.edn`.
The metamodel contains various domain models for
* concepts
* use cases
* software architecture
* state machines
* code
* deployments
* organisations
* processes

Overarch is also a command line tool to work with these models.
Overarch reads the models and provides features
* generate artifacts
  * PlantUML files to generate PlantUML diagrams (UML, C4, WBS)
  * Graphviz dot files to generate diagrams with Graphviz
  * Markdown files as documentation for model elements and views
* query the model elements and views
* template mechanism for artifact generation using weavejester/comb templates
* model exports (JSON, Structurizr)
