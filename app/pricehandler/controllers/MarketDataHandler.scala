package pricehandler.controllers

import play.api.mvc.{Action, Controller}

object MarketDataHandler extends Controller{
  def buildStuff(list: List[Int]): String = if(list.length > 1) list.head.toString() + ", " + buildStuff(list.tail) else list.head.toString()
  def idListTest(idList: List[Int]) = Action{ request =>
//    Ok("Got ID List: " + idList)
    Ok("Got stuff: " + request.body + ", " + idList)
    
  }
  
}