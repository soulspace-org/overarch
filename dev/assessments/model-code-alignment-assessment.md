# Overarch Model / Codebase Alignment Assessment

Date: 2026-10-14

Scope: `models/overarch` compared with `src/`, `resources/`, `project.clj` and the CLI options.
Method: reading the model and code, element/view type comparison, and `tools/overarch.jar --model-info`.
Not done: model validation run and diagram review.

## Summary

The container and component level of the architecture model is only partly in sync with the code,
and the data model is missing about 16 element and view types that `element.clj` defines.

## Misalignments

1. **Component `application.reader` has no matching namespace.**
   `architecture/model.edn` models it as holding "reader multimethods". `src/.../application/` only
   contains `export`, `model_repository`, `render` and `template`. The reader port is
   `adapter.reader.model-reader`, which is already modelled separately. Remove or rename the component.
2. **`adapter.reader.model-reader` has no relations.** None to `application.model-repository` or
   `domain.model`, although the code requires both. `file-input-model-reader` is not modelled at all.
3. **CLI wiring is incomplete.** The model only shows `cli` calling `model-repository`, `export`,
   `render` and `template`. There is no `cli` -> reader relation, although the CLI loads models.
4. **Diagram generator relations are partial.** The CLI does not call PlantUML or Graphviz itself,
   and the `--plantuml-*` CLI options have no relation in the model.
5. **The deployment model is thin** (`architecture/deployment.edn`). Graphviz is a
   `container-instance` without an `instance-of` relation, and PlantUML is missing.
6. **`diagram-generator` mentions Mermaid.** There is no Mermaid renderer in `src/`, so this is aspirational.

## Omissions: components in code but not in the model

- **Domain/application:** `domain.element`, `domain.spec` (plus `resources/specs.edn`), and the 18
  `domain.views.*` namespaces (one per view type).
- **Adapter layer:** `adapter.reader.input-model-reader`, `adapter.reader.file-input-model-reader`,
  `adapter.render.plantuml.c4`, `adapter.render.plantuml.uml`, `adapter.template.model-api`,
  `adapter.template.view-api`.
- **Util:** `util.plantuml-sprites`.
- **Dependencies/external systems:** Beholder (the `--watch` mode, which the requirement
  `watch-model-path` already states), SCI, zprint, charred (JSON), Tiara reader, and the `plantuml.jar`
  shipped in `tools/`.
- **Relations:** nearly all component relations for these namespaces are missing, e.g.
  `domain.model` -> `domain.element`, reader -> `spec`, render -> `view-api`.

## Omissions: data model versus code

`data-model/model.edn` has no class for these types, although `domain/element.clj` defines them:

- **Architecture:** `container-instance` (used by the deployment model itself), `system-boundary`,
  `container-boundary`, `boundary`.
- **Process/responsibility:** `non-goal` (used by the architecture model itself), `non-goal-for`,
  `risk`, `risk-for`, `stakeholder`, `stakeholder-of`, `step`, `responsibility-model-node`,
  `responsibility-model-relation`, `part-of`, `actor-node`.
- **Other:** `annotation`, `stereotype`, `deployable-node`.
- **Views:** `deployment-architecture-view`, `domain-view`, `process-view`. The corresponding
  namespaces exist in `domain/views/`.

## Smaller items

- No quality-attribute or risk content in `goals.edn`; only two non-goals.
- The use-case model has no use cases for watching the model path, model info, or listing/finding
  PlantUML sprites. All three are CLI options.
- The architecture views have no selection showing the domain-view namespaces or the readers.
  The component view's `:ct []` just uses a namespace selection.
- The Clean Architecture layering from `agents/architecture.md` is only described in the
  `implementation-architecture` decision, not modelled as boundaries.

## Recommended order of fixes

1. Fix or remove the `application.reader` component.
2. Add the missing components with their relations.
3. Add the missing data-model classes and views.
4. Extend the deployment model and the use-case model.
