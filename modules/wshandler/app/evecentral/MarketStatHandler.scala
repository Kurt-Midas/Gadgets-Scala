package wshandler.evecentral

import javax.inject.Inject
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.libs.ws._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.functional.syntax._



case class MarketStatResponse(buy: MarketStatSegment, all: MarketStatSegment, sell: MarketStatSegment)
case class MarketStatSegment(forQuery: MarketStatQueryInfo, volume: Double, wavg: Double, avg: Double,
    variance: Double, stdDev: Double, median: Double, fivePercent: Double, max: Double,
    min: Double, highToLow: Boolean, generated: Double)
case class MarketStatQueryInfo(types: Array[Int], regions: Array[Int], systems: Array[Int], 
    hours: Int, minq: Int)
    //"bid" is null for the "all" case and throws errors. Unused and excluded. 

class MarketStatHandler {//@Inject() (ws: WSClient) {
  
  implicit val marketStatQueryInfoReads = Json.reads[MarketStatQueryInfo]
  implicit val marketStatSegmentReads = Json.reads[MarketStatSegment]
  implicit val marketStatResponseReads = Json.reads[MarketStatResponse]

    
  def getMarketStatApiCall(market: Int, typeList : Set[Int]) : Future[WSResponse] = {
  /*
   * Example:
   * http://api.eve-central.com/api/marketstat/json?typeid=34&typeid=35&regionlimit=10000002
   * Form data is uglier plus .withQueryString may not allow multiple "typeid"
   */
    val baseUrl = "http://api.eve-central.com/api/marketstat/json?"
    val locale : String = {
      if(market > 30000000) "usesystem="
        else "regionlimit="
    } + market.toString()
    
    var types : String = "";
    typeList.foreach { types += "&typeid=" + _ }
    
    print(baseUrl + locale + types)
    WS.url(baseUrl +
      locale +
      types).get()
  }
  
  def getParsedMarketStatApiCall(market: Int, typeList : Set[Int]) : 
          Future[JsResult[Array[MarketStatResponse]]] = {
    val baseUrl = "http://api.eve-central.com/api/marketstat/json?"
    val locale : String = {
      if(market > 30000000) "usesystem="
        else "regionlimit="
    } + market.toString()
    
    var types : String = "";
    typeList.foreach { types += "&typeid=" + _ }
    
    print(baseUrl + locale + types)
    WS.url(baseUrl +
      locale +
      types).get().map { res =>  
        res.json.validate[Array[MarketStatResponse]]
    }
  }
}

object MarketStatCaller extends MarketStatHandler


