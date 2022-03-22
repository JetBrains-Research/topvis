#!/bin/bash
./scripts/check_requirements.sh
mkdir -p temp
cd temp
rm -r *
../scripts/clone_repositories.sh "$1"
cd ..
git submodule init
git submodule update
mkdir -p site/public/topics
mkdir -p src/main/resources/topics/
./scripts/run_sosed.sh "../temp/repos.txt"
./scripts/run_tfidf.sh "../temp/repos.txt"
./scripts/generate_site.sh
rm -rf ./temp