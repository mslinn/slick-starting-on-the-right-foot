package com.knol.db

package connection {
  trait DBComponent {
    import slick.driver.JdbcProfile

    val driver: JdbcProfile
    import driver.api._

    val db: Database
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
