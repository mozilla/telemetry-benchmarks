package com.mozilla.benchmark.moztelemetry.bug1481281

import com.mozilla.telemetry.heka.{Header, Message, RichMessage}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.rogach.scallop._

import java.io.{File, FileOutputStream, BufferedOutputStream, ByteArrayOutputStream}
import java.util.UUID.randomUUID
import scala.util.Random

object MessageGenerator {
  val keyLength = 4;
  val valueLength = 4;

  private class Conf(args: Seq[String]) extends ScallopConf(args) {
    val numMessages = opt[Int](required = true)
    val path = opt[File](required = true)
    val width = opt[Int](default = Some(5))
    val depth = opt[Int](default = Some(3))
    val branchFactor = opt[Int](default = Some(5))
    val isExtracted = opt[Boolean](default = Some(true))
    val partitionSize = opt[Long](descr = "Partition size in MB", default = Some(256))
    validateFileDoesNotExist(path)
    verify()
  }

  def main(args: Array[String]) {
    val opts = new Conf(args)
    opts.path().mkdir()

    // Generate an infinite stream of data, see this blog post for use of constructing a stream through #::
    // https://bradcollins.com/2015/08/29/scala-saturday-the-stream-take-method/
    def generate = generateMessage(opts.width(), opts.depth(), opts.branchFactor(), opts.isExtracted())
    def records: Stream[Message] = generate #:: records

    // How many messages do we write to a single partition
    val sample = generate.toJValue
    val sampleSize = compact(sample).length
    val messagesPerPartitions = (opts.partitionSize() * Math.pow(10, 6) / sampleSize).toInt

    println(s"Writing ${opts.numMessages()} messages, ${messagesPerPartitions} per partition")
    // Keep writing messages as long as there are more than fit into a single partition
    var curPartition = 0
    var toWrite: Int = opts.numMessages()
    while (toWrite > 0) {
      val numMessages = if (toWrite < messagesPerPartitions) { toWrite } else { messagesPerPartitions }
      val file = new File(opts.path(), s"part_${curPartition}.heka")
      writeBuffer(records.take(numMessages).toSeq, file.getPath())
      curPartition += 1
      toWrite -= numMessages
    }
  }

  /** Generate a Message for testing garbage collection between `toJValue` and `fieldsAsMap`.
    *
    * @param width
    * @param depth
    * @param branchFactor
    * @param isPayload
    * @return
    */
  def generateMessage(width: Int, depth: Int = 2, branchFactor: Int = 2, isExtracted: Boolean = true): Message = {
    val fieldsMap =
      if (isExtracted) {
        val base = Seq(("submission", "{}"))
        val extracted = Seq.fill(width)(
            s"submission.${Random.alphanumeric.take(keyLength).mkString}",
            compact(generateNodes(depth - 1, branchFactor)))
        (base ++ extracted).toMap
      }
      else {
        Map("submission" -> compact(generateNodes(depth, branchFactor)))
      }

    RichMessage(
      uuid = randomUUID().toString,
      fieldsMap = fieldsMap,
      payload = None
    )
  }

  /**
    * Recursively generate a JSON AST with parameters influencing the tree fan-out.
    *
    * @param depth
    * @param branchFactor
    * @return
    */
  def generateNodes(depth: Int, branchFactor: Int): JValue = {
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
          generateNodes(depth - 1, branchFactor)
        ).toMap
      }

    render(map)
  }

  /** Serialize a message to an array of bytes that can be written to disk.
    *
    * See the moztelemetry testing harness for more details.
    * https://github.com/mozilla/moztelemetry/blob/8ce975c38db9a336f342007605d6a053fe16a631/src/test/scala/com/mozilla/telemetry/heka/Resources.scala#L50-L70
    *
    * @param message A java object representing the heka-protobuf file
    * @return An array of bytes representing the serialized framed message
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

  def writeBuffer(records: Seq[Message], path: String): Unit = {
    println(s"Writing ${path} to disk")
    val bos = new BufferedOutputStream(new FileOutputStream(path))
    for (record <- records) {
      bos.write(framedMessage(record))
    }
    bos.close()
  }
}
