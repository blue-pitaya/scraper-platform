package example

import com.github.tototoshi.csv.CSVWriter

trait CsvSaver[A] {
  def toRow(value: A): List[String]
}

object CsvSaver {
  implicit val stringSaver = new CsvSaver[String] {
    override def toRow(value: String): List[String] = List(value)
  }
}

object CsvDataSaver {
  def appendData[A](data: A, filename: String)(implicit csvParser: CsvSaver[A]): Unit = {
    val writer = CSVWriter.open(filename, append = true)
    writer.writeRow(csvParser.toRow(data))
    writer.close()
  }
}
