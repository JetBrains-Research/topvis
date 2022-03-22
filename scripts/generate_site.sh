#!/bin/bash
cd src/main/resources/topics
ls *.json > ../resources.txt
cd ../../../..
./gradlew clean build
cp build/distributions/index.html site/public/
cp build/distributions/resources.txt site/public/
cp build/distributions/topvis.js site/public/
cp build/distributions/topvis.js.map site/public/
cp -r build/distributions/topics site/public/