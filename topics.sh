#!/bin/bash
./scripts/check_requirements.sh
mkdir -p temp
cd temp
rm -r *
echo -n > ./repos.txt
../scripts/clone_repositories.sh "$1"
cd ..
git submodule init
git submodule update
mkdir -p site/public/topics
mkdir -p src/main/resources/topics/
./scripts/run_sosed.sh
./scripts/run_tfidf.sh
./scripts/generate_site.sh
rm -rf ./temp