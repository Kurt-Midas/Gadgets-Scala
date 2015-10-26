package scrap.controllers

import play.api.mvc.{Action, Controller}
import scrap.jsonapi.ScrapJsonApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

/*
 * 1. Controller (Play)
 * 			Parses and validates input (possible horizontal stack)
 * 			Makes call to Json API
 * 2.	Json API (also Play)
 * 			Passes call to Call layer
 * 			Parses response into a Json object
 * 			No logic
 * 3.	Calls (Slick)
 * 			Constructs, executes, and parses response from query
 */

object ScrapController extends Controller{
  
  def scrapTest = Action.async {
    /*
     * Goal: POC of reply object. Use list of IDs and make recipes
     * 3653 (Medium Hull Repairer I), 4957 (Micro Brief Capacitor Overcharge I), 188 (Proton M), 5443 (Faint Epsilon Warp Scrambler I)
     */
    
    
    val idlist = List(3653, 4957, 188, 5443)
    val stringlist = List("Medium Hull Repairer I", "Micro Brief Capacitor Overcharge I")
//    val recipeList = ScrapCaller.getLotsOfInfoFromIDList(idlist)
//    val recipeList = ScrapCaller.getLotsOfInfoFromNameList(stringlist)
    val recipeList = ScrapJsonApi.getScrapFromNameList(stringlist)
      
      recipeList.map{ res => 
//        Ok(Json.toJson(res))
        /*res._2.map{ info =>
          Ok("Ignoring Jsonification for now: " + (res._1, info))
        }*/
//        Ok("Ignoring Jsonification for now: " + res._1 + res._2.map{info => info})
        Ok(Json.toJson((res._1, res._2))  )
      }
  }

}

/*
def test = Action.async {
    implicit val shipWrites = Json.writes[Ship]
    implicit val groupedShipWrites : Writes[Map[Int, List[Ship]]] = new Writes[Map[Int, List[Ship]]] {
      def writes(map: Map[Int, List[Ship]]) : JsValue = 
        Json.obj ( map.map { case (k,v) =>
            val ret : (String, JsValueWrapper) = k.toString() -> Json.toJson(v)
            ret
          }.toSeq:_*)
    }
    
    TestCaller.yieldFutureOfCategorySix().map { res => 
      res.toList.collect{case result => Ship.tupled(result)}
      }.map { res => Ok(Json.toJson(res.groupBy(_.groupId) ))}
  }*/