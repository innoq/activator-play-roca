package controllers

import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._
import org.joda.time.DateTime
import com.innoq.rocaplay.infra.issues.AnormIssueRepository
import scala.concurrent.{Future, ExecutionContext}
import com.innoq.rocaplay.domain.issues.Issue

object Issues extends Controller {

  import play.api.Play.current
  import ExecutionContext.Implicits.global

  val issueRepository = new AnormIssueRepository()

  case class IssueData(
    projectName: Option[String] = None,
    priority: Option[String] = None,
    issueType: Option[String] = None,
    summary: String,
    exceptionStackTrace: Option[String] = None,
    description: Option[String] = None,
    reporter: String,
    componentName: Option[String] = None,
    componentVersion: Option[String] = None,
    processingState: Option[String] = None,
    openDate: DateTime,
    closeDate: Option[DateTime] = None,
    closeAction: Option[String] = None,
    assignee: Option[String] = None,
    comment: Option[String] = None)

  object IssueData {
    def toNewIssue(issueData: IssueData): Issue = Issue(
      issueData.projectName, issueData.priority, issueData.issueType, issueData.summary, issueData.exceptionStackTrace,
      issueData.description, issueData.reporter, issueData.componentName, issueData.componentVersion,
      issueData.processingState, issueData.openDate, issueData.closeDate, issueData.closeAction,
      issueData.assignee, issueData.comment)
  }
  
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
    )(IssueData.apply)(IssueData.unapply)
  )

  def index = Action {
    Redirect(routes.Issues.issues())
  }

  def issues(offset: Int, count: Int, projectName: String) = Action.async {
    val issuesF = issueRepository.findByProjectName(projectName, offset, count)
    issuesF map { issues =>
      var links = Map[String, String]()
      if(issues.hasPrevious)
        links += ("prev" -> routes.Issues.issues(Math.max(0, offset-count), count, projectName).toString)
      if(issues.hasNext)
        links += ("next" -> routes.Issues.issues(issues.nextOffset, count, projectName).toString)

      Ok(views.html.issues(
        issues.items,
        links
      ))
    }

  }

  def newIssue = Action {
    Ok(views.html.issueForm(issueForm.fill(IssueData(summary = "", reporter = "You", openDate = new DateTime))))
  }

  def submit = Action.async { implicit request =>
    issueForm.bindFromRequest.fold(
      errors => Future.successful(BadRequest(views.html.issueForm(errors))),
      issueData => issueRepository.save(IssueData toNewIssue issueData) map (_ => Redirect(routes.Issues.issues()))
      )
  }
}
