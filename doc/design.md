Design
======

Situation
---------
Currently we create architecture diagrams with the C4 models and PlantUML.
The various diagrams contain duplicate model information and we don't have a single model description. Changes are made in specific diagrams and are missing from others.

The syntax of the PlantUML is inconsistent, brittle and errors are rendered
in the generated image and are often not very helpful. On the other hand,
PlantUML in combination with GraphViz are a pragmatic option to generate
C4 architecture diagrams.


Observations
------------
* Textual DSLs may provide a compact and succinct language
* Textual DSLs fall short at least in
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


Questions
---------

Q: What is architecture anyway?

A: Architecture in the context of information systems can be separated into
   software architecture and system architecture.

Q: What is software architecture?

A: Software architecture specifies the high level design of a software
   system. With an appropriate architecture (or high level design) the system is
   able to fulfill the functional and nonfunctional requirements for the
   system.

Q: What is system architecture?

A: System architeture specifies the high level design of the runtime
   environment and infrastructure of one or more software systems.

Q: What shall be captured in an architectural description model?

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

Q: What could also be captured in an extension of the descriptionẞ

A: An extension of the model could make sense if there is value in the connection
   of the additional elements to the existing elements, e.g. to provide traceability

   Additional elements could be
   * Enterprise architecture elements like capabilities
   * Business arcitecture elements like business processes
   * Functional and nonfunctional requirements and crosscutting concerns
   * ...

Q: Shall the boundaries be implicit in the model, e.g. rendering a
   system as a system-boundary in a container diagram, if it contains    container elements, that are visualized?

A: Implicit boundaries make the model more succinct.
   A boundary should be rendered for model elements from a higher level
   containing children on the level of the diagram.
   E.g. a system with containers should be rendered as a system boundary containing the containers.

Q: Shall relations between low level elements (e.g. components) and the
   outside world (e.g. users or external systems) be promoted/merged into higher levels in the relevant diagram?
   
   The relation between a user and a user interface component would be rendered directly in a component diagram. In a container diagram, the relation would be rendered between the user and the container of the user interface component. In a system diagram, the relation would be rendered between the user and the system of the user interface component.

A: An advantage would be that relations would have be specified on the
   component level, where the interaction is handled or the concrete dependency exists. But only relations between elements that are included
   in the diagram should be rendered. If a user or an external system is
   not included as a model element, relations to it should not be promoted or rendered. Otherwise the diagram would be polluted with unwanted
   elements.

Q: Shall relations be automatically included in a view, when the participating
   components are included?

   That would make the specification of the views much shorter but relations may be
   included, that should not be shown in the view. If there has to be an exclude mechanism, the usability of automatic inclusion shrink much.

Q: Shall it be possible to model subcomponents, components that contain
   components?
   That would require a component boundary and rules, when to render the
   component boundary in the context of a component diagram.

A: 

Q: How can architecture patterns like hexagonal or layered architectures
   be modelled and visualized appropriately within the C4 models?

A: Each container would contain components with the responsibilities and dependencies
   as specified by the given architecture.

Q: Should names be generated from ids if missing?

A: That would make the models more concise. Names can be generated from the name
   part of the keyword by converting kebab case to First upper case with spaces
   between words.

Q: Can views be specified in a generic manner, so that the elements contained in a view are
   selected with criteria based selectors/filters?
   
   C4 diagrams would be views that select the content based on the diagram type.
   Views could also select content based on the namespace of the id or on the element type.

A: 

Q: How to support different exporting formats, e.g. diagramming tools, and
   not be specific in the specification of the views/digrams?
    
A: Support a generic feature set in views and diagrams with optional specific
   configuration for a specific export format (e.g. PlantUML)

Q: How should the export be implemented so that there is a clear separation between the
   selection of and iteration over the relevant content and the format specific rendering
   of the content?

A: 

Q: Why EDN as the specification notation?

A: Because it is an open and extensible data format
   * simple syntax
   * richer semantics than JSON
     * keywords, sets, tagged values
   * no parsers needed, read directly into data structures (at least for clojure)
   * good tooling support with type completion
     * VS Code + Calva
     * IntelliJ + Cursive
     * Emacs + Cider

Q: Why not JSON?

A: JSON is a format that is widely used and supported by many programming languages.
   But compared to EDN it has a few shortcomings
   * JSON does not support sets and some literal types like uuids or instants of time
   * JSON does not support keywords, which are really helpfull in creating
     namespaced and conflict free attributes and ID spaces

   JSON is supported as an export format to make the model and view data available to as
   many languages and environments as possible, but the conversion might be lossy for the
   reasons above.

Q: Why Clojure?

A: Clojure is a perfect match
   * data oriented, data driven
     * rich data types with literal representations
     * rich library of functions make processing simple and easy
     * existing reader instead of hand written DSLs and parsers
   * functional, easy to reason about
   * clojure.spec
     * validation where it's needed
   * multiple value dispatch for export plugins


PlantUML Export
---------------

Q: Is a global configuration (e.g. via config file) needed?

   A potential use case would be the generation of internal links for imports on
   systems without an internet connection or with proxy restrictions on raw.github.com.

   Another use case would be the configurable inclusion of sprite/icon libraries.

A: 



Input from others
-----------------
* automatic import of generated diagrams into the documentation (e.g. confluence) (Quirin)
