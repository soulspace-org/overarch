---
name: assess
description: "Assess the codebase or a specific feature and document evidence-based findings. Use when asked to evaluate implementation state, documentation coverage, architecture, maintainability, or gaps; saves the assessment under dev/assessments/."
disable-model-invocation: true
---

# Codebase Assessment

Assess the repository or scope named in the request and answer the assessment
question with evidence from the current codebase.

## Procedure

1. Identify the exact scope and question. Read repository instructions and
   inspect relevant implementation, tests, documentation, and recent project
   history where useful.
2. Compare claims or documented behavior with implementation and tests. Avoid
   treating names, comments, or changelog entries alone as proof of current
   behavior.
3. Record findings with concise explanations, severity or priority when useful,
   and precise file, symbol, line, or external-source references. Distinguish
   verified facts from inference and note meaningful limitations.
4. Save the assessment as a Markdown file under `dev/assessments/`, using a
   descriptive kebab-case filename. Do not overwrite an existing assessment
   unless the request explicitly asks to revise it.
5. Summarize the conclusion and link the saved assessment in the final response.

Do not implement recommended changes unless the user also asks for them. Keep
the assessment focused on the requested question; use separate research threads
only when they need substantial independent investigation.