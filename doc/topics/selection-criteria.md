# Model Element Selection By Criteria
Model elements can be selected based on criteria.
Criterias are given as a map where each key/value pair specifies a criterium
for the selection. An element is selected, if it matches all criteria in the
map (logical conjunction).

Criterias can also be given as a vector of criteria maps. An element is
selected, if it is selected by any of the critria maps (logical disjunction). 

## Keys
key                    | type            | example values            | description
-----------------------|-----------------|---------------------------|------------
:key?                  | vector          | [:tech true]                 | elements for which the check for the key returns the value (useful for custom keys)
:key                   | vector          | [:tech "Clojure"]            | elements for which the lookup of the key returns the value (useful for custom keys)
:el                    | keyword         | :system                      | elements of the given type
:els                   | set of keywords | #{:system :person}           | elements with one of the given types
:namespace             | string          | "org.soulspace"              | elements with the given id namespace
:namespaces            | set of strings  | #{"org.soulspace"}           | elements with one of the given id namespaces
:namespace-prefix      | string          | "org"                        | elements with the given id namespace prefix
:from-namespace        | string          | "org.soulspace"              | relations with the given id namespace of the from reference
:from-namespaces       | set of strings  | #{"org.soulspace"}           | relations with one of the given id namespaces of the from reference
:from-namespace-prefix | string          | "org"                        | relations with the given id namespace prefix of the from reference
:to-namespace          | string          | "org.soulspace"              | relations with the given id namespace of the from reference
:to-namespaces         | set of strings  | #{"org.soulspace"}           | relations with one of the given id namespaces of the from reference
:to-namespace-prefix   | string          | "org"                        | relations with the given id namespace prefix of the from reference
:id?                   | boolean         | true, false                  | elements for which the id check returns the given value
:id                    | keyword         | :org.soulspace/overarch      | the element with the given id
:from                  | keyword         | :org.soulspace/overarch      | relations with the given from id
:to                    | keyword         | :org.soulspace/overarch      | relations with the given to id
:subtype?              | boolean         | true, false                  | nodes for which the subtype check returns the given boolean value
:subtype               | keyword         | :queue                       | nodes of the given subtype
:subtypes              | set of keywords | #{:queue :database}          | nodes of one of the given subtypes
:maturity?             | boolean         | true, false                  | elements for which the maturity check returns the given boolean value
:maturity              | keyword         | :proposed, :deprecated       | elements of the given maturity
:maturities            | set of keywords | #{:implemented  :deprecated} | elements of the given maturities
:external?             | boolean         | true, false                  | elements of the given external state
:name?                 | boolean         | true, false                  | elements for which the name check returns the given value
:name                  | string/regex    | "Overarch CLI"               | elements for which the name matches the given value
:desc?                 | boolean         | true, false                  | elements for which the description check returns the given value
:desc                  | string/regex    | "CLI" "(?i).*CLI.*"          | elements for which the description matches the given value
:doc?                  | boolean         | true, false                  | elements for which the documentation check returns the given value
:doc                   | string/regex    | "CLI" "(?i).*CLI.*"          | elements for which the documentation matches the given value
:tech?                 | boolean         | true, false                  | elements for which the technology check returns the given value
:tech                  | string          | "Clojure"                    | elements of the given technology
:techs                 | set of strings  | #{"Clojure" "Java"}          | elements with one or more of the given technologies
:all-techs             | set of strings  | #{"Clojure" "Java"}          | elements with all of the given technologies
:tags?                 | boolean         | true, false                  | elements for which the tags check returns the given value
:tag                   | string          | "critical"                   | elements with the given tag
:tags                  | set of strings  | #{"Clojure" "Java"}          | elements with one or more of the given tags
:all-tags              | set of strings  | #{"Clojure" "Java"}          | elements with all of the given tags
:refers?               | boolean         | true, false                  | nodes for which the check for refers returns the given value
:referred?             | boolean         | true, false                  | nodes for which the check for referred returns the given value
:refers-to             | keyword         | :org.soulspace/overarch      | nodes which refer to the node with the given id
:referred-by           | keyword         | :org.soulspace/overarch      | nodes which are referred by the node with the given id
:child?                | boolean         | true, false                  | nodes for which the check for child returns the given value
:child-of              | keyword         | :org.soulspace/overarch      | nodes which are children of the node with the given id
:descendant-of         | keyword         | :org.soulspace/overarch      | nodes which are descendants of the node with the given id
:parent?               | boolean         | true, false                  | nodes for which the check for children returns the given value
:parent-of             | keyword         | :org.soulspace/overarch      | node which is the parent of the node with the given id
:ancestor-of           | keyword         | :org.soulspace/overarch      | nodes which are ancestors of the node with the given id
