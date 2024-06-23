# Rationale for Overarch
Modelling and vizualizing a software system or a system landscape is useful for
thinking, communication, documentation and onboarding collegues. The systems
can be modelled with different level of details and vizualized with different
views. 

To model a system you can either use a modelling tool or a diagramming tool.
Most modelling tools are commercial and lock in your models or not comprehensive.
Modelling is also often decoupled from development.

Most diagramming tools don't use a model or don't separate the model information
from the layout. The scope of most diagramming tools is limited to diagrams only.
With diagramming tools like Excalidraw or Gliffy it's quite easy to draw some
diagrams for a system. But changing existing diagrams can be cumbersome when
diagram elements have to be placed and moved manually. This also limits the
scope and size of the diagrams that can be created and maintained.

Some problems most of these tools often face is versioning and collaboration.
By using a text based representation of the model and the views, a development
workflow with version control, e.g. git can be applied to modelling. 

Some tools like PlantUML or Structurizr provide a DSL to model a system or to
describe diagrams. While PlantUML operates at the diagram level and is
basically a text based diagramming tool with auto layout, structurizr is a
modelling tool which also features a graphical editor. But to use the model in
a different context, the DSL has to be parsed.

Overarch addresses these issues by providing a text based data format for the
specification of the model and the views of the model. By using plain data
literals like sets and maps, no DSL parser is needed to instanciate the model.


Overarch is free, open and extensible
* provides model information as plain data
* models are readable, discoverable, composable, reusable and extensible
* model queries, vizualization, artifact generation
