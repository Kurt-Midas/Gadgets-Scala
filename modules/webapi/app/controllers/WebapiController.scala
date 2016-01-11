package webapi.controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import play.api.data._
import play.api.data.Forms._

//import wshandler.evecentral.MarketstatHandler
//import wshandler.evecentral.MarketStatCaller
import play.api.libs.ws.WSResponse
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import wshandler.evecentral.MarketStatCaller
import wshandler.evecentral.{MarketStatResponse, MarketStatSegment, MarketStatQueryInfo}


case class MarketInfo(market: Int, statMap : Map[String, PriceInfo])
case class PriceInfo(buy: MarketStats, sell: MarketStats, all: MarketStats)
case class MarketStats(volume: Double, generated: Double, avg: Double, wavg: Double, variance: Double,
    stdDev: Double, median: Double, fivePercent: Double, max: Double, min: Double)


object WebapiController extends Controller{
  
  implicit val marketStatsWrites = Json.writes[MarketStats]
  implicit val priceInfoWrites = Json.writes[PriceInfo]
  implicit val marketInfoWrites = Json.writes[MarketInfo]
  //implicit val marketStatQueryInfoReads = Json.reads[MarketStatQueryInfo]
  
  val idListForm = Form(
    tuple(
      "system" -> number,
      "idList" -> list(number(min=0))
    )
  )
  
  def webapiTest = Action{ implicit request =>
    val requestData = idListForm.bindFromRequest.get
    Ok("Got stuff: " + requestData)
  }
  
//  def getMarketInfo = Action.async{ implicit request =>
  def getMarketInfo = Action.async{ implicit request =>
    val requestData = idListForm.bindFromRequest.get
    
//    val locale = 10000002
//    val typelist : Set[Int] = Set(34, 35)
//    Ok("future info get!" + MarketStatCaller.getMarketStatApiCall(10000002, Set(34,35)));
    
    val locale = requestData._1
    val typelist : Set[Int] = requestData._2.toSet
    val result : Future[JsResult[Array[MarketStatResponse]]] = 
        MarketStatCaller.getParsedMarketStatApiCall(locale, typelist)
    
    result.map { res =>
      val response : Array[MarketStatResponse] = res match {
        case s : JsSuccess[Array[MarketStatResponse]] => s.get
        case e : JsError => null
      }
      if(response == null){ Ok("failed")}
      val priceInfoSeq : Map[String, PriceInfo] = response.map { info =>  
        val typeid = info.all.forQuery.types(0)
        val buy: MarketStats = MarketStats(
            info.buy.volume, info.buy.generated, info.buy.avg, info.buy.wavg, info.buy.variance,
            info.buy.stdDev, info.buy.median, info.buy.fivePercent, info.buy.max, info.buy.min)
        val sell: MarketStats = MarketStats(
            info.sell.volume, info.sell.generated, info.sell.avg, info.sell.wavg, info.sell.variance,
            info.sell.stdDev, info.sell.median, info.sell.fivePercent, info.sell.max, info.sell.min)
        val all: MarketStats = MarketStats(
            info.all.volume, info.all.generated, info.all.avg, info.all.wavg, info.all.variance,
            info.all.stdDev, info.all.median, info.all.fivePercent, info.all.max, info.all.min)
        typeid.toString() -> PriceInfo(buy,sell,all)
      }.toMap
      Ok(Json.toJson(MarketInfo(locale, priceInfoSeq)))
    }
  }
  
  
}



/*
 * forQuery: MarketStatQueryInfo, volume: Double, wavg: Double, avg: Double,
    variance: Double, stdDev: Double, median: Double, fivePercent: Double, max: Double,
    min: Double, highToLow: Boolean, generated: Double)
 */
