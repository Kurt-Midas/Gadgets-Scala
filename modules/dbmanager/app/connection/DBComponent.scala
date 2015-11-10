package dbmanager.connection

import slick.driver.MySQLDriver
//import slick.driver.JdbcProfile.api

trait DBComponent {
  val driver = MySQLDriver
  import driver.api._
  val db : Database = MySqlDB.connectionPool
}

private[connection] object MySqlDB {
  import slick.driver.MySQLDriver.api._
  val connectionPool = Database.forConfig("mysql")
}