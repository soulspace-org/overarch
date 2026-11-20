# Overarch Overview
* open platform for architecture modeling, analysis and generation.
* provides a data model for the holistic description of software systems and enterprises with their system landscapes
* opening multiple use cases on the model data
* written in Clojure
* build with Leiningen

# Project Structure
Overarch is a Clojure project built with Leiningen. The general organization is
* [Source Code](/src/)
* [Test Code](/test)
* [Development Code](/dev)
* [Classpath Resources](/resources)
* [Generation Templates](/templates)
* [Usage Documentation](/doc)
* [System Documentation](/docs)
* [Overarch Models](/models)
* [Example Models](/examples)

The namespace prefix for source code is: `org.soulspace.overarch`.

# Relevant usage documentation:
* [Command Line Reference](doc/topics/commandline.md)
* [Selection Criteria Reference](doc/topics/selection-criteria.md)
* [Modelling Reference](/doc/topics/modelling.md)
* [Views Reference](doc/topics/views.md)
* [Best Practices](doc/topics/best-practices.md)

# Additional Overarch Information
* [Overarch Features](dev/features.md)
* [Overarch Architecture](dev/architecture.md)

# Overarch Architecture Skills
The agent skills in [.claude/skills](.claude/skills) are project-independent. Their shared rules are in [.claude/references/principles.md](.claude/references/principles.md), [.claude/references/model-change-policy.md](.claude/references/model-change-policy.md) and [.claude/references/decision-records.md](.claude/references/decision-records.md). Everything specific to this project is defined here.

Use the `architecture-modelling` skill for model editing and the `architecture-query` skill to answer questions about the architecture  (don't rely on the generated documentation in `docs`, it may not be current).

The Overarch directory is the project root. The system id for Overarch in the architecture model is `:overarch.architecture/overarch`.
