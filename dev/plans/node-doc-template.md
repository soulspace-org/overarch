# Plan: Complete the Node Documentation Template

## Goal

Make `templates/docs/node.md.cmb` consistent with the runtime model and complete for node documentation, while retaining the current generation boundary:

- Generate one page for every type in `el/model-node-types` except `:field`, `:method`, and `:function`.
- Document those three excluded types under their containing node; they do not get standalone pages.
- Include every model relation attached to a generated node, with correct endpoint direction and useful relation metadata.

The generation selector already excludes exactly these three types, but its comment is outdated ([generation entry](templates/gencfg.edn#L53-L56)). Keep the selection and `:per-element true`; update its comment and test the boundary.

## Current Findings

- Runtime node and relation inventories are defined by `model-node-types` and `model-relation-types` ([element inventories](src/org/soulspace/overarch/domain/element.clj#L176-L201)). These are the coverage contract.
- Common node attributes are defined in the model-element spec ([spec](src/org/soulspace/overarch/domain/spec.clj#L51-L59)). The template renders several of them, but omits `:id`, `:subtype`, `:maturity`, `:external`, and `:collapsed` as explicit documentation fields.
- Code model examples use additional attributes such as `:type`, `:scope`, and `:abstract` ([code model example](examples/code/model.edn#L18-L72)). Fields and methods are handled specially, but functions are currently absent from that parent-level rendering.
- Relation type lookups currently mismatch the model: `:systems` vs `:system`, `:includes` vs `:include`, and `:input-for`/`:output-for` vs `:input-of`/`:output-of` ([affected template sections](templates/docs/node.md.cmb#L208-L246), [containment sections](templates/docs/node.md.cmb#L316-L330), [use-case and process sections](templates/docs/node.md.cmb#L463-L530)).
- The responsibility lookup direction is reversed: the template binds `responsible-for` from `m/referred-nodes`, whereas the API defines it using `m/referring-nodes`; responsibility-of uses the reverse ([template bindings](templates/docs/node.md.cmb#L37-L38), [navigation API](src/org/soulspace/overarch/adapter/template/model_api.clj#L558-L565)).
- The template computes general referring/referred relation collections, but does not use them to cover all relation types ([bindings](templates/docs/node.md.cmb#L29-L46)). Its “Other Relations” output is limited to `:rel` ([relation section](templates/docs/node.md.cmb#L428-L454)).
- There are additional output defects: duplicate “Including Use Cases” sections ([duplicate sections](templates/docs/node.md.cmb#L475-L489)), hard-coded or invalid node type labels, and method parameters looked up from the containing node instead of the method ([code sections](templates/docs/node.md.cmb#L637-L719)).

## Implementation Plan

### 1. Establish inventory-driven acceptance tests

Add focused tests for the node-documentation template and its helpers, following the existing Comb test style ([Comb test](test/org/soulspace/overarch/adapter/template/comb_test.clj#L1-L8)). Build a small indexed model fixture containing representative nodes and relations from each model family, rather than relying only on string-level assertions.

Assert the generation boundary against `el/model-node-types`: each type except `#{:field :method :function}` is selected for per-element generation, while those three are rendered by their parent. Also assert complete coverage against `el/model-relation-types` for relations attached to a node. These checks should fail if a new model type is added without a corresponding documentation path.

Use asymmetric relation fixtures (distinct `:from` and `:to` nodes) to make endpoint direction observable. Include at least one relation from each family and relations that are currently omitted, such as `:response`, `:step`, `:generalizes`, `:part-of`, `:triggers`, `:transition`, `:collaborates-with`, and code-model relations. Assert relation IDs occur once and that the emitted `From` and `To` values match the model.

### 2. Render all declared node attributes

Update the common node header/details to document all applicable fields from the model-element spec: `:id`, `:el`, `:name`, `:desc`, `:doc`, `:subtype`, `:maturity`, `:external`, `:tech`, `:tags`, and `:collapsed`. `:ct` should be represented through rendered containment children, not as a raw EDN value. Retain the existing `:doc/links` section as supported documentation metadata.

For code-specific and other extension attributes, inspect repository examples before choosing the output contract. At minimum preserve and render known useful code attributes including `:type`, `:scope`, and `:abstract`. Do not dump arbitrary map entries without a decision: namespaced extension keys may contain implementation-specific data or URLs, so either document a deliberate allowlist or explicitly state that arbitrary extensions are outside the completeness guarantee.

### 3. Cover every node family and containment child

Drive the coverage audit from `el/model-node-types` and the current `:contained-in` children. Keep readable family headings where useful, but ensure each possible child type has exactly one rendering path. Add missing groups for permission, domain, process/business, deployment, and code families, including the node types not currently grouped by the template.

Use a type-aware child-table helper for mixed collections, deriving the display type from each child's actual `:el`. For homogeneous tables, retain the existing `md/node-description-table` API. In particular, do not pass invented labels such as `:subject`, `:type`, or `:version` when the rows have another actual element type ([table helper](templates/lib/markdown.clj#L170-L183)).

For `:field`, `:method`, and `:function`, render them under their parent with their own ID, type/signature, description, and applicable attributes. Render method/function parameters from the actual method/function node's children, not from the containing class/interface. This preserves the requested no-standalone-page rule without omitting those nodes' information.

### 4. Correct relation queries and endpoint labels

Replace the invalid query keywords with the runtime types: `:systems` becomes `:system`; `:includes` becomes `:include`; `:input-for` becomes `:input-of`; and `:output-for` becomes `:output-of`. Confirm every relation query against the relation type sets in [element.clj](src/org/soulspace/overarch/domain/element.clj#L28-L46), [element.clj](src/org/soulspace/overarch/domain/element.clj#L55-L84), [element.clj](src/org/soulspace/overarch/domain/element.clj#L110-L158), and the actual `:from`/`:to` endpoint convention.

Correct responsibility direction so “responsible for” selects the outgoing endpoint and “responsibility of” selects the incoming endpoint, according to [the model API](src/org/soulspace/overarch/adapter/template/model_api.clj#L558-L565). Apply the same explicit endpoint check to role, use-case, artifact/process, implementation, inheritance, instance, version, and deployment sections.

### 5. Make relation coverage authoritative and non-duplicating

Add an authoritative relation table for all relations incident to the current node, collecting both incoming and outgoing relations and deduplicating by relation ID. Include relation type and `From`/`To` endpoint columns so direction is unambiguous. Include common relation metadata from the model-relation spec: ID, name, description, documentation, maturity, technology, index, direction, constraint, and tags where present.

Prefer one complete relation table over a growing set of relation-specific sections. Keep parent/child hierarchy sections if they improve navigation, but avoid duplicating full relation details there. If existing specialized relation summaries are retained, define a precise partition: every relation ID must be rendered by exactly one detailed relation section. Test this property against the complete relation inventory.

Implement the table in `templates/lib/markdown.clj` as an additive helper or a clearly generalized helper; avoid silently changing the existing helper output used by other templates. The current generic relation helper does not display relation type or all model-relation attributes ([Markdown relation helpers](templates/lib/markdown.clj#L213-L240)).

### 6. Remove duplicate and misleading output

Remove the duplicate “Including Use Cases” section as part of replacing or repairing relation summaries. Fix instance and other heterogeneous node tables to derive type labels from each row's `:el`. Fix method/function parameter lookup to use the member node. Remove now-unused bindings and the template-local `type->name` definition if the final implementation no longer needs them; keep unrelated cleanup out of scope.

### 7. Validate template generation and generated output

Run the focused template/helper tests, then `lein test`. Smoke-test the actual generation config with a model containing all covered families, writing into a temporary output directory. Verify:

- one page is produced for every selected node type and none for `:field`, `:method`, or `:function`;
- fields, methods, functions, and their parameters appear under their parent;
- declared model attributes are represented;
- each incident relation appears exactly once with correct type and endpoints;
- the previously invalid relation types now render;
- no duplicated sections or incorrect type labels remain;
- existing Markdown generation for views and other template consumers remains unchanged.

## Key Decisions and Risks

- **Source of truth:** use runtime inventories in `domain/element.clj`, not a separately maintained hard-coded list in the template. The tests should compare against these inventories directly.
- **Relation layout:** a single complete relation table is the lowest-risk way to guarantee no relation type is missed. Retaining specialized sections is possible but requires exact disjointness and more maintenance.
- **Extension attributes:** the common model spec is finite, but examples contain arbitrary unqualified and namespaced keys. Define the completeness guarantee explicitly; include known semantic code attributes without rendering arbitrary metadata indiscriminately.
- **Shared helpers:** `templates/lib/markdown.clj` is used by multiple templates. Add a helper or preserve old behavior with tests to prevent regressions outside node docs.
- **Output churn:** generated Markdown will change substantially if all node metadata and relations become visible. Keep generated-output updates separate from template implementation unless repository policy expects checked-in generated files to be refreshed.

## Relevant References

- [Assessment of current issues](dev/assessments/node-template-assessment.md)
- [Node template](templates/docs/node.md.cmb)
- [Generation configuration](templates/gencfg.edn)
- [Runtime node and relation inventories](src/org/soulspace/overarch/domain/element.clj)
- [Model element/relation specs](src/org/soulspace/overarch/domain/spec.clj)
- [Model navigation API](src/org/soulspace/overarch/adapter/template/model_api.clj)
- [Markdown helpers](templates/lib/markdown.clj)
- [Code model example](examples/code/model.edn)
- [Comb tests](test/org/soulspace/overarch/adapter/template/comb_test.clj)