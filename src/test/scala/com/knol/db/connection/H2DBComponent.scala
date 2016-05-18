package com.knol.db.connection

import org.slf4j.LoggerFactory
import java.util.UUID

trait H2DBComponent extends DBComponent {
  val logger = LoggerFactory.getLogger(getClass)
  val driver = slick.driver.H2Driver
  import driver.api._

  val h2Url = {
    val randomDB = s"jdbc:h2:mem:test${ UUID.randomUUID };"
    val toUpper   = "DATABASE_TO_UPPER=false;"
    val init      = "INIT=runscript from 'src/test/resources/mysqlSchema.sql'\\;"
    val runScript = "runscript from 'src/test/resources/schemaData.sql'"
    s"${randomDB}MODE=MySql;$toUpper$init$runScript"
  }

  val db: Database = {
    logger.info("Creating test connection")
    Database.forURL(url = h2Url, driver = "org.h2.Driver")
  }
}
