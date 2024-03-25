Design
======

Situation
---------
When you architecture diagrams with the C4 models and PlantUML, the various diagrams contain duplicate model information and there isn't a single model description. Changes are made in specific diagrams and are missing from others.

The DSL of C4/PlantUML is not very consistent and errors are rendered
in the generated image and are often not very helpful. On the other hand,
PlantUML in combination with GraphViz is a pragmatic option to generate
C4 architecture diagrams.

Therefore a model description as data and views specification based on this model would solve the problem of duplicated information. The model description
as pure data would also solve the problem of DSL parsers and would make the
model information reusable.


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
* Holistic model of the system under description
* Reduced duplication of information
* Extensibility of the data format
* Extensibility of the renderings and exports


Design Questions and Answers
----------------------------
This section documents design decisions in the form of questions and answers.
It represents some of the inner dialogue in finding good solutions.
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


Q: **What shall be captured in an architectural model?**

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


Q: **What could also be captured in extensions of the model?**

A: An extension of the model could make sense if there is value in the
   connection of the additional elements to the existing elements, e.g. to provide traceability or insights

   Additional elements could be
   * Concepts that have not a direct representation in the system (yet)
   * Enterprise architecture elements like capabilities
   * Business arcitecture elements like business processes
   * Functional or nonfunctional requirements and crosscutting concerns
   * Inner workings of some components
     * e.g. state machines, activty or sequence diagrams
   * ...

   Some or all of these extensions might be added to Overarch in the future.

   The schema of the model is open, as the spec just checks for the existence
   of elements (keys) needed for overarch (e.g. the generation of the implemented view representations). Additional elements can be added without impact on overarch. The keys for additional elements should be prefixed with a meaningful namespace to avoid conflicts with future extensions of Overach.


Q: **How can reusability of the models and views be achieved?**

A: With :ref the reusablility of model elements in different views is
   archieved, which is a benefit over the diagram focused specification in the
   PlantUML C4 DSL, where you often have to duplicate information in the
   different diagrams. Also the same view may be rendered in different formats,
   e.g. a diagram and a textual description of the elements shown in the
   diagram.

   Another level of reuse (and composablity) is the combination of smaller models to larger models with loading all EDN files in a directory,
   namespaced IDs to avoid conflicts and :ref's to refer to other model
   elements.

   Even another level of reuse lies in the plain data specification and the extensible nature of EDN. you can augment models with information that is not evaluated by Overarch, but maybe other tools working on the data. Because it is just plain data, you don't need a specific parser to read the model and view descriptions. And with the JSON export you can even reuse the data in languages, for which no EDN reader exists. So the model is not bound to Overarch as a specific tool but stands for itself.

   Implemented.


Q: **Shall the boundaries be implicit in the model, e.g. rendering a system as a system-boundary in a container diagram, if it contains container elements, that are visualized?**

A: Implicit boundaries make the model more succinct.
   A boundary should be rendered for model elements from a higher level
   containing children on the level of the diagram.
   For example a system with containers should be rendered as a system boundary containing the containers.

   Explicit boundaries make sense for grouping elements, e.g. for bounded
   contexts or for enterprise boundaries.
   
   So the model should support explicit boundaries and views should also render implicit boundaries for higher level elements referenced in a specific view,
   for example in the C4 container and component views.

   Implemented.


Q: **Which kinds of relations are neccessary in the architectural model to express the different semantics of the connections of the architectural nodes?**

A: In architecture views there a two interesting directions to show,
   dependencies and data flow. Dependencies are established by communication
   between architecture elements and are modelled from the sender to the
   receiver. Communication can be synchronous or asynchronous. It can also be
   one-way or two-way.

   The direction of dataflow is independent of the call direction,
   e.g. data can be send with a request and returned with the response.


