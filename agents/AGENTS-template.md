# AGENTS.md Template
```markdown
# <Project>
<What it does, in a few lines. Key requirements and constraints.>

# Project Structure
<Language, architecture style, build tool. Directory layout with links: source, tests, dev code, resources, model, templates, tools.>
<Naming rules, e.g. namespace prefix.>

# Overarch Model
Use the `architecture-modelling` skill for model editing and the `architecture-query` skill for questions about the architecture. Don't rely on generated documentation, it may not be current.

# Agent Skills: Project Specifics

## Model Tooling and References
* Overarch jar path and how it is run; model directory; id namespace prefix.
* Paths of the modelling reference, the model query reference, the change policy and the decision-records guide.

## Build, Test and Validation
* Test command. Build command (and tools that may be missing in some environments).
* Model validation command.
* Commands that regenerate derived files, and whether agents may run them.

## Architecture Rules
* Layers or modules and the allowed dependency directions.
* Id conventions in the model.
* Public contracts (changes are Tier 3): <CLI, APIs, file and data formats, exit codes, ...>.

## Where Things Live
* Model files and what each contains.
* Where decisions are recorded, their tags and links.
* Where plans and ideas live, with the plan template and status vocabulary.
* Model areas the project does not have (yet): requirements, deployment, domain concepts, tests, ... Agents then work from other evidence and may propose modelling the area.

## Security Starting Points
* Untrusted inputs; external services and credentials; sensitive data; own dependencies; output that could leak data.

## Overrides
* Any deviation from the default change tiers or approval rules, with the reason.
```
