package example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object PageScrapper {
  sealed trait Command
  final case class ScrapPage(
      ticket: UrlTicket,
      replyTo: ActorRef[CrawlingController.Command]
  ) extends Command

  def apply(): Behavior[Command] = {
    val browser = JsoupBrowser()
    Behaviors.receive { (context, message) =>
      message match {
        case ScrapPage(ticket, replyTo) =>
          val document = Try(browser.get(ticket.url))
          document match {
            case Failure(exception) =>
              context.log.error(s"Error scanning ${ticket.url}. $exception")
              Behaviors.same
            case Success(document) =>
              val links = LinkParser.parse(document)
              val h1Texts = DataParser.parseH1Texts(document)
              h1Texts.foreach(h1 => CsvDataSaver.appendData(h1, "example.csv"))
              replyTo ! CrawlingController.PageScrapped(ticket, links.toSet)
              Behaviors.same
          }
      }
    }
  }
}
