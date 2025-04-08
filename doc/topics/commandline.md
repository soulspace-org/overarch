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
For the CLI examples a model repository with the following directory layout is expected:
* `models` containes the EDN files for models and views 
* `templates` contains the templates for the artifact generation
* `tools` contains the overarch.jar (and the plantuml.jar)

Renders all artifacts to the `generated` directory using the generation config in `templates/gencfg.edn`, this is the commandline used in the `publish.sh` scripts in the example models. 
```
java -jar tools/overarch.jar --no-render-format-subdirs -R generated -r plantuml -g templates/gencfg.edn
```

To render all views for all models, use
```
> java -jar tools/overarch.jar -r all
```

or

```
> java -jar tools/overarch.jar -r all --debug
```

To render all views for all models with a directory watch to trigger rerendering on changes, use
```
> java -jar tools/overarch.jar -r all -w --debug
```

To export the models to JSON, use
```
> java -jar tools/overarch.jar -x json
```

To query the model for all containers, use
```
> java -jar tools/overarch.jar -s '{:el :container}'
```
or
```
> java -jar tools/overarch.jar -S '{:el :container}'
```

## Start Scripts
The [My Bank Example](https://github.com/soulspace-org/my-bank-model) contains
a setup with the Overarch and PlantUML jar files in the tools directory and
two simple bash scripts for the development and publication of overarch models.

