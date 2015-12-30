package dbmanager.calls

import dbmanager.connection.DBComponent
import dbmanager.repos.{InvtypesRepo, PlanetaryResourcesTypeMapRepo, PlanetschematicstypemapRepo}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class SchematicRow(schematicID: Int, isInput: Int, typeID: Int, quantity: Int)
case class SchematicRecipe(output: SchematicRow, recipe: List[SchematicRow])
case class ItemDetails(typeID: Int, typeName: String, groupId: Int);

trait PlanetCalls extends PlanetschematicstypemapRepo
        with InvtypesRepo
        with PlanetaryResourcesTypeMapRepo
      { this : DBComponent =>
  import driver.api._
  
  /*
   * schematicMap: Map(Int -> (outputID, quantity, recipe: List[(typeID, quantity)] aka (Int, Int)->List(Int, Int)
	 * select RELEVANT from planetschematicstypemap;  groupBy schematicID; sortBy isOutput; collect item => item.head -> item.tail
   */
  def getSchematicMap: Future[(Map[Int, SchematicRecipe], Set[Int])] = {
    val query = for {
      p <- planetschematicstypemap
    } yield (p.schematicID, p.isInput, p.typeID, p.quantity)
    
    db.run(query.result).map { res => 
      res.collect{ case result => SchematicRow.tupled(result)}
    }.map { res => //Seq[SchematicRow]
      
      val unzipped: (Seq[Int], Seq[Int]) = res.unzip{case a => (a.schematicID, a.typeID)}
      val typeIdList = (unzipped._1 ++ unzipped._2).toSet
      
      val schematicMap: Map[Int, SchematicRecipe] = 
        res.toList.groupBy(_.schematicID).
          collect{case result => //(Int, List[SchematicRow]
          SchematicRecipe.tupled((result._2.sortBy(_.isInput).head -> result._2.sortBy(_.isInput).tail)) 
          //I don't like repeating the sort
        }.toList.map{ res =>
          res.output.typeID -> res 
        }.toMap
        
        (schematicMap, typeIdList)
      }
      
    }
    
//  select * from planetaryresourcetypemap; map res => (OnlyJsonifyFirst.tuple(res._1, res._2).groupBy(_._1), reversed)
  def getPlanetResourceMaps: Future[(Map[Int, Seq[Int]], Map[Int, Seq[Int]])] = {
    val query = for{
      p <- planetaryresourcestypemap
    } yield (p.planetID, p.resourceID) //which is *
    
    db.run(query.result).map { res =>
//      (res.groupBy(_._1).toMap, res.groupBy(_._2).toMap) //easy to fix this is json. I choose hard!
      
       def build(m: Seq[(Int, Int)]) : Map[Int, Seq[Int]] = m.groupBy(_._1).map{ p =>
         val unzipped: (Seq[Int], Seq[Int]) = p._2.unzip
         p._1 -> unzipped._2
       }.toMap
//      def build(m: Seq[(Int, Int)]) : Map[Int, Seq[Int]] = m.map{ p =>
//        p.groupBy(_._1).map{ item =>
//          val unzipped: (Seq[Int], Seq[Int]) = res.unzip
//          item._1 -> unzipped._2
//        }
//        ???
      (build(res), build(res.collect{ case order => order.swap}))
//      (PlanetResourceMap(build(res)), PlanetResourceMap(build(res.collect{ case order => order.swap})))
    }
      
//      def build(m: Map[Int, Seq[(Int, Int)]]): Map[Int, Seq[Int]] = m.map { p => 
//        val unzipped: (Seq[Int], Seq[Int]) = res.unzip
//        p._1 -> unzipped._2
//      }.toMap
      
//      (build(res.groupBy(_._1)), build(res.groupBy(_._2)))
      
  }
  
  
  
  def getItemDetails(set: Set[Int]): Future[Map[Int, ItemDetails]] = {
    def query = for{
      t <- invtypes if t.typeID inSetBind set
    } yield(t.typeID, t.typeName, t.marketGroupID)
    
    db.run(query.result).map{ res =>
      res.collect { 
        case item => item._1 -> ItemDetails.tupled(item)
      }
    }.map{ res =>
      res.toMap
    }
  }
  
  

}

object PlanetCaller extends PlanetCalls with DBComponent

/* Comment redundant to PlanetController
 * 
 * New Format:
 * schematicMap: Map(Int -> (outputID, quantity, recipe: List[(typeID, quantity)] aka (Int, Int)->List(Int, Int)
 * select RELEVANT from planetschematicstypemap;  groupBy schematicID; sortBy isOutput; collect item => item.head -> item.tail
 * 
 * planetMap: Map(Int -> List(Int))
 * resourceMap: Map(Int -> List(Int)) 
 * select * from planetaryresourcetypemap; map res => (OnlyJsonifyFirst.tuple(res._1, res._2).groupBy(_._1), reversed)
 * 
 * itemDetails: Map(Int -> Map(typeID, name, marketGroup/groupID aka tier)
 * select RELEVANT from invtypes... blaaaaaaah
 * nameMap: Map(String -> Int) //an exercise in laziness. It'd be possible to build JS without this crutch, just harder.  
 */