Q: **Shall relations between low level elements (e.g. components) and the outside world (e.g. users or external systems) be promoted/merged into higher levels in the relevant diagram?**
   
   The relation between a user and a user interface component would be rendered directly in a component diagram. In a container diagram, the relation would be rendered between the user and the container of the user interface component. In a system diagram, the relation would be rendered between the user and the system of the user interface component.

   An advantage would be that relations would have be specified on the
   component level, where the interaction is handled or the concrete dependency exists. But only relations between elements that are included
   in the diagram should be rendered. If a user or an external system is
   not included as a model element, relations to it should not be promoted or rendered. Otherwise the diagram would be polluted with unwanted elements.

   Relations of different elements in a lower level could be merged into a
   relation on a higher level, but some merge rules would have to be
   established first.

A: 


Q: **Shall it be possible to model subcomponents, components that contain components?**

   That would require a component boundary and rules, when to render the
   component boundary in the context of a component diagram.

A: Deeper levels than components are not supported in the architecture models.
    
   The class model can be used to model packages, interfaces and classes.
   So a class view provides a way to show more detail than a component view.

   On the other hand, components do not have to be all on the same abstraction
   level. If you model components on different levels of abstraction, they can
   be shown together with their relations in a component view.


Q: **How can methods like domain driven design or architecture patterns like hexagonal architecture be modelled and visualized appropriately within Overarch?**

A: Concepts and patterns like the ones from Domain Driven Design can be
   modelled as concepts and rendered as a glossary or a concept map.

   For architecture patterns a generic or sample instance of the architecture
   can be modeled as a system in the architecture model.
   Each container would contain components with the responsibilities and
   dependencies as specified by the given architecture. This is perfectly
   possible in the current C4 model (see [DDD example](/models/ddd/)).

   The same approach would be possible e.g. for deployment blueprints.


Q: **Should names be generated from ids if missing?**

A: That would make the models more concise. Names can be generated from the
   name part of the keyword by converting kebab case to first upper case with
   spaces between words. But the models should contain names to make them more
   readable, so the generated names should be just a fallback, not an exuse
   for not specifying names in the first place.

   Implemented.


Q: **Should a flat model be used (internally)?**
   
   The model is currently hierarchical, e.g. systems contain containers, which
   contain components. The elements have the ```:ct``` key, which has to be
   traversed recursively in the code. The parent-child relation could
   alternatively modelled as a relation, which would make some model queries
   simpler and more uniform.

A: By decoupling the external representation of the model, the input model,
   from the internal model, the domain model, we gain a degree of freedom in
   the implementation of the domain model. The input model should be
   transformed to the domain model after reading. In the transformation,
   synthetic contains relations should be generated for the parent-child
   relations in the model expressed be the content of the ```:ct``` key
   of an element. By doing so, but leaving the content of the ```:ct```
   intact, the domain model could be traversed in a hierarchical and a
   relational manner.

   With the insertion of the contains relations, the whole model is
   represented as a graph consisting of model nodes and relations.
   As such it can be navigated and queried with graph algorithms in addition to
   the tree traversal via the ```:ct``` key of the the elements.
   
   Implemented

Q: **Shall relations be automatically included in a view, when the participating components are included?**

   That would make the specification of the views much shorter but relations
   may be included, that should not be shown in the view. If there has to be an
   exclude mechanism, the usability of automatic inclusion would shrink much.

A: The automatic inclusion of elements should be controlled on a per view
   basis, without a default automatism.


Q: **Can views be specified in a generic manner, so that the elements contained in a view are selected with criteria based selectors/filters?**
   
   C4 diagrams would be views that select the content based on the diagram type.
   Views could also select content based on the namespace of the id or on the
   element type.

A: An :include option in the view spec could contain different strategies for
   the automatic selection of content, e.g.
   * :relations to select all relations for the referenced model elements
   * :related to select all elements for the referenced relations

   Some views are rendered in a hierachical way, where only some top level
   elements have to be specified, and the content of these top level elements
   is rendered recursively according to the rules of the view. For example in
   a component-view it is fine to reference the internal system and the
   elements of this system are rendered recursively down to the the components
   of the system.

   Other views may only render the elements directly specified in a flat list.
   
   The include option should work for both kind of views by only make it more
   convenient to specify the elements as you would normally without the
   includes. It should have no influence on the rendering rules of the views.

   A criteria based selection 
   * :transitive or :convex-hull to select all elements reachable from the
     referenced elements
   * a map of selection criteria on the element attributes


