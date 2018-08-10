#!/bin/bash

set -euo pipefail

jar_path=/tmp/jars
mkdir -p ${jar_path}

git clone https://github.com/acmiyaguchi/telemetry-batch-view.git
cd telemetry-batch-view

# fetch a patch to fix YAML and to speed up computation
wget https://gist.githubusercontent.com/acmiyaguchi/76f393ed8f4df532a0716948f2678698/raw/d6b8656b4955db873c3ba31e8175f35e0a288550/tbv-yaml.patch

# Build tbv with a given changeset. Apply a patch if it exists
function build_rev {
    rev=$1
    name=$2
    jar_path=$3
    patch=$4

    git stash
    if [[ -d target ]]; then
        rm -r target
    fi
    git checkout ${rev}
    if [[ ! -z ${patch} ]]; then
        git apply $patch
    fi
    sbt assembly
    cp target/scala-2.11/telemetry-batch-view-1.1.jar ${jar_path}/${name}.jar
}

build_rev bb1abf1cf631f5263ade0c2f4aab0b4ac4831dc3 "map" ${jar_path} "tbv-yaml.patch"
build_rev 6df421c98e309f2a0417a96c39401e6807f3e580 "jvalue" ${jar_path} "tbv-yaml.patch"

function bench {
    name=$1
    date=$2
    jar_name=$3
    options=$4

    echo "Running ${name} for ${date}"

    /usr/bin/time -o "logs/${name}_${date}.txt" \
    spark-submit \
        --master yarn \
        --deploy-mode client \
        --class com.mozilla.telemetry.views.MainSummaryView  \
        --conf "spark.driver.extraJavaOptions=${options}" \
        --conf "spark.executor.extraJavaOptions=${options}" \
        ${jar_path}/${jar_name}.jar \
        --from ${date} --to ${date} \
        --bucket telemetry-test-bucket \
        --channel nightly

    curl localhost:18080/api/v1/applications | echo "$(jq -r .[0].id) ${name}_${date}" >> logs/mapping.txt
}

base_opt="-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

if [[ ! -d logs ]]; then mkdir logs; fi

export date=20180801
bench "jvalue-g1gc" $date "jvalue"  "$base_opt -XX:+UseG1GC"
bench "jvalue-cms"  $date "jvalue"  "$base_opt -XX:+UseConcMarkSweepGC"

bench "map-g1gc"    $date "map"     "$base_opt -XX:+UseG1GC"
bench "map-cms"     $date "map"     "$base_opt -XX:+UseConcMarkSweepGC"

# read the GC timings
while read line
do
    app=`echo $line | cut -f1 -d' '`
    name=`echo $line | cut -f2 -d' '`
    filename="logs/gc_$name.txt"
    yarn logs -applicationId ${app} > ${filename}
    gzip ${filename}
done < logs/mapping.txt
