package helpers

import play.api.data.{Field, Form}
import play.api.i18n.{Lang, Messages}
import scala.language.existentials

case class WrappedForm[A](form: Form[A]) {
  private val errorForm = form.bind(form.mapping.mappings.map(_.key -> "").toMap)
  def apply(key: String): WrappedField = WrappedField(form(key), this)
  def errorMsg(key: String)(implicit lang: Lang): String =
    errorForm(key).error.map(e => Messages(e.message, e.args: _*)).getOrElse("")
}

case class WrappedField(field: Field, private val wrappedForm: WrappedForm[_]) {
  def inputType: String = {
    field.format match {
      case Some(("format.date", _)) => "date"
      case _ => "text"
    }
  }
  def required: Boolean =
    field.constraints.exists(_._1 == "constraint.required")

  def onErrorMsg(implicit lang: Lang): String = wrappedForm.errorMsg(field.name)
}
