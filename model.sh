#!/bin/sh

java -jar tools/overarch.jar -w --no-render-format-subdirs -R generated -r plantuml -g templates/gencfg.edn -T ../overarch/templates
