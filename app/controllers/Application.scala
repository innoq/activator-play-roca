package controllers

import _root_.java.lang.Math
import play.api.mvc.{Action, Controller}
import models.Issue
import java.util.Date

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Hello Play Framework"))
  }

  def issues(offset: Int, count: Int) = Action {
    Issue.save(Issue(System.currentTimeMillis(), "projectName", "priority", "issueType", "summary", new Exception().toString,
      "description", "reporter", "componentName", "componentVersion", "processingState", new Date(), new Date(), "closeAction",
      "hannes", "comment"))

    val issues = Issue.load(offset, count)
    var links = Map[String, String]()

    if(offset > 0)
      links += ("prev" -> routes.Application.issues(Math.max(0, offset-count), count).toString)

    val nextOffset = offset+count
    if(nextOffset < issues.total)
      links += ("next" -> routes.Application.issues(nextOffset, count).toString)

    Ok(views.html.issues(
      "List of " + issues.items.size + " issue(s) (total: " + issues.total + ")",
      issues.items,
      links
    ))
  }
}
