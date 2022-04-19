package example.parsers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

object LinkParser {
  def parse(doc: Document): List[String] = {
    val linkElements = doc >> elementList("a")
    linkElements.map(_ >?> attr("abs:href")).flatten
  }
}
