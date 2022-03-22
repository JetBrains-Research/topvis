#!/bin/bash
cd tfidf
pip3 install -U scikit-learn==1.0.1
python3 -m tfidf -i "$1" -o "out" --local
cd ..
cp tfidf/out/topics.json src/main/resources/topics/tfidf.json