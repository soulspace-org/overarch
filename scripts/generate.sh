#!/bin/sh

# read model and generate output with overarch
java -jar overarch.jar --no-render-format-subdirs -R generated -r plantuml -g templates/gencfg.edn

# generate diagrams with PlantUML
find generated -type f -name "*.puml" | while read file; do
  target_dir=$(dirname "$file")
  mkdir -p "$target_dir"
  cat "$file" | java -Xmx1024m -jar plantuml.jar -DPLANTUML_LIMIT_SIZE=32768 -tpng -pipe > "$target_dir/$(basename "$file" .puml).png"
done

# generate diagrams with GraphViz dot
find generated -type f -name "*.dot" | while read file; do
  target_dir=$(dirname "$file")
  mkdir -p "$target_dir"
  dot "$file" -Tpng -o "$target_dir/$(basename "$file" .dot).png"
done

# copy results?
