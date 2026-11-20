# Model Change Policy

Defines which model changes an agent may make, which need approval, and when a code change is incomplete without a model change. Applies to all agent skills; see [principles.md](principles.md).

The human owner is the maintainer of the repository. "Approval" means an explicit yes in the current conversation or review for that specific change; approval for one change does not extend to another.

## Change tiers

| Tier | Examples | Agent may | Approval |
|---|---|---|---|
| **0 – Editorial** | Fix typos, clarify `:desc`/`:doc`, add or fix `:tags`, fix a dangling reference caused by the agent's own edit | Apply directly, report afterwards | None |
| **1 – Additive** | Add a node (component, namespace, use case…), add a relation, add a view or add elements to a view, add a `:decision` with `:maturity :proposed` | Propose first (what, where, why), apply after approval | Maintainer, per change set |
| **2 – Structural** | Rename or move an element (changes its id or `:contained-in`), delete an element or relation, change a relation type or direction, change boundaries/containment of systems, containers or layers | Propose with impact analysis (direct and transitive references, views affected). Apply only after approval | Maintainer, explicitly named change |
| **3 – Intent** | Accept, supersede or deprecate a decision; change goals, non-goals or constraints; change a public contract; anything that alters what the system is meant to be | Draft a proposal only. Do not apply. | Maintainer decides and applies or explicitly instructs |

When in doubt, use the higher tier. A change that alters meaning, boundaries or important relationships is never Tier 0.

## Rules for every change

1. Describe the intended change and its rationale before editing (Tier 1 and up).
2. Keep change sets small, reviewable and focused on one purpose. No drive-by normalization or restructuring.
3. Preserve ids and relations unless the change is explicitly Tier 2.
4. Don't delete elements only because they look unused.
5. Follow existing conventions (id namespaces, relation id pattern, file placement, tags). See [modelling-reference.md](modelling-reference.md).
6. Validate after every change set and report the command, scope and result. A clean validation proves syntax and reference integrity only, not that the model is correct or complete.
7. Re-query the affected neighbourhood afterwards and report exactly what changed (ids added, modified, removed).
8. Only one agent edits the canonical model at a time. In multi-agent work the orchestrator assigns the editor, other agents propose.

## Definition of done: code and model stay in sync

A code or build change is **not done** until the model is consistent with it. Check at the end of every change:

| If the change… | Then the model must… | Tier |
|---|---|---|
| adds, removes or renames a container or component | have the element, its containment and its views updated | 1–2 |
| adds or removes a dependency between components or namespaces at architectural level | have the corresponding `:dependency`/`:uses`/etc. relation updated | 1–2 |
| adds a user-visible capability or CLI behavior | have a use case (and its relations to goals) added or updated | 1 |
| introduces a new technology, external system or format | have the system/container/tech reflected and, if consequential, a decision recorded | 1 |
| changes a layer boundary, a public contract (CLI, exit codes, file formats, policy file format) or a mandated constraint | have a decision recorded and the affected constraints/goals reviewed | 3 |
| is purely internal (private functions, refactoring inside a component, tests, formatting) | need **no** model change | – |

Not every namespace, class or function belongs in the model. Model what a reader needs to understand structure, responsibility and dependencies.

Before finishing, state one of:
* "Model unchanged: <reason>", or
* "Model updated: <ids and validation result>", or
* "Model update proposed: <what, tier>, awaiting approval".

Pending proposals are not silently dropped; they are listed under remaining work.

## Drift

Differences between model and code found while working are reported, not hidden and not auto-fixed. Classify each as: model outdated (propose update), code violates intended boundary (propose code fix), or unclear (ask the maintainer).
