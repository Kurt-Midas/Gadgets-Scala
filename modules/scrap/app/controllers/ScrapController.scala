package scrap.controllers

import play.api.mvc.{Action, Controller}
import scrap.service.ScrapApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scrap.writers.GetScrapWriter

object ScrapController extends Controller with GetScrapWriter{
  
  def scrapTest = Action.async {
    /*
     * Goal: POC of reply object. Use list of IDs and make recipes
     * 3653 (Medium Hull Repairer I), 4957 (Micro Brief Capacitor Overcharge I), 188 (Proton M), 5443 (Faint Epsilon Warp Scrambler I)
     */
    
    
    val idlist = List(3653, 4957, 188, 5443)
    val stringlist = List("Medium Hull Repairer I", "Micro Brief Capacitor Overcharge I")
    
    val recipeList = ScrapApi.getScrapFromNameList(stringlist)
      
      recipeList.map{ res => 
//        Ok(Json.toJson(res))
        res._2.map{ info =>
          Ok("Ignoring Jsonification for now: " + (res._1, info))
        }
//        Ok("Ignoring Jsonification for now: " + res._1 + res._2.map{info => info})
        Ok(Json.toJson((res._1, res._2))  )
      }
  }

}

