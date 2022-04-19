package example

import com.github.tototoshi.csv.CSVWriter
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object CsvDataSaver {
  sealed trait Command
  final case class SaveData(data: List[List[String]]) extends Command
  final case object Stop extends Command

  final case class State(writer: CSVWriter, filename: String)

  def apply(filename: String): Behavior[Command] = {
    val writer = CSVWriter.open(filename, append = true)
    Behaviors.receive { (context, message) =>
      message match {
        case SaveData(data) =>
          writer.writeAll(data)
          Behaviors.same
        case Stop =>
          writer.close()
          Behaviors.stopped
      }
    }
  }
}
