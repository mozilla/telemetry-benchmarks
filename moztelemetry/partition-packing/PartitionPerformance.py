# This program is a benchmark test for running Spark Jobs on a cluster with different partition 
# strategies.
import argparse
from pyspark.sql import SparkSession

from moztelemetry.dataset import Dataset

def submit(docType, channel, timestamp, group_by):
    spark = SparkSession.builder.getOrCreate()
    sc = spark.sparkContext
    rdd = Dataset.from_source("telemetry"
        ).where(
            docType=docType,
            appUpdateChannel=channel,
            submissionDate=timestamp
        ).records(sc, group_by=group_by)
    print("Number of rows of RDD: {}".format(rdd.count()))
    print("Number of partitions used: {}".format(rdd.getNumPartitions()))
    sc.stop()

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("docType", type=str)
    parser.add_argument("channel", type=str)
    parser.add_argument("timestamp", type=str)
    parser.add_argument("group_by", type=str)
    args = parser.parse_args()
    submit(docType=args.docType, channel=args.channel, timestamp=args.timestamp, group_by=args.group_by)
