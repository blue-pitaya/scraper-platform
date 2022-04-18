package example

import com.github.tototoshi.csv.CSVWriter

trait CsvParser[A] {
  def parse(value: A): List[List[String]]
}

object CsvParser {
  implicit val stringSaver = new CsvParser[List[String]] {
    override def parse(value: List[String]): List[List[String]] = List(value)
  }
}

object CsvDataSaver {
  def appendData[A](data: A, filename: String)(implicit csvParser: CsvParser[A]): Unit = {
    val writer = CSVWriter.open(filename, append = true)
    writer.writeAll(csvParser.parse(data))
    writer.close()
  }
}
