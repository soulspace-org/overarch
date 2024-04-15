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

```
java -jar overarch.jar -g gencfg.edn
```

## Comb Template Engine
Overarch incorporates the [Comb](https://github.com/weavejester/comb) template engine by James Reeves.

Comb is a simple templating system for Clojure. You can use Comb to embed fragments of Clojure code into a text file.

### Syntax
Clojure fragments in a template are demarkated with `<%` and `%>`.
You can embed clojure code as an expression, where the result of the
execution is included in the resulting artifact. You can also embed
the clojure code as a control structure, where the result of the
execution of the control structure is not included in the resulting
artifact, only the template text or other expressions inside of the
control structure.

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
foo<% (dotimes [x 3] %> bar<%) %>
```
Result:
```
foo bar bar bar
```

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

## Security Considerations

Comb templates can contain arbitrary clojure code, which gets evaluated in the context of the overarch process. Be aware of this fact and review templates accordingly, especially when using templates from external sources.
