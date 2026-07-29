---
name: 'Clojure Instructions'
description: 'General instructions for Clojure code'
applyTo: '**/*.clj'
---
# Clojure Instructions
Develop REPL-driven: define test data, sketch a minimal skeleton, build and test incrementally in the REPL, then refactor for simplicity before adding `clojure.test` tests.

If Calva Backseat Driver tools are available, use them for REPL evaluation, symbol/doc lookup, and the output log — reload the namespace before looking up symbols. Otherwise use `lein repl` / `lein test` directly. Use rich comment blocks in the namespace under experimentation.

Check `@workspace` before writing something that may already exist.

## Conventions
- Clean Architecture layering: `domain` (pure, no I/O) → `application` (use-case orchestration, port multimethods) → `adapter` (I/O, implements ports) → `util` (stateless helpers). Respect the dependency direction — don't reach from `domain` into `adapter`.
- Data-oriented: EDN, maps/vectors/sets, destructuring, over ad-hoc types.
- Validate model data with `clojure.spec` (project uses `expound` for readable spec errors).
- Prefer named functions and data over anonymous ones; meaningful names over short ones.
- Macros only as a last resort.
- Java interop is fine, but prefer idiomatic Clojure when available.

## Style
Simple, not simplistic — accidental complexity is the enemy, not essential complexity. Small, focused functions.
Don't add docstrings/comments beyond what the project already does in surrounding code — match existing convention rather than a blanket "document everything" rule.

## Testing
Use `clojure.test`, with `clojure.spec` generators for property-style coverage where it adds value.