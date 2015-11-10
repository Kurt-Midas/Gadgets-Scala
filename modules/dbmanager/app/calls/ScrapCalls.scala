package dbmanager.calls

import dbmanager.connection.DBComponent
import dbmanager.repos.{InvgroupsRepo, InvtypesRepo, InvtypematerialsRepo, InvmarketgroupsRepo}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Component(resultID: Int, typeId: Int, quantity: Int)
case class TypeInfo(typeID: Int, typeName: String, groupID: Int, groupName: String, categoryID: Int, marketGroupID: Int, marketGroupName: String)

trait ScrapCaller extends InvgroupsRepo 
      with InvtypesRepo 
      with InvtypematerialsRepo 
      with InvmarketgroupsRepo
      { this : DBComponent =>
  import driver.api._
  
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
}

object ScrapCaller extends ScrapCaller with DBComponent