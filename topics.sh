#!/bin/bash
apt update
apt install python3-pip
apt install git
git clone git@github.com:salkaevruslan/sosed.git
cd sosed/
git checkout topics
git pull
pip3 install cython
pip3 install -r requirements.txt
python3 -m sosed.setup_tokenizer
python3 -m sosed.run_topics -i $1 -o out --force
cp sosed/out/topics.txt src/main/resources/topics.txt
cd ..
./gradlew clean build
cp build/distributions/index.html site/public/
cp build/distributions/topvis.js site/public/
cp build/distributions/topics.json site/public/
