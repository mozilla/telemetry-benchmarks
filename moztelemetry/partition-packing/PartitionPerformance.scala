/*
 * This program is a benchmark test for running Spark Jobs on a cluster with different partition strategies.
 */

package com.mozilla.telemetry

import com.mozilla.telemetry.heka.{Dataset}
import org.apache.spark.sql.SparkSession
import org.rogach.scallop.ScallopConf

object PartitionPerformance {
  def submitOnce(docType: String, channel: String, timestamp:String, minPartitions: Option[Int]) {
    val startTime = System.currentTimeMillis()
    val spark = SparkSession.builder.appName("ecole").getOrCreate()
    implicit val sc = spark.sparkContext

    val rdd = {
      Dataset("telemetry")
        .where("sourceName") {
          case "telemetry" => true
        }
        .where("docType") {
          case dt => dt == docType
        }
        .where("appUpdateChannel") {
          case ch => ch == channel
        }
        .where("submissionDate") {
          case t => t == timestamp
        }
        .records(minPartitions=minPartitions)
    }
    println(rdd.count())
    println(rdd.getNumPartitions)
    sc.stop()
  }

  def main(args: Array[String]) {
    val opts = new ScallopConf(args) {
      val docType = opt[String]("docType", required=true)
      val channel = opt[String]("channel", required=true)
      val timestamp = opt[String]("timestamp", required=true)
      val minPartitions = opt[Int]("minPartitions", default=None)
      verify()
    }
    submitOnce(opts.docType(), opts.channel(), opts.timestamp(), opts.minPartitions.toOption)
  }
}
