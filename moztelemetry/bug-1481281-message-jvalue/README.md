# Message deserialization performance

[Bug 1481281](https://bugzilla.mozilla.org/show_bug.cgi?id=1481281) - Benchmark `Dataset.toJValue` against `Dataset.fieldsAsMap`

## Background

Spark runs on the Java Virtual Machine and inherits some it's runtime characteristics.
The object lifecycle is opaque in the DataFrame API, but becomes more of a concern when using the lower level RDD API.
Tuning the garbage collector and being mindful of the underlying VM can improve performance.

Our general use-case of the RDD API is working with semi-unstructured data represented as JSON and Heka.
In `MainSummaryView`, binary data containing JSON is transformed and flattened into a format that can be converted into a DataFrame.
This conversion of semi-unstructured to structured data is done by serializing data using an in-memory object representation.

In [Bug 1419116](https://bugzilla.mozilla.org/show_bug.cgi?id=1419116), the `Message.toJValue` method was added for
abstracting access to the full JSON document from the underlying storage.
This is similar to how the python implementation of the library works.
The micro-benchmarks showed that there was overhead in creating this view.
When the method was used in `MainSummaryView`, the overall time decreased by a factor of 1.5-2.0x.
[Bug 1436850](https://bugzilla.mozilla.org/show_bug.cgi?id=1436850) is introduces the changes and some initial benchmarking.

The cause of the improvement is hard to pin down, but indicators from the Spark UI point to the decreased load on the garbage collector.
In this benchmark, we perform another set of micro-benchmarks and re-run the `MainSummaryView` jobs using alternative garbage collection methods.

## Experimental Setup

This benchmark is split into two separate parts.
The first is a micro-benchmark that is meant to be run on Spark through the moztelemetry API.
JSON data in the shape of a tree is generated in `MessageGenerator`.
The number of nodes in the resulting Heka data-set can be controlled as parameters in the generator.
The data is then fed into a Spark application that performs tree unnesting to create a flat data structure.
The goal of this benchmark is to see how Spark reacts with data larger than the heap.

1. Generate two datasets, a baseline with 5^3 leaf nodes and a wide set with 10^4 leaf nodes, stored as Heka protobuf
2. Run the tree flattening application using both the `toJValue` and `fieldsAsMap` methods on the `Message` object
3. Collect timings

Refer to the deserialization benchmark in this repo.

```bash
$ ./run_deserialization.sh
```

In the second benchmark, we rerun `MainSummaryView` using two revisions in the source tree.
The first revision is a snapshot of the job prior to the use of `Messsage.toJValue`.
The second revision uses `JValues` from the start.

1. Build the assembly jars of the two revisions, with a patch to enable equally-sized-partition packing
2. Run `MainSummaryView` on a single day of Firefox nightly
    - Repeat for each jar using the Concurrent Mark Sweep and G1 methods
3. Collect timings
4. Request the GC logs from yarn

Refer to the main summary benchmark.

```
# SSH into an EMR spark cluster and change to a working directory
$ ./run_main_summary.sh
```

## Results

### Tree-flattening microbenchmark

The datasets in the results are generated with the following parameters.
These can be adjusted to increase the total number of objects that are created when deserializing.

dataset | width | depth | branch factor | total leaf nodes
---|---|---|---|---
baseline | 5 | 3 | 5 | 125
wide | 10 | 4 | 10 | 10,000

The baseline and wide datasets have differing number of rows.
Within each dataset, the map and jvalue methods can be compared relative to each other.

dataset  | method | userland (sec) | kernel (sec) | elapsed (mm:ss)
---------|--------|----------|--------|---
baseline | map    | 237.67   | 2.31   | 2:04.48
baseline | jvalue | 282.86   | 3.63   | 2:33.80
wide     | map    | 672.44   | 2.89   | 5:47.62
wide     | jvalue | 1367.46  | 4.54   | 12:12.43

The map method has less overhead in this context.
In the baseline, the difference is fairly small.
When the number of parsed objects is increased to 10,000 nodes per row, the timing difference is more apparent.

### MainSummaryView benchmark

The job is run twice with each revision.
The JValue method was introduced in [this PR](https://github.com/mozilla/telemetry-batch-view/pull/377).

method | gc | userland (sec) | kernel (sec) | elapsed (mm:ss)
---|---|---|---|---
jvalue | cms | 71.43 | 8.92 | 8:38.81
jvalue | g1 | 68.33 | 9.15  |6:17.00
map | cms | 71.94 | 8.89 | 14:56.84
map | g1 | 70.51 | 9.36 | 16:43.99

The JValue method is consistently faster than the original `fieldsAsMap` methodology.
This timing corresponds to the overall GC time measured in the Spark UI dashboard.
The following graphs show heap usage over the course of the jobs.

#### toJValue with Concurrent Mark Sweep
![jvalue-cms](images/jvalue-cms.png)

The CMS garbage collector shows a steady increase in the heap usage over the course of the application.
Heap usage reaches 2000 MB after 5 minutes.

#### fieldsAsMap with Concurrent Mark Sweep
![map-cms](images/map-cms.png)

The heap usage follows the same pattern as the CMS with the `toJValue` method.
However, the heap reaches 2000 MB after 14 minutes.

#### toJValue with G1
![jvalue-g1](images/jvalue-g1.png)

G1 has a small periodic sawtooth behavior that reaches a peak of 2000 MB.
This method performs better than CMS on the same method.


#### fieldsAsMap with G1
![map-cms](images/map-g1.png)

G1 with the Map method has a smaller sawtooth period than the the JValue method.
The garbage collector more frequently clears the heap of unused objects.

## Discussion

There are no clear performance wins for the `Message.toJValue` method in the microbenchmarks.
It's unclear what conditions need to be met in order to produce the behavior seen in `MainSummaryView`.
The `JValue` method is more concise than it's `Map[String, Any]` equivalent and has the nice property of logically reconstructing
the ping.

For `MainSummaryView`, the logs collected from Yarn seem to point to object management and improved garbage collection for increased job efficiency.
The GC hours for the JValue method is smaller than the Map method (1.8h vs 4h), but the GC logs for CMS looks equivalent.
The more frequent sawtoothing of G1 in `fieldsAsMap` is interesting because it implies that the heap is being filled more frequently.
The steady increase in heap usage in the CMS graph is likely the overhead required to keep track of the RDD.

Tuning the garbage collector for batch data jobs is overkill.
However, understanding that the garbage collector can significantly impact the performance of Spark jobs is something that should be kept in mind.

## Resources

* [Tuning Java Garbage Collection for Apache Spark Applications](https://databricks.com/blog/2015/05/28/tuning-java-garbage-collection-for-spark-applications.html)
* [Tuning Spark](https://spark.apache.org/docs/latest/tuning.html)
* [Getting Started with the G1 Garbage Collector](http://www.oracle.com/technetwork/tutorials/tutorials-1876574.html)
* [GCEasy - Analysis of GC logs](http://gceasy.io/index.jsp)
