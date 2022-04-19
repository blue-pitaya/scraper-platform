package example.parsers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

object DataParser {
  def parseH1Texts(document: Document): List[String] = {
    val h1Elements = document >> elementList("h1")
    h1Elements.map(e => e >> text("h1"))
  }
}
