---
name: with-calva
description: "Develop and test Clojure code interactively with Calva and its REPL. Use for REPL-driven implementation, debugging, namespace exploration, or running Clojure tests; fall back to Leiningen or Clojure CLI when Calva tools are unavailable."
disable-model-invocation: true
---

# Clojure Development with Calva

Use a REPL-driven workflow for Clojure implementation, debugging, and tests.
Follow the repository's Clojure instructions and nearby code/test conventions.

## Procedure

1. Inspect repository instructions and the relevant namespace, call sites, and
   tests. Check for existing helpers and established patterns before editing.
2. If Calva Backseat Driver tools are available, use them for REPL evaluation,
   symbol/documentation lookup, and the output log. Reload the namespace before
   looking up or evaluating changed definitions.
3. Iterate with small, representative forms in the REPL when that provides
   useful feedback. Add or update `clojure.test` coverage for behavior changes.
4. Run the smallest relevant test command. Prefer the REPL/test tools when
   available; otherwise use the repository's supported command such as
   `lein test` or a focused test selector.
5. Report test results and any unverified behavior. If the REPL or an extension
   tool is unavailable, do not claim to have used it; proceed with available
   validation and state the limitation.

Ask the user a concise clarification question only when requirements or a
design decision materially affect the implementation. Do not ask the user to
repair files or start a REPL unless an actual blocker makes their input
necessary; use the available tools and ordinary error reporting instead.
