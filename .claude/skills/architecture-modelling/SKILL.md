---
name: architecture-modelling
description: Create or change Overarch models and views (EDN files) and validate them with tools/overarch.jar. Mechanics layer used by the specialist agent skills; executes approved changes.
---

# Architecture modelling with Overarch

Models and views are EDN files. Change them directly, then validate by loading the model with `tools/overarch.jar`. Don't edit generated documentation (derived output).

This skill covers *how* to change the model, not *whether* a change may be made. If the project has a change policy ([model-change-policy.md](../../references/model-change-policy.md)), classify the change by tier and get the approval the tier requires before editing. Without one, describe the change and get the user's agreement for anything beyond typo and description fixes. Don't apply Tier 3 changes (accepted decisions, goals, constraints, public contracts) yourself. A request to "look at" or "evaluate" something is not approval to edit.

## Before editing

1. Read the modelling reference named in the project's `AGENTS.md`/`CLAUDE.md` (Overarch repository: `../../references/modelling-reference.md`, `../../references/model-query-reference.md`).
2. Look at the existing model with the `architecture-query` skill (e.g. `-S '{:namespace-prefix "<ns>"}'`) and a neighbouring file, so the change follows its conventions (id namespaces, relation naming, tags, view naming).
3. Find the file for the new element: the id namespace mirrors the folder structure, and a relation goes into the file of its `:from` node.

## Rules

* Every element has `:el` and a model-wide unique namespaced `:id`. Nodes have `:name`, relations `:from` and `:to`, views `:title`.
* Use the specific types of the model area (`:request`, `:send`, `:deployed-to`, `:uses`, ...); `:rel` only in concept models.
* Containment: nested `:ct` sets, or `:contained-in` relations (child → parent) when the parent is in another file. Both load identically: the loaded model has only `:contained-in` relations (`:ct` is dropped on read), so verify containment by querying them.
* Link a `:container-instance` to its `:container` with `:instance-of` (instance → definition) and to its node with `:deployed-to`; don't rely on names.
* Name relation ids `<from-name>-<verb>-<to-name>`. Use persons or systems of the architecture model as use case actors. Keep `:desc` short (rendered in diagrams).
* Views: prefer `:selection` plus `:include`; use refs in `:ct` for per-view customization.
* When renaming an id, replace it in all model and view files and check refs and relations.
* Keep the existing layout and formatting; don't reformat unrelated parts.
* Mark planned elements `:maturity :proposed` and obsolete ones `:maturity :deprecated`. Never model a target state as current, and don't remove a marker without the maintainer's decision.
* Decisions are `:decision` elements linked with `:implements` to the goals, constraints or systems they serve; follow existing ones for namespace, tags and wording, and [decision-records.md](../../references/decision-records.md) for lifecycle and approval, if the project has it.
* Code model (`:namespace`, `:package`, `:function`, ...): mirror source structure at the level of detail the project already uses. Check how existing code elements are named, contained and related (`:contained-in`, `:dependency`) first; don't add one per source construct.
* When adding, removing or renaming an element, check which views show it (`--select-views`; views whose `:selection` or `:ct` covers it) and update explicit refs. A view that omits a new element or references a removed one is a defect.

## Validate every change

From the overarch directory (with bash quoting):

```
java -jar tools/overarch.jar -m <model-dir> -S '{:namespace-prefix "<ns>"}'
```

In the "Model Warnings" block, `:build-problems`, `:unresolved-refs-in-relations` and `:unresolved-refs-in-views` must be empty; "Spec failed" names the invalid value. Then confirm the change with a query (new element and relations present, right parent); check affected views with `--select-views '{:id <view-id>}'` or render with `-r plantuml`. Don't continue while warnings are non-empty unless they pre-date your change; report those separately. A clean validation proves syntax and reference integrity only, not correctness or completeness.

## Report exactly what changed

Compare before and after instead of reporting from memory:

```
git diff --stat -- <model-dir>
git diff -U0 -- <model-dir> | grep -E '^[+-].*:(id|from|to) '    # ids and endpoints added (+) or removed (-)
```

A modified element appears as a removed and an added line. The diff shows the *source*: a nested child appears only as an `:id` line, without its `:contained-in` relation, so confirm containment, `:instance-of` and other trace relations by query. Re-query the neighbourhood of every changed element and check that no unintended relation appeared or vanished. Untracked files are new elements; list them from the file.

## Answering

1. **Files changed**, and elements added, modified, removed (ids).
2. **Validation:** command and result, pre-existing warnings separately.
3. **Choices for review:** relation type, parent, tags, maturity, and anything you left unchanged but should be reviewed.
4. **Model status:** "Model updated: <ids>" or "Model update proposed: <what, tier>, awaiting approval".
