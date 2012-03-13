/**
 * Processing LongTraversables using the Processor API
 */
object LongTraversableProcessing {
  /**
   * Parse a binary file
   * Consider a file that contains data with the following organization (the | are for visual representation only):
   * {{{
   *   100 byte header|1byte row header|row data
   * }}}
   *
   * the 100 byte header might have information like number of rows in file and file author
   * each row would start with a single byte that declares how big the row is.
   * the row data is a UTF-8 encoded string
   *
   * If one would like to iterate through each record (row), the implementation could be as follows (using the file API):
   */
  def readBinaryFile {
    import scalax.io.{Resource,Codec,LongTraversable}

    // drop the header because we are not interested in it now
    val bytes = Resource.fromFile("somefile").bytes.drop(100)
    val rowTransformer = for {
        processor <- bytes.processor
        // repeat the following process until all bytes are consumed
        _ <- processor.repeatUntilEmpty()
      // this block is called as long as the data remains
      // get one byte.  This is the row header (and indicates the amount of row data)
        rowLength <- processor.next
        // read the row data
        rowData <- processor.take(rowLength.toInt)
      } yield {
      // Convert the rows data to a string
      new String(rowData.toArray, Codec.UTF8.charSet)
    }
    
    // rowTranformer is designed to be used to define the structure of a file or other data
    // it does not actually process the file.
    // At this point the file has not been opened yet
    val rowTraversable:LongTraversable[String] = rowTransformer.traversable[String]
    
    // Since LongTraversable's are lazy, the file still has not been opened
    // only the actual calling of foreach (next line) will trigger the file read.
    rowTraversable.foreach(println)
  }
  
  /**
   * Combine multiple LongTraversable objects into one by combining them through several
   * transformers.
   * 
   * Consider a hypothetical csv like format where the attributes are split between
   * two files.  The first file has 3 columns. 
   * |title|id|
   * Each Id must be unique in this file
   * 
   * the second file also has 2 columns
   * |id|data|
   * the second file can have multiple rows with the same id but they must be adjacent to the other rows with the same
   * ids.
   * 
   * This example combines the two files into a single LongTraversable[Record]  
   */
  def parseMultipleFiles {
    import scalax.io.{Resource,Codec,LongTraversable}
    
    case class Record(id:Int, title:String, attributes:Iterable[String])

    val recordTransformer = for{
        titles <- Resource.fromFile("file.titles.csv").lines().processor
        atts <- Resource.fromFile("file.atts.csv").lines().processor
        _ <- titles.repeatUntilEmpty()
        titleLine <- titles.next
        // assuming "," is separator
        Array(title,id) = titleLine.split(",")
        attributes <- atts.takeWhile(_.split(",").head == id)
      } yield {
        Record(id.toInt, title, attributes.toSeq)
      }
      
    val records: LongTraversable[Record] = recordTransformer.traversable
    
    // now do something with the records.  remember the files have not
    // yet been opened and won't be until the LongTraversable is used
  }

  /**
   * Demonstrate how to define custom error handling.
   * <p>
   * All processors have an onError method that allow custom handling of errors during processing.
   * </p>
   */
  def errorHandling {
    import scalax.io.{Resource,LongTraversable}
    import scalax.io.processing.Processor

    val bytes = Resource.fromFile("somefile").bytes

    val return1OnError:PartialFunction[Throwable,Option[Byte]] = {
      case _ => Some(1.toByte)
    }
    val process:Processor[Char] = for {
      processor <- bytes.processor
        // On error handles an error raised during the call of next
        // it does not catch and handle errors from the yield or
        // or any processors later in the for-comprehension
      byte <- processor.next onError return1OnError
    } yield {
      byte.toChar
    }

    process.acquireAndGet{ byte =>
      // do something with byte
    }
  }
}