Q: **How should the rendering be implemented so that there is a clear separation between the selection of and iteration over the relevant content and the format specific rendering of the content?**

A: As said above, the rendering can be flat or hierarchical, depending on the
   type of view to be rendered. Both the selection and the iteration for
   rendering have to take this difference into account.

   It is not sufficent to just collect all the elements to be rendered in a
   view upfront and pass them to the rendering function as this would only
   work in the case of flat rendering. In the case of hierarchical rendering
   the render function has to recursively traverse the elements to be rendered
   to render the correct structure.

   Nevertheless it can still be useful to know all the elements to be rendered
   upfront, e.g. to automatically select all relevant relations between those
   elements or to collect the information needed for the imports in PlantUML.

   Thus the traversal logic has to be replicated in the upfront collection of
   elements and in the rendering functions.


Q: **How should the content of the view be selected?**

A: The process could be
   1. select elements by criteria, if provided
   2. merge directly referenced elements
   3. add additional elements from :include spec, if provided

   Only relevant elements for the views shall be added/rendered.


Q: **How can we avoid duplication for style specifications used in multiple views?**

A: Themes could encapsulate the specification of the styles and the can be
   referenced in the various diagrams to provide a consistent style.

   Implemented.


Q: **How can we support different exporting formats, e.g. diagramming tools, and not be specific in the specification of the views/diagrams?**
    
A: Support a generic feature set in views and diagrams with optional specific
   configuration for a specific export format (e.g. PlantUML)

   Implemented.


Q: **How can icons/sprites be implemented in a generic way, so they are not bound to a specific diagram tool?**

A: The handling of icons is very tool specific and not easily implemented in a
   generic way. As such icons should not be specified explicitly in the model
   or the view, the technologies should be specified. If a diagram tool supports icons, they should be rendered automatically based on the technology, if an icon for the technology exists and the rendering of icons
   is enabled in the view spec.


Q: **Are notes on model elements and relations possible in the view rendering?**

A: PlantUML supports notes but as it seems only on elements and not on
   relations. So if notes would be annotated on elements and relations, the
   notes on relations could currently not be rendered in PlantUML diagrams.


Q: **Which are the levels/granularities of the export?**

A: It differs on the type of the export. There are different type of exports.
   The JSON and structurizr exports export the model elements and the views to
   make them available for other tools. The view exports (e.g. PlantUML,
   Markdown or Graphviz) render the defined views on the model but do not
   export the model data as is.
   
   For JSON the export should be done on an individual file level, to keep the
   structure of data files intact. The only problem is that file comments will
   not be exported because they are ignored by the EDN reader and not available
   in the JSON export. 

   For Stucturizr the export should work on the architecture model and the
   associated diagram specifications to generate a Structurizr workspace.
   Other models (e.g. use-cases, state machines or concepts) have to be ignored
   because they are (currently) not supported by Structurizr.

   For view rendering like PlantUML the rendering can work on all the
   loaded model and view specifications and generate all relevant renderings,
   even for multiple models.

   Implemented.


Q: **Should the different granularities of exports made explicit in the code?**

   Currently in v0.3.0 all exports are implementing the same export functions.
   The logic for the granularity of the export is implemented in the specific
   implementation for the export format. There is no distinction between the
   levels/granularities on which the export is done.

A: Distinguish between data exports and view rendering.

   Implemented.


Q: **How shall multiple export formats be specified and implemented?**

   Currently there is a --format command line option and the export multifn
   dispatches on the value of this option. Thus it is currently not possible to
   export more than one format per execution.

   It should be possible to specify multiple formats on the command line and 
   all of the specified formats should be exported.

A: Distinguish between data exports and view rendering.

   Implemented. 


Q: **Should exporting and rendering be separated in the code?**

