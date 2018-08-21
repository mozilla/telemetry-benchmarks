#!/bin/bash

set -eou pipefail

sbt assembly

if [[ ! -d "data-bench-baseline" ]]; then
    sbt "runMain com.mozilla.benchmark.moztelemetry.bug1481281.MessageGenerator \
        --num-messages 100000 \
        --width 5 \
        --depth 3 \
        --branch-factor 5 \
        --path data-bench-baseline"
fi

if [[ ! -d "data-bench-wide" ]]; then
    sbt "runMain com.mozilla.benchmark.moztelemetry.bug1481281.MessageGenerator \
        --num-messages 1700 \
        --width 10 \
        --depth 4 \
        --branch-factor 10 \
        --path data-bench-wide"
fi

function bench {
    name=$1
    path=$2
    method=$3

    /usr/bin/time -o "timing-${name}.log" \
    spark-submit \
        --master local[2] \
        --driver-memory 2g \
        --class com.mozilla.benchmark.moztelemetry.bug1481281.BenchDeserialization \
        target/scala-2.11/message-jvalue-assembly-1.0.jar \
        --path $path \
        --method $method
}

if [[ ! -d logs ]]; then mkdir logs; fi

bench "baseline-jvalue" "data-bench-baseline"   "jvalue"
bench "baseline-map"    "data-bench-baseline"   "map"
bench "wide-jvalue"     "data-bench-wide"       "jvalue"
bench "wide-map"        "data-bench-wide"       "map"