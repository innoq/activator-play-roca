package controllers

import play.api.mvc.{Action, Controller}
import models.Issue

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Hello Play Framework"))
  }

  def issues = Action {
    Issue.list
    Ok(views.html.issues("List of issues"))
  }
}