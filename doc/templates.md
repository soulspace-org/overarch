# Artifact Generation with Templates

Overarch can generate artifacts for model elements via templates.

It supports
* forward engineering
* protected areas for handwritten code

## Generator Config

You can configure the generation of artifacts with a EDN file.
The configuration contains a vector of generation context maps.
A generation context map specifies a selection of model elements, a template
to use, how the template should be applied, and where the resulting artifact
should be created.

key               | type     | values             | default | description
------------------|----------|--------------------|---------|-------------
:selection        | CRITERIA | {:el :system}      |         | Criteria to select model elements
:template         | PATH     | "report/node.cmb"  |         | Path to the template relative to the template dir
:engine           | :keyword | :comb              | :comb   | The template engine to use (currently only Comb)
:encoding         | string   | "UTF-8"            | "UTF-8" | The encoding of the result artifact
:per-element      | boolean  | true/false         | false   | Apply  the template for each element of the selection or on the selection as a whole
:subdir           | string   | "report"           |         | Subdirectory for generated artifact under the generator directory
:namespace-prefix | string   | "src"              |         | Prefix to the namespace to use as path element
:base-namespace   | string   |                    |         | Base namespace to use as path element
:namespace-suffix | string   | "impl"             |         | Suffix to the namespace to use as path element
:prefix           | string   | "Abstract"         |         | Prefix for the filename
:base-name        | string   |                    |         | Base of the filename
:suffix           | string   | "Impl"             |         | Suffix for the filename
:extension        | string   | "md" "clj" "java"  |         | Extension for the filename
:filename         | string   | "README.md"        |         | Specific filename to use
:id-as-namespace  | boolean  | true/false         | false   | Use the element id as the namespace for path generation
:protected-areas  | string   | "PA"               |         | Marker for protected areas in the template/artifact


### Example config file
```clojure
[{:selection {:el :system}      ; selection criteria for the model elements
 :template "node-report.md.cmb" ; relative path of the template to apply
 :engine :comb                  ; the template engine to use (currently only :comb)
 :encoding "UTF-8"              ; artifact encoding
 :per-element false             ; apply the template for each element of the selection or on the selection as a whole
 :subdir "reports"              ; subdirectory for generated artifact
; :namespace-prefix ""          ; prefix for the namespace of the generated artifact
 :base-namespace "systems"      ; base namespace of the generated artifact
; :namespace-suffix ""          ; suffix for the namespace of the generated artifact
; :prefix ""                    ; prefix for the name of the generated artifact
 :base-name "systems-report"    ; base name of the generated artifact
; :suffix ""                    ; suffix for the name of the generated artifact
 :extension "md"                ; extension of the generated artifact
 :id-as-namespace false         ; use the name as the namespace of the artifact
; :protected-area "PA"          ; protected area prefix
}]
```



## Overarch CLI

Relevant CLI options for template based artifact generation:
```
  -t, --template-dir DIRNAME          templates  Template directory
  -g, --generator-config FILE                    Generator configuration
  -G, --generator-dir DIRNAME         generated  Generator artifact directory
  -B, --generator-backup-dir DIRNAME  backup     Generator backup directory
```

## Comb Template Engine
Overarch incorporates the [Comb](https://github.com/weavejester/comb) template engine by James Reeves.

Comb is a simple templating system for Clojure. You can use Comb to embed fragments of Clojure code into a text file.

### Syntax

Expressions:
```clojure
1 + 2 = <%= (+ 1 2) %>
```
Result:
```
1 + 2 = 3
```

Control structures:
```clojure
foo<% (dotimes [x 3] %> bar<%) %>
```
Result:
```
foo bar bar bar
```

### Security Considerations

Comb templates can contain arbitrary clojure code, which gets evaluated in the context of the overarch process. Be aware of this fact and review templates accordingly.

