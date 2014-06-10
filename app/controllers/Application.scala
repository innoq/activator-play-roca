package controllers

import play.api.mvc.{Action, Controller}
import models.Issue
import play.api.data._
import play.api.data.Forms._
import org.joda.time.DateTime

object Application extends Controller {
  
  val issueForm = Form(
    mapping(
      "projectName" -> optional(text),
      "priority" -> optional(text),
      "issueType" -> optional(text),
      "summary" -> nonEmptyText,
      "exceptionStackTrace" -> optional(text),
      "description" -> optional(text),
      "reporter" -> nonEmptyText,
      "componentName" -> optional(text),
      "componentVersion" -> optional(text),
      "processingState" -> optional(text),
      "openDate" -> jodaDate,
      "closeDate" -> optional(jodaDate),
      "closeAction" -> optional(text),
      "assignee" -> optional(text),
      "comment" -> optional(text)
    )(Issue.applyWithoutId)(Issue.unapplyWithoutId)
  )

  def index = Action {
    Redirect(routes.Application.issues())
  }

  def issues(offset: Int, count: Int, projectName: String) = Action {
    val issues = Issue.load(offset, count, projectName)

    var links = Map[String, String]()
    if(issues.hasPrevious)
      links += ("prev" -> routes.Application.issues(Math.max(0, offset-count), count, projectName).toString)
    if(issues.hasNext)
      links += ("next" -> routes.Application.issues(issues.nextOffset, count, projectName).toString)

    Ok(views.html.issues(
      issues.items,
      links
    ))
  }

  def newIssue = Action {
    Ok(views.html.issueForm(issueForm.fill(Issue.applyWithoutId(summary = "", reporter = "You", openDate = new DateTime))))
  }

  def submit = Action { implicit request =>
    issueForm.bindFromRequest.fold(
      errors => {
        println(errors)
        BadRequest(views.html.issueForm(errors))
      },
      issue => {
        Issue.save(issue)
        Redirect(routes.Application.issues())
      })
  }
}
