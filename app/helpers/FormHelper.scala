package helpers

import play.api.mvc.Accepting
import views.html.helper.FieldConstructor
import views.html.fieldConstructor

object FormHelper {
  implicit val fields = FieldConstructor(fieldConstructor.f)

  val accept = Accepting("application/x-www-form-urlencoded")
}
