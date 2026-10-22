# Plan: C4 view template (`templates/views/c4-view.puml.cmb`)

## Goal

Replace the Clojure PlantUML C4 renderer
(`src/org/soulspace/overarch/adapter/render/plantuml/c4.clj`) with a template,
analogous to the UML view template (see [uml-view-template.md](uml-view-template.md)),
so users can customize the C4 diagram generation without changing Overarch code.

The template renders the views that the Clojure renderer handles as `:c4-view`
and `:deployment-view`:

- `:system-landscape-view`, `:context-view`, `:container-view`,
  `:component-view`, `:dynamic-view`
- `:deployment-view`, `:deployment-architecture-view`

## Current state

- No `templates/views/c4-view.puml.cmb` exists.
- `templates/lib/plantuml/c4.clj` is an empty namespace stub.
- `templates/lib/plantuml.clj` has `alias-name`, `short-name`, `indent`,
  `layouts`, `linetypes`, `render-layout`, `render-title`, `render-skinparams`
  (from the UML work). `lib.text` has `single-line`.
- The template API (`m`, `v`) is missing some functions the C4 renderer needs
  (see below).
- The sprite mappings (technology to sprite, loaded from
  `resources/plantuml/*.edn`) are only available on the Clojure side
  (`adapter/render/plantuml.clj`).

## What the Clojure renderer does

`c4.clj` (472 lines) consists of:

1. **Mappings:** `c4-element->method`, `c4-view-type->import`,
   `c4-subtype->suffix`, `c4-layouts`, `c4-directions`, `c4-style->method`,
   `c4-line-style->method`.
2. **`render-c4-element` multimethod** (dispatch on `:el`, element hierarchy)
   with methods for `:boundary`, `:person`, `:system`,
   `:technical-architecture-model-node`, `:artifact`, `:deployable-node`,
   `:node`, `:link`, `:deployed-to`, `:architecture-model-relation`, `:step`,
   `:model-relation` and the `:model-element` fallback.
3. **Imports:** `render-c4-imports` (C4 library, local or remote) and, from
   `plantuml.clj`, `render-sprite-imports` (sprite libraries and the sprites
   used in the view).
4. **Styles and layout:** `render-c4-style` (multimethod for `:element` and
   `:rel` styles), `render-c4-layout` (styles, sketch, layout, linetype,
   node/rank separation), `render-c4-legend`.
5. **Two entry points:** `:c4-view` and `:deployment-view`.
   The deployment view only renders deployment nodes as roots (deployed
   elements are rendered as children of the nodes), plus the person, system
   and container elements of the view.

## Gaps in the template API

| Needed in template | Clojure source | Plan |
| --- | --- | --- |
| `rendered-elements` of a view | `view/rendered-elements` | add `v/rendered-elements` (needed by `:node` to find deployed elements) |
| `hierarchical-view?` | `el/hierarchical-view?` | add `v/hierarchical-view?` |
| tech to sprite lookup | `puml/tech->sprite` | add `v/tech-sprite [tech]` |
| sprites used in a view | `puml/sprites-for-view` | add `v/sprites-for-view [model view]` |
| sprite path | `puml/sprite-path` | add `v/sprite-path [sprite]` |
| `referring-nodes` with criteria | `model/referring-nodes` | exists (`m/referring-nodes`) |
| `collapsed?` | `el/collapsed?` | exists (`m/collapsed?`) |
| `single-line` | `fns/single-line` | exists in `lib.text` (`t/single-line`) |

The sprite *mapping data* stays in Clojure (loaded from resources), the
*rendering* of the imports moves to the template lib. The sprite library
definitions (`sprite-libraries`, a pure data map) move to `lib.plantuml`, as
they are formatting configuration users may want to change.

## Design

### `templates/lib/plantuml.clj` (common PlantUML functions)

- Add `sprite-libraries` (data map moved from `plantuml.clj`).
- Add `local-import`, `remote-import`, `render-sprite-import`,
  `render-spritelib-import` and `render-sprite-imports [model view]`.
  These use `v/sprites-for-view`, `v/sprite-path` and the plantuml spec of
  the view.
- Generalize `render-layout`: add an arity taking an options map with the
  layout map and the sketch directive, so the UML (`skinparam handwritten true`,
  `top to bottom direction`) and C4 (`LAYOUT_AS_SKETCH()`, `LAYOUT_TOP_DOWN()`)
  variants share the linetype and `nodesep`/`ranksep` handling.

