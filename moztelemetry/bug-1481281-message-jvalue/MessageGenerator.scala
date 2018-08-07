package com.mozilla.benchmark.moztelemetry.bug1481281

import com.mozilla.telemetry.heka.{Header, Message, RichMessage}
import java.io.{ByteArrayOutputStream, BufferedOutputStream, FileOutputStream}
import org.rogach.scallop._
import scala.util.Random
import java.util.UUID.randomUUID
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object MessageGenerator {
  val keyLength = 16;
  val valueLength = 64;

  /** Generate a Message for testing garbage collection between `toJValue` and `fieldsAsMap`.
    *
    * @param width
    * @param depth
    * @param branchFactor
    * @param isPayload
    * @return
    */
  def generateMessage(width: Int, depth: Int = 2, branchFactor: Int = 2, isPayload: Boolean = false): Message = {
    val fieldsMap =
      if (isPayload) { Map[String, Any]() }
      else {
        Seq.fill(width)(
          Random.alphanumeric.take(keyLength).mkString,
          compact(generateNodes(depth-1, branchFactor))
        ).toMap
      }

    val payload =
      if (isPayload) { Some(compact(generateNodes(depth, branchFactor))) }
      else { None }

    RichMessage(
      uuid = randomUUID().toString,
      fieldsMap = fieldsMap,
      payload = payload
    )
  }

  /**
    * Recursively generate a JSON AST with parameters influencing the tree fan-out.
    *
    * @param depth
    * @param branchFactor
    * @return
    */
  def generateNodes(depth: Int, branchFactor :Int): JValue = {
    val map =
      if (depth == 0) {
        // leaf nodes
        Seq.fill(branchFactor)(
          Random.alphanumeric.take(keyLength).mkString,
          render(Random.alphanumeric.take(valueLength).mkString)
        ).toMap
      }
      else {
        // branch
        Seq.fill(branchFactor)(
          Random.alphanumeric.take(keyLength).mkString,
          generateNodes(depth-1, branchFactor)
        ).toMap
      }

    render(map)
  }

  /** Serialize a message to an array of bytes that can be written to disk.
    *
    * See the moztelemetry testing harness for more details.
    * https://github.com/mozilla/moztelemetry/blob/8ce975c38db9a336f342007605d6a053fe16a631/src/test/scala/com/mozilla/telemetry/heka/Resources.scala#L50-L70
    *
    * @param message  A java object representing the heka-protobuf file
    * @return         An array of bytes representing the serialized framed message
    */
  def framedMessage(message: Message): Array[Byte] = {
    val baos = new ByteArrayOutputStream()

    val header = Header(message.toByteArray.size)
    val bHeader = header.toByteArray
    val bMessage = message.toByteArray

    // see https://hekad.readthedocs.org/en/latest/message/index.html
    baos.write(0x1E)
    baos.write(bHeader.size)
    baos.write(bHeader, 0, bHeader.size)
    baos.write(0x1F)
    baos.write(bMessage, 0, bMessage.size)
    baos.toByteArray
  }
  // TODO: https://stackoverflow.com/questions/17488534/create-a-file-from-a-bytearrayoutputstream
}


object Main {
  private class Conf(args: Seq[String]) extends ScallopConf(args) {
    val numMessages = opt[Int](required = true)
    val path = opt[String](required = true)  // TODO: use the Path type and verify it's an empty folder
    val width = opt[Int](default = Some(5))
    val depth = opt[Int](default = Some(3))
    val branchFactor = opt[Int](default = Some(5))
    val isPayload = opt[Boolean](default = Some(false))
    val partitionSize = opt[Int](default = Some(1 << 31))
    verify()
  }

  def writeBuffer(records: Seq[Array[Byte]], path: String): Unit = {
    val buf = records.foldLeft(Array[Byte]())(_ ++ _)
    val bos = new BufferedOutputStream(new FileOutputStream(path))
    bos.write(buf)
    bos.close()
    println(s"Wrote ${path} to disk")
  }

  def main(args: Array[String]) {
    val opts = new Conf(args)

    def generate = MessageGenerator.generateMessage(opts.width(), opts.depth(), opts.branchFactor(), opts.isPayload())

    val sample = generate.toJValue
    println("Printing a sample generated ping")
    println(pretty(sample))

    // TODO: write many partitions
    val records = Seq.fill(opts.numMessages())(generate).map(MessageGenerator.framedMessage(_));
    // TODO: add the partition number to the filename
    writeBuffer(records, opts.path())
  }

}