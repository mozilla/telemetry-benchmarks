#!/bin/bash
set -euo pipefail

sbt assembly

jarPath=`ls target/scala-*/*.jar`

time spark-submit \
    --master yarn \
    --deploy-mode client \
    --class com.mozilla.telemetry.DefaultMainPings \
    "$jarPath"

time spark-submit \
    --master yarn \
    --deploy-mode client \
    --class com.mozilla.telemetry.PartitionMainPings \
    "$jarPath"

unset PYSPARK_DRIVER_PYTHON
time spark-submit \
    --master yarn \
    --deploy-mode client \
    DefaultMainPings.py

time spark-submit \
    --master yarn \
    --deploy-mode client \
    PartitionMainPings.py