### `templates/lib/plantuml/c4.clj` (port of `c4.clj`)

- Requires `clojure.string`, `lib.plantuml`, `lib.text`, and the full
  `model-api` and `view-api` namespaces (as in `lib.plantuml.uml`).
- Maps: `element->method`, `view-type->import`, `subtype->suffix`, `layouts`,
  `directions`, `style->method`, `line-style->method`.
- Helpers:
  - `render-link`, `sprite` (via `v/tech-sprite`), `render-imports`.
  - `render-arg`-style helpers building the optional named arguments
    (`$descr`, `$techn`/`$type`, `$link`, `$sprite`, `$tags`).
- `render-c4-element` multimethod with `:hierarchy #'m/element-hierarchy`,
  returning a vector of lines, with the same methods as the Clojure version.
- The node methods (`:system`, `:technical-architecture-model-node`,
  `:artifact`, `:deployable-node`, `:node`) differ only in the argument list
  (`$type` vs. `$techn`, `_Ext` suffix, sprite, tags). Extract one
  `render-node-call [e opts]` helper.
- The relation methods (`:link`, `:architecture-model-relation`, `:step`,
  `:model-relation`) differ only in direction defaults, index and the
  constraint case (`Lay_*`). Extract one `render-rel-call` helper.
- `render-c4-style` as a multimethod on `(:el style)` (`:element`, `:rel`).
  The local `c4-styles-hierarchy` derives from the type keys and is not
  needed for the two dispatch values, so the multimethod is a plain `defmulti`.
- `render-c4-layout`, `render-c4-legend`.
- Entry points `render-c4-view [model view]` for the normal views and the
  deployment variant, selected by `(:el view)` via a small helper that picks
  the root elements (as in the two Clojure methods). Returns a vector of lines.

### `templates/views/c4-view.puml.cmb`

Thin wrapper, like `uml-view.puml.cmb`:

- header comment (purpose, config, extension point `render-c4-element`),
- `(require '[lib.plantuml.c4 :as c4])`,
- `<%= (str/join "\n" (c4/render-c4-view model e)) %>` and the footer comment.

### `templates/gencfg.edn`

Add an entry (same format as the UML entry):

```clojure
; rendered as plantuml C4
{:view-selection {:els #{:system-landscape-view :context-view :container-view
                         :component-view :dynamic-view :deployment-view
                         :deployment-architecture-view}}
 :template "views/c4-view.puml.cmb"
 :per-element true
 :id-as-name true
 :extension "puml"}
```

### Removal of the Clojure renderer

After the output has been verified as equivalent:

- Delete `render/plantuml/c4.clj`, its test (`c4_test.clj`) and the require
  in `cli.clj`.
- In `render/plantuml.clj`, nothing renders through PlantUML any more.
  Remove `render-plantuml-view`, `view-hierarchy`, `renderer`,
  `plantuml-views`, `plantuml-view?`, the `rndr/render-view :plantuml` and
  `rndr/render-file :plantuml` methods, `linetypes`, `render-title`,
  `render-skinparam(s)` and the import/sprite *rendering* functions moved to
  the template lib.
- Keep the sprite data and lookups (`sprite-resources`, `tech->sprite`,
  `sprite-path`, `sorted-sprite-mappings`, `sprites-for-view`,
  `collect-*`), which back the new `v/` functions and the CLI option
  `--plantuml-list-sprites`.
- Keep the `-r`/`--render-format` flag for compatibility, but deprecate it
  (see "Deprecation of the `-r` flag").
- Update the docs (`doc/topics`, `AGENTS.md`/instructions mentioning the
  renderers) and the `Changelog.md`.

## Deprecation of the `-r` flag

After the UML and C4 templates, no view is rendered by the built-in PlantUML
renderer any more, so `-r`/`--render-format` has no effect. The flag stays
for compatibility with existing scripts, but is deprecated:

- In `cli.clj`, where the render format is handled
  (`(when (:render-format options) ...)`), print a warning to stderr when the
  flag is given, e.g. `Warning: the -r/--render-format option is deprecated
  and has no effect. All render artifacts are generated via the templates,
  use the generation config (-g) instead.`
