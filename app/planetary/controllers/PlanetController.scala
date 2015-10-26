package planetary.controllers

import play.api.mvc.{Action, Controller}
//import scrap.jsonapi.ScrapJsonApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import planetary.jsonapi.PlanetaryJsonApi

object PlanetController extends Controller{
  
  def planetTest = Action.async {
//    PlanetaryJsonApi.getSchematicMap.map{ case res => 
//      Ok("SchematicMap Test: " + res)
//    }
    
//    PlanetaryJsonApi.getPlanetResourceMappings.map{ case res =>
//      Ok("Planet Resource Mappings Test: " + res)
//    }
    
//    PlanetaryJsonApi.getFullResponse.map {case res =>
//      Ok("Unjsonified Full Info Test: " + res)
//    }
    
    PlanetaryJsonApi.getFullResponse.map {case res =>
      Ok(Json.toJson(res))
    }
  }
  
  
}

/*
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


/*
 * Format of existing (Spring Java) endpoint:
 * schematicMap:
 * 		Map(Int -> (outputID, name, outputQuantity, marketGroup, cycleTime, recipe: List[(typeId, quantity)]))
 * 	planetMap: 
 * 		Map(Int -> (id, resourceIDs: List[Int]))
 * 	resourceMap:
 * 		Map(Int -> (id, planetIDs: List[Int]))
 * 	nameMap:
 * 		Map(String -> Int)
 * 	itemDetails: 
 * 		Map(Int -> (name, tier, typeID))
 * 
 * Changes:
 * 		schematicMap should just be outputID, outputQuantity, recipe
 * 		itemDetails to (typeID, name, marketGroup /*which means tier*/)...
 * Is my continuous desire to redesign the data structures a "best practices" or an "OCD" thing? Probably _._2
 * On the other hand the earlier setup was based on considerably more difficult (and unnecessary) joins
 */


