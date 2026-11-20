# Overarch Modelling Reference

Reference for creating and editing Overarch models and views. For the element types and their keys see [model-query-reference.md](model-query-reference.md). For querying a model see the `architecture-query` skill. Details are in `doc/topics/modelling.md`, `doc/topics/views.md`, `doc/topics/best-practices.md` and `doc/topics/commandline.md` in the [Overarch repository](https://github.com/soulspace-org/overarch).

## File format

* Models and views are [EDN](https://github.com/edn-format/edn) files. Each file contains one top-level **set** `#{...}` of element maps.
* Overarch reads all `.edn` files below the model directory recursively (`-m, --model-dir`, default `models`), so the folder structure is free. Model nodes, relations and views can be in any file or folder.
* Comments start with `;`. A `;;;` header comment at the top of a file is customary.
* Strings can span several lines. Keywords are written `:namespace/name`.
* Unprefixed keywords and the namespace `overarch` are reserved for map keys defined by Overarch. Custom keys on elements need your own namespace prefix, e.g. `:my.org/owner`.

```clojure
;;;;
;;;; Model of the Example System
;;;;
#{{:el :person
   :id :example/customer
   :name "Customer"
   :desc "Buys things."}}
```

## Elements

Every element is a map with at least `:el` (type) and `:id` (identifier). Nodes need a `:name`. Relations need `:from` and `:to`. Views need a `:title`.

| Key | Meaning |
|---|---|
| `:el` | element type keyword, e.g. `:system` |
| `:id` | globally unique, namespaced keyword |
| `:name` | short name |
| `:desc` | short description, rendered in diagrams |
| `:doc` | longer documentation, only in textual output |
| `:tags` | set of strings for selection, e.g. `#{"critical"}` |
| `:maturity` | keyword, e.g. `:proposed`, `:deprecated` |
| `:ct` | children (nodes) of a node, or the content of a view |
| `:tech` | technology string (architecture and deployment nodes, technical relations) |
| `:subtype` | refinement of the type, e.g. `:database`, `:queue` |
| `:external` | boolean, true for systems and persons outside the described system |

The types and further keys per area are listed in [model-query-reference.md](model-query-reference.md).

## Identifiers

* Ids are namespaced keywords. The namespace should mirror the folder structure of the model, e.g. file `models/shop/billing/model.edn` uses `:shop.billing/...`.
* The name part is lower-case with hyphens (`:shop.billing/invoice-service`).
* Ids must be unique over the whole model. Selection by `:namespace` or `:namespace-prefix` depends on consistent namespaces.
* When renaming an id, replace it in all model files at once. Relations and views refer to ids and break otherwise.
* If a relation has no `:id`, one is generated as `<from-name>-<relation-type>-<to-name>`.

## Hierarchy

Containment can be written in two ways.

**Nested with `:ct`** (a set of child maps):

```clojure
{:el :system
 :id :shop/webshop
 :name "Webshop"
 :ct #{{:el :container
        :id :shop.webshop/api
        :name "API"
        :tech "Clojure"}
       {:el :container
        :id :shop.webshop/db
        :subtype :database
        :name "Database"
        :tech "PostgreSQL"}}}
```

**Flat with `:contained-in` relations** (child → parent). This avoids touching the parent when adding children and works across files:

```clojure
{:el :contained-in
 :id :shop.webshop/cache-contained-in-webshop
 :from :shop.webshop/cache
 :to :shop/webshop}
```

Nested `:ct` hierarchies are converted to `:contained-in` relations when the model is loaded, so both forms give the same model. Allowed parent/child combinations are in the containment table of [model-query-reference.md](model-query-reference.md).

For big systems prefer `:contained-in` relations to nesting across files.

## Relations

```clojure
{:el :request
 :id :shop.webshop/spa-requests-api
 :from :shop.webshop/spa
 :to :shop.webshop/api
 :name "Gets data from"
 :tech "HTTPS, JSON"}
```

* `:from` and `:to` are node ids. Both nodes must exist. Otherwise loading reports an unresolved reference (see Validation).
* Prefer the specific types (`:request`, `:response`, `:send`, `:publish`, `:subscribe`, `:dataflow`, `:uses`, `:deployed-to`, ...) and use `:rel` only for concept models.
* Id convention: the id of the `:from` node name, a verb for the relation type, and the name of the `:to` node, e.g. `spa-requests-api`. Put the relation in the file of the `:from` node.
* Semantics in the architecture model: `:request` and `:response` are synchronous, `:send`, `:publish` and `:subscribe` are asynchronous. For `:publish` and `:subscribe` the other end should be a broker, topic or queue.

### Deployment

Nodes (`:node`) form the infrastructure hierarchy through `:ct`. Container instances (`:container-instance`) are attached to nodes with `:deployed-to` (container instance → node):

```clojure
{:el :node
 :id :shop.deployment/datacenter
 :name "Datacenter"
 :ct #{{:el :node
        :id :shop.deployment/k8s
        :name "Kubernetes"
        :tech "Kubernetes"}}}

{:el :container-instance
 :id :shop.webshop/api-instance
 :name "API"
 :tech "Clojure"}
       
{:el :deployed-to
 :id :shop.webshop/api-instance-deployed-to-k8s
 :from :shop.webshop/api-instance
 :to :shop.deployment/k8s}
```

`:instance-of` relates an instance (e.g. a `:container-instance` in a deployment node) to its definition.

### Use cases

Use persons, systems or containers from the architecture model as actors. Connect them to `:use-case` nodes with `:uses` (actor → use case), and use cases with `:include`, `:extends` and `:generalizes`:

```clojure
{:el :use-case
 :id :shop.usecase/place-order
 :level :user-goal
 :name "place order"}

{:el :uses
 :id :example/customer-uses-place-order
 :from :example/customer
 :to :shop.usecase/place-order}
```

`:level` is one of `:summary`, `:user-goal`, `:subfunction`.

### Traceability

`:implements` relates an implementation to its definition (e.g. a container to a use case). `:instance-of` relates an instance to its definition.

## References

`{:ref <id>}` points to an existing element. It is used for view content and can also refer to elements in other files. Extra keys on a ref are merged with the referenced element for that context only:

```clojure
{:ref :shop/other-system :external true}      ; render as external in this view
{:ref :shop.webshop/spa-requests-api :direction :up}
```

Ref keys that control rendering:

| Key | Applies to | Meaning |
|---|---|---|
| `:collapsed` | nodes | true: don't render children |
| `:direction` | relations | `:left`, `:right`, `:up`, `:down` |
| `:reverse` | relations | true: reverse the direction (C4 diagrams) |
| `:constraint` | relations | true: layout hint only, not rendered (C4 diagrams) |
| `:index` | relations in dynamic views | number in the interaction order |
| `:sprite` | nodes | explicit PlantUML sprite, overrides `:tech` |
| `:name`, `:desc` | any | text override in this view |

## Boundaries

`:enterprise-boundary` groups systems by company. `:context-boundary` groups concepts, containers or components by context (e.g. a DDD bounded context). System and container boundaries are not modelled. They are drawn by the renderer when a system with containers is shown in a container or component view.

## Views

A view is an element with a view type as `:el`, an `:id` (also used as the output file name) and a `:title`.

```clojure
{:el :container-view
 :id :shop.webshop/container-view
 :title "Containers of the Webshop"
 :layout :left-right
 :selection {:namespace-prefix "shop"}
 :include :relations
 :ct [{:ref :shop.webshop/spa-requests-api :direction :up}]}
```

| Key | Meaning |
|---|---|
| `:el` | view type: `:context-view`, `:container-view`, `:component-view`, `:system-landscape-view`, `:dynamic-view`, `:deployment-view`, `:deployment-structure-view`, `:system-structure-view`, `:organization-structure-view`, `:use-case-view`, `:state-machine-view`, `:concept-view`, `:code-view`, `:model-view`, `:glossary-view` |
| `:id`, `:title` | file name, rendered title |
| `:selection` | criteria map or vector of maps that selects the content (see the selection criteria documentation) |
| `:include` | `:referenced-only` (default), `:relations` (add all relations between included elements), `:related` (add elements taking part in included relations) |
| `:ct` | **vector** of `:ref`s (or elements). The order matters for rendering |
| `:layout` | `:top-down`, `:left-right` |
| `:linetype` | `:orthogonal`, `:polygonal` |
| `:sketch` | boolean, sketch style |
| `:styles`, `:themes` | style definitions or theme id |
| `:expand-external` | boolean: show the content of external systems in container and component views |
| `:plantuml`, `:graphviz`, `:markdown` | renderer specific options, e.g. `:plantuml {:sprite-libs [:azure :devicons]}` |

Notes:
* Since Overarch v0.38.0 view keys like `:selection` sit directly on the view, no `:spec` map is needed.
* A view only renders element kinds that fit its type. A system landscape view shows persons and systems but no use cases, even when they are referenced.
* The result is the selection merged with the refs in `:ct` (refs can override keys), then the includes are added. Select with `:selection`/`:include` and customize with refs in `:ct`.
* A `:model-view` with `:selection {}` renders the whole model. Useful to find unconnected elements.
* Use consistent view ids and names per namespace (e.g. a `context-view` in each top-level architecture namespace) so generated documentation stays regular.
* Use tags for focused views: `:selection {:tags #{"payment"}}` or `:all-tags`.
* Views for specific content, such as the order of interactions in a dynamic view, may contain their own relations in `:ct`.

### Styles

```clojure
:styles #{{:id :shop/critical :for :element :border-color "#FF0000" :legend-text "critical"}}
```

Style keys: `:id`, `:for` (`:rel` or `:element`), `:line-style` (`:dashed`, `:dotted`, `:bold`), `:line-color`, `:border-color`, `:text-color`, `:legend-text`.

## Workflow

Run from the repository root with `java -jar tools/overarch.jar`.

| Task | Command |
|---|---|
| Load the model and check for problems | `-m models -s '{:id :x/none}'` or any query. The model warnings are printed first |
| Render all views | `-r plantuml`. Output goes to `export/`, or to the directory given with `-R` |
| Rebuild on every change | `-r plantuml -w` |
| Export | `-x json` or `-x structurizr` (`-X` for the directory) |
| Generate artifacts from templates | `-T templates -g templates/gencfg.edn -G generated` |
| Model location | `-m <dir>`, models directory or path |
| Use the model without warnings | `--no-model-warnings` |
| Print views | `--select-views '<criteria>'`, `--select-view-references '<criteria>'` |

PlantUML renders `.puml` to diagrams with `tools/plantuml.jar`. Rendering needs no extra configuration.

## Validation

After every change, load the model and read the warnings:

```
java -jar tools/overarch.jar -m <model-dir> -S '{:namespace-prefix "<your.ns>"}'
```

The "Model Warnings" block at the start of the output has three lists:
* `:build-problems`: the model could not be built correctly.
* `:unresolved-refs-in-relations`: a `:from` or `:to` points to an id that does not exist. Typical cause: a typo or a node that is defined in a file outside the model path.
* `:unresolved-refs-in-views`: a `:ref` in a view points to a missing element or relation.

All three lists should be empty. A "Spec failed" error means an element or a criteria map has an invalid shape. The message names the offending value.

Check the result with queries, e.g. count elements per type, list containers of a system, and check that every new node appears in the views that should show it (see the `architecture-query` skill).

## Practices

* **Be consistent.** Consistent ids, types and structure are the basis for selection-based views and template generation.
* Split large models into several folders and files. Mirror the folders in the id namespaces.
* Put each relation in the file of its `:from` node and name it as described above.
* Use specific relation types. Use `:rel` only in concept models.
* Model actors of use cases as persons or systems of the architecture model. Use `:actor` only for actors that don't exist there (e.g. a timer).
* Select view content by criteria and use `:include :relations` for the connections. Use refs for per-view customization.
* Tag elements consistently if views should select by tags.
* Keep `:desc` short, since it is rendered in diagrams. Use `:doc` for longer text.
* Don't model the code base completely. Use code models for domain models and design discussion.
* Start with the level that provides value: context, containers, key components. Add use cases, deployment, concepts and other areas when they are useful.
