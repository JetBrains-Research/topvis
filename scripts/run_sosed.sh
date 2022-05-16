#!/bin/bash
cd sosed/
mkdir -p out
cd out
rm -r *
cd ..
python3 -m pip install --user virtualenv
python3 -m venv ./venv
source ./venv/bin/activate
which python3
pip3 install --upgrade pip
pip3 install cython
pip3 install -r requirements.txt
python3 -m sosed.setup_tokenizer
if [ -n "$2" ]
then
  cd ..
  cp "$2/sosed/data/clusters.npy" sosed/data/clusters.npy
  cp "$2/sosed/data/tokens.txt" sosed/data/tokens.txt
  cd sosed/
fi
python3 -m sosed.run_topics -i "$1" -o "out" --force --local
deactivate
rm -rf ./venv
cd ..
cp sosed/out/topics.json src/main/resources/topics/sosed.json