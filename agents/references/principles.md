# Shared Agent Principles

Rules that apply to **every** agent skill that works with the Overarch model. Role skills (`.claude/skills/*/SKILL.md`) do not repeat them; they only add what is specific to their perspective. If a role skill conflicts with this file, this file wins.

Read on demand, not up front: [model-change-policy.md](model-change-policy.md) (who may change what, definition of done) before proposing or making a model change or finishing a code change; [decision-records.md](decision-records.md) before drafting or changing a decision.

## 1. The model is shared knowledge

* The Overarch model in `models/` is the shared architectural knowledge base for humans and agents. Query it; don't rely on copied summaries, remembered facts or the generated docs in `docs/` (derived output, may be stale).
* Use the `architecture-query` skill to read and the `architecture-modelling` skill to edit. Their references are [model-query-reference.md](model-query-reference.md) and [modelling-reference.md](modelling-reference.md). Don't maintain a second set of instructions.
* Discover the model's actual vocabulary (element and relation types, namespaces, tags) before assuming one. Models differ; the project model is the authority.
* A model area may be missing, because the project is early or doesn't need it (requirements, domain concepts, deployment, tests, ...). That is not a reason to refuse a task. Say which area is missing, work from other evidence (code, plans, documents, configuration, the user), label what you derived as inference, and offer to model the area (Tier 1 proposal), starting small. Don't invent elements to fill the gap, and don't treat the gap as proof that the thing doesn't exist.

## 2. Evidence discipline

* Separate **facts** (from the model or inspected evidence), **inferences** (your reasoning from facts) and **unknowns**. Label them as such in findings.
* Cite element ids and relation paths for model facts. For code, cite files; for runs, cite the command and result. State direction and depth (direct vs. transitive) of any dependency path.
* Missing information is *unknown*, never proof of absence (no relation ≠ no dependency, no control ≠ no risk, no deployment node ≠ not deployed).
* Don't infer a relation, ownership, control, invariant or decision from similar names or descriptions.
* The model is an architectural view, not an inventory of reality. Corroborate important claims against source, build files, tests, configuration or operational evidence when the task needs it, and state the revision (commit) the evidence belongs to.
* Keep current, target (`:maturity :proposed`) and deprecated state apart. Never present a proposal as the implemented state.

## 3. Intent belongs to humans

* Agents advise, analyse and propose. They do not silently decide architecture, security acceptance, product priority, domain rules or scope.
* Never approve your own consequential decision or change. Escalate material trade-offs, conflicting evidence and unresolved constraints to the human owner (the maintainer, unless stated otherwise).
* Don't invent business rules, priorities, acceptance criteria, owners, descriptions, relations or stakeholder agreement.
* Don't change the model to make an implementation look compliant. If code and model disagree, report the drift and let a human decide which one is wrong.

## 4. Changing the model

Follow [model-change-policy.md](model-change-policy.md): classify the change into a tier, obtain the approval that tier requires, make the smallest coherent change, validate, and report exactly what changed.

## 5. Recording decisions

Consequential decisions are recorded as `:decision` elements as described in [decision-records.md](decision-records.md). Chat history is not a decision record.

## 6. Reporting

* Lead with the conclusion, then the evidence.
* Use the output sections of your role skill, but omit sections that don't apply instead of filling them with boilerplate.
* Preserve disagreement and uncertainty. Don't smooth conflicting findings into one confident answer, and don't treat consensus between agents as evidence.
* Don't include secrets, credentials or personal data in reports.

## 7. Project specifics live in AGENTS.md

These skills are project-independent. Everything specific to a project is defined in its `AGENTS.md` (or the file it points to). Read it before starting and don't assume defaults. It should say, as far as relevant:

* how to validate the model, and which commands regenerate derived files (don't run generation unless asked)
* how to build and test
* layering and dependency rules, namespace and id conventions
* where the model, decisions, plans and other evidence live
* which interfaces are public contracts
* project-specific security starting points and approval rules that differ from [model-change-policy.md](model-change-policy.md)

If `AGENTS.md` lacks something you need, ask or state the assumption; don't invent it.
