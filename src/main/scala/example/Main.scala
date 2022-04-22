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
  val url = "https://www.scala-lang.org"
  val postgresConfig = DbSaverConfig[HeaderInfo](
    transactor = transactor.Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      "jdbc:postgresql://192.168.0.10:5432/scraperdata", // connect URL (driver-specific)
      "scraper", // user
      "asdf" // password
    ),
    columnNames = List("value", "site")
  )
  val config = ScrapConfig[HeaderInfo](
    startUrl = Url.parse(url),
    maxDepth = 1,
    linkFilter = UrlUtils.isUrlMatchingBase(url),
    documentParser = DataParser.parseH1Text,
    dataSaver = postgresConfig
  )
  implicit val system = ActorSystem(CrawlingController(config), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
