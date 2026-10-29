# Assessment: `node.md.cmb`

**Date:** October 29, 2026  
**Subject:** `templates/docs/node.md.cmb`

## Verdict

The template is **not complete with respect to the current model elements**, and it is **inconsistent with the data model in several executable queries**. It works as a selective node summary, rendering common descriptive metadata and a useful subset of containment, relation, and view information. It should not be treated as a complete representation of a model node or all relations attached to it.

The generation config applies it to each `:model-node?` except `:field` and `:method`; those two are instead emitted inside their parent node's documentation. See [the node documentation generation entry](templates/gencfg.edn#L53-L56). The runtime model inventory is defined by [the node and relation type sets](src/org/soulspace/overarch/domain/element.clj#L176-L201), not just by the groups currently listed in the template.

## Coverage

### Node data and element families

The common header renders the node name, element type, and description, with conditional sections for technology, tags, documentation, and `:doc/links` ([template metadata sections](templates/docs/node.md.cmb#L50-L84)). These are useful but not all declared model-element attributes: the model-element spec also permits `:subtype`, `:maturity`, `:external`, and `:collapsed` ([model-element spec](src/org/soulspace/overarch/domain/spec.clj#L51-L59)), none of which is rendered generally.

Containment documentation covers some families, including concepts, organizations, architecture nodes, deployment nodes, and state-machine states ([containment sections](templates/docs/node.md.cmb#L270-L415)). There is no equivalent general coverage for many valid node types, including permissions, container instances, process/domain elements such as constraints and regulations, and code elements such as enums, functions, namespaces, protocols, schemas, and parameters. Fields and methods receive special handling, but the code section itself still marks functions and enums as future work ([code section](templates/docs/node.md.cmb#L670-L719)). Some omitted types can appear in selected relation sections, but that is not a complete fallback for all node types or their attributes.

### Relations

The template handles selected relation types explicitly: for example `:is-a`, `:has`, `:uses`, `:extends`, deployment relations, and several technical architecture relations. Its “Other Relations” section only collects `:rel` relations ([relation sections](templates/docs/node.md.cmb#L428-L454)); the initially computed general referring/referred relation collections are not used as a catch-all ([template bindings](templates/docs/node.md.cmb#L29-L46)). As a result, relation families in the model such as `:response`, `:step`, `:generalizes`, `:part-of`, `:triggers`, `:collaborates-with`, `:transition`, and most code-model relations have no general rendering path. The current model inventories these across the architecture, code, concept, domain, state-machine, organization, use-case, and process models ([relation type definitions](src/org/soulspace/overarch/domain/element.clj#L28-L46), [additional relation type definitions](src/org/soulspace/overarch/domain/element.clj#L55-L84), [use-case and organization relations](src/org/soulspace/overarch/domain/element.clj#L110-L131), [process relations](src/org/soulspace/overarch/domain/element.clj#L148-L158)).

## Data-Model Inconsistencies

These are not merely absent headings: the following filters use element keywords that do not match the runtime model types, so valid model data will not satisfy them:

| Template query | Runtime model type | Effect |
|---|---|---|
| `:systems` ([two locations](templates/docs/node.md.cmb#L215-L218), [containment location](templates/docs/node.md.cmb#L316-L319)) | `:system` ([architecture node types](src/org/soulspace/overarch/domain/element.clj#L35-L46)) | Responsibility and child-system lists remain empty for valid `:system` nodes. |
| `:includes` ([use-case sections](templates/docs/node.md.cmb#L475-L501)) | `:include` ([use-case relation types](src/org/soulspace/overarch/domain/element.clj#L110-L120)) | Include-related use cases are not found. |
| `:input-for`, `:output-for` ([process sections](templates/docs/node.md.cmb#L510-L530)) | `:input-of`, `:output-of` ([process relation types](src/org/soulspace/overarch/domain/element.clj#L154-L158)) | Input/output artifact and process sections are not found. |

For example, the model uses singular `:include`, while the template queries plural `:includes`:

```clojure
;; Template
(m/referred-nodes model e {:el :includes})

;; Runtime model relation type
:include
```

There is also a direction mismatch for responsibility. The template binds `responsible-for` from `m/referred-nodes` and uses it for “Responsibility for …” sections ([binding and sections](templates/docs/node.md.cmb#L37-L38), [responsibility output](templates/docs/node.md.cmb#L208-L246)). The model API defines `responsible-for` as `m/referring-nodes` and `responsibility-of` as `m/referred-nodes` ([responsibility navigation API](src/org/soulspace/overarch/adapter/template/model_api.clj#L558-L565)). Consequently, those “for” sections inspect the opposite endpoint from the API's meaning.

Two further output defects are visible in the template:

- The “Including Use Cases” section is emitted twice with the same query ([first occurrence](templates/docs/node.md.cmb#L475-L477), [duplicate occurrence](templates/docs/node.md.cmb#L487-L489)). Both currently use the invalid `:includes` keyword.
- The instance sections pass `:version` and `:type` as the table type for instance nodes ([instance sections](templates/docs/node.md.cmb#L637-L645)). The table helper uses that argument as the displayed type heading rather than deriving each row's actual element type ([table helper](templates/lib/markdown.clj#L176-L183)); this can mislabel instances.

## Conclusion and Recommendations

The template is not complete, and it has data-model inconsistencies that cause valid data to disappear or be shown under an incorrect direction/type. The most important fixes are:

1. Align every queried `:el` keyword with the runtime type sets and verify relation endpoint direction against `model_api.clj`.
2. Add a generic relation fallback for all referring and referred relations not already rendered by specialized sections; retain the technical relation tables where their extra technology column is useful.
3. Decide and document the intended scope for omitted node families and optional attributes. If completeness is intended, render those families and fields; otherwise describe this as selective documentation.
4. Add focused template-render tests for the mismatched keywords, responsibility direction, duplicate section, and instance type labels.

**Overall:** useful but selective; **not complete** and **not fully consistent with the data model**.