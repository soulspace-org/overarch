#!/bin/sh

# copy old docs to backup
rm -rf backup
mkdir backup
cp -r docs/* backup/

# read model and generate output with overarch
echo "reading model and generating with overarch"
java -jar tools/overarch.jar --no-render-format-subdirs -R generated -r plantuml -g templates/gencfg.edn

# generate diagrams with PlantUML
echo "generating diagrams with PlantUML"
find generated -type f -name "*.puml" | while read file; do
  target_dir=$(dirname "$file")
  mkdir -p "$target_dir"
  cat "$file" | java -Xmx1024m -jar tools/plantuml.jar -DPLANTUML_LIMIT_SIZE=32768 -tpng -pipe > "$target_dir/$(basename "$file" .puml).png"
done

# generate diagrams with GraphViz
echo "generating diagrams with GraphViz"
find generated -type f -name "*.dot" | while read file; do
  target_dir=$(dirname "$file")
  mkdir -p "$target_dir"
  dot "$file" -Tpng -o "$target_dir/$(basename "$file" .dot).png"
done

# publish to docs
echo "publishing the documentation"
rm -rf docs
mv generated docs
cp -r docs-in/* docs/