- Do not call `rndr/render` for `:plantuml` any more (nothing to render).
  Check that other values (`:all`) don't break, and that the related options
  (`-R`, `--[no-]render-format-subdirs`) are still accepted.
- Change the help text of `-r` to `Deprecated, has no effect. Use templates (-g) to generate artifacts`.
- Remove the `render/plantuml` render-view/render-file methods (see above),
  keep the option definitions.
- Add a test for the warning and a note in the `Changelog.md`.
- Update the `comment` blocks in `cli.clj` that call `-main` with
  `--render-format` to use `--generation-config`.

## Fixes of oddities in the Clojure version

The Clojure code contains behaviour that looks like an accident. These are
fixed as part of this change. To keep the equivalence check meaningful, the
port is first done 1:1 (the diff must be empty), then the fixes are applied
one by one and the resulting output differences are reviewed and documented.

| # | Oddity in `c4.clj` | Fix |
| --- | --- | --- |
| 1 | `:node` with children renders `$tag=` instead of `$tags=` (the C4 library expects `$tags`), so the style tag is ignored | render `$tags=` (falls out of the shared `render-node-call` helper) |
| 2 | `:node` without content renders `(:desc e)` unprocessed, multi-line descriptions break the call | use `single-line`, as in all other methods |
| 3 | `:boundary` always renders `{ ... }`: `if-let` on a lazy sequence is truthy even when empty, so the plain call branch is dead code | use `seq`, so an empty boundary is rendered as a plain call without braces |
| 4 | the `:model-element` fallback prints to stdout, which the template engine captures into the diagram | emit a PlantUML comment line, as in the UML template |
| 5 | `render-c4-style` binds an unused `el` in both methods and uses the unused `c4-styles-hierarchy` | plain `defmulti` on `(:el style)`, no unused bindings |
| 6 | `:node` collapsed/empty branch renders `$type` from `:tech`, the open branch too, while the other nodes use `$techn` | decide per C4 call signature: `Node(alias, label, ?type, ?descr, ...)` takes `$type`, containers/components take `$techn`; keep `$type` for `:node`, `:person` and `:system` as documented by C4-PlantUML, and cover it in the helper by an explicit option instead of copy-pasted strings |
| 7 | `:step`, `:link`, `:model-relation` and `:architecture-model-relation` repeat the relation logic with small differences (index only on dynamic views, direction defaults only on `:architecture-model-relation`, sprite scale) | one `render-rel-call` helper, so all relations get the same treatment (reverse, index, direction, tags) |

Item 6 needs a check against the C4-PlantUML macro signatures before the change.
If a fix changes the diagram semantics, it is listed with before/after in
the implementation notes of this plan.

## SCI risks to check first

- Everything the UML port needed has been shown to work (multimethod with
  `:hierarchy #'m/element-hierarchy`, `lib.*` namespaces with full requires).
- `case` with keyword branches and default (used for relation direction
  defaults) and `str/join`, `distinct`, `flatten` in SCI: expected to work.
- Symbols returned by `alias-name` inside `str`: already used by the UML lib.
- The sprite maps returned by `v/tech-sprite` are plain Clojure maps, so they
  are unproblematic in SCI.
- `sci/copy-ns` is a macro. The new `v/` functions are only visible with a
  build that includes the API change, so test from source with the stale
  compiled classes removed (the prebuilt `tools/overarch.jar` does not know them).

## Verification

1. **Baseline** (before removing anything): generate with the Clojure renderer,
   `java -jar tools/overarch.jar --no-render-format-subdirs -R <dir> -r plantuml`
   (no `-w`, that starts the watch mode).
2. **Template output** from source (new API functions), generated into a
   separate directory with `-G <dir> -B <backup-dir> -g templates/gencfg.edn -T templates`.
3. Compare the `.puml` files ignoring blank lines and the footer comment.
4. Run both for several models, so all view types and features are covered:
   - `models/overarch` (container, component, context and deployment views),
   - `examples/banking`, `examples/storytelling`,
   - `examples/test/collapsed`, `dynamic-view`, `expand-external`, `includes`.
5. Check the features explicitly: external elements (`_Ext`), subtypes
   (`Db`, `Queue`), sprites and technologies (sprite imports), links, styles
   and themes, legend, sketch, layout and linetype, `:remote-imports`,
   relation directions, `:reverse`, `:constraint` links (`Lay_*`), dynamic
   view indexes, collapsed nodes, nodes with deployed containers/artifacts.
