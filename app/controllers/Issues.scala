package controllers

import com.innoq.rocaplay.domain.issues.{IssueRepository, Issue}
import helpers._
import HalFormat._
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class Issues(issueRepository: IssueRepository, ec: ExecutionContext) extends Controller with ConditionalLayout with JsonRequests {

  implicit val e = ec
  import Issues._

  implicit val issueReads = Json.reads[IssueData]

  def index = Action {
    Redirect(routes.Issues.issues())
  }

  def issues(offset: Int, count: Int, projectName: String) = ConditionalLayoutAction.async { implicit req =>
    issueRepository.findByProjectName(projectName, offset, count) map { issues =>
      render {
        case Accepts.Html() =>
          val view =
            if (req.requiresLayout) views.html.issuesPage.render _ else views.html.issues.render _
          Ok(view(
            issues.items,
            Pagination.Navigation(issues, count, projectName),
            projectName))
        case HalFormat.accept() | Accepts.Json() =>
          val halJson = HalFormat.issuesToHal(issues, count, projectName)
          Ok(halJson)
      }
    }
  }

  def load(id: String) = Action.async { implicit request =>
    issueRepository.findById(id).map { issue =>
      issue.map { issue =>
        render {
          case Accepts.Html() =>
           Ok(views.html.issueFormPage(
             WrappedForm(issueForm.fill(IssueData toIssueData issue)),
             routes.Issues.updateUnsafe(id)
           ))
          case HalFormat.accept() | Accepts.Json() =>
           Ok(HalFormat.issueToHal(issue))
        }
      }.getOrElse(NotFound)
    }
  }

  def newIssue = Action { implicit  req =>
    Ok(views.html.issueFormPage(
      WrappedForm(issueForm.fill(
        IssueData(summary = "", reporter = "You", openDate = new DateTime))),
      routes.Issues.submit()
    ))
  }

  def updateUnsafe(id: String) = Action.async { implicit request =>
    render.async {
      case FormHelper.accept() =>
        issueForm.bindFromRequest.fold(
          errors => Future.successful(BadRequest(views.html.issueFormPage(
            WrappedForm(errors), routes.Issues.updateUnsafe(id)))),
          issueData => issueRepository.save(IssueData.toIssue(issueData, id)) map (_ => Redirect(routes.Issues.issues()))
        )
    }
  }

  def update(id: String) = Action.async { implicit request =>
    render.async {
      case Accepts.Json() =>
        jsonAction[IssueData](issue => issueRepository.save(IssueData.toIssue(issue, id)).map(res => Ok))
    }
  }

  def submit = Action.async { implicit request =>
    render.async {
      case FormHelper.accept() =>
        issueForm.bindFromRequest.fold(
          errors => Future.successful(BadRequest(views.html.issueFormPage(
            WrappedForm(errors), routes.Issues.newIssue()))),
          issueData => issueRepository.save(IssueData toNewIssue issueData) map (_ => Redirect(routes.Issues.issues()))
        )
      case Accepts.Json() =>
        jsonAction[IssueData](issue => issueRepository.save(IssueData toNewIssue issue).map(res => Ok))
    }
  }

}

object Issues {
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

    def toIssue(issueData: IssueData, issueId: String): Issue = Issue(
      issueId,
      issueData.projectName, issueData.priority, issueData.issueType, issueData.summary, issueData.exceptionStackTrace,
      issueData.description, issueData.reporter, issueData.componentName, issueData.componentVersion,
      issueData.processingState, issueData.openDate, issueData.closeDate, issueData.closeAction,
      issueData.assignee, issueData.comment)

    def toIssueData(issues: Issue): IssueData = {
      IssueData(issues.projectName, issues.priority, issues.issueType, issues.summary, issues.exceptionStackTrace,
        issues.description, issues.reporter, issues.componentName, issues.componentVersion,
        issues.processingState, issues.openDate, issues.closeDate, issues.closeAction,
        issues.assignee, issues.comment)
    }
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

}
