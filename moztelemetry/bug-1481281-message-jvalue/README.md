# Message deserialization performance

[Bug 1481281](https://bugzilla.mozilla.org/show_bug.cgi?id=1481281) - Benchmark `Dataset.toJValue` against `Dataset.fieldsAsMap`

## Background

Spark runs on the Java Virtual Machine and inherits some it's runtime characteristics. The object lifecycle is opaque
in the DataFrame API, but becomes more of a concern when using the lower level RDD API. Tuning the garbage collector
and being mindful of the underlying VM can improve performance.

Our general use-case of the RDD API is working with semi-unstructured data represented as JSON and Heka. 
In `MainSummaryView`, binary data containing JSON is transformed and flattened into a format that can be converted
into a DataFrame. This conversion of semi-unstructured to structured data is done by serializing data using an in-memory
object representation.

In [Bug 1419116](https://bugzilla.mozilla.org/show_bug.cgi?id=1419116), the `Message.toJValue` method was added for
abstracting access to the full JSON document from the underlying storage. This is similar to how the python implementation
of the library works. The micro-benchmarks showed that there was overhead in creating this view. When the method was 
used in `MainSummaryView`, the overall time decreased by a factor of 1.5-2.0x. 
[Bug 1436850](https://bugzilla.mozilla.org/show_bug.cgi?id=1436850) 

The cause of the improvement is hard to pin down, but indicators from the Spark UI point to the decreased load on the
garbage collector. In this benchmark, we perform another set of micro-benchmarks and re-run the `MainSummaryView` jobs
using alternative garbage collection methods. 

## Experimental Setup

This benchmark is split into two separate parts. The first is a micro-benchmark that is meant to be run on Spark through
the moztelemetry API. JSON data in the shape of a tree is generated in `MessageGenerator`. The number of nodes in the
resulting Heka data-set can be controlled as parameters in the generator. The data is then fed into a Spark application
that performs tree unnesting to create a flat data structure. The goal of this benchmark is to see how Spark reacts with
data larger than the heap.

1. Generate two datasets, a baseline with 5^3 leaf nodes and a wide set with 10^4 leaf nodes, stored as Heka protobuf
2. Run the tree flattening application using both the `toJValue` and `fieldsAsMap` methods on the `Message` object
3. Collect user, system, and wallclock timings




## Results
## Discussion
## Resources

https://databricks.com/blog/2015/05/28/tuning-java-garbage-collection-for-spark-applications.html
https://spark.apache.org/docs/latest/tuning.html