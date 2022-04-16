package example

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import akka.actor.typed.ActorRef
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

object PageScrapper {
  sealed trait Command
  final case class ScrapPage(
      ticket: UrlTicket,
      replyTo: ActorRef[CrawlingController.Command]
  ) extends Command

  final case class State(browser: Browser)

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    val browser = JsoupBrowser()
    ready(State(browser))
  }

  def ready(state: State): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case ScrapPage(ticket, replyTo) =>
          try {
            val doc = state.browser.get(ticket.url)
            val links = extractLinks(doc)
            replyTo ! CrawlingController.PageScrapped(ticket, links)
          } catch {
            case ex: Throwable =>
              context.log.error(s"Error scanning ${ticket.url}. $ex")
              replyTo ! CrawlingController.PageScrapped(ticket, Set())
          }
          Behaviors.same
      }
    }

  private def extractLinks(doc: Document): Set[String] = {
    val linkElements = doc >> elementList("a")
    linkElements.map(_ >?> attr("abs:href")).flatten.toSet
  }
}
