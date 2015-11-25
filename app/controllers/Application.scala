package controllers

import play.api.mvc.{Action, Controller, Result}
import play.api.http.MimeTypes
//import dbmanager.calls.TestCaller
import planetary.controllers.PlanetController

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper

object Application extends Controller {

  def index = Action.async {
//    TestCaller.printMapOfCategorySix()
//    TestCaller.printHeadOptionOfCategorySix()
    PlanetController.getPlanetStuff.map{ res =>
      Ok(views.html.Index(res))
    }
//    Ok(views.html.Index())
  }
  
  /*case class Ship(groupId: Int, id: Int, name: String)
  case class GroupedShips(map: Map[Int, Seq[Ship]])
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
  
}

/*
 * Custom organization
 * 1. Controller (Play)
 * 			Parses and validates input (possible horizontal stack)
 * 			Makes call to Json API
 * 2.	Json API (also Play)
 * 			Passes call to Call layer
 * 			Parses response into a Json object
 * 			No logic
 * 3.	Calls (Slick)
 * 			Constructs, executes, and parses response from query
 * 
 */

