package db.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._

import db.connection.DBComponent

trait InvgroupsRepo { this: DBComponent => 
  
  
  class Invgroups (tag: Tag) extends 
  Table[(Int, Int, String, String, Int, Int, Int, Int, Int, Int, Int, Int)](tag,"invgroups"){
    val groupID = column[Int]("groupID", O.PrimaryKey)
    val categoryID = column[Int]("categoryID")
    val groupName = column[String]("groupName")
    val description = column[String]("description")
    val iconID = column[Int]("iconID")
    val useBasePrice = column[Int]("useBasePrice")
    val allowManufacture = column[Int]("iconID")
    val allowRecycler = column[Int]("allowRecycler")
    val anchored = column[Int]("anchored")
    val anchorable = column[Int]("anchorable")
    val fittableNonSingleton = column[Int]("fittableNonSingleton")
    val published = column[Int]("published")
  
    def * : ProvenShape[(Int, Int, String, String, Int, Int, Int, Int, Int, Int, Int, Int)] =
      (groupID, categoryID, groupName, description, iconID, useBasePrice, allowManufacture, 
        allowRecycler, anchored, anchorable, fittableNonSingleton, published)
  }
  
  val invgroups = TableQuery[Invgroups]

}