/*
 * This program is a benchmark test for the default behavior of running Spark Jobs on a cluster. It uses the data for Saved Sessions.
 */

package com.mozilla.telemetry

import com.mozilla.telemetry.heka.{Dataset}
import org.apache.spark.sql.SparkSession

object DefaultSavedSession {
  def main(args: Array[String]) {
    val startTime = System.currentTimeMillis()
    val spark = SparkSession.builder.appName("ecole").getOrCreate()
    implicit val sc = spark.sparkContext

    println()
    println("Testing performance with Default Behavior and Saved Session data...")

    val fs_rdd = {
      Dataset("telemetry")
        .where("sourceName") {
          case "telemetry" => true
        }
        .where("docType") {
          case "core" => true
        }
        .where("appUpdateChannel") {
          case "nightly" => true
        }
        .records()
    }

    fs_rdd.first().fields
    sc.stop()
    val endTime = System.currentTimeMillis()

    //used to measure peformance in terms of runtime of program
    printf("Test ran in %d seconds", (endTime - startTime)/1000)
    println()
  }
}