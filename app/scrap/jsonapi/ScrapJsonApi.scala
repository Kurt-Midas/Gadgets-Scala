package scrap.jsonapi

import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper
import scrap.calls.ScrapCaller
import play.api.libs.json.Json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

//import db.calls.ScrapCaller


//object ScrapJsonApi 

object ScrapJsonApi {
  def getScrapFromIdList (list: List[Int]) = ScrapCaller.getLotsOfInfoFromIDList(list)
  def getScrapFromNameList (list: List[String]) = ScrapCaller.getLotsOfInfoFromNameList(list)
  
  private[scrap] case class Component(resultID: Int, typeId: Int, quantity: Int)
  private[scrap] case class TypeInfo(typeID: Int, typeName: String, groupID: Int, groupName: String, categoryID: Int, marketGroupID: Int, marketGroupName: String)

  
  implicit val componentWrites = new Writes[Component] {
    def writes(c: Component) = Json.obj("id" -> c.typeId, "quantity" -> c.quantity)
  }
  implicit val recipeListWrites : Writes[Map[Int, List[Component]]] = new Writes[Map[Int, List[Component]]] {
      def writes(map: Map[Int, List[Component]]) : JsValue = 
        Json.obj ( map.map { case (k,v) =>
            val ret : (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
            ret
          }.toSeq:_*)
   }
  
  implicit val typeInfoWrites = new Writes[TypeInfo] {
    def writes(t: TypeInfo) = Json.obj(
        "id" -> t.typeID, 
        "name" -> t.typeName,
        "groupID" -> t.groupID,
        "group" -> t.groupName,
        "categoryID" -> t.categoryID
//        t.marketGroupID,
//        t.marketGroupName
        
    )
  }
  
  implicit val typeInfoMapWrites = new Writes[Map[Int, TypeInfo]] {
    def writes(map: Map[Int, TypeInfo]): JsValue =
      Json.obj(map.map {case (k,v) =>
        val ret : (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
        ret
      }.toSeq:_*)
  }

  implicit val getLotsOfInfoFromIDListResultWrites = new Writes[(Map[Int, List[Component]], Map[Int, TypeInfo])] {
    def writes(tupleOfMaps : (Map[Int, List[Component]], Map[Int, TypeInfo])): JsValue =
      Json.obj("recipes" -> Json.toJson(tupleOfMaps._1), "typeInfo" -> Json.toJson(tupleOfMaps._2))
  } //I need a better way of organizing Writes, this is ugly

}