A: Exporting of the model and view information into other formats (e.g. JSON or
   structurizr) and rendering of the views to some target formats (e.g.
   PlantUML or markdown) are currently (v0.5.0) handled by the same multi methods. With more rendering and export formats, specifying the formats and
   dispatching to multiple rendering and export formats will be problematic and complected with the current implementation. For example it is useful to
   render the views to the different formats in one go without exporting the data to JSON and structurizr in the same invocation.

   To decomplect the specifications and transformations, exports and rendering
   should be separated into different modules/methods and specified with
   different command line options.

   Implemented.


Q: **What textual views/exports make sense?**

A: The model should contain information like names and descriptions for most
   elements, e.g. to render this information in C4 views. Even if the
   description of an element is not rendered in an UML view, the element should
   be described in the model. With consistent desciptions of the model elements
   and relations, textual representations of the views can be rendered, which describe the elements visible in the view.

   Also text-only views on the model make sense, e.g. a glossary view, which
   contains the main elements of the model with their names, types and descripions alphabetically sorted by name. So the information in the model
   can be used to create more value than just for the creation of diagrams.

   For the glossary it might be useful to add concepts to the model to describe
   the parts of the language of the system, which are not directly represented
   by elements of the architecture.

   Implemented. 


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


Q: **Whould overarch benefit from a generic generation mechanism with templates?**

A: Overarch would benefit from a template based generation mechanism because
   it would enable users to create textual renderings of model elements without
   creating render adapters in the overarch codebase itself. Therefor,
   depending on the programming language used in the template engine, no
   clojure knowledge would neccessary to create useful textual renderings for
   the model content.

   Also the useful textual renderings from the model content will be highly
   specific to the context (e.g. the project). The render adapters provided by
   overarch should be generic and useful for all usage contexts.

Q: **What properties should a generic generation mechanism have?**

A: 


Q: **Why EDN as the specification notation?**

A: Because it is an open and extensible data format
   * simple syntax
   * richer semantics than JSON
     * keywords, sets, tagged values
   * no parsers needed, read directly into data structures (at least for
     clojure and its host languages)
   * good tooling support with type completion, e.g.
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
   available to as many languages and environments as possible, but the
   conversion might be lossy for the reasons above.


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
     * flexible validation where and when it's needed
     * open for custom extensions of the data
   * multiple value dispatch for export plugins


PlantUML Export
---------------

Q: **Is a global configuration (e.g. via config file) needed?**

   A potential use case would be the generation of internal links for imports
   on systems without an internet connection or with proxy restrictions on
   raw.githubusercontent.com.

   Another use case would be the configurable inclusion of sprite/icon libraries.

A: It does not seem to be needed so far.


Component Design
----------------
The diagram below shows the current structure of the code. The components
map to clojure namespaces and the diagram describes the reposibilities and
dependencies of the namespaces.

![Component View of Overarch](/doc/images/overarch_overarchComponentView.svg)


Data Model
----------
The diagram below is a reference of the current logical data model of Overarch.
It contains all elements currently implemented by Overarch.
The [usage doc](/doc/usage.md) contains views of relevant parts of the model.

It is a logical model, because it is not modelled as classes or
records in Overarch. Overarch treats the model as plain data and doesn't care
about additional fields or elements. But this logical model shows the
structure of the data model Overarch cares about and acts on.

![Overview of the Data Model of Overarch](/doc/images/overarch_dataModelOverview.svg)


### Remarks
Abstract elements in the logical data model, denoted by the (A) in the class,
are just a way to structure the logical data model and to reduce redundancy
by not repeating the inherited keys on every element. The elements used to
model a system are only the concrete elements in the logical data model, which
are denoted by the (C) in the diagram.

A *ref* to an *identifiable element* could be used anywhere, where this element could have been modelled in the first place. Refs are mostly used to
pull model elements into a view, but they could also be used in the model
itself, e.g. to split the model of a huge system into different files.


Ideas
-----
This section collects ideas that may make sense to implement in the future.

* Consistency checks and reports (implemented partially)
* Inference on model information e.g. with
  * core.logic
  * Hyperdimensional vectors
