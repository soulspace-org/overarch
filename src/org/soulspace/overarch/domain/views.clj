(ns org.soulspace.overarch.domain.views
  "Registers the multimethod implementations for all view types.

   Require this namespace (for its side effects) before working with views, otherwise the
   view multimethods have no methods for the view types."
  (:require [org.soulspace.overarch.domain.views.code-view]
            [org.soulspace.overarch.domain.views.component-view]
            [org.soulspace.overarch.domain.views.concept-view]
            [org.soulspace.overarch.domain.views.container-view]
            [org.soulspace.overarch.domain.views.context-view]
            [org.soulspace.overarch.domain.views.deployment-view]
            [org.soulspace.overarch.domain.views.deployment-architecture-view]
            [org.soulspace.overarch.domain.views.deployment-structure-view]
            [org.soulspace.overarch.domain.views.domain-view]
            [org.soulspace.overarch.domain.views.dynamic-view]
            [org.soulspace.overarch.domain.views.glossary-view]
            [org.soulspace.overarch.domain.views.model-view]
            [org.soulspace.overarch.domain.views.organization-structure-view]
            [org.soulspace.overarch.domain.views.process-view]
            [org.soulspace.overarch.domain.views.state-machine-view]
            [org.soulspace.overarch.domain.views.system-landscape-view]
            [org.soulspace.overarch.domain.views.system-structure-view]
            [org.soulspace.overarch.domain.views.use-case-view]))
