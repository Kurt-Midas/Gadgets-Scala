package planetary.jsonapi

import planetary.calls.PlanetCaller
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.Json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

import scala.concurrent.Future


object PlanetaryJsonApi {
  
  
  private[planetary] case class SchematicRow(schematicID: Int, isInput: Int, typeID: Int, quantity: Int)
  private[planetary] case class SchematicRecipe(output: SchematicRow, recipe: List[SchematicRow])
  private[planetary] case class ItemDetails(typeID: Int, typeName: String, groupId: Int);
//  private[planetary] case class PlanetResourceMap(map: Map[Int, Seq[Int]])
  
  /*implicit val schematicRowWrites = new Writes[SchematicRow] {
    def writes(r : SchematicRow) = Json.obj("outputID" -> r.typeID, "quantity" -> r.quantity)
  }*/
  implicit val fullResponseWrites = new Writes[(Map[Int, Seq[Int]], 
                          Map[Int, Seq[Int]], 
                          Map[Int, SchematicRecipe], 
                          Map[Int, ItemDetails])] {
    implicit val planetMapWrites = new Writes[Map[Int, Seq[Int]]] {
      def writes( m: Map[Int, Seq[Int]]) : JsValue =
        Json.obj( m.map{ case(k,v) =>
          val ret : (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
          ret
        }.toSeq:_*)
    }
    implicit val schematicRecipeWrites = new Writes[SchematicRecipe]{
      def writes(r: SchematicRecipe): JsValue = 
        Json.obj(
            "outputID" -> r.output.typeID,
            "quantity" -> r.output.quantity,
            "recipe" -> Json.toJson(r.recipe.map { item => 
              Json.obj("typeID" -> item.typeID, "quantity" -> item.quantity) })
        )
    }
    
    implicit val itemDetailWrites = new Writes[Map[Int, ItemDetails]]{
      implicit val itemDetailWrites = new Writes[ItemDetails] {
        def writes(d: ItemDetails) = 
          Json.obj("typeID" -> d.typeID, "name" -> d.typeName, "groupID" -> d.groupId)
      }
      def writes(map: Map[Int, ItemDetails]) = Json.obj(
          map.map{case (key, details) => 
            val ret : (String, JsValueWrapper) = key.toString() ->
              Json.toJson(details)
            ret
          }.toSeq:_*)
    }
    
/*    def writes(p: Map[Int, Seq[Int]], 
         r: Map[Int, Seq[Int]], 
         s: Map[Int, SchematicRecipe], 
         d: Map[Int, ItemDetails])*/ 
    def writes(tup: (Map[Int, Seq[Int]], 
         Map[Int, Seq[Int]], 
         Map[Int, SchematicRecipe], 
         Map[Int, ItemDetails]))  : JsValue = Json.obj(
             "planetMap" -> Json.toJson(tup._1),
             "resourceMap" -> Json.toJson(tup._2),
             "schematicMap" -> Json.obj(tup._3.map{case (k,v) => 
               val ret: (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
               ret}.toSeq:_*),
             "itemDetails" -> Json.toJson(tup._4)
         )
  }
  
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
//    val maps: Future[(Map[Int, Seq[Int]], Map[Int, Seq[Int]])] =
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