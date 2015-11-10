package planetary.jsonapi

import dbmanager.calls.PlanetCaller
import dbmanager.calls.{SchematicRow, SchematicRecipe, ItemDetails}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

object PlanetaryApi {
  
  def getSchematicMap = {
    PlanetCaller.getSchematicMap
  }
  
  def getPlanetResourceMappings = {
    PlanetCaller.getPlanetResourceMaps
  }
  
  def getItemDetails(itemIdSet : Set[Int]) = {
    PlanetCaller.getItemDetails(itemIdSet)
  }
  
  def getFullResponse: Future[(Map[Int, Seq[Int]], 
                          Map[Int, Seq[Int]], 
                          Map[Int, SchematicRecipe], 
                          Map[Int, ItemDetails])] = {
    val maps: Future[(Map[Int, Seq[Int]], Map[Int, Seq[Int]])] = 
        PlanetCaller.getPlanetResourceMaps.map{ maps => (maps._1, maps._2) }
    
    val details: Future[(Map[Int, SchematicRecipe], Map[Int, ItemDetails])] = 
          PlanetCaller.getSchematicMap.map{ res =>
      val schematicMap = res._1
      val itemIdSet : Set[Int] = res._2
      PlanetCaller.getItemDetails(itemIdSet).map {items => 
        (schematicMap, items)
      }
    }.flatMap(identity)
    
    maps.zip(details).map{ res =>
      (res._1._1, res._1._2, res._2._1, res._2._2)
    }//flatTuple?
  }
  
  
  
}