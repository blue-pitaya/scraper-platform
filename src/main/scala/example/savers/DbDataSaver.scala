package example.savers

import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import example.models.TextValue
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior

object DbDataSaver {
  def saveData(value: TextValue): Unit = {
    import doobie.util.ExecutionContexts
    import cats.effect.unsafe.implicits.global
    insert(value).run.transact(transactor).unsafeRunSync()
  }

  private val transactor: Transactor.Aux[IO, Unit] =
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      "jdbc:postgresql://192.168.0.10:5432/scraperdata", // connect URL (driver-specific)
      "scraper", // user
      "asdf" // password
    )

  /*
   Column | Type | Collation | Nullable |      Default
  --------+------+-----------+----------+--------------------
   id     | uuid |           | not null | uuid_generate_v4()
   value  | text |           |          |
   site   | text |           | not null |
   */
  private def insert(value: TextValue): Update0 =
    sql"insert into webheaders (value, site) values (${value.value}, ${value.url})".update

}
