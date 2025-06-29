# Views
To show model elements in diagrams or in textual representations you can define
different kind of views. The kind of view defines the visual rendering of the
elements and the kind of elements and relations that are shown.

Overarch supports different types of views and renderings. For architecture and
deployment models, C4 diagrams can be rendered. Use case models, state machine
models and code models can be rendered as UML diagrams. Structure diagams can
be used to show the hierarchical structure of organisations, systems or
deployments.

Overarch supports the description of all C4 core and supplementary diagrams
as views. The core C4 views form a hierarchy of views. See
[c4model.com](https://c4model.com) for the rationale and detailed information
about the C4 model and diagrams.

Overarch also supports conceptual views as part of the documentation of the
system. Conceptual views can be used in early stages of the development project,
when the requirements and the architecture are not yet fixed, to get an overview
of the system to be designed. They can also be used to document the relevant
concept ofthe domain of the system for discussion, onboarding and learning.
Concepts should also be part of the glossary, as well as actors, systems and
the applications and containers developed for the system.

## System Context View (:context-view)
Shows the system in the context of the actors and other systems it is
interacting with. Contains users, external systems and the system to be
described.

![System Context View rendered with PlantUML](/doc/images/banking/system-context-view.svg)

## Container View (:container-view)
Shows the containers (e.g. processes, deployment units of the system) and
the interactions between them and the outside world. Contains the elements
of the system context diagram and the containers of the system to be described.
The system to be described is rendered as a system boundary in the container
diagram. External systems are rendered as boxes by default, but can be expanded
to show their containers by adding `:expand-external true` to the view spec.

![Container View rendered with PlantUML](/doc/images/banking/container-view.svg)

## Component View (:component-view)
Shows the components and their interactions inside of a container and with
outside systems and actors. External systems are rendered as boxes by default,
but can be expanded to show their containers and components by adding
`:expand-external true` to the view spec.


![Component View rendered with PlantUML](/doc/images/banking/api-component-view.svg)

## Code View (:code-view)
A code view is used to show the design of parts of the software. You can use it
e.g. to model a domain and to communicate the model with domain experts.

Normally it is not neccessary to model the whole code base, the level of
abstraction for implementation details is usually not high enough to justify
modelling implemeted code. Also the speed of change in the code is most likely
to high and renders a code model obsolete fast.
If you want to visualize existing code, you can use the features of your IDE to
generate a diagram of it. Maybe you can generate a code model from the existing
code base.

On the other hand it can be useful to create a view of code not yet implemented.
An UML class view can be used to model a domain or communicate a design
pattern.

The data models shown here in the documentation are examples of code-views.

## System Structure View (:deployment-structure-view)
The system structure view shows hierarchical composition of a system.

### System Landscape Views (:system-landscape-view)
The system landscape view shows a high level picture, a broader view of the
system landscape and the interactions of the systems.

![System Landscape View rendered with PlantUML](/doc/images/banking/system-landscape-view.svg)

### Dynamic Views (:dynamic-view)
Shows the order of interactions. The relations get numerated in the given order
and the nuber is rendered in the diagram. The `:index` attribute can be used on
relations or refs to relations to set the number for a relation in the view.

If the relations are describing interactions specific for the diagram instead
of general architectural relations (e.g. interfaces) of the model elements,
it is ok to to specify the relations in the content of the view.

## Deployment View (:deployment-view)
The deployment view shows the infrastucture and deployment of the containers of
the system.

![Deployment View rendered with PlantUML](/doc/images/banking/deployment-view.svg)

## Deployment Structure View (:deployment-structure-view)
The deployment structure view shows hierarchical composition of the
infrastructure and deployments of the system.

## State Machine Views (:state-machine-view)
A state machine view is used to show the different states a component can be in.
It also shows the transitions between these states based on the input events
the component receives. 

## Use Case View (:use-case-view)
A use case view is used to show the actors of the system under design and their
goals using this system.

![Use Case View rendered with PlantUML](/doc/images/banking/use-case-view.svg)

## Concept View (:concept-view)
The concept view is a graphical view. It shows the concepts as a concept map
with the relations between the concepts.

## Organization Structure View (:organization-structure-view)
The organization structure view shows hierarchical composition of a organization.

## Glossary View (:glossary)
The glossary view is a textual view. It shows a sorted list of elements with
their type and their descriptions.

## Logical Data Model for the View Model Elements
![View Model Elements](/doc/images/overarch/data-model/view-model-elements.svg)

## View Content Specification
In a specific view you can reference the model elements you want to include in
this view. A Model element can be included in as many views as you want, but the
element has to match the expected kinds of model elements to be shown.
For example, a system landscape view renders person and system elements but no
use cases or state machines, even if they are referenced in the view. Please consult
the models for the model and view elements.

Model elements can be referenced directly via a `:ref`. They can also be
selected via model criteria. Either References for selected nodes or nodes for
selected references can be included automatically.

The views can reference elements from the model as their content. The
content of a view should be a list instead of a set because the order
of elements may be relevant in the rendering of a view. 

### Keys
key              | type          | example values            | description 
-----------------|---------------|---------------------------|------------
:el              | keyword       | see views                 | type of the view
:id              | keyword       | namespaced id             | used for export file name
:title           | string        |                           | rendered title
:selection       | map or vector | {:namespace "banking"}    | select the content by criteria (see [Model Element Selection](#model-element-selection-by-criteria))
:include         | keyword       | :relations :related       | specify automatic includes (work in progress)
:ct              | list          | model refs (or elements)  | view specific keys possible
:layout          | keyword       | :top-down, :left-right    | rendering direction
:linetype        | keyword       | :orthogonal, :polygonal   | different line types for relations
:sketch          | boolean       | true, false               | visual clue for sketches
:styles          | set           | see Styling               | visual customization of elements
:themes          | keyword       | id of the theme           | theme containing styles
:expand-external | boolean       | true, false               | show the content of external systems in container/component views (default false)

Since Overarch v0.38.0 it is not neccessary to use a ``:spec`` map for keys like ``:selection`` or ``:plantuml`` anymore.

## View Specific Keys on Model Elements
There are some keys on model elements that control the rendering of the
elements in views. As they may be different for specific views, it is best to
add them as keys on a `:ref` reference to the element in the content of the
view and not on the model element itself. Keys on the reference are merged
with the keys on the model element.

# Node Keys
key        | type    | values                   | description 
-----------|---------|--------------------------|------------
:collapsed | boolean | true, false              | if true, don't render children of the element 

# Relation Keys
key        | type    | values                    | description 
-----------|---------|---------------------------|------------
:direction | keyword | :left, :right, :up, :down | hint on the direction of the relation

### Selection
With the `:selection` key a criteria map or a vector of criterias can be specified.
The matching elements will be included in the view. This feature can be used to
create 'dynamic' views that always contain the latest model content matching
the criteria. See section [Model Element Selection by Criteria](#model-element-selection-by-criteria)
for details and [banking views](/models/banking/views.edn) for examples.

### Includes
With the `:include` key elements can be automatically included in a view. 
The default behaviour is `:referenced-only` which only includes the referenced
elements.

With the value `:relations` all relations to the referenced elements will be
 automatically included.

With the value `:related` all elements participating in the referenced
relations will be automatically included in addidtion to the referenced
elements.

### Preference Rules for View Content
Criteria based selection, direct element references and includes can be
combined in a view. First the selection is merged with the references in such
a way, that key overrides and additions on references are preserved. Then the
included elements are calculated and merged. This merge also preserves the key
overrides and additions made on the references.

Therefore you can select the content with the `:selection` and `:include` keys
and customize the rendering with direct references in the `:ct` vector of the
view.

### Styling
Overarch supports custom styles for elements. For an example see
[views.edn](/models/test/views.edn).

#### Keys

key           | type    | values                   | description 
--------------|---------|--------------------------|------------
:id           | keyword | namespaced id            | used to reference styles
:for          | keyword | :rel, :element           | element type to be styled
:line-style   | keyword | :dashed, :dotted, :bold  | line style for relations
:line-color   | hex rgb | #0000FF for bright blue  | line color for relations
:border-color | hex rgb | #FF0000 for bright red   | border color for nodes
:text-color   | hex rgb | #003300 for dark green   | text color for names and descriptions
:legend-text  | string  |                          | meaningful text to show in legend

# Rendering
Views can be rendered into different formats via the Overarch CLI tool.
Views can also be rendered via templates, which allows full control over the
output and allows new output formats without changes in Overarch itself (see
[Template Based Artifact Generation](#template-based-artifact-generation)).

This section describes the rendering of views via the Overarch CLI tool.

## PlantUML
The specified views C4 architecture and UML views can be rendered to PlantUML
diagram specification (*.puml files). These can be rendered into different
formats (e.g. SVG, PNG, PDF) with PlantUML.

You can specify PlantUML specific directives with the **:plantuml** key.

```
   :plantuml {:sprite-libs [:azure :devicons]}
```

### Keys
key              | type    | example values           | description 
-----------------|---------|--------------------------|------------
:node-separation | integer | 50 (for 50 pixels)       | separation between nodes
:rank-separation | integer | 250 (for 250 pixels)     | separation between ranks
:sprite-libs     | vector  | sprite-lib keywords      | used to render sprites for techs, order defines precedence of the libs
:skinparams      | map     | {"monochrome" "true"}    | render generic skinparams (as `skinparam <key> <value>`)

### Sprite Support
Overarch supports PlantUML sprites to show a visual cue of the technology in
the elements of a diagram.
It does so by matching the value of the **:tech** key of a model element to
the list of sprites. You can also directly add a **:sprite** key to the
reference of a model element in a view. The explicit **:sprite** value takes
precedence over the **:tech** value.

The list of sprites contains sprites of the PlantUML standard library, e.g.
sprites for AWS and Azure. The mapping files from tech name to sprite
reside in [resources/plantuml](/resources/plantuml). 

Currently the following keys for sprite libs are supported:
 * :awslib14
 * :azure
 * :cloudinsight
 * :cloudogu
 * :devicons
 * :devicons2
 * :font-awesome-5
 * :logos

Overarch uses a default list of sprite libs to resolve sprites, if none is
provided in the view. By specifying the sprite list in the view, you can
change the order of the libs for lookups or specify different sprite libs.

The command line interface supports the option `--plantuml-list-sprites`
which prints the (long) list of sprite mappings. 

### Rendering PlantUML diagrams in VS Code
The Visual Studio Code PlantUML Extension allows previewing and exporting these
diagrams right from the IDE.

![PlantUML preview](/doc/images/overarch_plantuml_preview.png)

PlantUML plugins also exists for major IDEs and build tools (e.g. IntelliJ,
Eclipse, Maven, Leiningen).

* [PlantUML Plugin for Leiningen](https://github.com/vbauer/lein-plantuml)

## GraphViz
The concept view can be exported as a concept map to a GraphViz *.dot file.

### Rendering GraphViz diagrams
For GraphViz there are a few Visual Studio Code  extensions available that
allow previews of the generated Graphviz files. 

The images can be created with the *dot* executable, which resides in the bin
directory of the GraphViz installation.

You can specify Graphviz directives with the **:graphviz** key.
Currently only the configuration of the
[layout engine](https://graphviz.org/docs/layouts/) is supported.

### Keys

key              | type    | values                   | description 
-----------------|---------|--------------------------|------------
:engine          | keyword | e.g. :dot, :neato, :sfdp | the graphviz layout engine to use

## Markdown
Markdown is used to render textual representations of the views.
You can use converters to generate other formats like HTML or PDF from markdown.

You can specify Markdown directives with the **:markdown** key.

### Keys

key              | type    | values                   | description 
-----------------|---------|--------------------------|------------
:references      | boolean | true, false              | render references for nodes
:diagram         | map     | {:format "png"}          | render diagram image

