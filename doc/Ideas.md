Ideas
=====

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
* Data in plain text files (e.g. EDN) is better in all of these points, but
  * is likely not as compact and succint

Questions
---------

Q: What is architecture anyway?

A: Architecture in the context of information systems can be separated into
   software architecture and system architecture.

Q: What is software architecture?

A: Software architecture specifies the high level design of a software
   system.
   With an appropriate architecture (or high level design) the system is
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

Q: Shall the boundaries be implicit in the model, e.g. rendering a
   system as a system-boundary in a container diagram, if it contains    container elements, that are visualized?

A: Implicit boundaries make the model more succinct.
   A boundary should be rendered for model elements from a higher level
   containing children on the level of the diagram.
   E.g. a system with containers should be rendered as a system boundary containing the containers.

Q: Shall relations between low level elements (e.g. components) and the
   outside world (e.g. users or external systems) be promoted to higher levels in the relevant diagram?
   
   The relation between a user and a user interface component would be rendered directly in a component diagram. In a container diagram, the relation would be rendered between the user and the container of the user interface component. In a system diagram, the relation would be rendered between the user and the system of the user interface component.

A: An advantage would be that relations would have be specified on the
   component level, where the interaction is handled or the concrete dependency exists. But only relations between elements that are included
   in the diagram should be rendered. If a user or an external system is
   not included as a model element, relations to it should not be promoted or rendered. Otherwise the diagram would be polluted with unwanted
   elements.

Q: Shall it be possible to model subcomponents, components that contain
   components?
   That would require a component boundary and rules, when to render the
   component boundary in the context of a component diagram.

A: 

Q: How can architecture patterns like hexagonal or layered architectures
   be modelled and visualized appropriately within the C4 models?

A:

Q: How to support different exporting formats, e.g. diagramming tools, and
   not be specific in the specification of the views/digrams?
    
A:

Q: Why EDN?

A: Because it is an open and extensible data format
   * simple syntax
   * richer semantics than JSON
     * keywords, sets, tagged values
   * no parsers needed (at least for clojure)
   * good tooling support
     * VS Code + Calva
     * IntelliJ + Cursive
     * Emacs + Cider

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




Input from others
-----------------
* automatic import of generated diagrams into the documentation (e.g. confluence) (Quirin)