6. Run the test suite after removing the Clojure renderer.
7. Optionally render the generated `.puml` files with `tools/plantuml.jar` to
   confirm that they are valid.

## Implementation notes

All steps were implemented and verified.

- **API:** `v/rendered-elements`, `v/hierarchical-view?`, `v/tech-sprite`,
  `v/sprites-for-view`, `v/sprite-path` added to `view_api.clj`.
- **Libs:** `lib.plantuml` has the sprite libraries, imports and the
  generalized `render-layout`; `lib.plantuml.c4` has the port with the
  `render-call`, `render-technical-node` and `render-rel` helpers.
- **Equivalence:** the 1:1 port produced identical diagrams (ignoring blank
  lines and the footer) for 52 diagrams of `models`, `examples/banking`,
  `examples/storytelling` and `examples/test`, and for a test model
  with queue/database subtypes, `Lay_*` constraint links, links with
  directions, sketch, layout, linetype, node/rank separation, relation
  sprites and boundaries.
- **Fixes applied** (output differences against the Clojure renderer, all
  reviewed): 1 (`$tags` on nodes with children), 3 (empty boundary without
  braces), 4, 5, 7 (shared relation helper, no differences in the test data),
  and 2 (no visible difference in the test data).
- **Item 6 (`$type` vs `$techn`):** checked against the C4-PlantUML macro
  signatures. `Person`, `System` and `Node` take `$type`, `Container` and
  `Component` take `$techn`, so the Clojure choice was correct. No change.
- **Additional fix 8:** the remote sprite library imports were broken in the
  Clojure renderer (`:remote-imports true` failed with a NullPointerException,
  because the library key was used as the library map). Fixed in
  `render-spritelib-import`. Also no `!define` without prefix (`logos`) and a
  space between the prefix and the URL. The remote URLs for AWS/Azure are
  still empty in the library definitions (existing FIXME).
- **Additional fix 9:** styles of an unsupported type (e.g. `:boundary`, which
  has an `AddBoundaryTag` mapping but no render method) produce a comment
  line instead of an exception.
- **Validation:** the generated container, component, context, deployment and
  structure diagrams were rendered with `tools/plantuml.jar` without syntax errors.
- **Removed:** `render/plantuml/c4.clj` and its test; `plantuml.clj` now
  only holds the sprite data and lookups.
- **`-r` deprecation:** `cli.clj` prints the warning to stderr and doesn't
  render any more. A test covers the warning. Scripts, README and docs
  (`doc/usage.md`, `doc/topics/commandline.md`, `agents/modelling-reference.md`,
  the modelling skill, `model.sh`, `publish.sh`) use `-g` instead of `-r`.
- **View registration:** `application/render.clj` used to register the view
  type multimethods as a side effect (it required all `domain.views.*`
  namespaces). This moved to the dedicated namespace
  `org.soulspace.overarch.domain.views`, which is required by `cli.clj` and
  by the template `view-api` (so templates also work from other entry points).
  The render multimethods in `application/render.clj` were unused, so the
  namespace and its test were removed, as well as the component and relations
  for it in `models/overarch/architecture/model.edn`.

## Order of work

1. Add `v/rendered-elements`, `v/hierarchical-view?`, `v/tech-sprite`,
   `v/sprites-for-view`, `v/sprite-path` to `view_api.clj`.
2. Extend `lib/plantuml.clj` (sprite libraries and imports, generalized
   `render-layout`).
3. Port maps, helpers and the multimethods to `lib/plantuml/c4.clj`.
4. Write `c4-view.puml.cmb`, add the `gencfg.edn` entry.
5. Generate, diff against the baseline, fix differences until the 1:1 port
   is equivalent.
6. Apply the fixes from the table above one at a time. After each fix,
   regenerate and review the diff against the baseline; every remaining
   difference must be explained by a fix. Validate item 6 against the
   C4-PlantUML macro signatures and render the changed diagrams with
   `tools/plantuml.jar`.
7. Remove the Clojure renderer and the dead code in `plantuml.clj`.
8. Deprecate the `-r` flag in `cli.clj` (warning, help text, test).
9. Run the tests, update docs and changelog (mention the fixes and the
   deprecation there).

## Open questions

None. Decided: the `-r` flag stays as a deprecated no-op with a warning
(see "Deprecation of the `-r` flag").
