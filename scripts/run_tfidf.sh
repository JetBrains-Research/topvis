#!/bin/bash
cd tfidf
python3 -m pip install --user virtualenv
python3 -m venv ./venv
source ./venv/bin/activate
which python3
pip3 install --upgrade pip
pip3 install -U scikit-learn==1.0.2
python3 -m tfidf -i "$1" -o "out" --local
deactivate
rm -rf ./venv
cd ..
cp tfidf/out/topics.json src/main/resources/topics/tfidf.json