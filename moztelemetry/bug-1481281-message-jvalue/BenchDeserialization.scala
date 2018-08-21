package com.mozilla.benchmark.moztelemetry.bug1481281

import com.mozilla.telemetry.heka.{Message, File => HekaFile}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.rogach.scallop._
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

import java.io.{File, FileInputStream, PrintWriter}

object BenchDeserialization {
  private class Conf(args: Seq[String]) extends ScallopConf(args) {
    val path = opt[File](required = true)
    val method = choice(Seq("jvalue", "map"), required = true)
    val printSample = opt[File]()
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

    if (opts.printSample.isSupplied) {
      def print(line: FlatTree): String =
        line.map {
          case (k: String, v: String) => s"${k}: ${v}"
        }.mkString("\n")

      val pw = new PrintWriter(opts.printSample())
      pw.write(print(flattenTreeJValue(rdd).first()))
      pw.write("\n\n")
      pw.write(print(flattenTreeMap(rdd).first()))
      pw.close()
    }

    val count = opts.method() match {
      case "jvalue" => flattenTreeJValue(rdd).count()
      case "map" => flattenTreeMap(rdd).count()
      case _ => throw new IllegalArgumentException("No such method")
    }

    println(s"File contains ${count} lines")
  }

  private type FlatTree = Seq[(String, String)]

  private def prefixPath(prefix: String, key: String): String = {
    if (prefix.isEmpty) { key }
    else s"${prefix}.${key}"
  }

  def flatten(node: JValue, prefix: String = ""): FlatTree = {
    node match {
      case JObject(obj) =>
        obj
          .map { case (key: String, value: JValue) =>
            flatten(value, prefixPath(prefix, key))
          }
          .fold(Seq())(_ ++ _)
      case JString(s) => Seq((prefix, s))
      case _ => Seq()
    }
  }

  def flattenTreeJValue(rdd: RDD[Message]): RDD[FlatTree] = {
    rdd
      .map(_.toJValue.get)
      .map(flatten(_))
  }

  def flattenTreeMap(rdd: RDD[Message]): RDD[FlatTree] = {
    rdd
      .map(_.fieldsAsMap)
      .map(m =>
        m.map { case (k: String, v: Any) => flatten(parse(v.asInstanceOf[String]), k) }
          .fold(Seq())(_ ++ _)
      )
  }
}