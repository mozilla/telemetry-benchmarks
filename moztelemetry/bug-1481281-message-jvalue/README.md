# Message deserialization performance

[Bug 1481281](https://bugzilla.mozilla.org/show_bug.cgi?id=1481281) - Benchmark `Dataset.toJValue` against `Dataset.fieldsAsMap`

## Background

Spark runs on the Java Virtual Machine and inherits many of it's runtime characteristics. One issue that should be
on the back of our minds is the management of objects when using the RDD api. Since we generally process data on the
order of 10^8 rows on some of our larger jobs, we should take the GC into consideration.

The main use-case of the RDD api is working with semi-unstructured data in the form of JSON and Heka data. 
In `MainSummaryView`, binary data containing json is transformed and flattened into a format that can be converted
into a DataFrame. This provides a cleaned up view of the data that can written to parquet and processed across our
toolchain. One of the steps in the process involves deserializing the data into a datastructure we can process.

The original way of processing this data map the data to a field and parse each part individually. As part of unifying
the extracted and unextracted documents into a single logical data-structure, it was found that performance 
of data processing increased by a significant margin.

One likely source of this performance increase is the decrease in the number of objects created during the function
runtime. A micro-benchmark of this job showed that while the the performance of constructing a single JValue structure
was on average 30x slower, it created 6x less objects. 

## Experimental Setup

```bash
sbt "runMain com.mozilla.benchmark.moztelemetry.bug1481281.MessageGenerator \
    --num-messages 100000 \
    --width 5 \
    --depth 3 \
    --branch-factor 5 \
    --path test-extracted-w5d3b5"

sbt "runMain com.mozilla.benchmark.moztelemetry.bug1481281.MessageGenerator \
    --num-messages 500 \
    --width 10 \
    --depth 4 \
    --branch-factor 10 \
    --path test-extracted-w10d4b10"
```

```bash
spark-submit \
    --master local[*] \
    --deploy-mode client \
    --class com.mozilla.benchmark.moztelemetry.bug1481281.BenchDeserialization \
    target/scala-2.11/message-jvalue-assembly-1.0.jar \
    --path test-data/
```

1. Generate data with a particular shape
2. Run the benchmark on a Spark instance, collect the garbage collection timings


## Results
## Discussion
## Resources

https://databricks.com/blog/2015/05/28/tuning-java-garbage-collection-for-spark-applications.html
https://spark.apache.org/docs/latest/tuning.html