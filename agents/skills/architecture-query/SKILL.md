---
name: architecture-query
description: Answer factual questions about an architecture (containers, systems, relations, dependencies, hierarchy, deployment, views, goals, impact) by querying the Overarch model with tools/overarch.jar instead of the generated docs. Mechanics layer used by the specialist agent skills.
---

# Architecture queries with Overarch

The model is the source of truth; generated docs are derived and can be stale. Project specifics (namespaces, ids, reference paths) are in the project's `AGENTS.md`/`CLAUDE.md`.

Read the model query reference named there (Overarch repository: `agents/model-query-reference.md`) before querying an unfamiliar model and whenever a query returns nothing. It lists the element and relation types, selection criteria keys, discovery queries, id resolution and common query patterns. This skill adds only how to run queries, hierarchy and transitive queries, and traceability checks.

## Run a query

From the overarch directory (with bash quoting):

```
java -jar tools/overarch.jar -s '<criteria>'                  # full elements
java -jar tools/overarch.jar -S '<criteria>'                  # references (ids only)
java -jar tools/overarch.jar --select-views '<criteria>'      # full views
java -jar tools/overarch.jar --select-view-references '<criteria>'
```

- Use single quotes around the criteria in bash.
- Output starts with a "Model Warnings" block; check it for build problems or unresolved refs before trusting the result (`| grep -v -E "build-problems|unresolved"` hides it).
- Use `-S` to count or list, `-s` for names, descriptions, technologies. "Spec failed" means malformed criteria; the message points at the value.

| Question | Query (`X` = id keyword, `NS` = namespace string) |
|---|---|
| Containers in a namespace | `-S '{:el :container :namespace "NS"}'` |
| All relations of a node (in and out) | `-s '[{:model-relation? true :from {:id X}} {:model-relation? true :to {:id X}}]'` |
| Outgoing relations of a node | `-s '{:model-relation? true :from {:id X}}'` |
| Outgoing relations to other systems | `-S '{:model-relation? true :from {:id X} :to {:el :system}}'` |
| Views in a namespace | `--select-view-references '{:namespace-prefix "NS"}'` |

If a query returns too little, list the node's relations with the "All relations" row (the `:el` of each result is its type) and query the type that actually connects the elements. Container instances, for example, attach to nodes by `:deployed-to`, not `:contained-in`.

## Hierarchy (`:contained-in`)

The loaded model is a transformation of the input EDN: nested `:ct` children are dropped on read and become `:contained-in` relations (child → parent), so `:ct` is never visible in results and the model looks the same whether the source nests or uses flat relations. (A *view's* `:ct` is its content and stays.) `:contained-in` relations are regular relations and appear in every unfiltered relation query; add `:!el :contained-in` for runtime interactions only.

| Question | Query |
|---|---|
| Direct children of X (relations) | `-S '{:el :contained-in :to {:id X}}'` |
| Children of X, transitively (the nodes) | `-S '{:start {:id X} :refers {:el :contained-in}}'` |
| Parent of X | `-S '{:el :contained-in :from {:id X}}'` |
| All ancestors of X | `-S '{:start {:id X} :referred {:el :contained-in}}'` |
| Runtime relations of X without containment | `-S '{:model-relation? true :!el :contained-in :from {:id X}}'` |

## Transitive queries

A map with `:start` follows relations transitively:

- `:refers {:el :request}` walks backwards: the callers, i.e. what is affected if the start node changes. `-S '{:start {:id X} :refers {:el :request}}'`
- `:referred {:el :request}` walks forwards: the dependencies, i.e. what the start node needs. `-S '{:start {:id X} :referred {:el :request}}'`
- `:elements {...}` restricts which visited nodes are returned.

They work only at the top level of the criteria, not inside `:from`/`:to` (`unknown criterium start`). To combine, run the transitive query first and feed the resulting ids into a second query as an OR vector of `:id` criteria.

## Traceability and model health

Discover which of these relation types the model uses and substitute accordingly. Trace relations point from the concrete element to its definition: `:instance-of` from a `:container-instance` to its `:container`, `:implements` from a decision or component to what it realizes.

| Question | Query |
|---|---|
| What implements / does X implement | `-s '{:el :implements :to {:id X}}'` / `-s '{:el :implements :from {:id X}}'` |
| Definition of instance X / instances of definition X | `-s '{:el :instance-of :from {:id X}}'` / `-s '{:el :instance-of :to {:id X}}'` |
| Where is container X deployed | instances as above, then `-s '{:el :deployed-to :from {:id <instance id>}}'` |
| Goals, constraints, risks attached to a system | `-s '{:els #{:goal-for :non-goal-for :constraint-for :risk-for} :to {:id X}}'` |
| Proposed or deprecated elements | `-S '{:maturity :proposed}'`, `-S '{:maturity :deprecated}'` |
| Relations pointing at a deprecated element | `-s '{:model-relation? true :to {:maturity :deprecated}}'` |

**What is *not* linked** (goals without use cases, use cases without a realizing component, elements without relations) can't be a single criterion. Use two queries: list the candidates (`-S '{:el :use-case}'`), list the linking relations (`-s '{:el :uses}'`, or `:implements`, `:instance-of`, ...) and collect the ids at the relevant end; candidates missing from the second list are unlinked. This is a *candidate list for review*, not proof: the link may use another relation type, or the project may not model it. State which relation types you checked, and don't call anything orphaned or unused on this basis alone.

## Answering

State the answer first, then the query that produced it, plus the model revision (commit) when it will be used as evidence. Give element ids and, for dependencies, relation type, direction and whether the result is direct or transitive. An empty result means "nothing found with this criterion", not "does not exist": say what you checked. Mention dependence on modelling choices or model warnings.
