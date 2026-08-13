# Queries and Selection
Overarch elements can be queried and selected by criteria.
Selection works on model elements (nodes and relations) and views.
It is a central mechanism of overarch and is used in many places, e.g.
* selection of elements to include in a specific view
* selection of model elements or views in template based artifact generation
* model or view queries via the command line 

## Model Element Selection By Criteria
Criterias are given as a criteria map or a vector of criteria maps. 

In a critera map each key/value pair specifies a criterium
for the selection. An element is selected, if it matches all criteria in the
map (logical conjunction). For example a criteria map of
```{:el :component :namespace "overarch.domain"}``` 
selects all components in the "overarch.domain" namespace.

In a vector of criteria maps an element is selected, if it is selected by any
of the criteria maps in the vector (logical disjunction).

A criterium can be negated by adding a ``!`` in front of the key name.
For example a criteria map of ```{:el :component :!namespace "overarch.domain"}``` 
selects all components not in the "overarch.domain" namespace.

### Criterium Keys
criterium key           | type            | scope   | example values               | description
------------------------|-----------------|---------|------------------------------|------------
:model-node?            | boolean         | element | true, false                  | elements for which the check for model node returns the given value
:model-relation?        | boolean         | element | true, false                  | elements for which the check for model relation returns the given value
:key?                   | vector          | element | [:tech true]                 | elements for which the check for the key returns the value (useful for custom keys)
:key                    | vector          | element | [:tech "Clojure"]            | elements for which the lookup of the key returns the value (useful for custom keys)
:el                     | keyword         | element | :system                      | elements in the given set of ``:el`` types
:els                    | set of keywords | element | #{:system :person}           | elements with one of the given ``:el`` types
:namespace              | string          | element | "org.soulspace"              | elements with the given ``:id`` namespace
:namespaces             | set of strings  | element | #{"org.soulspace"}           | elements with one of the given ``:id`` namespaces
:namespace-prefix       | string          | element | "org"                        | elements with the given ``:id`` namespace prefix
:id?                    | boolean         | element | true, false                  | elements for which the ``:id`` check returns the given value
:id                     | keyword         | element | :org.soulspace/overarch      | the element with the given ``:id``
:ids                    | keyword         | element | #{:org.soulspace/overarch}   | elements in the given set of``:id``s
:from                   | criteria        | model   | {:el system :external? true} | relations where the ``:from`` node matches the criteria
:to                     | criteria        | model   | {:el system :external? true} | relations where the ``:to`` node matches the criteria
:subtype?               | boolean         | element | true, false                  | nodes for which the ``:subtype`` check returns the given boolean value
:subtype                | keyword         | element | :queue                       | nodes of the given ``:subtype``
:subtypes               | set of keywords | element | #{:queue :database}          | nodes of one of the given ``:subtype``s
:maturity?              | boolean         | element | true, false                  | elements for which the ``:maturity`` check returns the given boolean value
:maturity               | keyword         | element | :proposed, :deprecated       | elements of the given ``:maturity``
:maturities             | set of keywords | element | #{:implemented  :deprecated} | elements of the given maturities
:external?              | boolean         | model   | true, false                  | elements of the given external state
:name?                  | boolean         | element | true, false                  | elements for which the ``:name`` check returns the given value
:name                   | string/regex    | element | "Overarch CLI" "(?i).*CLI.*" | elements for which the ``:name`` matches the given value
:desc?                  | boolean         | element | true, false                  | elements for which the ``:desc`` check returns the given value
:desc                   | string/regex    | element | "CLI" "(?i).*CLI.*"          | elements for which the ``:desc`` matches the given value
:doc?                   | boolean         | element | true, false                  | elements for which the ``:doc`` check returns the given value
:doc                    | string/regex    | element | "CLI" "(?i).*CLI.*"          | elements for which the ``:doc`` matches the given value
:tech?                  | boolean         | element | true, false                  | elements for which the ``:tech`` check returns the given value
:tech                   | string          | element | "Clojure"                    | elements of the given ``:tech``
:techs                  | set of strings  | element | #{"Clojure" "Java"}          | elements with one or more of the given ``:tech``s
:all-techs              | set of strings  | element | #{"Clojure" "Java"}          | elements with all of the given ``:tech``s
:tags?                  | boolean         | element | true, false                  | elements for which the ``:tags`` check returns the given value
:tag                    | string          | element | "critical"                   | elements with the given tag
:tags                   | set of strings  | element | #{"Clojure" "Java"}          | elements with one or more of the given ``:tags``
:all-tags               | set of strings  | element | #{"Clojure" "Java"}          | elements with all of the given ``:tags``
:refers?                | boolean         | model   | true, false                  | nodes for which the check for refers returns the given value
:referred?              | boolean         | model   | true, false                  | nodes for which the check for referred returns the given value
:refers                 | criteria        | model   | {:el :request}               | nodes with a referring relation matching the criteria 
:referred               | criteria        | model   | {:el :request}               | nodes with a referred relation matching the criteria
:refers-to              | keyword         | model   | :org.soulspace/overarch      | nodes which refer to the node with the given id
:referred-by            | keyword         | model   | :org.soulspace/overarch      | nodes which are referred by the node with the
:child?                 | boolean         | model   | true, false                  | nodes for which the check for child returns the given value
:child-of               | keyword         | model   | :org.soulspace/overarch      | nodes which are children of the node with the given id
:descendant-of          | keyword         | model   | :org.soulspace/overarch      | nodes which are descendants of the node with the given id
:parent?                | boolean         | model   | true, false                  | nodes for which the check for children returns the given value
:parent-of              | keyword         | model   | :org.soulspace/overarch      | node which is the parent of the node with the given id
:ancestor-of            | keyword         | model   | :org.soulspace/overarch      | nodes which are ancestors of the node with the given id

