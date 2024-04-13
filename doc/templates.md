# Artifact Generation with Templates

Overarch can generate artifacts for model elements with templates.

It supports
* forward engineering
* protected areas for handwritten code

## Generator Context

```clojure
{:selection {:el :system} ; selection criteria for the model elements
 :template ""             ; relative path of the template to apply
 :engine :comb            ; the template engine to use (currently only :comb)
 :encoding "UTF-8"        ; artifact encoding
 :per-element true        ; apply the template for each element of the selection
 :subdir ""               ; subdirectory for generated artifact
 :namespace-prefix ""     ; prefix for the namespace of the generated artifact
 :base-namespace ""       ; base namespace of the generated artifact
 :namespace-suffix ""     ; suffix for the namespace of the generated artifact
 :prefix ""               ; prefix for the name of the generated artifact
 :base-name ""            ; base name of the generated artifact
 :suffix ""               ; suffix for the name of the generated artifact
 :extension ""            ; extension of the generated artifact
 :id-as-namespace false ; use the name as the namespace of the artifact
 :protected-area "PA"     ; protected area prefix
}
```

## Overarch CLI

Relevant options:
  -t, --template-dir DIRNAME          templates  Template directory
  -g, --generator-config FILE                    Generator configuration
  -G, --generator-dir DIRNAME         generated  Generator artifact directory
  -B, --generator-backup-dir DIRNAME  backup     Generator backup directory

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

