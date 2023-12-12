---
marp: true
#theme: gaia
theme: uncover
class: invert
author: "Ludger Solbach"
---
## overarch
### data driven modelling
### Ludger Solbach
![bg cover 100%](./images/overarch.jpg)

---
<!-- paginate: true -->
<!-- header: "overarch - data driven modelling" -->
<!-- footer: "Ludger Solbach" -->

# Rationale

* system models for
  * communication
  * documentation
  * thinking

---

# Rationale

* commercial modelling tools
  * e.g. Enterprise Architect, Magic Draw
  * comprehensive but complex
  * license needed

---

# Rationale

* drawing tools
  * e.g. Visio, Gliffy, ExcaliDraw
  * pretty pictures only
  * no reuse between diagrams

---

# Rationale

lightweight open source modelling

data driven and text based

with visualization

### => **overarch**

---

# Models

* models as data
  * concept models, concept maps and glossaries
  * system landscape and architecture, deployment
  * use cases, state machines and class models
 * hierarchical models and element references
 * composable / reusable
 * extensible format

---

# Views

* views as data
  * separation of models and views
  * view specific customization of model elements
 
---

# Rendering

 * PlantUML
   * C4 views, UML views
     * use case, state machine and class diagrams
   * styling and sprite support
 * GraphViz
   * Concept maps
 * Markdown
   * Glossary, textual representations of graphical views

---

# Exporting

* exports
  * JSON if you need to process models with languages without EDN support
  * Structurizr *experimental*

---

# Example Model

![bg right 100%](./images/overarch_vscode_model.png)

---

# Example View

![bg right 100%](./images/overarch_vscode_diagram.png)

---

# Example Diagram

![bg right 70%](./images/banking_systemContextView.svg)

---

# Example Diagram

![bg right 90%](./images/banking_containerView.svg)


---

# Extensible Data Notation (EDN)

Similar to JSON but richer set of data literals
Subset of Clojure

---

# EDN - Primitives

Numbers
Strings
Keywords

---

# EDN - Data Structures

Vectors
Sets
Maps

---













