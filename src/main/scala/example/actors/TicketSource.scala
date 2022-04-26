package example.actors

import example.models.UrlTicket
import akka.stream.typed.scaladsl.ActorSource
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.stream.scaladsl.Sink

object TicketSource {
  sealed trait Command
  final case class Execute(ticket: UrlTicket) extends Command
  final case object Complete extends Command
  final case class Fail(ex: Exception) extends Command

  def apply(): Source[Command, ActorRef[Command]] = ActorSource.actorRef[Command](
    completionMatcher = { case Complete =>
    },
    failureMatcher = { case Fail(ex) =>
      ex
    },
    bufferSize = 8,
    overflowStrategy = OverflowStrategy.fail
  )
}
