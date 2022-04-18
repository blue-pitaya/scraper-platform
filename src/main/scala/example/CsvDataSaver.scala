package example

import com.github.tototoshi.csv.CSVWriter

trait CsvSaver[A] {
  def parse(value: A): List[String]
}

object CsvSaver {
  implicit val stringSaver = new CsvSaver[String] {
    override def parse(value: String): List[String] = List(value)
  }
}

object CsvDataSaver {
  def appendData[A](data: A, filename: String)(implicit csvParser: CsvSaver[A]): Unit = {
    val writer = CSVWriter.open(filename, append = true)
    writer.writeRow(csvParser.parse(data))
    writer.close()
  }
}
