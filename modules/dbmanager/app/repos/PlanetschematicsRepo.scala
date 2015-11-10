package dbmanager.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape.proveShapeOf
//import slick.profile.RelationalProfile.CommonAPI.Table
import dbmanager.connection.DBComponent

class PlanetschematicsRepo (tag: Tag) extends 
Table[(Int, String, Int)](tag,"planetschematics"){
  val schematicID = column[Int]("schematicID", O.PrimaryKey)
  val schematicName = column[String]("schematicName")
  val cycleTime = column[Int]("cycleTime")

  def * : ProvenShape[(Int, String, Int)] =
    (schematicID, schematicName, cycleTime)
}