package example.actors

import com.github.tototoshi.csv.CSVWriter
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.PostStop

object CsvDataSaver {
  sealed trait Command
  final case class SaveData(data: List[List[String]]) extends Command

  final case class State(writer: CSVWriter, filename: String)

  def apply(filename: String): Behavior[Command] = {
    val writer = CSVWriter.open(filename, append = true)
    Behaviors
      .receive[Command] { (context, message) =>
        message match {
          case SaveData(data) =>
            writer.writeAll(data)
            Behaviors.same
        }
      }
      .receiveSignal { case (context, PostStop) =>
        writer.close() //TODO: exception if not opened?
        context.log.info(s"$filename file closed.")
        Behaviors.same
      }
  }
}
