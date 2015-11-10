package dbmanager.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape.proveShapeOf
//import slick.profile.RelationalProfile.CommonAPI.Table
import dbmanager.connection.DBComponent

trait PlanetschematicstypemapRepo { this: DBComponent =>
  class Planetschematicstypemap (tag: Tag) extends 
  Table[(Int, Int, Int, Int)](tag,"planetschematicstypemap"){
    val schematicID = column[Int]("schematicID", O.PrimaryKey)
    val typeID = column[Int]("typeID", O.PrimaryKey)
    val quantity = column[Int]("quantity")
    val isInput = column[Int]("isInput")
  
    def * : ProvenShape[(Int, Int, Int, Int)] =
      (schematicID, typeID, quantity, isInput)
  }
  
  val planetschematicstypemap = TableQuery[Planetschematicstypemap]
  
}