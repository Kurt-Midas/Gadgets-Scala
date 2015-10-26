package db.connection

import slick.driver.MySQLDriver

trait DBComponent {
  val driver = MySQLDriver
  import driver.api._
  val db : Database = MySqlDB.connectionPool
}

private[connection] object MySqlDB {
  import slick.driver.MySQLDriver.api._
  val connectionPool = Database.forConfig("mysql")
}