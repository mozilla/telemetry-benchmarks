# This program is a benchmark test for the default behavior of running Spark Jobs on a cluster. It uses the data 
# for Saved Session.

from datetime import datetime
from pyspark import SparkContext

from moztelemetry.dataset import Dataset

if __name__ == '__main__':
  startTime = datetime.now()
  sc = SparkContext("local", "ecole")

  print("\nTesting performance with Partition Algorithm and Saved Session data...")

  fs_rdd = Dataset.from_source("telemetry"
    ).where(
        docType='core',
        appUpdateChannel="saved_session"
    ).records(sc)

  sc.stop()
  endTime = datetime.now()
  # used to measure performance in terms of runtime of program
  print "Test ran in {} seconds\n".format(endTime.second-startTime.second)
