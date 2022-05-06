package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn
import io.lemonlabs.uri.Url
import example.actors.CrawlingController
import example.models._
import example.savers.CsvSaverConfig
import example.parsers.DataParser
import example.savers.DbSaverConfig
import doobie.util.transactor
import cats.effect.IO

object Main extends App {
  val url1 = "https://www.scala-lang.org"
  val url2 = "https://typelevel.org/cats/"

  val config = ScrapConfig[HeaderInfo](
    startUrls = Set(Url.parse(url1), Url.parse(url2)),
    maxDepth = 1,
    linkFilter = UrlUtils.isUrlMatchingBase(url1),
    documentParser = DataParser.parseH1Text,
    dataSaver = CsvSaverConfig[HeaderInfo]("example.csv", h => List(h.url, h.value))
  )
  implicit val system = ActorSystem(CrawlingController(config), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
