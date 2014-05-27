package controllers

import play.api.mvc.{Action, Controller}
import models.Issue
import play.api.data._
import play.api.data.Forms._
import java.util.Date

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
      "openDate" -> date,
      "closeDate" -> optional(date),
      "closeAction" -> optional(text),
      "assignee" -> optional(text),
      "comment" -> optional(text)
    )(Issue.applyWithoutId)(Issue.unapplyWithoutId)
  )

  def index = Action {
    Redirect(routes.Application.issues())
  }

  def issues(offset: Int, count: Int) = Action {
    val issues = Issue.load(offset, count)

    var links = Map[String, String]()
    if(issues.hasPrevious)
      links += ("prev" -> routes.Application.issues(Math.max(0, offset-count), count).toString)
    if(issues.hasNext)
      links += ("next" -> routes.Application.issues(issues.nextOffset, count).toString)

    Ok(views.html.issues(
      "List of " + issues.items.size + " issue(s) (total: " + issues.total + ")",
      issues.items,
      links
    ))
  }

  def newIssue = Action {
    Ok(views.html.issueForm(issueForm.fill(Issue.applyWithoutId(None, None, None, "", None, None, "You", None, None, None, new Date(), None, None, None, None))))
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
