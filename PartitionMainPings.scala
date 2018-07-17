/*
 * This program is a benchmark test for the partition behavior of running Spark  * Jobs on a cluster. It uses 6 Paritions and the data for Main Pings.
 */

package com.mozilla.telemetry

import com.mozilla.telemetry.heka.{Dataset}
import org.apache.spark.sql.SparkSession

object PartitionMainPings {
  def main(args: Array[String]) {
    val startTime = System.currentTimeMillis()
    val spark = SparkSession.builder.appName("ecole").getOrCreate()
    implicit val sc = spark.sparkContext
    // specifcation for partition behavior
    val numPartitions = Some(6)

    println()
    println("Testing performance with Partition Algorithm and Main Pings data...")

    val fs_rdd = {
      Dataset("telemetry")
        .where("sourceName") {
          case "telemetry" => true
        }
        .where("docType") {
          case "main" => true
        }
        .where("appUpdateChannel") {
          case "nightly" => true
        }
        .records(numPartitions)
    }

    fs_rdd.first().fields
    sc.stop()
    val endTime = System.currentTimeMillis()

    //used to measure peformance in terms of runtime of program
    printf("Test ran in %d seconds", (endTime - startTime)/1000)
    println()
  }
}