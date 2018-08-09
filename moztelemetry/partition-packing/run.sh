#!/bin/bash
set -euo pipefail

sbt assembly

jarPath=`ls target/scala-*/*.jar`

time spark-submit \
    --master yarn \
    --deploy-mode client \
    --class com.mozilla.telemetry.PartitionPerformance \
    "$jarPath"
    --docType main --channel nightly --timestamp 20180301 --minPartitions 6
