package dbmanager.repos

import slick.lifted.ProvenShape
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape.proveShapeOf
import dbmanager.connection.DBComponent

trait InvtypesRepo { this: DBComponent =>
  class Invtypes(tag: Tag) extends 
  Table[(Int, Int, String, String, Double, Double, Double, Int, Int, Double, Int, Int, Double)](tag, "invtypes") {
    val typeID = column[Int]("typeID", O.PrimaryKey)
    val groupID = column[Int]("groupID")
    val typeName = column[String]("typeName")
    val description = column[String]("description")
    val mass = column[Double]("mass")
    val volume = column[Double]("volume")
    val capacity = column[Double]("capacity")
    val portionSize = column[Int]("portionSize")
    val raceID = column[Int]("raceID")
    val basePrice = column[Double]("basePrice")
    val published = column[Int]("published")
    val marketGroupID = column[Int]("marketGroupID")
    val chanceOfDuplicating = column[Double]("chanceOfDuplicating")
    
    def * : ProvenShape[(Int, Int, String, String, Double, Double, Double, Int, Int, Double, Int, Int, Double)] =
      (typeID, groupID, typeName, description, mass, volume, capacity, portionSize,
          raceID, basePrice, published, marketGroupID, chanceOfDuplicating)
    
  //  def * : ProvenShape[(Int, Int, String, Int)] = (typeID, groupID, typeName, marketGroupID)
  }
  
  val invtypes = TableQuery[Invtypes]
  
}