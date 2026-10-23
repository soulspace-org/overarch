# Command Line Interface

```
Overarch CLI
   
   Reads your model and view specifications and renders or exports
   into the specified formats.

   For more information see https://github.com/soulspace-org/overarch

Usage: java -jar overarch.jar [options].

Options:

  -m, --model-dir PATH                   models     Models directory or path
  -r, --render-format FORMAT                        Deprecated, has no effect. Use templates (-g) to generate artifacts
  -R, --render-dir DIRNAME               export     Render directory (deprecated with -r)
      --[no-]render-format-subdirs       true       Use subdir per render format (deprecated with -r)
  -x, --export-format FORMAT                        Export format (json, structurizr)
  -X, --export-dir DIRNAME               export     Export directory
  -w, --watch                            false      Watch model dir for changes and trigger action
  -s, --select-elements CRITERIA                    Select and print model elements by criteria
  -S, --select-references CRITERIA                  Select model elements by criteria and print as references
      --select-views CRITERIA                       Select and print views by criteria
      --select-view-references CRITERIA             Select views by criteria and print as references
  -T, --template-dir DIRNAME             templates  Template directory
  -g, --generation-config FILE                      Generation configuration
  -G, --generation-dir DIRNAME           generated  Generation artifact directory
  -B, --backup-dir DIRNAME               backup     Generation backup directory
      --scope NAMESPACE                             Sets the internal scope by namespace prefix
      --[no-]model-warnings              true       Returns warnings for the loaded model
      --[no-]model-info                  false      Returns infos for the loaded model
      --plantuml-list-sprites            false      Lists the loaded PlantUML sprites
  -h, --help                                        Print help
      --debug                            false      Print debug messages
 ```

Model warnings are enabled by default. They report model build problems and
unresolved references in relations and views; use `--no-model-warnings` to
disable them. `--model-info` prints counts of nodes, relations, views and
elements by type or namespace, plus external and synthetic element counts.

## CLI Examples
By default the CLI reads models from `models/` and templates from `templates/`.
Use `-m` and `-T` to choose different directories.

Generate artifacts, including view diagrams configured in
`templates/gencfg.edn`, into the default `generated/` directory:
```
java -jar overarch.jar -g templates/gencfg.edn
```

Use `--debug` for additional diagnostic output, or `-w` to rerun the action
when model files change:

```
java -jar overarch.jar -g templates/gencfg.edn --debug
```

The `-r/--render-format` option is deprecated and has no effect. Configure
diagram generation through templates and `-g/--generation-config`.

To export the models to JSON, use
```
java -jar overarch.jar -x json
```

To query the model for all containers, use
```
java -jar overarch.jar -s '{:el :container}'
```
or
```
java -jar overarch.jar -S '{:el :container}'
```
Use `-s` to print selected elements and `-S` to print them as references.
Transitive selection is also available by passing criteria with a `:start`
key, as described in [Transitive Model Element Selection](selection-criteria.md#transitive-model-element-selection).

