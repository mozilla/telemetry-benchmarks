# Partition Packing Performance Measures

This group of tests measures loading strategies for moztelemetry datasets. Partitions are made according to
two different strategies, default behavior and equally sized groups.

## Background:
We set up the tests in Scala and Python and compare the results of default
behavior and equally sized groups in each language. The tests measure how long
it takes to partition the data. Dataset, channel, the submission date are all
variables. For the partition strategy specifically, we also vary the number of
partitions. It is possible to run the tests with the provided Bash script, as
well as manually on the command line.

## Experimental Setup:
1. Make sure to have made a Spark cluster on [ATMO](https://analysis.telemetry.mozilla.org/). If not, refer to the
documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html).
2. ssh into the cluster from the local terminal, using the instructions on ATMO for the cluster.
3. `cd /mnt/var/` from the home directory. Clone this repo into the directory. Spark jobs will run much faster from
this directory compared to the home directory.

### To run with Bash script:
Type `./run.sh` to execute the tests. The Bash script runs the Scala tests with `core` for docType, `nightly`
 for channel, `20180301` fortimestamp, and 6 or 0 for minPartitions. Python tests are run with the same
parameters, except that rather than specify minPartitions, we specify either the `greedy` strategy or
`equal_size` for the partitioning behavior.

### To run manually:
Scala:
1. Determine which docType, channel, timestamp, and how many minPartitions are desired for the test. The
docType, channel, and timestamp options are used in the call to `Dataset` to
filter the data. The minPartitions option determines how the groupings will be
set.
2. Run the command `spark submit --master yarn --deploy-mode client --class com.mozilla.telemetry.PartitionPerformance <jar path> --docType <docType> --channel <channel>  --timestamp <timestamp> --minPartitions <minParitiions>`

Python:
1. Determine which docType, channel, timestamp, to use, as well as whether or
   not to specify `greedy` or `equal_size` for the group_by option. The
docType, channel, and timestamp options are used in the call to `Dataset` to
filter the data. The group_by option is set for the desired partition behavior.
2. Run the command `spark-submit --master yarn --deploy-mide client
   PartitionPerformance.py <docType> <channel> <timestamp> <group_by>`

## Results:
Scala with Partition Behavior

|Run   |Time (user)   |Time (system)   |Time (elapsed)   |
|------|--------------|----------------|-----------------|
|1     |33.68         |6.64            |2:29.79          |
|2     |31.62         |6.94            |2:24.39          |
|3     |32.38         |7.11            |2:17.99          |
|4     |31.82         |6.67            |2:31.20          |
|5     |32.46         |6.81            |2:19.35          |
|6     |32.96         |7.00            |2:14.57          |
|7     |32.91         |7.13            |2:14.97          |
|8     |32.58         |6.94            |1:59.02          |
|9     |32.64         |7.04            |2:20.65          |
|10    |32.97         |6.78            |2:17.22          |


Scala with Default Behavior

|Run   |Time (user)  |Time (system)   |Time (elapsed)    |
|------|-------------|----------------|------------------|
|1     |34.94        |7.61            |9:59.61           |
|2     |34.89        |7.16            |11:05.31          |
|3     |33.68        |7.52            |11:39.53          |
|4     |34.16        |7.51            |11:08.54          |
|5     |33.54        |7.61            |10:55.03          |
|6     |34.16        |7.19            |11:17.62          |
|7     |33.91        |7.09            |10:53.06          |
|8     |34.70        |7.31            |11:15.83          |
|9     |42.83        |7.76            |11:22.32          |
|10    |33.48        |7.29            |11:22.54          |


Python with Partition Behavior

|Run   |Time (user)  |Time (system)  |Time (elapsed)     |
|------|-------------|---------------|-------------------|
|1     |53.94        |16.42          |2:42.39            |
|2     |55.83        |17.26          |2:48.20            |
|3     |55.39        |16.45          |2:49.61            |
|4     |53.07        |16.75          |2:50.76            |
|5     |55.30        |16.62          |2:30.83            |
|6     |54.10        |17.30          |2:27.92            |
|7     |54.77        |16.52          |2:49.59            |
|8     |54.22        |16.76          |2:28.22            |
|9     |57.31        |16.69          |2:24.58            |
|10    |53.61        |17.00          |2:33.12            |


Python with Default Behavior

|Run   |Time (user)  |Time (system) |Time (elapsed)      |
|------|-------------|--------------|--------------------|
|1     |55.70        |16.62         |2:30.40             |
|2     |53.52        |16.84         |2:33.54             |
|3     |54.42        |15.83         |2:23.14             |
|4     |52.52        |17.74         |2:24.42             |
|5     |50.89        |15.96         |2:25.24             |
|6     |55.97        |17.49         |2:30.82             |
|7     |54.62        |16.44         |2:27.41             |
|8     |52.41        |16.79         |2:25.46             |
|9     |54.83        |15.97         |2:26.36             |
|10    |50.99        |14.86         |2:28.06             |


Aggregated Results for Elapsed Time

|Type of Performance|Mean   |Standard Deviation   |
|-------------------|-------|---------------------|
|Scala Partition    |2:18.99|8.58                 |
|Scala Default      |11:05.94|25.19               |
|Python Partition   |2:38.52|10.03                |
|Python Default     |2:27.49|3.51                 |


## Discussion

Adding algorithms for partitioning files into equal partitions showed
improvement in Scala, but not in Python. In Scala, the equal partitions
algorithm showed a significant improvement in elapsed time of the runtime of
the program. The data gathered also has a much smaller standard deviation for
partition behavior versus the default behavior, showing that the algorithm will
produce a more consistent runtime.

For the Python algorithm, the default algorithm has a better runtime than the
new equal partition algorithm. Upon examining how the default algorithm works,
this does make sense. The default algorithm in Python already partitions the
data equally. The standard deviation of the default algorithm is also
smaller, which means that the default algorithm can also be considered more
reliable in terms of consistency of runtime.
