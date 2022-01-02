#!/bin/bash
requirements=(pip3 python3 git)
for item in ${requirements[*]}
do
    if ! $item --version $>/dev/null; then
        echo "Please install $item"
        exit 1
    fi
done
mkdir -p temp
cd temp
echo -n > ./repos.txt
while read line; do
  git clone --quiet --depth 1 "$line"
  echo "Cloning $(basename "$line" .git)"
  touch repos.txt
  echo ../temp/$(basename "$line" .git) >> ./repos.txt
done <../"$1"
cd ..
git submodule init
git submodule update
mkdir -p site/public/topics
mkdir -p src/main/resources/topics/
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
cp sosed/out/topics.json src/main/resources/topics/sosed.json
cd tfidf
pip3 install -U scikit-learn==1.0.1
python3 -m tfidf -i "../temp/repos.txt" -o "out" --local
cd ..
cp tfidf/out/topics.json src/main/resources/topics/tfidf.json
cd src/main/resources/topics
ls *.json > ../resources.txt
cd ../../../..
./gradlew clean build
cp build/distributions/index.html site/public/
cp build/distributions/topvis.js site/public/
cp build/distributions/topvis.js.map site/public/
cp -r build/distributions/topics site/public/
rm -rf ./temp