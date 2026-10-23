---
name: research
description: "Research a technical topic using repository files and reliable external sources, then document sourced findings. Use for technology comparisons, design questions, standards, APIs, or background research; saves the report under dev/research/."
disable-model-invocation: true
---

# Technical Research

Research the topic in the user's request using relevant repository material and,
when needed, reliable external sources.

## Procedure

1. Define the research question, scope, and any constraints from the request.
   Inspect relevant repository instructions and existing research to avoid
   duplicating or contradicting established work.
2. Gather evidence from primary sources where possible: implementation,
   tests, official documentation, specifications, and maintained project
   references. Verify that sources directly support the claims made.
3. Separate sourced facts from analysis or recommendations. Define each
   abbreviation at first use, and include code snippets only when they clarify
   a finding.
4. Cite repository evidence with file and symbol/line references and external
   claims with direct source links. Note uncertainty, version differences, and
   any limitations in the evidence.
5. Save a self-contained Markdown report under `dev/research/` with a
   descriptive kebab-case filename. Do not overwrite an existing report unless
   the user explicitly asks for an update.
6. Summarize the key findings and link the report in the final response.

Keep research focused on the question. Do not implement changes unless asked;
use separate research threads only when substantial independent investigation
is needed.