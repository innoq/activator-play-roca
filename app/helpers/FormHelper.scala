package helpers

import views.html.helper.FieldConstructor
import views.html.fieldConstructor

object FormHelper {
  implicit val fields = FieldConstructor(fieldConstructor.f)
}
