package db.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._

import db.connection.DBComponent

trait PlanetaryResourcesTypeMapRepo{ this: DBComponent =>
  class PlanetaryResourcesTypeMap (tag: Tag) extends 
  Table[(Int, Int)](tag,"planetaryresourcetypemap"){
    val planetID = column[Int]("planetId", O.PrimaryKey)
    val resourceID = column[Int]("resourceId")
  
    def * : ProvenShape[(Int, Int)] =
      (planetID, resourceID)
  }
  
  val planetaryresourcestypemap = TableQuery[PlanetaryResourcesTypeMap]
}

//this is the only DB which I built manually: fuzzworks doesn't have it. 