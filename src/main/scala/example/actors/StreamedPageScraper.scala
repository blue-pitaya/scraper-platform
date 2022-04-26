package example.actors

import example.models.UrlTicket
import akka.actor.typed.ActorRef
import net.ruippeixotog.scalascraper.model.Document
import akka.actor.typed.Behavior
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import akka.actor.typed.scaladsl.Behaviors
import scala.util.{Failure, Success, Try}
import example.parsers.LinkParser

object StreamedPageScraper {
  sealed trait Command
  final case class ScrapPage(
      ticket: UrlTicket,
      replyTo: ActorRef[CrawlingController.Command]
  ) extends Command

  def apply[A](
      parseData: (UrlTicket, Document) => Option[A],
      saveData: A => Unit
  ): Behavior[Command] = {
    implicit val browser = JsoupBrowser()
    Behaviors
      .receive { (context, message) =>
        message match {
          case ScrapPage(ticket, replyTo) =>
            implicit val system = context.system
            //ScraperGraph.graph(ticket, parseData, replyTo).run()
            Behaviors.same
        }
      }
  }

}
