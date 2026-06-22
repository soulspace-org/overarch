# Overarch Features
Overarch is a lightweight modelling tool to capture architecture knowledge at various levels (e.g. enterprise, process, system, code, deployment).

## Model as Data
* uses Extensible Data Notation (EDN)
  * exportable to JSON
* files with sets (or vectors) of maps (nodes and relations, views, themes)
  * input model may be hierarchical
  * working model is purely graph based
* open, maps can be extended with namespaced keys
* composeable
* queryable via selection criteria

## Extensible Meta Model
* metamodel contains various domain models for
  * concepts
  * organisations
  * processes
  * use cases
  * software architecture
  * state machines
  * code/schemas
  * deployment architecture
  * domain driven design

## Template Based Text Generation
* user customizable Comb templates for rendering for textual formats, e.g.
  * PlantUML .puml files
  * Graphviz .dot files
  * markdown files
  * source code
  * reports
* configurable generation process (via /templates/gencfg,edn)
* access to overarch functionality from inside the templates

## CLI Tool
* provides access to all use cases via the command line