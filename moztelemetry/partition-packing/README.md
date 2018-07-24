# Partition Packing Performance Measures

This group of tests measures how long it takes for different processes within Moztelemetry datasets. Partitions are made according to two different strategies: default behavior and equally sized groups.

## How to Run:

1. Make sure to have made a Spark cluster on [ATO](https://analysis.telemetry.mozilla.org/). If not, refer to the documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html). 

2. ssh into the cluster from the local terminal, using the instructions on ATO for the cluster.

3. cd into /mnt/var/ from the home directory. Clone this repo into the directory. Spark jobs will run much faster from this directory compared to the home directory.

4. type `./run.sh` to execute the tests
