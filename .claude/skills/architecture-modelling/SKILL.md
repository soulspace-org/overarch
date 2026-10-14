---
name: architecture-modelling
description: Create or change Overarch architecture models and views (EDN files) - add or edit systems, containers, components, persons, relations, deployment nodes, use cases, concepts, views, and check the result with tools/overarch.jar. Use for requests like "add a container to the model", "model the new service", "add a relation from X to Y", "create a container view", "rename an element", "model the deployment", "add use cases".
---

# Architecture modelling with Overarch

Models and views are EDN files. Change them directly, then validate by loading the model with `tools/overarch.jar`. Don't edit generated documentation, it is derived output.

## Before editing

1. Read the modelling reference. Use the path named in the project's `AGENTS.md`/`CLAUDE.md`. In the Overarch repository these are `agents/modelling-reference.md` (file format, ids, hierarchy, relations, views, validation) and `agents/model-query-reference.md` (element types and keys by model area).
2. Look at the existing model first, so the change follows its conventions (id namespaces, relation naming, tags, view naming). Use the `architecture-query` skill for this, e.g. `-S '{:namespace-prefix "<ns>"}'`, and read a neighbouring file.
3. Find the file where the new element belongs. The id namespace mirrors the folder structure, and a relation goes into the file of its `:from` node.

## Rules to follow

* Every element has `:el` and a namespaced `:id` that is unique in the whole model. Nodes have a `:name`, relations `:from` and `:to`, views a `:title`.
* Use the specific element and relation types of the model area (`:request`, `:send`, `:deployed-to`, `:uses`, ...). Use `:rel` only in concept models.
* Containment: nested `:ct` sets, or `:contained-in` relations (child → parent) when the parent is in another file.
* Name relation ids `<from-name>-<verb>-<to-name>`.
* Use persons or systems of the architecture model as use case actors.
* Keep `:desc` short. It is rendered in diagrams.
* For views prefer `:selection` plus `:include`, and use refs in `:ct` for per-view customization.
* When renaming an id, replace it in all model and view files, and check refs and relations.
* Keep the existing file layout and formatting. Don't reformat unrelated parts.

## Validate every change

Load the model and read the "Model Warnings" block that starts the output:

```
java -jar tools/overarch.jar -m <model-dir> -S '{:namespace-prefix "<ns>"}'
```

`:build-problems`, `:unresolved-refs-in-relations` and `:unresolved-refs-in-views` must be empty. A "Spec failed" error names the invalid value. Then confirm the change with a query, e.g. that the new element and its relations are present and that it is contained in the right parent. If a view should show the new element, check the view with `--select-views '{:id <view-id>}'` or render it with `-r plantuml`.

Run commands from the repository root. Don't continue with further changes while the warnings aren't empty, unless the warnings existed before your change. Report those separately.

## Answering

Say which files you changed and what you added or modified, show the validation result, and mention any open warnings or modelling choices (e.g. the relation type or parent you picked) for review.
