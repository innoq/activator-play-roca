package helpers

import play.api.libs.json.{Reads, Json}
import play.api.mvc.{Controller, Result, AnyContent}

import scala.concurrent.Future

trait JsonRequests {

  self: Controller =>

  def jsonAction[T](toRes: T => Future[Result])(implicit request: play.api.mvc.Request[AnyContent], reads: Reads[T]) = {
    request.body.asJson.map { json =>
      Json.fromJson[T](json)
        .fold(errors => Future.successful(BadRequest(JsonFormat.error(errors))), toRes)
    }.getOrElse(Future.successful(BadRequest(JsonFormat.error("parse error -> json expected"))))
  }

}
