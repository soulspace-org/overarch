# Template Based Artifact Generation
Overarch can generate artifacts for model elements and views via templates.
The use cases of the tempates range from reports up to automatic code generation.
Overarch supports forward engineering protected areas for manually written
content in generated artifacts

## Generation Configuration
You can configure the generation of artifacts with an EDN file.
The configuration contains a vector of generation context maps.
A generation context map specifies a selection of model elements, a template
to use, how the template should be applied, and where the resulting artifact
should be created.

key               | type     | values              | default  | description
------------------|----------|---------------------|----------|-------------
:selection        | CRITERIA | {:el :system}       |          | Criteria to select model elements
:view-selection   | CRITERIA | {:el :context-view} |          | Criteria to select views
:template         | PATH     | "report/node.cmb"   |          | Path to the template relative to the template dir
:engine           | :keyword | :comb               | :combsci | The template engine to use (currently just :comb and :combsci)
:encoding         | string   | "UTF-8"             | "UTF-8"  | The encoding of the result artifact
:per-element      | boolean  | true/false          | false    | Apply  the template for each element of the selection
:per-namespace    | boolean  | true/false          | false    | Apply  the template for each namespace of the selection
:subdir           | string   | "report"            |          | Subdirectory for generated artifact under the generator directory
:namespace-prefix | string   | "src"               |          | Prefix to the namespace to use as path element
:base-namespace   | string   |                     |          | Base namespace to use as path element
:namespace-suffix | string   | "impl"              |          | Suffix to the namespace to use as path element
:prefix           | string   | "Abstract"          |          | Prefix for the filename
:base-name        | string   |                     |          | Base of the filename
:suffix           | string   | "Impl"              |          | Suffix for the filename
:extension        | string   | "md" "clj" "java"   |          | Extension for the filename
:filename         | string   | "README.md"         |          | Specific filename to use
:id-as-namespace  | boolean  | true/false          | false    | Use the element id as the namespace for path generation
:id-as-name       | boolean  | true/false          | false    | Use the name part of the element id as the name for path generation
:protected-areas  | string   | "PA"                |          | Marker for protected areas in the template/artifact
:debug            | boolean  | true/false          | false    | Print parsed template on error

You can add additional (namespaced) keys to the generation context map, which
are available in via the `ctx` symbol in the template.

The model elements, to which a template is applied, are selected via criteria
with the selection key.
A template can be applied to the collection of selected elements or to each
element of the collection.

Templates can also be applied on views selected by criteria with the
:view-selection key. The view selection also returns a collection of views,
even if there is only one view selected. So use the :per-element key to enable
the generation on a view level.

### Example Templates
Some example templates can be found in the [templates](/templates) folder.

### Example Generation Config File
```clojure
[;; Report for all systems in the banking namespace
 {:selection {:namespace "banking" :el :system} ; selection criteria for the model elements
 :template "node-report.md.cmb"  ; relative path of the template to apply
 :title "Banking Systems Report" ; title of the report
 :engine :comb                   ; the template engine to use (currently only :comb)
 :encoding "UTF-8"               ; artifact encoding
 :per-element false              ; apply the template for each element of the selection or on the selection as a whole
 :subdir "reports"               ; subdirectory for generated artifact
 ; :namespace-prefix ""          ; prefix for the namespace of the generated artifact
 :base-namespace "systems"       ; base namespace of the generated artifact
 ; :namespace-suffix ""          ; suffix for the namespace of the generated artifact
 ; :prefix ""                    ; prefix for the name of the generated artifact
 :base-name "systems-report"     ; base name of the generated artifact
 ; :suffix ""                    ; suffix for the name of the generated artifact
 :extension "md"                 ; extension of the generated artifact
 :id-as-namespace false          ; use the name as the namespace of the artifact
 ; :protected-area "PA"          ; protected area prefix
 }

;; Report for the REST interfaces in the model
{:selection {:el :model-relation :techs #{"REST"}} ; selection criteria for the model elements
 :template "rel-report.md.cmb"   ; relative path of the template to apply
 :title "REST Interface Report"  ; title of the report
 :engine :comb                   ; the template engine to use (currently only :comb)
 :encoding "UTF-8"               ; artifact encoding
 :per-element false              ; apply the template for each element of the selection or on the selection as a whole
 :subdir "reports"               ; subdirectory for generated artifact
; :namespace-prefix ""           ; prefix for the namespace of the generated artifact
 :base-namespace "interfaces"    ; base namespace of the generated artifact
; :namespace-suffix ""           ; suffix for the namespace of the generated artifact
; :prefix ""                     ; prefix for the name of the generated artifact
 :base-name "systems-report"     ; base name of the generated artifact
; :suffix ""                     ; suffix for the name of the generated artifact
 :extension "md"                 ; extension of the generated artifact
 :id-as-namespace false          ; use the name as the namespace of the artifact
; :protected-area "PA"           ; protected area prefix
}]
```

