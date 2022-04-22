package example.savers

import doobie.util.transactor.Transactor
import cats.effect._

sealed trait DataSaverConfig[A]
final case class CsvSaverConfig[A](filename: String, dataToValueList: A => List[String])
    extends DataSaverConfig[A]
final case class DbSaverConfig[A](transactor: Transactor.Aux[IO, Unit], columnNames: List[String])
    extends DataSaverConfig[A]
