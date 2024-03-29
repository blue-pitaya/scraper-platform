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
import example.actors.TicketSource.Execute
import akka.stream.SinkShape
import akka.NotUsed
import akka.stream.UniformFanOutShape
import akka.stream.FanOutShape2
import akka.actor.SupervisorStrategy

object ScraperGraph {
  def graph[A](
      mParseData: (UrlTicket, Document) => Option[A],
      replyTo: ActorRef[CrawlingController.Command],
      saveData: A => Unit
  )(implicit
      browser: Browser
  ) =
    GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val bcast1 = b.add(Broadcast[UrlTicket](3))
      val zip1 = b.add(Zip[Document, UrlTicket]())
      val zip2 = b.add(Zip[List[String], UrlTicket]())
      val bcast2 = b.add(Broadcast[Document](2))

      val download = Flow
        .fromFunction((t: UrlTicket) => {
          println(s"Started download ${t.url}")
          Try(browser.get(t.url)) match {
            case Failure(exception) =>
              replyTo ! CrawlingController.PageScrapped(t, Set())
              throw exception
            case Success(value) => value
          }
        })

      val parseLinks = Flow.fromFunction((d: Document) => LinkParser.parse(d))
      val parseData = Flow.fromFunction((t: (Document, UrlTicket)) => {
        //println(t._1.location)
        mParseData(t._2, t._1)
      })

      val dataSink = Sink.foreach((x: Option[A]) =>
        x match {
          case Some(value) =>
            println(s"VALUE: $value")
            saveData(value)
          case None => {}
        }
      )
      val linkSink =
        Sink.foreach((t: (List[String], UrlTicket)) => {
          //println(s"Reached linkSink for ${t._2.url}")
          replyTo ! CrawlingController.PageScrapped(t._2, t._1.toSet)
        })

      bcast1 ~> download ~> bcast2 ~> zip1.in0
      bcast1 ~> zip1.in1
      zip1.out ~> parseData ~> dataSink
      bcast2 ~> parseLinks ~> zip2.in0
      bcast1 ~> zip2.in1
      zip2.out ~> linkSink

      SinkShape(bcast1.in)
    }
}
