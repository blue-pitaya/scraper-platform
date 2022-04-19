package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn
import io.lemonlabs.uri.Url
import example.models.CrawlConfig
import example.actors.CrawlingController

object Main extends App {
  val config = CrawlConfig.fromUrl("https://www.scala-lang.org", 1, "example.csv")
  implicit val system = ActorSystem(CrawlingController(config), "scraper-system")

  StdIn.readLine()

  system ! CrawlingController.Abort
}
