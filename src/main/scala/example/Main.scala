package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn
import io.lemonlabs.uri.Url
import example.actors.CrawlingController
import example.models._
import example.savers.CsvSaverConfig
import example.parsers.DataParser

object Main extends App {
  val url = "https://www.scala-lang.org"
  val config = ScrapConfig[HeaderInfo](
    startUrl = Url.parse(url),
    maxDepth = 1,
    linkFilter = UrlUtils.isUrlMatchingBase(url),
    documentParser = DataParser.parseH1Text,
    dataSaver = CsvSaverConfig[HeaderInfo]("example.csv", info => List(info.value, info.url))
  )
  implicit val system = ActorSystem(CrawlingController(config), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