## Overarch CLI
The relevant CLI options for template based artifact generation are
```
  -m, --model-dir PATH           models     Models directory or path
  -T, --template-dir DIRNAME     templates  Template directory
  -g, --generation-config FILE              Generation configuration
  -G, --generation-dir DIRNAME   generated  Generation artifact directory
  -B, --backup-dir DIRNAME       backup     Generation backup directory
```

Example using a config file in the current directory and default directories
```
java -jar overarch.jar -g gencfg.edn
```

## Comb Template Engine
Overarch uses the [Comb](https://github.com/weavejester/comb)
template syntax by James Reeves.

Comb is a simple templating system for Clojure. You can use Comb to embed
fragments of Clojure code into a text file.

### Syntax
Clojure fragments in a template are demarkated with `<%` and `%>`.
You can embed clojure code as an expression, where the result of the
execution is included in the resulting artifact. You can also embed the clojure
code as a control structure, where the result of the execution of the control
structure is not included in the resulting artifact, only the template text or
other expressions inside of the control structure.

#### Expressions
```clojure
1 + 2 = <%= (+ 1 2) %>
```

Result:
```
1 + 2 = 3
```

#### Control structures
```clojure
foo<%
(dotimes [x 3]
%> bar<%
)
%>
```
Result:
```
foo bar bar bar
```

### API for Templates
In the comb templates you can use most of the functions of the clojure.core
namespace. Additionally the functions of clojure.set and clojure.string are
provided under the aliases `set` and `str`, e.g. `set/union` or `str/join`.

Overarch also provides functions to query and navigate the model under the
`m` alias, e.g. `m/resolve-element`.

### Security Considerations
Comb templates evaluated with `:comb` are compiled and can contain arbitrary
clojure code, which gets evaluated in the context of the overarch process.
Be aware of this fact and review templates accordingly, especially when using
templates from external sources.

When the comp templates are evaluated by the `:combsci` engine, they are
interpreted with Babashka SCI, the small clojure interpreter. This has many
advantages:
* sandboxed evaluation
* control over the the available clojure namespaces and symbols
* independent from the method of providing and starting overarch

As the template code is interpreted with SCI and not compiled, the generation
might be a bit slower then with using compiled templates. But the advantages
outweight the performance penalty by far. 

## Protected Areas
Protected areas are used to protect manually inserted text in generated
artifacts. For example, when generating source code from a code model element,
maybe only the signature of the function may be generated. The body of the
function may have to be inserted by a programmer.

When regenerating the source code artifact, you don't want the manually
inserted code to be deleted or overridden, but preserved.

Given this class node from a model
```clojure
{:el :class
 :id :model/calc
 :name "Calc"
 :ct [{:el method
       :name "square"
       :type "double"
       :visibility :public
       :ct [{:el :field
             :name "x"
             :type "double"}
           ]}
     ]}
```
and a template like
```clojure
public class <%= (:name e) %> {

  <% (doseq [m (:ct e)] %>
  public <%= (:type m) %> <%= (:name m) %>(<%
  (doseq [p (:ct m)] %> (:type m) %> <%= (:name m) %>, <%)%>) {
    // PA-BEGIN(square-impl)
    <%= (:square-impl protected-areas)%>
    // PA-END(square-impl)
  }
  <%)%>
}
```

On the first generation pass, the generated file will look like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    // PA-END(square-impl)
  }
}
```
After manually inserting the implementation, the artifact looks like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    return x * x;
    // PA-END(square-impl)
  }
}
```
On regeneration, the content of the protected area is parsed by the generator before applying the template and reinserted by the template.

So after regeneration the artifact still looks like
```java
public class Calc {

  public double square(double x) {
    // PA-BEGIN(square-impl)
    return x * x;
    // PA-END(square-impl)
  }
}
```
