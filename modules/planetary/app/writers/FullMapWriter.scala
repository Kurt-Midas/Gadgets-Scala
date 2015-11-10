package planetary.writers

import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.Json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

trait FullMapWriter {
  import dbmanager.calls.{SchematicRow, SchematicRecipe, ItemDetails}
  
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
}