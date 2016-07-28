package com.knol.db.connection

// NOT USED: H2 allows only a single AutoInc column to be returned from an INSERT.
trait H2DBComponent extends DBComponent {
  val driver = slick.driver.H2Driver
  import driver.api._

  val h2Url = {
    val randomDB = s"jdbc:h2:mem:test${ java.util.UUID.randomUUID };"
    val toUpper   = "DATABASE_TO_UPPER=false;"
    val init      = "INIT=runscript from 'src/test/resources/mysqlSchema.sql'\\;"
    val runScript = "runscript from 'src/test/resources/schemaData.sql'"
    s"${ randomDB }MODE=MySql;$toUpper$init$runScript"
  }

  val db: Database = Database.forURL(url = h2Url, driver = "org.h2.Driver")
}
