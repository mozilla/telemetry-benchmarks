/*
 * This program is a benchmark test for the default behavior of running Spark  * Jobs on a cluster. It uses 6 Paritions and the data for Main Pings.
 */

package com.mozilla.telemetry

import com.mozilla.telemetry.heka.{Dataset}
import org.apache.spark.sql.SparkSession

object DefaultMainPings {
  def main(args: Array[String]) {
    val startTime = System.currentTimeMillis()
    val spark = SparkSession.builder.appName("ecole").getOrCreate()
    implicit val sc = spark.sparkContext

    println()
    println("Testing performance with Default Behavior and Main Pings data...")

    val rdd = {
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
        .where("submissionDate") {
          case "20180301" => true
        }
        .records()
    }
    println(rdd.count())
    sc.stop()
    val endTime = System.currentTimeMillis()

    //used to measure peformance in terms of runtime of program
    printf("Test ran in %d seconds", (endTime - startTime)/1000)
    println()
  }
}