### Deprecated Criterium keys
The following keys are deprecated since version 0.37.0 and can be rewritten
using ``:from`` and ``:to``, e.g ```{:to-namespace "overarch.domain"}```
should be replaced by ```{:to {:namespace "overarch.domain"}}```.
By using the new form, additional criteria could be added for the referred
nodes to match the relation.

criterium key           | type            | scope   | example values               | description
------------------------|-----------------|---------|------------------------------|------------
:from-namespace         | string          | element | "org.soulspace"              | relations with the given id namespace of the ``:from`` reference
:from-namespaces        | set of strings  | element | #{"org.soulspace"}           | relations with one of the given id namespaces of the ``:from`` reference
:from-namespace-prefix  | string          | element | "org"                        | relations with the given id namespace prefix of the ``:from`` reference
:to-namespace           | string          | element | "org.soulspace"              | relations with the given id namespace of the ``:to`` reference
:to-namespaces          | set of strings  | element | #{"org.soulspace"}           | relations with one of the given id namespaces of the ``:to`` reference
:to-namespace-prefix    | string          | element | "org"                        | relations with the given id namespace prefix of the ``:to`` reference

## Transitive Model Element Selection
*Experimental, subject to change*

Transitive queries on the model are a powerful tool to answer important
architectural questions, e.g.
* which services, applications and roles will be affected by a problem with a
  specific service because of synchronous coupling via request/response
* which service outages will cause a problem in my service or application

To use a transitive query of model elements, a map with a `:start` key must be
provided. The relations to follow transitively are specified by either the
`:referred` key or the `:referring` key. A selection predicate for the elements
 to be selected can be specified with the `:elements` key. 

### Transitive Selection Keys
key        | type | example values | description
-----------|------|----------------|------------
:start     | selection criteria | {:id :overarch.data-model/architecture-element} | criteria for the start element of the transitive query
:elements  | selection criteria | {:el :class} | optional criteria for the elements to select
:referred  | selection criteria | {:el :inheritance} | criteria for the relations to follow transitively in the `:to` direction
:referring | selection criteria | {:el :inheritance} | criteria for the relations to follow transitively in the `:from` direction

