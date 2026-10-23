---
name: plan
description: "Create an implementation-ready development plan grounded in the codebase and relevant research. Use when asked to plan a feature, refactor, migration, or technical change without implementing it; saves the plan under dev/plans/."
disable-model-invocation: true
---

# Development Planning

Create a practical, implementation-ready plan for the task in the user's
request. Ground it in the current repository and relevant existing research.
Planning is the deliverable; do not implement the planned changes.

## Procedure

1. Clarify the requested outcome, boundaries, and acceptance criteria. Ask the
   user only when an unresolved decision materially changes the plan.
2. Read repository instructions and inspect the relevant implementation, tests,
   configuration, and documentation. Reuse existing patterns and identify
   dependencies between work items.
3. Write ordered, actionable steps. For each meaningful step, name the likely
   files or components to change, describe the outcome, and include focused
   verification such as targeted tests, builds, or manual checks.
4. Call out risks, assumptions, compatibility or migration concerns, and
   decisions that still need user input. Avoid speculative details and
   unnecessary implementation minutiae.
5. Save the plan as a Markdown file under `dev/plans/` with a descriptive
   kebab-case filename. Do not overwrite an existing plan unless the user
   explicitly asks to revise it.
6. Summarize the plan and link the saved document in the final response.

The plan should be followable by a software developer or coding agent without
requiring access to an unavailable dedicated planning agent. Use independent
research or delegated investigation only when it materially improves the plan.
