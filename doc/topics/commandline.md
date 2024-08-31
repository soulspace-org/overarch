# Command Line Interface

```
Overarch CLI
   
   Reads your model and view specifications and renders or exports
   into the specified formats.

   For more information see https://github.com/soulspace-org/overarch

Usage: java -jar overarch.jar [options].

Options:

  -m, --model-dir PATH                   models     Models directory or path
  -r, --render-format FORMAT                        Render format (all, graphviz, markdown, plantuml)
  -R, --render-dir DIRNAME               export     Render directory
      --[no-]render-format-subdirs       true       Use subdir per render format
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

## CLI Examples
To render all views for all models, use
```
> java -jar ./target/overarch.jar -r all
```
or
```
> java -jar ./target/overarch.jar -r all --debug
```

To render all views for all models with a directory watch to trigger rerendering on changes, use
```
> java -jar ./target/overarch.jar -r all -w --debug
```

To export the models to JSON, use
```
> java -jar ./target/overarch.jar -x json
```

To query the model for all containers, use
```
> java -jar ./target/overarch.jar -s '{:el :container}'
```
or
```
> java -jar ./target/overarch.jar -S '{:el :container}'
```

