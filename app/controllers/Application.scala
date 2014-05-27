package controllers

import play.api.mvc.{Action, Controller}
import models.Issue
import java.util.Date

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Hello Play Framework"))
  }

  def issues = Action {
    Issue.save(Issue(System.currentTimeMillis(), "projectName", "priority", "issueType", "summary", new Exception().toString,
      "description", "reporter", "componentName", "componentVersion", "processingState", new Date(), new Date(), "closeAction",
      "hannes", "comment"))
    val l = Issue.list
    Ok(views.html.issues("List of " + l.size + " issue(s)", l))
  }
}
