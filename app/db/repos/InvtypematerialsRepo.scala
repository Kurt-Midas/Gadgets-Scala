package db.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._

import db.connection.DBComponent

trait InvtypematerialsRepo { this: DBComponent =>
  
  class Invtypematerials (tag: Tag) extends 
  Table[(Int, Int, Int)](tag,"invtypematerials"){
    val typeID = column[Int]("typeID", O.PrimaryKey) //foreign key to InvtypesRepo>typeID
    val materialTypeID = column[Int]("materialTypeID") //also foreign key to InvtypesRepo>typeID
    val quantity = column[Int]("quantity")
  
    def * : ProvenShape[(Int, Int, Int)] =
      (typeID, materialTypeID, quantity)
  }
  
  val invtypematerials = TableQuery[Invtypematerials]
  
}