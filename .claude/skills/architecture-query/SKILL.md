---
name: architecture-query
description: Answer questions about a system, software or enterprise architecture (goals, quality attributes, containers, systems, deployment, relations, dependencies, hierarchy, views, impact analysis) by querying the Overarch model with tools/overarch.jar instead of reading the generated docs. Use for questions like "how many containers", "which systems does X talk to", "what depends on X", "what is contained in X", "which views exist", "who is responsible for X", "what are the quality attributes of X".
---

# Architecture queries with Overarch

The model is the source of truth. The generated documentation is derived output and can be stale, so query the model and don't grep the generated docs. Project-specific details (namespaces, system ids) are in the project's `AGENTS.md`/`CLAUDE.md`.

For the element types (`:el` values), their keys and relation semantics, read the model query reference linked in the project's `AGENTS.md`/`CLAUDE.md` (in the Overarch repository: `agents/model-query-reference.md`). Read it when the model is unfamiliar or a query returns nothing.

## Run a query

Run from the repository root, with the jar at `tools/overarch.jar`:

```
java -jar tools/overarch.jar -s '<criteria>'                  # full elements
java -jar tools/overarch.jar -S '<criteria>'                  # references (ids only), compact
java -jar tools/overarch.jar --select-views '<criteria>'      # full views
java -jar tools/overarch.jar --select-view-references '<criteria>'
```

- Criteria are EDN: a map (AND), or a vector of maps (OR). Prefix a key with `!` to negate it (`:!namespace`).
- Use single quotes around the criteria in bash.
- Output starts with a "Model Warnings" block. Check it for build problems or unresolved refs before trusting the result. Filter it with `| grep -v -E "build-problems|unresolved"` if needed.
- Use `-S` first for counting or listing. Use `-s` when you need names, descriptions, or technologies.
- A "Spec failed" error means the criteria are malformed. Read the message, as it points at the offending value.

## Element ids

Ids are namespaced keywords, e.g. `:some.namespace/some-element`. Relation ids are generated from the connected elements, e.g. `:some.namespace/a-request-b`. If you don't know an id, list candidates first, for example with `-S '{:el :system}'` or `-S '{:name "(?i).*part-of-name.*"}'`.

## Recipes

`X` stands for an id keyword, `NS` for an id namespace string.

| Question | Query |
|---|---|
| All containers | `-S '{:el :container}'` |
| Containers in a namespace | `-S '{:el :container :namespace "NS"}'` |
| All systems | `-S '{:el :system}'` |
| All relations of a node (in and out) | `-s '[{:model-relation? true :from {:id X}} {:model-relation? true :to {:id X}}]'` |
| Outgoing relations of a node | `-s '{:model-relation? true :from {:id X}}'` |
| Outgoing relations to other systems only | `-S '{:model-relation? true :from {:id X} :to {:el :system}}'` |
| Views in a namespace | `--select-view-references '{:namespace-prefix "NS"}'` |

### Hierarchy (`:contained-in`)

Overarch holds the model as a graph in memory, regardless of how the input EDN is structured (nested or flat). Hierarchical containment (system > container > component, boundaries) is represented by `:contained-in` relations. They are regular relations of the model, not synthetic or derived artifacts, so query containment through them. The relation points from the child to its parent.

`:contained-in` relations show up in every unfiltered relation query. Add `:!el :contained-in` (or `:el :request`) when you only want runtime interactions.

| Question | Query |
|---|---|
| Children of X (containment relations) | `-S '{:el :contained-in :to {:id X}}'` |
| Children of X (the nodes themselves) | `-S '{:start {:id X} :refers {:el :contained-in}}'` |
| Parent of X | `-S '{:el :contained-in :from {:id X}}'` |
| All ancestors of X | `-S '{:start {:id X} :referred {:el :contained-in}}'` |
| Runtime relations of X without containment | `-S '{:model-relation? true :!el :contained-in :from {:id X}}'` |

The `:start` form follows relations transitively, so with deeper nesting it also returns grandchildren. The direct form (`:el :contained-in :to`) returns only direct children.

### Other node and relation types

Overarch has many semantically distinct node and relation types, e.g. `:contained-in`, `:request`, `:deployed-to`, `:instance-of`, `:responsible-for`, and node types such as organizations, artifacts or deployment nodes. A given model uses only a subset. Don't assume that a question maps to `:contained-in` or `:request`: the same question can be answered through different relation types depending on what is being asked about. For example, container instances are attached to deployment nodes by `:deployed-to`, not by `:contained-in`.

When a query returns nothing or fewer results than expected:
- List all the nodes with `-s '{:model-node? true}'` and look at their types to understand what kinds of elements exist in the model.
- List a node's relations with `-s` (the `:el` of each result is its relation type), using the "All relations of a node" recipe.
- Then query with the relation type that actually connects the elements, and combine it with a transitive query where needed (see below).

### Transitive queries

A map with `:start` follows relations transitively:

- `:refers {:el :request}` walks relations backwards. It finds the callers, i.e. what is affected if the start node changes.
  Example: `-S '{:start {:id X} :refers {:el :request}}'`.
- `:referred {:el :request}` walks relations forwards. It finds the dependencies, i.e. what the start node needs.
  Example: `-S '{:start {:id X} :referred {:el :request}}'`.
- Add `:elements {...}` to restrict which of the visited nodes are returned.

Transitive queries only work at the top level of the criteria. They can't be used as a sub-selection, e.g. inside the `:from`/`:to` criteria of another query (`{:el :deployed-to :to {:start ...}}` fails with `unknown criterium start`). To combine them, run the transitive query first, then use the resulting ids in a second query, e.g. as an OR vector of `:id` criteria.

## Answering

State the answer first, then show the query that produced it. Mention it if the answer depends on a modelling choice or on a model warning.
