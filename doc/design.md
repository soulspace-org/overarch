Design
======


Situation
---------
Currently we create architecture diagrams with the C4 models and PlantUML.
The various diagrams contain duplicate model information and we don't have a single model description. Changes are made in specific diagrams and are missing from others.

The syntax of the PlantUML is inconsistent, brittle and errors are rendered
in the generated image and are often not very helpful. On the other hand,
PlantUML in combination with GraphViz is a pragmatic option to generate
C4 architecture diagrams.

### Existing Tools

* [C4 PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML)
  * C4 standard library for PlantUML
  * diagram oriented
  * no separation between model and presentation
  * pure textual representation
  * needs a parser implementation
  * useful as export target (implemented in overarch)

* [structurizr](https://structurizr.org/)
  * C4 modelling tools from Simon Brown, the inventor of C4 models
  * diagram oriented
  * separates model from views, but in the same files
  * unwieldy tooling (IMHO)
  * possible export target

* [fc4-framework](https://github.com/FundingCircle/fc4-framework)
  * FC4 is a Docs as Code tool to create software architecture diagrams.
  * just diagram oriented
  * no separation between model and presentation
  * tied to structurizr for modelling and visualization

* [archinsight](https://github.com/lonely-lockley/archinsight)
  * Insight language is a DSL (Domain Specific Language)
  * translates C4 Model description into DOT language,
  * rendered by Graphviz.
  * supports only the first two levels of C4.
  * pure textual representation
  * needs a parser implementation


Observations
------------
* Textual DSLs may provide a compact and succinct language
* current Textual DSLs fall short at least in
  * separation between layout and model
  * composablility
  * extendability
  * generalization
  * reusability
  * tooling
  * ease of use

* Advantages of data files
  * no special syntax needed
  * no parser needed
  * good editor support (e.g. VS Code/Calva)
  * readable/reusable outside of the proposed tools
    * self describing
  * dynamic validation

* Possible disadvantages
  * likely not as compact and succint as DSLs


Design Goals
------------

* Reusable models
* Reduced duplication of information
* Extensibility of the data format
* Extensibility of the exports


Design Questions and Answers
----------------------------
This section documents design decisions in the form of questions and answers.
Not all questions that arose have a definitve answer/decision yet.


Q: **What is architecture anyway?**

A: Architecture in the context of information systems can be separated into
   software architecture and system architecture.


Q: **What is software architecture?**

A: Software architecture specifies the high level design of a software
   system. With an appropriate architecture (or high level design) the system is
   able to fulfill the functional and nonfunctional requirements for the
   system.


Q: **What is system architecture?**

A: System architeture specifies the high level design of the runtime
   environment and infrastructure of one or more software systems.


Q: **What shall be captured in an architectural description model?**

A: The hierarchical structure of a system and its context
   * The system context and system landscape (if relevant)
   * The composition of the system
     * The containers (deployment units or processes) of the system
     * The components of the containers
   * The relations (e.g. dependencies, interactions) of the components
   * The relations (e.g. interactions) of the components and the outside
     world (with users or other systems)
   * The dynamics of the interactions between the elements of the model
     (if relevant)
   * The deployment of the containers in the infrastructure for the system
     (if relevant)


Q: **How can reusability of the models and views be achieved?**

A: With :ref the reusablility of model elements in different diagrams is
   archieved, which is a benefit over the diagram focused specification in the PlantUML C4 DSL, where you often have to duplicate information in the different diagrams.

   Another level of reuse (and composablity) is the combination of smaller models to larger models with loading all EDN files in a directory, namespaced IDs to avoid conflicts and :ref's to refer to other model elements.

   Even another level of reuse lies in the plain data specification and the extensible nature of EDN. you can augment models with information that is not evaluated by overarch, but maybe other tools working on the data. Because it is just plain data, you don't need a specific parser to read the model and view descriptions. And with the JSON export you can even reuse the data in languages, for which no EDN reader exists. So the model is not bound to overarch as a specific tool but stands for itself.

Q: **What could also be captured in an extension of the description?**

A: An extension of the model could make sense if there is value in the
   connection of the additional elements to the existing elements, e.g. to provide traceability

   Additional elements could be
   * Enterprise architecture elements like capabilities
   * Business arcitecture elements like business processes
   * Functional or nonfunctional requirements and crosscutting concerns
   * Inner workings of some components
     * e.g. state machines, activty or sequence diagrams
   * ...

   The schema of the model is open, as the spec just checks for the existence
   of elements (keys) needed for overarch (e.g. the generation of the implemented view representations). Additional elements can be added without impact on overarch. The keys for additional elements should be prefixed with a meaningful namespace.


Q: **Shall the boundaries be implicit in the model, e.g. rendering a system as a system-boundary in a container diagram, if it contains container elements, that are visualized?**

A: Implicit boundaries make the model more succinct.
   A boundary should be rendered for model elements from a higher level
   containing children on the level of the diagram.
   E.g. a system with containers should be rendered as a system boundary containing the containers.

   Explicit boundaries make sense for grouping elements, e.g. for bounded contexts or for enterprise boundaries.
   
   So the model should support explicit boundaries and views should also render implicit boundaries for higher level elements referenced in a specific view.


Q: **Shall relations between low level elements (e.g. components) and the outside world (e.g. users or external systems) be promoted/merged into higher levels in the relevant diagram?**
   
   The relation between a user and a user interface component would be rendered directly in a component diagram. In a container diagram, the relation would be rendered between the user and the container of the user interface component. In a system diagram, the relation would be rendered between the user and the system of the user interface component.

   An advantage would be that relations would have be specified on the
   component level, where the interaction is handled or the concrete dependency exists. But only relations between elements that are included
   in the diagram should be rendered. If a user or an external system is
   not included as a model element, relations to it should not be promoted or rendered. Otherwise the diagram would be polluted with unwanted elements.

   Relations of different elements in a lower level could be merged into a relation
   on a higher level, but some merge rules would have to be established first.

A: 


Q: **Shall relations be automatically included in a view, when the participating components are included?**

   That would make the specification of the views much shorter but relations may be included, that should not be shown in the view. If there has to be an
   exclude mechanism, the usability of automatic inclusion would shrink much.

A: 


Q: **Shall it be possible to model subcomponents, components that contain components?**

   That would require a component boundary and rules, when to render the
   component boundary in the context of a component diagram.

A: 


Q: **How can architecture patterns like hexagonal or layered architectures be modelled and visualized appropriately within the C4 models?**

A: Each container would contain components with the responsibilities and
   dependencies as specified by the given architecture. This is perfectly
   possible in the current C4 model (see [hexagonal](/models/hexagonal/)).


Q: **Should names be generated from ids if missing?**

A: That would make the models more concise. Names can be generated from the name
   part of the keyword by converting kebab case to first upper case with spaces
   between words.


Q: **Can views be specified in a generic manner, so that the elements contained in a view are selected with criteria based selectors/filters?**
   
   C4 diagrams would be views that select the content based on the diagram type.
   Views could also select content based on the namespace of the id or on the
   element type.

A: 


Q: **How should the export be implemented so that there is a clear separation between the selection of and iteration over the relevant content and the format specific rendering of the content?**

A: 


Q: **How can duplication reduced in views of specific instanciations of the model?**
   
   Use case:
   Deployment view to different stages with replacement of the stage
   variable with the name of the stage or stage specific values (e.g.
   CIDR ranges, ...).
   Merging of additional elements in the instantiation with elements in
   the template, e.g. local queues for testing in the dev stage, which
   are external in the prod stage.

A: Parameterized views, view templates with variable replacement and element
   merging (not implemented yet).


Q: **How can we avoid duplication for style specifications used in multiple views?**

A: Themes could encapsulate the specification of the styles and the can be referenced in the
   various diagrams to provide a consistent style.


Q: **How can we support different exporting formats, e.g. diagramming tools, and not be specific in the specification of the views/diagrams?**
    
A: Support a generic feature set in views and diagrams with optional specific
   configuration for a specific export format (e.g. PlantUML)


Q: **How can icons/sprites be implemented in a generic way, so they are not bound to a specific diagram tool?**

A: The handling of icons is very tool specific and not easily implemented in a generic way.
   As such icons should not be specified explicitly in the model or the view,
   the technologies should be specified. If a diagram tool supports icons,
   they should be rendered automatically based on the technology, if an icon for the
   technology exists and the rendering of icons is enabled in the view spec.


Q: **Are notes on model elements and relations possible in the view rendering?**

A: PlantUML supports notes but as it seems only on elements and not on relations.
   So if notes would be annotated on elements and relations, the notes on relations
   could currently not be rendered in PlantUML diagrams.


Q: **Which are the levels/granularities of the export?**

A: It differs on the type of the export.
   
   For PlantUML the export can work on all the loaded model and diagram
   specifications and generate all relevant diagrams, even for multiple models.

   For Stucturizr the export should work on a model and the associated diagram specifications to generate a Structurizr workspace.

   For JSON the export should be done on an individual file level, to keep the
   structure of data files intact.


Q: **Why EDN as the specification notation?**

A: Because it is an open and extensible data format
   * simple syntax
   * richer semantics than JSON
     * keywords, sets, tagged values
   * no parsers needed, read directly into data structures (at least for clojure)
   * good tooling support with type completion
     * VS Code + Calva
     * IntelliJ + Cursive
     * Emacs + Cider


Q: **Why not JSON?**

A: JSON is a format that is widely used and supported by many programming
   languages. But compared to EDN it has a few shortcomings
   * JSON does not support sets and some literal types like uuids or instants
     of time
   * JSON does not support keywords, which are really helpfull in creating
     namespaced and conflict free attributes and ID spaces

   JSON is supported as an export format to make the model and view data
   available to as
   many languages and environments as possible, but the conversion might be
   lossy for the reasons above.


Q: **Why Clojure?**

A: Clojure is a perfect match
   * data oriented, data driven
     * rich data types with literal representations
     * rich library of functions make processing simple and easy
     * existing reader instead of hand written DSLs and parsers
   * functional implementation
     * easy to reason about
     * explicit state when needed
   * clojure.spec
     * validation where and when it's needed
   * multiple value dispatch for export plugins


PlantUML Export
---------------

Q: **Is a global configuration (e.g. via config file) needed?**

   A potential use case would be the generation of internal links for imports on
   systems without an internet connection or with proxy restrictions on
   raw.githubusercontent.com.

   Another use case would be the configurable inclusion of sprite/icon libraries.

A: 


Component Design
----------------
The diagram below shows the current structure of the code. The components
map to clojure namespaces and the diagram describes the reposibilities and
dependencies of the namespaces.

![Component View of Overarch](/doc/images/overarch_componentView.svg)
