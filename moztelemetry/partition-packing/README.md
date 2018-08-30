# Partition Packing Performance Measures

This group of tests measures loading strategies for moztelemetry datasets. Partitions are made according to two different strategies, default behavior and equally sized groups.

## Background:
We set up the tests in Scala and Python and compare the results of default
behavior and equally sized groups in each language. The tests measure how long
it takes to partition the data. Dataset, channel, the submission date are all
variables. For the partition strategy specifically, we also vary the number of
partitions. It is possible to run the tests with the provided Bash script, as
well as manually on the command line.

## Experimental Setup:

1. Make sure to have made a Spark cluster on [ATMO](https://analysis.telemetry.mozilla.org/). If not, refer to the documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html). 

2. ssh into the cluster from the local terminal, using the instructions on ATMO for the cluster.

3. `cd /mnt/var/` from the home directory. Clone this repo into the directory. Spark jobs will run much faster from this directory compared to the home directory.

### To run with Bash script:
Type `./run.sh` to execute the tests. The Bash script runs the Scala tests with `core` for docType, `nightly` for channel, `20180301` fortimestamp, and 6 or 0 for minPartitions. Python tests are run with the same parameters, except that rather than specify minPartitions, we specifiy either the `greedy` strategy or `equal_size` for the partitioning behavior.

### To run mannually:
Scala:
1. Determine which docType, channel, timestamp, and how many minPartitions are
   desired for the test.
2. Run the command `spark submit --master yarn --deploy-mode client --class com.mozilla.telemetry.PartitionPerformance <jar path> --docType <docType> --channel <channel>  --timestamp <timestamp> --minPartitions <minParitiions>`

Python:
1. Determine which docType, channel, timestamp, to use, as well as whether or
   not to specify `greedy` or `equal_size` for the group-by option
2. Run the command `spark-submit --master yarn --deploy-mide client
   PartitionPerformance.py <docType> <channel> <timestamp> <group_by>`
## Results:

