package db.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._

import db.connection.DBComponent


trait InvmarketgroupsRepo {this: DBComponent =>
  class Invmarketgroups(tag: Tag) extends 
  Table[(Int, Int, String, String, Int, Int)](tag, "invmarketgroups") {
    val marketGroupID = column[Int]("marketGroupID", O.PrimaryKey)
    val parentGroupID = column[Int]("parentGroupID")
    val marketGroupName = column[String]("marketGroupName")
    val description = column[String]("description")
    val iconID = column[Int]("iconID")
    val hasTypes = column[Int]("hasTypes")
    
    def * : ProvenShape[(Int, Int, String, String, Int, Int)] =
      (marketGroupID, parentGroupID, marketGroupName, description, iconID, hasTypes)
  }
  
  val invmarketgroups = TableQuery[Invmarketgroups]
}