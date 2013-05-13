#!/bin/sh
cat /tmp/item-property.csv > data.csv
cat /tmp/item-property-value.csv >> data.csv
cp data.csv myrrix/
cat data.csv | cut -d',' -f1 | sort | uniq > item-list
cp item-list myrrix/
java -Xmx2500m -XX:NewRatio=12 -cp myrrix-serving-0.11.jar:dist/wikidata-entity-suggester.jar net.myrrix.web.Runner --rescorerProviderClass entitysuggester.rescorer.EntityRescorerProvider --localInputDir myrrix --port 8080
