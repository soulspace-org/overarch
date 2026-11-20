# Overarch Model Reference for Queries

Reference for building selection criteria against any Overarch model with the `architecture-query` skill: the element types (`:el` values) that can occur in a model, the keys they carry and how elements relate to each other.

## Anatomy of an element

Every element is a map. Keys of all elements:

| Key | Type | Meaning |
|---|---|---|
| `:el` | keyword | element type, e.g. `:system`, `:request` (see the lists below) |
| `:id` | keyword | namespaced identifier, e.g. `:my.ns/my-system` |
| `:name` | string | display name |
| `:desc` | string, optional | short description |
| `:doc` | string, optional | longer documentation |

Nodes and relations additionally have `:tags` (set of strings, optional) and `:maturity` (keyword, optional).

**Nodes** describe things, **relations** connect two nodes and have `:from` and `:to`. In query results `:from` and `:to` hold node ids. In selection criteria they take a sub-criteria map, e.g. `:from {:id X}`. Relations of the code model and concept model may also carry `:from-card`, `:to-card`, `:from-role`, `:to-role` and `:qualifier`.

**Views** have a `:title` and rendering options (`:layout`, `:linetype`, `:include`, `:selection` (criteria), `:sketch`, `:styles`, `:theme`).

### Further keys by type

| Key | Applies to | Meaning |
|---|---|---|
| `:tech` | architecture and deployment elements (system, container, component, deployment nodes, technical relations such as `:request`) | technology |
| `:external` | systems and persons of the architecture model, role model nodes | external to the described system |
| `:subtype` | nodes | refinement of the type, e.g. `:database`, `:queue` |
| `:level` | `:use-case` | goal level |
| `:condition` | `:choice` | condition of the state machine choice |
| `:type`, `:visibility`, `:scope`, `:optional`, `:abstract` | code model nodes (field, function, method, parameter, class, ...) | code attributes |

## Selection criteria

`doc/topics/selection-criteria.md` describes all criterion keys. Frequently useful:

* Type and identity: `:el` (one keyword), `:els` (set of types), `:id`, `:ids`, `:name` (string or regex), `:namespace`, `:namespace-prefix`
* Classification: `:tag`, `:tags` (any of a set), `:all-tags`, `:tech`, `:techs`, `:subtype`, `:maturity`, `:external?`
* Kind: `:model-node?` (all nodes), `:model-relation?` (all relations)
* Relations: `:from` and `:to` with sub-criteria
* Hierarchy: `:child-of`, `:descendant-of`, `:parent-of`, `:ancestor-of`
* References: `:refers-to`, `:referred-by`
* Transitive queries: `:start` with `:refers` / `:referred` (and optional `:elements`)

For several types use `:els #{...}` or a vector of criteria maps (OR). Negate a key with `!`, e.g. `:!el :contained-in`.

## Start with discovery

A model uses only a subset of the types below, and the same thing can be modelled in different ways. For an unfamiliar model, first find out which types it contains:

```
-s '{:model-node? true}'        # read the :el of the results
-s '{:model-relation? true}'    # read the :el of the results
--select-view-references '{}'   # views present
```

Then query the types that actually occur.

## Resolving ids to names

Reference results (`-S`) and relations (`:from` / `:to`) contain only ids. To answer a question with names or descriptions:

1. Run the relation query and collect the ids.
2. Query the elements with `:ids`: `-s '{:ids #{:ns/a :ns/b}}'`. The result contains `:el`, `:name`, `:desc` and further keys.

For a single node, `{:id X}` is enough. To restrict the ids to a type, add `:el`, e.g. `{:el :person :ids #{...}}`.

Example, who uses what: `-s '{:el :uses}'` returns relations with `:from` and `:to` ids, and `-s '{:ids #{<collected ids>}}'` returns the persons, systems and use cases behind them.

## Node types by model area

| Area | Node types (`:el`) |
|---|---|
| Architecture | `:person`, `:system`, `:container`, `:component`, `:context-boundary`, `:enterprise-boundary` |
| Deployment | `:node` (infrastructure node, contains nodes and deployed containers), `:container-instance`, `:artifact`, `:system` (external) |
| Use cases | `:actor`, `:use-case`, `:person`, `:system`, `:container` |
| Concepts | `:concept`, `:person`, `:system`, `:container`, `:context-boundary`, `:enterprise-boundary` |
| Domain | `:domain`, `:bounded-context`, `:aggregate`, `:entity`, `:value-object`, `:command`, `:domain-event`, `:policy`, `:model` |
| Organization | `:organization`, `:org-unit` |
| Roles | `:role`, `:permission`, `:person` |
| Process | `:process`, `:capability`, `:goal`, `:non-goal`, `:requirement`, `:risk`, `:stakeholder`, `:constraint`, `:regulation`, `:control`, `:test`, `:decision`, `:information`, `:knowledge`, `:artifact`, `:version` |
| State machine | `:state-machine`, `:state`, `:start-state`, `:end-state`, `:choice`, `:fork`, `:join`, `:history-state`, `:deep-history-state` |
| Code | `:namespace`, `:package`, `:class`, `:interface`, `:protocol`, `:schema`, `:enum`, `:enum-value`, `:field`, `:function`, `:method`, `:parameter` |

