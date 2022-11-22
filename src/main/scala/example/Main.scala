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
import example.actors.CsvDataSaver

object Main extends App {
  val urls = List() //sug pages of based url page to scrap
  val baseUrl = "" //url of site to scrap
  val testUrl = ""
  val csvSaver =
    CsvSaverConfig[Hotel](
      "hotels.csv",
      (hotel: Hotel) =>
        List(
          hotel.name,
          hotel.stars.toString(),
          hotel.description,
          hotel.location.country,
          hotel.location.region.getOrElse("NULL"),
          hotel.location.city.getOrElse("NULL"),
          hotel.basePrice.toString()
        )
    )
  val finalUrls = urls
    .map(Url.parse(_))
    .toSet
  val testUrls = Set(Url.parse(testUrl))
  val config = ScrapConfig[Hotel](
    startUrls = testUrls,
    maxDepth = 0,
    linkFilter = u =>
      UrlUtils.isUrlMatchingBase(baseUrl)(u) && (u.contains("kierunki") || u.contains("wczasy")),
    documentParser = (_, document) => DataParser.parseHotel(document),
    dataSaver = csvSaver
  )
  implicit val system = ActorSystem(CrawlingController(config), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
