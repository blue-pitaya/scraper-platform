package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn
import io.lemonlabs.uri.Url
import example.actors.CrawlingController
import example.models.ScrapConfig

object Main extends App {
  val url = "https://www.scala-lang.org"
  case class HeaderInfo(value: String, url: String)
  val config2 = ScrapConfig[HeaderInfo](
    startUrl = Url.parse(url),
    maxDepth = 1,
    linkFilter = UrlUtils.isUrlMatchingBase(url),
    documentParser = (t, d) => None, //TODO:
    dataSaver = d => {} //TODO:
  )
  implicit val system = ActorSystem(CrawlingController(config2), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
