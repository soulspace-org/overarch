# Decision Records

How architecture decisions are recorded so they outlive a conversation. Decisions live in the Overarch model as `:decision` elements, in the file where the project's existing decisions are kept (see `AGENTS.md`). Applies to all agent skills; see [principles.md](principles.md) and [model-change-policy.md](model-change-policy.md).

## When to record a decision

Record a decision when it is **consequential and hard to reverse or hard to rediscover**, for example:

* a technology, library, format or runtime choice
* a layer or component boundary, or the direction of a dependency
* a public contract (CLI options, exit codes, policy file formats, output formats)
* a deliberate trade-off between quality attributes
* a deliberate non-decision with lasting effect ("we will not support X because…")

Don't record routine implementation choices that are visible in the code and easy to change.

## Element convention

Follow the project's existing `:decision` elements for id namespace, tags and wording. Typical shape (`<ns>` is the project's id namespace):

```clojure
{:el :decision
 :id :<ns>.decision/<short-kebab-slug>
 :name "<decision as a short statement>"
 :desc "<what was decided and why, one or two sentences>"
 :doc "<context, options considered, consequences, how to revisit>"
 :maturity :proposed          ; omit once accepted
 :tags #{"<existing tag>"}}

{:el :implements
 :id :<ns>.decision/<slug>-implements-<goal-or-constraint-slug>
 :from :<ns>.decision/<slug>
 :to :<ns>.goal/<goal>}  ; also constraints, the system, or components affected
```

* `:desc` is rendered in diagrams: keep it short. Put options and consequences in `:doc` as markdown, based on the template by Michael Nygard (status, context, options, decision and consequences).
* Link each decision with `:implements` relations to the goal(s) it serves, the constraint(s) it respects and, where useful, the system or component it applies to. Decisions without links are hard to find from the affected elements.
* Reuse the tags the model already uses for decisions; introduce new tags only deliberately.

## Lifecycle

| State | Model | Who sets it |
|---|---|---|
| Proposed | `:maturity :proposed` | Any agent may draft (Tier 1, approval to apply) |
| Accepted | no `:maturity` key | Maintainer only (Tier 3) |
| Deprecated / superseded | `:maturity :deprecated`; `:doc` names the successor decision id and the reason | Maintainer only (Tier 3) |

* Never edit the meaning of an accepted decision in place. Add a new decision that supersedes it and deprecate the old one, so history stays readable.
* Don't treat a `:proposed` decision as binding when judging code or other changes. Say that it is only a proposal.
* An agent must not accept its own proposal.

## What a good proposal contains

Put it in the response and in `:doc`:

1. **Context:** the situation, forces and constraints (cite goal and constraint ids).
2. **Options:** at least two, including "do nothing" where realistic.
3. **Decision (proposed):** the recommended option, or "human decision needed" when evidence doesn't decide it.
4. **Consequences:** benefits, costs, risks, reversibility, follow-up work and model/code impact.
5. **Evidence:** model paths, code, measurements and their revision.
