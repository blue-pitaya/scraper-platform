package example

import com.github.tototoshi.csv.CSVWriter

trait CsvSaver[A] {
  def parse(value: A): List[List[String]]
}

object CsvSaver {
  implicit val stringSaver = new CsvSaver[List[String]] {
    override def parse(value: List[String]): List[List[String]] = List(value)
  }
}

object CsvDataSaver {
  def appendData[A](data: A, filename: String)(implicit csvParser: CsvSaver[A]): Unit = {
    val writer = CSVWriter.open(filename, append = true)
    writer.writeAll(csvParser.parse(data))
    writer.close()
  }
}
