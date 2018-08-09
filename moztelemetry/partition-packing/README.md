# Partition Packing Performance Measures

This group of tests measures loading strategies for moztelemetry datasets. Partitions are made according to two different strategies: default behavior and equally sized groups.

## How to Run:

1. Make sure to have made a Spark cluster on [ATMO](https://analysis.telemetry.mozilla.org/). If not, refer to the documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html). 

2. ssh into the cluster from the local terminal, using the instructions on ATMO for the cluster.

3. `cd /mnt/var/` from the home directory. Clone this repo into the directory. Spark jobs will run much faster from this directory compared to the home directory.

### To run with Bash script:
Type `./run.sh` to execute the Scala test. The Bash script runs the test with `core` for docType, `nightly` for channel, '20180301` for timestamp, and `6` for minPartitions.

### To run mannually:
1. Determine which docType, channel, timestamp, and how many minPartitions are
   desired for the test.
2. Run the command `spark submit --master yarn --deploy-mode client --class com.mozilla.telemetry.PartitionPerformance <jar path> --docType <docType> --channel <channel>  --timestamp <timestamp> --minPartitions <minParitiions>`

## Note:
Python tests still need to be modified. Soon both Python and Scala tests will
demonstrate performance based on running the tests multiple times so that a
mean and variance can be measured between performance of the partition
strategies for default behavior and equally sized partitions.
