package example.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Routers
import scala.collection.immutable.Queue
import scala.collection.SortedSet
import akka.actor.typed.ActorRef
import akka.actor.typed.SupervisorStrategy
import scala.collection.immutable.TreeSet
import akka.actor.typed.scaladsl.ActorContext
import example.models._
import example.parsers._
import example.savers.CsvSaverConfig
import example.savers.DbSaverConfig
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import akka.stream.scaladsl.Sink

object CrawlingController {
  sealed trait Command
  final case class PageScrapped(ticket: UrlTicket, urls: Set[String]) extends Command
  final case object Abort extends Command

  final case class State[A](
      config: ScrapConfig[A],
      urlsToVisit: Queue[UrlTicket],
      visitedUrls: SortedSet[String],
      workerPoolRouter: ActorRef[TicketSource.Command]
  )

  def sendingRequest[A](state: State[A], ctx: ActorContext[Command]): Behavior[Command] = {
    state.urlsToVisit.dequeueOption match {
      case Some((ticket, queue)) =>
        state.workerPoolRouter ! TicketSource.Execute(
          ticket
        ) //send ticket to scrap
        //val g = ScraperGraph.graph(ticket, state.config.documentParser, ctx.self)
        val nextState = state.copy(urlsToVisit = queue)
        processing(nextState)
      case None =>
        ctx.log.info("Finished crawling.")
        Behaviors.stopped
    }
  }

  def processing[A](state: State[A]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case PageScrapped(ticket, urls) =>
        val nextVisitedUrls = state.visitedUrls.union(Set(ticket.url))
        val nextUrlsToVisit =
          if (ticket.depth < state.config.maxDepth) {
            val legalUrls = urls.filter(state.config.linkFilter)
            state.urlsToVisit ++ (legalUrls -- nextVisitedUrls).map(UrlTicket(_, ticket.depth + 1))
          } else
            state.urlsToVisit
        val nextState = state.copy(urlsToVisit = nextUrlsToVisit)
        ctx.log.info(s"Scrapped ${ticket.url}. ${nextUrlsToVisit.size} left.")
        sendingRequest(nextState, ctx)
      case Abort =>
        ctx.log.info("Abort requested. Stopping.")
        Behaviors.stopped
    }
  }

  def apply[A](config: ScrapConfig[A]): Behavior[Command] =
    Behaviors.setup { ctx =>
      val dataSaver = config.dataSaver match {
        case CsvSaverConfig(filename, dataToValueList) =>
          val csvDataSaver = ctx.spawn(CsvDataSaver(filename), "csv-data-saver")
          val saver: A => Unit = value => {
            val parsedData = CsvParser.stringSaver.parse(dataToValueList(value))
            csvDataSaver ! CsvDataSaver.SaveData(parsedData)
          }
          saver
        case DbSaverConfig(transactor, columnNames) =>
          val saver: A => Unit = v => {}
          saver
      }

      val workerPool = Routers.pool(poolSize = 4) {
        Behaviors
          .supervise(
            StreamedPageScraper(
              config.documentParser,
              dataSaver
            )
          )
          .onFailure[Exception](SupervisorStrategy.resume) //TODO: log
      }

      implicit val browser = JsoupBrowser()
      implicit val system = ctx.system
      val scrapSink = ScraperGraph.graph(config.documentParser, ctx.self)
      val ticketActor = TicketSource()
        .collect { case TicketSource.Execute(ticket) =>
          ticket
        }
        .to(scrapSink)
        .run()

      //val router = ctx.spawn(workerPool, "page-scraper-pool")
      val state = State(
        config = config,
        urlsToVisit = Queue(UrlTicket(config.startUrl.toString(), 0)),
        visitedUrls = TreeSet(),
        workerPoolRouter = ticketActor
      )
      sendingRequest(state, ctx)
    }
}
