package com.knol.db.connection

trait DBComponent {
  import slick.driver.JdbcProfile

  val driver: JdbcProfile

  import driver.api._

  val db: Database
}


trait MySqlDBComponent extends DBComponent {
  import slick.driver.MySQLDriver.api._
  import slick.driver.MySQLDriver

  val driver = MySQLDriver
  val db: Database = Database.forConfig("mysql")
}

trait PostgresDBComponent extends DBComponent {
  import slick.driver.PostgresDriver.api._
  import slick.driver.PostgresDriver

  val driver = PostgresDriver
  val db: Database = Database.forConfig("postgres")
}

// Redefine as extending MySqlDBComponent to use MySQL instead; no other changes in app required
trait SelectedDB extends PostgresDBComponent
