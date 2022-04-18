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

  def apply[A](parseData: Document => A, saveData: A => Unit): Behavior[Command] = {
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
              val data = parseData(document) //TODO: possible exception
              saveData(data) //TODO: possible exception
              replyTo ! CrawlingController.PageScrapped(ticket, links.toSet)
              Behaviors.same
          }
      }
    }
  }
}
