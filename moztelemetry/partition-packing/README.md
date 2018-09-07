# Partition Packing Performance Measures

This group of tests measures loading strategies for moztelemetry datasets. Partitions are made according to
two different strategies, default behavior and equally sized groups.

The link to the bug pertaining to this benchmark can be found here:
https://bugzilla.mozilla.org/show_bug.cgi?id=1479113

## Background:
We set up the tests in Scala and Python and compare the results of default
behavior and equally sized groups in each language. The tests measure how long
it takes to partition the data. Dataset, channel, the submission date can be adjusted in order to vary 
the number and size of the files. For the partition strategy specifically, we also vary the number of
partitions. It is possible to run the tests with the provided Bash script, as
well as manually on the command line.

## Experimental Setup:
1. Make sure to have made a Spark cluster on [ATMO](https://analysis.telemetry.mozilla.org/). If not, refer to the
documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html).
2. ssh into the cluster from the local terminal, using the instructions on ATMO for the cluster.
3. `cd /mnt/var/` from the home directory. Clone this repo into the directory. Spark jobs will run much faster from
this directory compared to the home directory.

### To run with Bash script:
Type `./run.sh` to execute the tests. The Bash script runs the Scala tests with `main` for docType, `nightly` for channel, `20180301` for timestamp. We set minPartitions to 6 for the equal partition test. When
running the test for default behavior, the test ran with 6 partitions. To make
results comparable across tests, we set the minPartitions to 6 for the equal
partitions test. Python tests are run with the same parameters, except that rather than specify 
minPartitions, we specify either the `greedy` strategy or `equal_size` for the partitioning behavior.

### To run manually:
Scala:
1. Determine which docType, channel, timestamp, and how many minPartitions are desired for the test. The
docType, channel, and timestamp options are used in the call to `Dataset` to
filter the data. The minPartitions option determines how the groupings will be
set.
2. Run the command `spark submit --master yarn --deploy-mode client --class com.mozilla.telemetry.PartitionPerformance <jar path> --docType <docType> --channel <channel>  --timestamp <timestamp> --minPartitions <minPartitions>`

Python:
1. Determine which docType, channel, timestamp, to use, as well as whether or
   not to specify `greedy` or `equal_size` for the group_by option. The
docType, channel, and timestamp options are used in the call to `Dataset` to
filter the data. The group_by option is set for the desired partition behavior.
2. Run the command `spark-submit --master yarn --deploy-mode client
   PartitionPerformance.py <docType> <channel> <timestamp> <group_by>`

## Results:
Scala with Partition Behavior

|Run   |Time in seconds (user)   |Time in seconds (system)   |Time in minutes (elapsed)   |
|------|--------------|----------------|-----------------|
|1     |38.22         |7.14            |5:49.33          |
|2     |38.84         |7.05            |5:50.12          |
|3     |38.02         |7.06            |6:01.70          |
|4     |37.80         |7.14            |5:36.19          |
|5     |36.96         |7.05            |5:49.27          |
|6     |37.17         |7.14            |5:48.73          |
|7     |38.43         |7.21            |5:35.11          |
|8     |37.70         |7.34            |5:33.13          |
|9     |46.66         |7.62            |5:40.67          |
|10    |37.47         |7.30            |5:38.99          |


Scala with Default Behavior

|Run   |Time in seconds (user)  |Time in seconds (system)   |Time in minutes (elapsed)    |
|------|-------------|----------------|------------------|
|1     |40.22        |8.02            |21:16.96          |
|2     |42.88        |7.91            |21:33.01          |
|3     |40.10        |7.85            |21:42.47          |
|4     |40.53        |7.84            |21:32.61          |
|5     |40.89        |7.77            |21:16.63          |
|6     |41.82        |8.11            |21:18.48          |
|7     |40.84        |7.72            |22:31.48          |
|8     |40.89        |7.93            |22:40.58          |
|9     |41.10        |7.74            |22:07.57          |
|10    |42.55        |8.07            |22:04.52          |

Python with Partition Behavior

|Run   |Time in seconds (user)  |Time in seconds (system)  |Time in minutes (elapsed)     |
|------|-------------|---------------|-------------------|
|1     |49.05        |16.00          |4:30.82            |
|2     |51.46        |16.72          |4:16.50            |
|3     |51.90        |15.74          |4:36.15            |
|4     |52.15        |16.94          |3:44.69            |
|5     |54.48        |17.18          |3:47.85            |
|6     |49.93        |16.28          |3:52.03            |
|7     |50.15        |16.23          |4:15.00            |
|8     |51.67        |16.81          |5:13.03            |
|9     |56.44        |16.75          |4:32.40            |
|10    |53.69        |15.90          |4:27.83            |

Python with Default Behavior

|Run   |Time in seconds (user)  |Time in seconds (system) |Time in minutes (elapsed)      |
|------|-------------|--------------|--------------------|
|1     |52.98        |16.45         |4:08.10             |
|2     |48.89        |16.50         |5:05.10             |
|3     |51.22        |15.83         |4:16.39             |
|4     |50.04        |16.15         |4:01.96             |
|5     |49.46        |15.86         |3:57.89             |
|6     |50.90        |16.00         |4:28.20             |
|7     |50.65        |16.10         |4:06.45             |
|8     |49.97        |15.89         |4:14.22             |
|9     |49.95        |15.03         |4:03.76             |
|10    |51.87        |16.66         |3:52.31             |


Aggregated Results for Elapsed Time

|Type of Performance|Mean   |Standard Deviation   |
|-------------------|-------|---------------------|
|Scala Partition    |5:44.32|26.88                |
|Scala Default      |21:48.42|92.79               |
|Python Partition   |4:19.63|80.43                |
|Python Default     |4:07.94|79.09                |


## Discussion

Adding algorithms for partitioning files into equal partitions showed
improvement in Scala, but not in Python. In Scala, the equal partitions
algorithm showed a significant improvement in elapsed time of the runtime of
the program. The default Scala method runs four times slower than the partition
method. The new method has a similar runtime as the Python algorithms. The data
gathered also has a much smaller standard deviation for partition behavior versus the
default behavior, showing that the algorithm will produce a more consistent runtime.

For the Python algorithm, the default algorithm and the equal partition
algorithm have similar runtimes. Upon examining how the default algorithm works,
this does make sense. The default algorithm in Python already partitions the
data equally. The standard deviations of the strategies are also similar,
showing relatively similar consistencies in runtime.

If we consider running these tests on larger datasets, for example,
production-sized datasets, we can probably conclude that the partition strategy
for Scala will save a signficant amount of time compared to the default
strategy. I ran the Scala default and partition strategies once each with a
larger dataset to get more information about my motivations. I ran the tests
with a full day of data, so there was no filter on channel with 10 nodes in the
cluster. The default strategy used 659 partitions and ran in about 30 minutes
and 15 seconds. The partition strategy, set with 659 partitions, ran in about
18 minutes and 45 seconds.

Further testing should be done to determine the runtime when more
than six nodes are needed for the amount of data, and also for when a very
large file will need to be run in a separate partition. Overall, these results
indicate strong motivations to switch to the partition strategy for Scala.
