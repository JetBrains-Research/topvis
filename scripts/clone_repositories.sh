#!/bin/bash
echo -n > ./repos.txt
while read -r line; do
  git clone --quiet --depth 1 "$line"
  echo "Cloning $(basename "$line" .git)"
  touch repos.txt
  echo ../temp/$(basename "$line" .git) >> ./repos.txt
done <../"$1"