---
name: 'Comb Template Instructions'
description: 'General instructions for comb templates' 
applyTo: '**/*.cmb'
---
# Comb Template Instructions
Comb templates
* based on the weavejester/comb template library
* contain Clojure code
  * control code is demarcated by `<%` and `%>`
  * expressions generating output in the artifact are demarcated by `<%=` and `%>`
* text not demarcated is written to the artifact as-is
