#!/bin/bash
requirements=(pip3 python3 git)
for item in ${requirements[*]}
do
    if ! $item --version $>/dev/null; then
        echo "Please install $item"
        exit 1
    fi
done
pip3 install --upgrade pip3
mkdir -p temp
cd temp
echo -n > ./repos.txt
while read line; do
  echo $line
  uri="https://$USER:$2@git.jetbrains.team/${line}.git"
  echo "Cloning $(basename "$line" .git)"
  git clone --depth 1 "$uri"
  cd ${line##*/} && ls -la && cd ..
  touch repos.txt
  echo ../temp/$(basename "${line##*/}") >> ./repos.txt
  cat repos.txt
done <../"$1"
cd .. 
git submodule init
git submodule update
mkdir -p site/internal/topics
mkdir -p src/main/resources/topics_internal/
cd sosed/
mkdir -p out
cd out
rm -r *
cd ..
pip3 install cython
pip3 install -r requirements.txt
python3 -m sosed.setup_tokenizer
python3 -m sosed.run_topics -i "../temp/repos.txt" -o "out" --force --local
cd ..
cp sosed/out/topics.json src/main/resources/topics_internal/sosed.json
cd tfidf
pip3 install -U scikit-learn==1.0.1
python3 -m tfidf -i "../temp/repos.txt" -o "out" --local
cd ..
cp tfidf/out/topics.json src/main/resources/topics_internal/tfidf.json
./gradlew clean build
cp build/distributions/index.html site/internal/
cp build/distributions/topvis.js site/internal/
cp build/distributions/topvis.js.map site/internal/
cp -r build/distributions/topics_internal/* site/internal/topics
rm -rf ./temp
