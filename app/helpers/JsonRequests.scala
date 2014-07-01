package helpers

import controllers.Issues._
import play.api.libs.json.{Reads, Json}
import play.api.mvc.{Result, AnyContent}

import scala.concurrent.Future

trait JsonRequests {

  def jsonAction[T](toRes: T => Future[Result])(implicit request: play.api.mvc.Request[AnyContent], reads: Reads[T]) = {
    request.body.asJson.map { json =>
      val data = Json.fromJson[T](json)
      data.fold(errors => Future.successful(BadRequest(JsonFormat.error(errors))), r => {
        toRes(r)
      })
    }.getOrElse(Future.successful(BadRequest(JsonFormat.error("parse error -> json expected"))))
  }

}
