#!/bin/sh
for i in {1..10}
do
    ./runcorrector.sh uniform data/dev_set/queries.txt extra data/dev_set/gold.txt  $i  > uniform_result.txt
    diff -u uniform_result.txt data/dev_set/gold.txt > uniform_gold_diff.txt
    echo "$i of 10 has wrong count:"
    cat uniform_gold_diff.txt | grep ^+ | wc -l
done