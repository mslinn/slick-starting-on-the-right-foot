package com.knol.db

package connection {
  object DBComponent {
    import concurrent.duration._
    import scala.language.postfixOps

    val Logger = org.slf4j.LoggerFactory.getLogger("persistence")
    val dbDuration: FiniteDuration = 1 minute
  }

  trait DBComponent {
    import DBComponent._
    import slick.driver.JdbcProfile
    import scala.concurrent.{Await, Future}
    import scala.concurrent.ExecutionContext.Implicits.global

    val driver: JdbcProfile
    import driver.api._

    val db: Database

    /** Independent of schema, just dependent on Database */
    @inline def run[R](action: DBIOAction[R, NoStream, Nothing]): R =
      Await.result(runAsync(action), dbDuration)

    /** Independent of schema, just dependent on Database */
    @inline def runAsync[R](action: DBIOAction[R, NoStream, Nothing]): Future[R] =
      db.run(action) andThen {
        case scala.util.Failure(ex) =>
          DBComponent.Logger.error(s"DBComponent.runAsync: ${ ex.getCause }: ${ ex.getMessage }")
      }
  }

  trait MySqlDBComponent extends DBComponent {
    import slick.driver.MySQLDriver
    import slick.driver.MySQLDriver.api._

    val driver = MySQLDriver
    val db: Database = Database.forConfig("mysql")
  }

  trait PostgresDBComponent extends DBComponent {
    import slick.driver.PostgresDriver
    import slick.driver.PostgresDriver.api._

    val driver = PostgresDriver
    val db: Database = Database.forConfig("postgres")
  }
}

package object connection {
  // Redefine as extending MySqlDBComponent to use MySQL instead; no other changes in app required
  type SelectedDB = PostgresDBComponent
}
