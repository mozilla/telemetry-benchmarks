#!/bin/bash
set -euo pipefail

sbt assembly

jarPath=`ls target/scala-*/*.jar`

echo "Performance of equal partitions for Scala: " >> ~/scala_performance.txt
for run in {1..10}
do
    /usr/bin/time -o ~/scala_performance.txt -a spark-submit \
        --master yarn \
        --deploy-mode client \
        --class com.mozilla.telemetry.PartitionPerformance \
        "$jarPath" \
        --docType main --channel nightly --timestamp 20180301 --minPartitions 6
done
echo "Performance of default behavior for Scala: " >> ~/scala_performance.txt
for run in {1..10}
do
    /usr/bin/time -o ~/scala_performance.txt -a spark-submit \
        --master yarn \
        --deploy-mode client \
        --class com.mozilla.celemetry.PartitionPerformance \
        "$jarPath" \
        --docType main --channel nightly --timestamp 20180301
done
echo "Performance of equal partitions for Python: " >> ~/python_performance.txt
for run in {1..10}
do
    /usr/bin/time -o ~/python_performance.txt -a spark-submit \
        --master yarn \
        --deploy-mode client \
        PartitionPerformance.py core nightly 20180301 equal_size
done
echo "Performance of default behavior for Python: " >> ~/python_performance.txt
for run in {1..10}
do
    /usr/bin/time -o ~/python_performance.txt -a spark-submit \
        --master yarn \
        --deploy-mode client \
        PertitionPerformance.py core nightly 20180301 greedy
done
