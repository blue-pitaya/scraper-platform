package example.parsers

trait CsvParser[A] {
  def parse(value: A): List[List[String]]
}

object CsvParser {
  implicit val stringSaver = new CsvParser[List[String]] {
    override def parse(value: List[String]): List[List[String]] = List(value)
  }
}
