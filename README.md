# Benchmarks

Benchmarks will be used to test performance of Telemetry tools with measurable, experimental outcomes to help determine improvements in the future. The repository contains tests in both Scala and Python. Results are provided with each group of tests in order to demonstrate expected outcomes. Instructions are also provided in order to replicate the results.

## How to Run:

1. Make sure to have made a Spark cluster on [ATO](https://analysis.telemetry.mozilla.org/). If not, refer to the documentation [here](https://docs.telemetry.mozilla.org/tools/spark.html). 

2. ssh into the cluster from the local terminal, using the instructions on ATO for the cluster.

3. cd into /mnt/var/ from the home directory. Clone this repo into the directory. Spark jobs will run much faster from this directory compared to the home directory.

4. type `./run.sh` to execute the tests

## Contribute:

To include benchmark tests, create a separate folder for each benchmark being tested, include a README describing the tests, analysis, and sample information about expected outcomes of the tests. Upload the tests as well. The goal is to make the results as reproducible as possible.
