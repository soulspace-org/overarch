# Overarch Project Assessment

**Date:** June 9, 2026  
**Version assessed:** 0.41.0 (snapshot 0.42.0 in development)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Project Purpose and Scope](#2-project-purpose-and-scope)
3. [Architecture Assessment](#3-architecture-assessment)
4. [Data Model Assessment](#4-data-model-assessment)
5. [Codebase Quality](#5-codebase-quality)
6. [Testing Assessment](#6-testing-assessment)
7. [Build and Tooling](#7-build-and-tooling)
8. [Template System Assessment](#8-template-system-assessment)
9. [Documentation Assessment](#9-documentation-assessment)
10. [Strengths](#10-strengths)
11. [Weaknesses and Risks](#11-weaknesses-and-risks)
12. [Development Roadmap Observations](#12-development-roadmap-observations)
13. [Overall Verdict](#13-overall-verdict)

---

## 1. Executive Summary

Overarch is a mature open-source Clojure tool for **data-driven, multi-perspective software architecture modelling**. It stores models as plain EDN (Extensible Data Notation) files, separates model data from visual representations, and generates diagrams (PlantUML C4/UML, Graphviz) and rich documentation (Markdown, arc42) from those models. The codebase follows clean architecture principles with a clear domain / application / adapter layering, extensive multimethod-based extensibility, and a powerful template generation system.

The project is well-structured and the core design decisions are sound. The main areas for improvement are test coverage depth, documentation completeness, and a few open roadmap items related to advanced querying and custom view rendering.

---

## 2. Project Purpose and Scope

### 2.1 Core Problem Solved

Traditional diagram tools embed models inside diagram syntax (PlantUML scripts, draw.io XML, Structurizr DSL). This creates:
- Model duplication across diagrams
- No queryability
- Tight coupling of data to rendering format

Overarch's key insight is: **the model is data, the view is a projection**. By storing all architecture knowledge as plain EDN sets and maps, the model becomes:
- Queryable with selection criteria
- Renderable to multiple formats from the same source
- Composable and mergeable across files
- Version-controllable with standard text diffing
- Extendable with arbitrary custom attributes

### 2.2 Supported Modelling Perspectives

| Model Type | Element Types | Example Use |
|---|---|---|
| Role Model | `:person`, `:permission` | Access control, ownership |
| Concept Model | `:concept` | Domain vocabulary, ontologies |
| Architecture Model | `:system`, `:container`, `:component`, `:enterprise-boundary` | C4 architecture diagrams |
| Code Model | `:class`, `:interface`, `:method`, `:field`, `:namespace`, `:package`, `:enum`, `:schema` | UML class diagrams |
| State Machine Model | `:state-machine`, `:state`, `:start-state`, `:end-state`, `:fork`, `:join`, `:choice` | Behaviour modelling |
| Deployment Model | `:node`, `:container`, `:artifact` | Infrastructure diagrams |
| Organization Model | `:organization`, `:org-unit` | Team structures |
| Use Case Model | `:use-case`, `:actor` | Functional scope |
| Domain Model | `:domain`, `:bounded-context` | DDD context mapping |
| Process Model | `:capability`, `:process`, `:artifact`, `:decision`, `:requirement` | Business process modelling |

### 2.3 Output Formats

| Category | Formats |
|---|---|
| Diagram rendering | PlantUML (C4 + UML), Graphviz dot |
| Documentation | Markdown (node docs, glossaries, view indexes) |
| arc42 templates | 12 architectural documentation sections |
| Data export | JSON, Structurizr |
| Custom generation | Any text format via Comb templates with SCI evaluation |

---

## 3. Architecture Assessment

### 3.1 Layered Architecture

The codebase strictly follows a **four-layer clean architecture**:

```
┌─────────────────────────────────────────────────────┐
│  adapter/ui/cli.clj          CLI entry point         │
├─────────────────────────────────────────────────────┤
│  adapter/reader/*            File system input       │
│  adapter/render/*            PlantUML / Graphviz /   │
│                              Markdown output         │
│  adapter/exports/*           JSON / Structurizr      │
│  adapter/template/*          Comb template engine    │
├─────────────────────────────────────────────────────┤
│  application/model_repository.clj   State management│
│  application/render.clj             Render dispatch  │
│  application/export.clj             Export dispatch  │
│  application/template.clj           Template dispatch│
├─────────────────────────────────────────────────────┤
│  domain/element.clj          Category definitions    │
│  domain/model.clj            Graph operations        │
│  domain/spec.clj             Spec validation         │
│  domain/view.clj             View configuration      │
│  domain/views/*.clj          18 view implementations │
├─────────────────────────────────────────────────────┤
│  util/io.clj, functions.clj, plantuml_sprites.clj   │
└─────────────────────────────────────────────────────┘
```

The domain layer has **zero dependencies** on adapter or application code. This allows:
- Domain logic to be tested in isolation
- Multiple rendering backends to be swapped without touching domain logic
- New view types to be added without any framework changes

### 3.2 Extensibility via Multimethods

Overarch uses Clojure multimethods as its primary extensibility mechanism. Key dispatch points:

```clojure
;; Format-level dispatch
(defmulti export-file (fn [model format options] format))
(defmulti render-file (fn [model view format options] format))

;; View-type dispatch (within a format)
(defmulti render-view  (fn [model view format options] (:el view)))
(defmulti render-model-element? (fn [model view e] (:el view)))
(defmulti element-to-render (fn [model view e] (:el view)))
```

This means new formats and new view types are added as standalone multimethod implementations without modifying existing code — a strong open/closed principle in practice.

### 3.3 Data Flow

```
EDN Model Files
      │
      ▼
file-input-model-reader      reads EDN files from paths
      │  resolves :ct children → :contained-in relations
      │  normalises :tech strings → ordered sets
      ▼
input-model-reader            validates elements (clojure.spec)
      │  builds problem list
      ▼
model-reader                  builds indices
      │  :id->element
      │  :id->parent-id
      │  :referred-id->relations
      │  :referrer-id->relations
      ▼
model-repository              stores model in application atom
      │
      ├──► render.clj ──► view-specific filtering ──► format renderer ──► files
      ├──► export.clj ──► format-specific serializer ──► files
      └──► template.clj ──► Comb/SCI evaluation ──► generated artifacts
```

The separation between the indexing step and the rendering step is architecturally clean: all graph traversal costs are paid once at load time, and rendering accesses pre-built indices.

### 3.4 Graph Model

The underlying model is a **labeled property graph**:
- Nodes = model elements (`:system`, `:concept`, `:state`, etc.) with `:id`, `:name`, `:desc`, and arbitrary custom properties
- Edges = relations (`:request`, `:is-a`, `:contained-in`, etc.) with `:from`, `:to`, and optional attributes
- Hierarchy is encoded as `:contained-in` relations (children declared with `:ct` in EDN are auto-converted on load)

Four indices give O(1) access to:
- Any element by ID
- Parent of any element
- All incoming relations to an element
- All outgoing relations from an element

The `traverse` function provides generic depth-first and breadth-first traversal with accumulator patterns, enabling transitive closure queries (e.g. "all components reachable from this system").

### 3.5 Model State Management

Application state is managed via a single Clojure `atom` (`model-repository`). The atom holds the full indexed model. On watch mode, file changes trigger a full reload and re-index — simple and correct, though for very large models an incremental update strategy could be beneficial.

---

## 4. Data Model Assessment

### 4.1 EDN Format

EDN is a well-chosen format for this use case. It is:
- Human-readable and human-writable (no DSL parser required)
- Natively supported by Clojure — no serialization layer needed
- Richly typed (keywords, sets, maps, vectors, tagged literals)
- Supported by excellent Clojure editor tooling (Calva, CIDER)

A representative model element:
```clojure
{:el :system
 :id :banking/internet-banking-system
 :name "Internet Banking System"
 :desc "Allows customers to view information about their bank accounts and make payments."
 :ct #{{:el :container
        :id :banking/web-application
        :name "Web Application"
        :tech "Clojure, ClojureScript"
        :desc "Delivers the static content and the internet banking single page application."}}}
```

The `:ct` set (children) is automatically expanded into `:contained-in` relations on load, keeping the authoring format concise while the runtime model is a flat, indexed graph.

### 4.2 Selection Criteria

Overarch implements a rich query language as data maps:

```clojure
;; Find all containers with Java technology
{:el :container :tech "Java"}

;; Find elements by specific IDs
{:ids #{:banking/web-application :banking/api-application}}

;; Find elements related to a specific element  
{:refers :banking/internet-banking-system}

;; Negate criteria
{:!el :person}

;; Combined criteria (implicit AND)
{:namespace "banking" :el :container}
```

This query-as-data pattern is idiomatic Clojure and enables both CLI queries and programmatic template-based selection.

### 4.3 Metamodel

Overarch bootstraps itself: the `models/overarch/` directory contains overarch model files describing overarch's own data model. The metamodel covers:
- All element types and their properties
- All relation types
- View types and their allowed elements
- Generation configuration structures

This self-describing quality is a valuable design property — the modelling tool is used to document itself.

### 4.4 View Specifications

The `resources/specs.edn` file defines 16 view types with explicit:
- Allowed node element types
- Allowed relation types
- Which elements render as boundaries vs. boxes
- Hierarchical vs. flat rendering
- Include/exclude rules (e.g. container-view excludes component children of external systems)

This declarative view specification keeps view logic data-driven and auditable.

---

## 5. Codebase Quality

### 5.1 Code Style

The code is consistently formatted with:
- Standard Clojure namespace organisation (`ns`, `:require`, `:use` avoided)
- Docstrings on public functions (mostly present)
- Descriptive function names following Clojure conventions (`->`-based data transformations, `?`-suffix predicates)
- Thread-last (`->>`) macros used appropriately for data pipelines

### 5.2 Functional Programming Patterns

The codebase makes good use of Clojure idioms:
- **Transducers** — used in relation filtering for efficiency (avoids intermediate collections)
- **Multimethods** — used for open extensibility (not `cond`/`case` chains)
- **Spec** — validation with problem accumulation rather than exceptions
- **Ordered sets** (`tiara.data/ordered-set`) — preserves insertion order for `:tech` values
- **`partial`/`comp`** — used to build resolver functions passed as parameters

### 5.3 Potential Code Quality Issues

**`cli.clj` size (500+ lines):** The CLI adapter is notably large. It handles model loading, all rendering, all exports, all template generation, watch mode, and output. This is the one area where more decomposition into sub-namespaces would improve readability and testability.

**`parent-new` dead code:** `domain/model.clj` contains a `parent-new` function with a `; TODO check correctness...` comment alongside the active `parent` function. This suggests in-progress work that hasn't been cleaned up:
```clojure
; TODO check correctness and performance and replace parent if ok
(defn parent-new ...)
```

**Atom-based state:** The single `model-repository` atom works well for a CLI tool. For a future library/server usage, this global mutable state would need to be revisited.

### 5.4 Error Handling

Validation errors are collected into a `:problems` list during model loading rather than throwing exceptions. This gives the user a complete list of issues at once. The `expound` library provides human-friendly spec error messages. This is a thoughtful UX choice.

---

## 6. Testing Assessment

### 6.1 Test Coverage Overview

The test suite mirrors the source structure with 21 test namespaces covering all four layers:

| Layer | Test Files | Coverage Style |
|---|---|---|
| domain | `element_test`, `model_test`, `spec_test`, `view_test` | Predicate tests, fixture-based model tests |
| application | `model_repository_test`, `render_test`, `export_test`, `template_test` | Compilation + output validation |
| adapter/reader | `input_model_reader_test`, `file_input_model_reader_test` | Input predicate tests |
| adapter/render | `plantuml_test` | Compilation smoke test |
| adapter/exports | `json_test`, `structurizr_test` | Compilation smoke test |
| adapter/template | `comb_test`, `markdown_api_test` | Template evaluation and link generation |
| util | `functions_test`, `io_test`, `plantuml_sprites_test` | Unit tests |
| ui | `cli_test` | Compilation smoke test |

### 6.2 Test Quality

**Strengths:**
- Domain layer has genuine property-testing with `are` macro across many element types
- `model_test.clj` uses realistic fixture data (C4 banking example with person/system/container/component)
- Template evaluation tested both with native Clojure and with SCI (sandbox) execution

**Gaps:**
- Most adapter tests are **compilation smoke tests** only — they verify the namespace loads but don't test rendering output
- No integration tests that load a real model directory and verify the generated output files
- No round-trip tests (generate → parse back)
- PlantUML and Graphviz rendering correctness is not verified (would benefit from snapshot/golden file tests)
- No performance tests for large model loading

### 6.3 Test Infrastructure

The test runner uses Clojure's built-in `clojure.test`. No external test runner is configured in deps.edn (the cognitect test-runner is commented out). Tests are run via `clj -T:build/task test` or the Leiningen equivalent.

---

## 7. Build and Tooling

### 7.1 Build System

The project uses **Clojure tools.deps** as primary dependency management with a `build.clj` script using `clojure.tools.build.api`:

```clojure
;; Key build tasks
(defn clean  [directory] ...)    ; delete target/
(defn uberjar [options] ...)     ; AOT compile + package as overarch.jar
```

The build produces a self-contained uberjar (`target/overarch.jar`) deployable with just a JVM. The entry point is `org.soulspace.overarch.adapter.ui.cli`.

**Leiningen compatibility:** `project.clj` is maintained as a secondary build file, likely for Clojars deployment workflows.

### 7.2 Dependencies

| Dependency | Version | Assessment |
|---|---|---|
| `org.clojure/clojure` | 1.12.0 | Up-to-date |
| `org.clojure/tools.cli` | 1.1.230 | Standard CLI parsing |
| `com.cnuernber/charred` | 1.037 | Fast JSON — good choice |
| `com.nextjournal/beholder` | 1.0.2 | File watching for `--watch` mode |
| `expound/expound` | 0.9.0 | Better spec error messages |
| `org.babashka/sci` | 0.9.45 | Safe template evaluation sandbox |
| `org.clojars.quoll/tiara` | 0.5.1 | Ordered sets |
| `org.soulspace.clj/cmp.markdown` | 0.4.1 | Markdown utilities — internal |
| `org.slf4j/slf4j-nop` | 2.0.17 | Suppresses SLF4J warnings |

The dependency set is lean and purposeful. No bloated framework dependencies. SCI for template sandboxing is an excellent choice — it allows safe evaluation of user-provided Clojure code without `eval` security risks.

### 7.3 Development Setup

The `dev/` folder provides:
- `user.clj` — REPL initialisation namespace
- `dev.clj` — doc compilation utilities
- `scratch.clj` — exploratory experiments
- `model-gencfg.edn` / `project-gencfg.edn` — generation configs used during development

The project has good Calva/REPL-driven development support, consistent with Clojure best practices.

---

## 8. Template System Assessment

### 8.1 Design

The template system is a first-class feature, not an afterthought. It enables generation of any text artifact from the model, going beyond the built-in renderers. The key components:

- **Comb templates** (`.cmb` files): Weavejester's minimal template engine, EEx-style `<% %>` syntax
- **SCI evaluation**: All template code runs in a Babashka SCI sandbox — safe execution of user-supplied code
- **Model APIs**: Templates get access to well-designed query APIs (`model-api`, `view-api`, `template-api`, `graphviz-api`, `markdown-api`)
- **Generation config** (`gencfg.edn`): Data-driven configuration of which templates to run, on which elements, with what output naming

### 8.2 Template Organisation

```
templates/
├── arc42/         12 arc42 section templates
├── docs/          Node docs, view docs, use-case docs, role docs
├── exports/       EDN model export, Avro schema generation
├── reports/       Element reports in Markdown and CSV
└── views/         View rendering templates (alternative to built-in renderers)
```

The arc42 templates are particularly valuable — they allow automatic generation of a complete arc42 architectural documentation document from overarch model data.

### 8.3 Generation Config

```clojure
;; Example from templates/gencfg.edn
{:view-selection {:els #{:concept-view :process-view :model-view}}
 :template "views/view.dot.cmb"
 :per-element true
 :id-as-name true
 :extension "dot"}
```

The config is clean and declarative. Supporting `:per-element true` means one file per matched element can be generated, enabling systematic documentation generation.

### 8.4 Open Issues

The `dev/Todo.md` notes two template system issues still under investigation:
1. **SCI import/require mechanisms** — whether templates can `require` other namespaces inside SCI
2. **Generation for `:artifact-of`, `:version-of`, `:instance-of`** — newer relation types not yet covered by templates

---

## 9. Documentation Assessment

### 9.1 Structure

Documentation is organized in `doc/topics/` as individual Markdown files compiled into `doc/usage.md`:

| File | Content |
|---|---|
| `overview.md` | Project overview and concept |
| `rationale.md` | Design decisions and philosophy |
| `commandline.md` | CLI reference |
| `modelling.md` | Modelling guide |
| `views.md` | View types and configuration |
| `templates.md` | Template system guide |
| `exports.md` | Export format documentation |
| `selection-criteria.md` | Query system reference |
| `best-practices.md` | Usage patterns |
| `editor.md` | Editor setup (Calva) |

### 9.2 Quality

The documentation is comprehensive for the core use cases. The rationale document clearly explains the design philosophy. The command-line reference is well-structured.

**Gaps identified:**
- The README needs updating (noted in `dev/Todo.md`)
- No code of conduct (noted in `dev/Todo.md`)
- API documentation for using overarch as a library (not just CLI) is absent — though this may be by design
- The template system documentation could benefit from more worked examples showing the full model→template→output cycle

### 9.3 Self-Documentation

Overarch documents itself using overarch models in `models/overarch/`. This includes:
- The data model (all element and view types)
- Architecture views of overarch's own structure

This is an impressive demonstration of the tool's capability and keeps the metamodel honest.

---

## 10. Strengths

1. **Clean separation of model and view** — the fundamental insight that model data and visual representation are separate concerns is implemented consistently throughout.

2. **Layered clean architecture** — domain layer is dependency-free; new formats/views added without touching existing code.

3. **Data-driven everything** — models, views, selection queries, generation configs, and even view specifications are all expressed as plain data. This makes the system highly composable.

4. **Self-describing metamodel** — overarch's own structure is documented as an overarch model, demonstrating the tool's expressiveness and keeping the metamodel accurate.

5. **Rich view type support** — 18 view types covering 10 modelling perspectives is comprehensive. The C4 model support is complete and the extensions into process, organisation, domain, and concept modelling are valuable.

6. **Safe template evaluation** — using SCI for template code execution prevents arbitrary code injection while still allowing full Clojure semantics.

7. **Lean dependency set** — minimal, purposeful dependencies. No framework bloat.

8. **Extensible multimethod architecture** — adding a new render format or view type is a clean, well-defined operation.

9. **Watch mode** — real-time regeneration during modelling is a significant usability feature.

10. **Cross-platform path handling** — `util/io.clj` explicitly handles Windows vs Unix path separators.

---

## 11. Weaknesses and Risks

### 11.1 Test Coverage Gaps

The most significant technical risk is that rendering correctness is not verified. A refactor of `plantuml/c4.clj` or `graphviz.clj` could silently produce incorrect diagrams. Golden-file/snapshot tests for the built-in renderers would substantially increase confidence.

### 11.2 CLI Adapter Complexity

`adapter/ui/cli.clj` at 500+ lines handles too many concerns. It could be decomposed into:
- `cli_options.clj` — option parsing and validation
- `cli_actions.clj` — action dispatch
- `cli_watch.clj` — file watching logic

### 11.3 Global Mutable State

The `model-repository` atom is application-global. This is fine for a single-process CLI tool but would be a significant refactor if overarch were to be embedded as a library in another application or run as a server.

### 11.4 Full Model Reload on Change

Watch mode triggers a complete model reload on any file change. For large model repositories with many EDN files, this could become slow. An incremental update (reload only the changed file and patch the indices) would be more scalable.

### 11.5 `parent-new` Dead Code

The `parent-new` function in `domain/model.clj` with its TODO comment represents unfinished work. It should either be promoted to replace `parent` or removed.

### 11.6 Limited Library API

Overarch is primarily designed as a CLI tool. Using it programmatically as a Clojure library requires understanding internal namespaces. A stable, documented library API surface would increase adoption and composability.

### 11.7 SCI Import Limitations

The open Todo item about SCI `import/require` mechanisms means templates cannot currently use external libraries. This limits template expressiveness for complex generation scenarios.

---

## 12. Development Roadmap Observations

From `dev/Todo.md`, the active development areas are:

### Short-term (tactical)
- **Transitive search from CLI** — the `traverse` function exists in domain layer but isn't exposed in CLI options yet. This is a valuable usability improvement.
- **README and code of conduct updates** — housekeeping.

### Medium-term (feature)
- **Graphviz and PlantUML UML styling** — styling support is more complete in C4 views than UML/Graphviz views.
- **Generation for newer relation types** — `:artifact-of`, `:version-of`, `:instance-of` templates.
- **Model extract by selection** — the ability to export a sub-model satisfying a selection criteria would enable model slicing.
- **Canonical model export** — a stable, canonical export format for model interchange.

### Long-term (strategic)
- **Multi-perspective modelling coverage** — the Todo lists 10 modelling questions (What/Why/Who/How/When/Where/Quality/Duration/Configuration) as a framework for ensuring all perspectives are represented. This is a thoughtful expansion of the modelling ontology.
- **Custom views rendered by templates** — allowing entirely custom view types beyond the built-in 18.
- **Custom element types** — `{:el :my/custom-type}` support in the graph would make overarch a true open modelling platform.

---

## 13. Overall Verdict

### Summary Scorecard

| Area | Rating | Notes |
|---|---|---|
| Architecture design | ★★★★★ | Clean layers, multimethod extensibility, no circular deps |
| Data model | ★★★★★ | Data-driven, queryable, composable, self-describing |
| Code quality | ★★★★☆ | Idiomatic Clojure; `cli.clj` and dead code are minor issues |
| Test coverage | ★★★☆☆ | Domain well-tested; rendering correctness not verified |
| Documentation | ★★★★☆ | Comprehensive; README and some examples need updating |
| Build tooling | ★★★★★ | Clean, minimal, reproducible uberjar build |
| Template system | ★★★★☆ | Powerful and well-designed; some open SCI limitations |
| Extensibility | ★★★★★ | Multimethods make adding formats/views straightforward |

### Conclusion

Overarch is a well-designed, principled tool that solves a real problem in software architecture documentation. The core architecture is sound and the data-driven approach is genuinely differentiated from alternatives. The most impactful investments to improve the project would be:

1. **Adding golden-file/snapshot tests** for PlantUML, Graphviz, and Markdown rendering to catch regressions
2. **Decomposing `cli.clj`** into focused sub-namespaces
3. **Exposing transitive search in the CLI** — a high-value, apparently low-effort feature already present in the domain layer
4. **Updating the README** to reflect the current state of the project

The project has reached a level of maturity where the core value proposition is solid and the roadmap items represent genuine extensions rather than fundamental fixes.
