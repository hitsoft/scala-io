package scalax.io

import sperformance.Keys.WarmupRuns
import sperformance.dsl._
import util.Random._
import Resource._
import Line.Terminators._
import java.io.{ ByteArrayOutputStream, ByteArrayInputStream }
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

object SmallSetsInMemoryInputTest extends AbstractInputTest {

  val MaxSize = 50
  val Inc = 25
  val From = 1
  val WarmUpRuns = 1000

  def newIn(size: Int, lines: Int = 2, term: String = NewLine.sep) = {
    val lineStrings = 1 to lines map { _ =>
      nextString(size).replaceAll("\n"," ")
    }
    val data = lineStrings mkString term
    () => new ByteArrayInputStream(data.getBytes)
  }

  def main(args: Array[String]) {
    Main.runTests(this)
  }

}