package db.calls

import db.connection.DBComponent
import db.repos.{InvtypesRepo, InvgroupsRepo}
//import db.repos.InvgroupsRepo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait TestCaller extends InvtypesRepo with InvgroupsRepo { this : DBComponent =>
  import driver.api._
//  private val invtypes = TableQuery[InvtypesRepo]
//  private val invgroups = TableQuery[InvgroupsRepo]
  def oneToMany(){
    println("unimplemented")
  }
  
  def printMapOfCategorySix() = {
    val query = for{
      g <- invgroups if g.categoryID === 6
      t <- invtypes if t.groupID === g.groupID
    } yield(g.groupID, t.typeID, t.typeName)
    val result = db.run(query.result)
    result.onSuccess{case(x) => println(x.groupBy(_._1))}
  }
  
  def printHeadOptionOfCategorySix() = {
    val query = for{
      g <- invgroups if g.categoryID === 6
      t <- invtypes if t.groupID === g.groupID
    } yield(g.groupID, t.typeID, t.typeName)
    val result = db.run(query.result.headOption)
    result.onSuccess{case(x) => println(x.groupBy(_._1))}
  }
  
  case class Ship( group: Int, id: Int, name: String )
  def yieldFutureOfCategorySix() : Future[Seq[(Int, Int, String)]] = {
    val query = for{
      g <- invgroups if g.categoryID === 6
      t <- invtypes if t.groupID === g.groupID
    } yield(g.groupID, t.typeID, t.typeName)
    db.run(query.result) 
  }
}

object TestCaller extends TestCaller with DBComponent