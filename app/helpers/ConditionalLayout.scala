package helpers

import play.api.mvc.{ActionBuilder, WrappedRequest, Request, ActionTransformer}
import scala.concurrent.Future

trait ConditionalLayout {

  class ConditionalLayoutRequest[A](
    val requiresLayout: Boolean,
    request: Request[A]) extends WrappedRequest[A](request)

  object ConditionalLayoutAction extends ActionBuilder[ConditionalLayoutRequest] with ActionTransformer[Request, ConditionalLayoutRequest] {
    override protected def transform[A](request: Request[A]) = Future.successful {
      val layoutRequired = request.headers.get("X-PJAX") != Some("true")
      new ConditionalLayoutRequest[A](requiresLayout = layoutRequired, request)
    }
  }

}
