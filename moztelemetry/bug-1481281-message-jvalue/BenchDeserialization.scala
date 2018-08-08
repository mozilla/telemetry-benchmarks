package com.mozilla.benchmark.moztelemetry.bug1481281

import com.mozilla.telemetry.heka.{Message, File => HekaFile}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.rogach.scallop._
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

import java.io.{File, FileInputStream}

object BenchDeserialization {
  private class Conf(args: Seq[String]) extends ScallopConf(args) {
    val path = opt[File](required = true)
    validateFileIsDirectory(path)
    verify()
  }

  def main(args: Array[String]) {
    val opts = new Conf(args)
    val files = opts.path().listFiles.filter(_.getPath.endsWith(".heka"))

    val spark = SparkSession.builder().getOrCreate()
    val sc = spark.sparkContext

    val rdd: RDD[Message] = spark.sparkContext
      .parallelize(files)
      .map(new FileInputStream(_))
      .flatMap(HekaFile.parse(_))
  }

  private type FlatTree = Seq[(String, String)]

  // TODO: implement a tree traversal that generates all paths to leaves
  def flattenTreeJValue(rdd: RDD[Message]): RDD[FlatTree] = {
    rdd.map(_.toJValue.get)
  }

  def flattenTreeMap(rdd: RDD[Message]): RDD[FlatTree] = {
    rdd.map(_.fieldsAsMap)
  }
}