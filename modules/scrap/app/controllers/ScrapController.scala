package scrap.controllers

import play.api.mvc.{Action, Controller}
import scrap.service.ScrapApi
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scrap.writers.GetScrapWriter

import play.api.data._
import play.api.data.Forms._

import dbmanager.calls.{Component, TypeInfo}

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
        res._2.map{ info =>
          Ok("Ignoring Jsonification for now: " + (res._1, info))
        }
        Ok(Json.toJson((res._1, res._2)))
      }
  }
  
  case class RequestItem(name: String, quantity: Int)
  case class ScrapDataRequest(items: List[RequestItem])
  val idListForm = Form(
      mapping(
        "items" -> list(
            mapping(
              "name" -> text,
              "quantity" -> number
            )(RequestItem.apply)(RequestItem.unapply)
          )
      )(ScrapDataRequest.apply)(ScrapDataRequest.unapply)
  )
  
  
  case class ItemQuantityAndRecipe(quantity: Int, recipe: List[Component])
  case class ResponseItem(
      info: Map[String, TypeInfo], 
      items: Map[String, ItemQuantityAndRecipe])
  
  def scrapInfoEndpoint = Action.async {implicit request =>
    val requestData : ScrapDataRequest = idListForm.bindFromRequest.get
    val nameList : List[String] = requestData.items.map { res => 
      res.name 
    }
    val recipeList : Future[(Map[Int, List[Component]], Map[Int, TypeInfo])] = 
      ScrapApi.getScrapFromNameList(nameList);

    val quantityMap : Map[String, Int] = requestData.items.map { res => 
      (res.name, res.quantity)
    }.toMap
    
    implicit val itemQuantityAndRecipeWrites = Json.writes[ItemQuantityAndRecipe]
    implicit val responseItemWrites = Json.writes[ResponseItem]
    
    recipeList.map{ res =>
      val infoMap : Map[String, TypeInfo] = res._2.map{ case (key, info) =>
        key.toString() -> info
      }
      /*val componentsMap : Map[String, List[Component]] = res._1.map{ case (key, list) =>
        key.toString() -> list
      }*/
      
      val idToQuantityMap : Map[Int, Int] = res._2.collect{  //case (id, list) =>
        case (id, info) if quantityMap.contains(info.typeName) =>
          (id, quantityMap(info.typeName))
      }
      val componentsMap : Map[String, ItemQuantityAndRecipe] = res._1.map{ case (key, list) =>
        key.toString() -> ItemQuantityAndRecipe(idToQuantityMap(key), list)
      }
      //writers, all maps need string keys
      
      Ok(Json.toJson(ResponseItem(infoMap, componentsMap)))
    }
  }

}


/*
{
	"items": [
		{
			"name":"Medium Hull Repairer I",
			"quantity":1
		},
		{
			"name":"Micro Brief Capacitor Overcharge I",
			"quantity":1
		},
		{
			"name":"Proton M",
			"quantity":100
		},
		{
			"name":"Faint Epsilon Warp Scrambler I",
			"quantity":1
		}
	],
	"market": 0
}
 */
