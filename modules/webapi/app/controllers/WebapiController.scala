package webapi.controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import play.api.data._
import play.api.data.Forms._

object WebapiController extends Controller{
  
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

}