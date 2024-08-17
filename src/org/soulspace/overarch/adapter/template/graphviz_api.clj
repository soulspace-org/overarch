(ns org.soulspace.overarch.adapter.template.graphviz-api
  (:require [org.soulspace.overarch.adapter.render.graphviz :as gv]
            [org.soulspace.overarch.adapter.template.model-api :as m]))

(defn alias-name
  "Returns a valid alias for the namespaced keyword `kw`."
  [kw]
  (gv/alias-name kw))

(defn short-name
  "Returns a valid alias for the name part of the keyword `kw`."
  [kw]
  (gv/short-name kw))

