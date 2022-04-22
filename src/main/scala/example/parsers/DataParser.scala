package example.parsers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import example.models.UrlTicket
import example.models.HeaderInfo

object DataParser {
  def parseH1Text(ticket: UrlTicket, document: Document): Option[HeaderInfo] = {
    val h1Elements = document >> elementList("h1")
    h1Elements.map(e => e >> text("h1")).collectFirst { case x =>
      HeaderInfo(x, ticket.url)
    }
  }
}
