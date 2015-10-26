package scrap.calls

import db.connection.DBComponent
import db.repos.{InvgroupsRepo, InvtypesRepo, InvtypematerialsRepo, InvmarketgroupsRepo}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import scrap.jsonapi.ScrapJsonApi.{Component, TypeInfo}

trait ScrapCaller extends InvgroupsRepo 
      with InvtypesRepo 
      with InvtypematerialsRepo 
      with InvmarketgroupsRepo
      { this : DBComponent =>
  import driver.api._
  
  //I should actually put these in the Repos and import them. Probably. 
//  private val invtypes = TableQuery[InvtypesRepo]
//  private val invgroups = TableQuery[InvgroupsRepo]
//  private val invtypematerials = TableQuery[InvtypematerialsRepo]
//  private val invmarketgroups = TableQuery[InvmarketgroupsRepo]

  //select t.typeID, m.materialTypeID, m.quantity from invtypes t inner join invtypematerials m on m.typeID=t.typeID where t.typeName="";
  
//  case class Component(resultID: Int, typeId: Int, quantity: Int)
  /*implicit val componentWrites = new Writes[Component] {
    def writes(c: Component) = Json.obj("id" -> c.typeId, "quantity" -> c.quantity)
  }
  implicit val recipeListWrites : Writes[Map[Int, List[Component]]] = new Writes[Map[Int, List[Component]]] {
      def writes(map: Map[Int, List[Component]]) : JsValue = 
        Json.obj ( map.map { case (k,v) =>
            val ret : (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
            ret
          }.toSeq:_*)
   }*/
  
  
  def getRecipesForIDList(list: List[Int]) : Future[Map[Int, List[Component]]] = {
    val query = for {
      t <- invtypes if t.typeID inSetBind list
      m <- invtypematerials if m.typeID === t.typeID
    } yield(t.typeID, m.materialTypeID, m.quantity) //eventually want typeName and marketGroupID and marketGroupName (maybe, that's contextual on parent) in here
    
    db.run(query.result).map{ res => 
      res.toList.collect { case result => Component.tupled(result)}
    }.map { res => res.groupBy(_.resultID) }
  }
  
  
  /*
   * More complex call. This is an attempt to zip EVERYTHING in a single future. 
   * No idea if this is Best Practices or not but this is actually really fun
   */
  def getLotsOfInfoFromIDList(list: List[Int]) : Future[(Map[Int, List[Component]], Map[Int, TypeInfo])] = {
    //query
    val query = for {
      t <- invtypes if t.typeID inSetBind list
      m <- invtypematerials if m.typeID === t.typeID
    } yield(t.typeID, m.materialTypeID, m.quantity) //eventually want typeName and marketGroupID and marketGroupName (maybe, that's contextual on parent) in here
    getProcessedResults(db.run(query.result))
  }
  
  def getLotsOfInfoFromNameList(list: List[String]) : Future[(Map[Int, List[Component]], Map[Int, TypeInfo])] = {
    //query
    val query = for {
      t <- invtypes if t.typeName inSetBind list
      m <- invtypematerials if m.typeID === t.typeID
    } yield(t.typeID, m.materialTypeID, m.quantity) //eventually want typeName and marketGroupID and marketGroupName (maybe, that's contextual on parent) in here
    getProcessedResults(db.run(query.result))
  }
    
  private def getProcessedResults(future: Future[Seq[(Int, Int, Int)]]) = {
    //private vs member of calling function? 
    future.map{ res => 
      //get list of IDs 
      val unzipped: (Seq[Int], Seq[Int], Seq[Int]) = res.unzip3
      val idset: Set[Int] = unzipped._1.toSet ++ unzipped._2.toSet
      //create Future[Future[tuple]]
      getTypeInfoStuff(idset).map{info => 
        (res.toList.collect ({ case result => Component.tupled(result)}).groupBy { c => c.resultID }, info)
      }
    }.flatMap(identity) //transform from Future[Future] to Future
  }
  
//  case class TypeInfo(typeID: Int, typeName: String, groupID: Int, groupName: String, categoryID: Int, marketGroupID: Int, marketGroupName: String)
  private def getTypeInfoStuff(list: Set[Int]) : Future[Map[Int, TypeInfo]] = {
    val query = for {
      t <- invtypes if t.typeID inSetBind list
      g <- invgroups if t.groupID === g.groupID
      m <- invmarketgroups if t.marketGroupID === m.marketGroupID
    } yield (t.typeID, t.typeName, g.groupID, g.groupName, g.categoryID, m.marketGroupID, m.marketGroupName)
    
    db.run(query.result).map{ case res =>
      res.map(info => info._1 -> TypeInfo.tupled(info))(collection.breakOut): Map[Int, TypeInfo]
      //turns result into map on element 1 (typeID) -> TypeInfo. And magic (ty Ben Lings)
    }
  }
  /*implicit val typeInfoWrites = new Writes[TypeInfo] {
    def writes(t: TypeInfo) = Json.obj(
        "id" -> t.typeID, 
        "name" -> t.typeName,
        "groupID" -> t.groupID,
        "group" -> t.groupName,
        "categoryID" -> t.categoryID
//        t.marketGroupID,
//        t.marketGroupName
        
    )
  }*/
  //(Map[Int,List[db.calls.ScrapCaller.Component]], Map[Int,db.calls.ScrapCaller.TypeInfo]). 
  /*implicit val typeInfoMapWrites = new Writes[Map[Int, TypeInfo]] {
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
*/
}

object ScrapCaller extends ScrapCaller with DBComponent