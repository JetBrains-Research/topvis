#!/bin/bash
requirements=(pip3 python3 git)
for item in ${requirements[*]}
do
    if ! $item --version $>/dev/null; then
        echo "Please install $item"
        exit 1
    fi
done
git submodule init
git submodule update
#git clone git@github.com:salkaevruslan/sosed.git
cd sosed/
#git checkout topics
pip3 install cython
pip3 install -r requirements.txt
python3 -m sosed.setup_tokenizer
python3 -m sosed.run_topics -i ../"$1" -o out --force
cd ..
cp sosed/out/topics.json src/main/resources/topics.json
./gradlew clean build
cp build/distributions/index.html site/public/
cp build/distributions/topvis.js site/public/
cp build/distributions/topics.json site/public/
