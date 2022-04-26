package example.actors

import akka.stream.scaladsl._
import example.models.UrlTicket
import akka.stream.ClosedShape
import scala.util.{Try, Failure}
import net.ruippeixotog.scalascraper.browser.Browser
import akka.actor.typed.scaladsl.Behaviors
import scala.util.Success
import example.parsers.LinkParser
import net.ruippeixotog.scalascraper.model.Document
import akka.actor.typed.ActorRef

object ScraperGraph {
  def graph[A](
      ticket: UrlTicket,
      mParseData: (UrlTicket, Document) => Option[A],
      replyTo: ActorRef[CrawlingController.Command]
  )(implicit
      browser: Browser
  ) =
    RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val source = Source.single(ticket)

      val download = Flow.fromFunction((t: UrlTicket) => browser.get(t.url)) //TODO: exception
      val bcast = b.add(Broadcast[Document](2))
      val parseLinks = Flow.fromFunction((d: Document) => LinkParser.parse(d))
      val parseData = Flow.fromFunction((d: Document) => mParseData(ticket, d))

      val dataSink = Sink.foreach((x: Option[A]) => println(x.toString()))
      val linkSink =
        Sink.foreach((xs: List[String]) =>
          replyTo ! CrawlingController.PageScrapped(ticket, xs.toSet)
        )

      source ~> download ~> bcast ~> parseLinks ~> linkSink
      bcast ~> parseData ~> dataSink

      ClosedShape
    })
}