Actors of use cases are usually `:person` or `:system` nodes (from the architecture model) connected to a `:use-case` by `:uses`, not `:actor` nodes. To find them, query the `:uses` relations and read the `:from` and `:to` ids that are not use cases. Query `{:el :actor}` only if the model really contains such nodes.

Meaning of the architecture nodes:
* `:system`: a system. External systems are black boxes, the described system contains `:container`s.
* `:container`: a deployable process, data store or queue (service, application, database).
* `:component`: a logical building block inside a container (module, layer).
* `:person`: a human user or role.
* `:context-boundary`, `:enterprise-boundary`: group elements that belong together.

## Relation types by model area

| Area | Relation types (`:el`) | Connects |
|---|---|---|
| Hierarchy (all areas) | `:contained-in` | child → parent. Derived from the nesting of elements. |
| Architecture (technical) | `:request` (synchronous), `:response`, `:send` (asynchronous, point-to-point), `:publish`, `:subscribe` (asynchronous via broker or topic), `:dataflow` | architecture elements |
| Deployment | `:deployed-to`, `:link` | container-instance or artifact → node. Node ↔ node. |
| Trace | `:instance-of`, `:implements` | element → its definition |
| Use cases | `:uses`, `:include`, `:extends`, `:generalizes` | actor ↔ use case. Use case ↔ use case. |
| Concepts | `:rel` (generic), `:is-a` (specialization), `:has` (composition or association) | concepts |
| Organization | `:responsible-for`, `:collaborates-with`, `:role-in` | organization nodes ↔ architecture or deployment elements. Person → org unit or process. |
| Roles | `:permission-of`, `:granted-for` | permission → system or role |
| Process | `:input-of`, `:output-of`, `:goal-for`, `:non-goal-for`, `:required-for`, `:risk-for`, `:stakeholder-of`, `:constraint-for`, `:regulation-for`, `:control-for`, `:test-for`, `:artifact-of`, `:version-of` | process nodes |
| State machine | `:transition` | state → state |
| Code | `:inheritance`, `:implementation`, `:association`, `:aggregation`, `:composition`, `:dependency` | code nodes |

`:rel` is the generic relation of concept models and is also usable between architecture elements. A model may use `:rel` or the specific types (`:request`, `:send`, ...) for the same purpose.

The id of a relation is generated from the connected elements (e.g. `:ns/a-request-b`) unless it is set explicitly.

## View types

Selected with `--select-views` / `--select-view-references`.

| Kind | View types (`:el`) |
|---|---|
| Architecture, static | `:system-landscape-view`, `:context-view`, `:container-view`, `:component-view`, `:deployment-view` |
| Architecture, dynamic | `:dynamic-view` |
| Structure | `:system-structure-view`, `:deployment-structure-view`, `:organization-structure-view` |
| Behaviour and requirements | `:use-case-view`, `:state-machine-view` |
| Concept, code, generic | `:concept-view`, `:code-view`, `:model-view` (all selected elements as a graph), `:glossary-view` (alphabetical text) |

The content of a view is given explicitly (element references) or selected by criteria in its rendering options.

## Containment (`:contained-in`)

| Parent | Children |
|---|---|
| `:system` | `:container` |
| `:container` | `:component` |
| `:context-boundary`, `:enterprise-boundary` | architecture nodes |
| `:node` | `:node` (nested deployment nodes), deployed containers |
| `:organization` | organization nodes |
| `:state-machine`, `:state` | state machine nodes |
| `:namespace`, `:package` | code nodes |
| `:class` | `:field`, `:function`, `:method` |
| `:interface` | `:method` |
| `:protocol` | `:function` |
| `:schema` | `:field` |
| `:enum` | `:enum-value` |
| `:function`, `:method` | `:parameter` |

Direct children: `{:el :contained-in :to {:id X}}`. Transitive: the `:start` form with `:refers` / `:referred`.

## Query patterns

| Question | Query |
|---|---|
| Which element types does the model use | `-s '{:model-node? true}'` and `-s '{:model-relation? true}'`, read the `:el` values |
| Actors of use cases | `-s '{:el :uses}'`, then resolve the non-use-case ids with `:ids` |
| Names for a list of ids | `-s '{:ids #{:ns/a :ns/b}}'` |
| All elements of one type | `-S '{:el :container}'` |
| Elements of several types | `-S '{:els #{:system :container :component}}'` |
| All runtime relations (no hierarchy) | `-S '{:model-relation? true :!el :contained-in}'` |
| Asynchronous messaging | `-S '{:els #{:send :publish :subscribe}}'` |
| External systems | `-s '{:el :system :external? true}'` |
| Where is X deployed | `-s '{:el :deployed-to :from {:id X}}'` |
| What is deployed on node N | `-s '{:el :deployed-to :to {:id N}}'` |
| Who is responsible for X | `-s '{:el :responsible-for :to {:id X}}'` |
| Elements using a technology | `-S '{:tech "Clojure"}'` |
| Elements with a tag | `-S '{:tag "critical"}'` |
| Elements by name | `-S '{:name "(?i).*part.*"}'` |

If a query returns nothing, the model may use a different type than assumed (e.g. `:rel` instead of `:request`). List the types in use first, then query again.
