package controllers

import com.innoq.rocaplay.domain.issues.Issue
import helpers.{Pagination, ConditionalLayout, HalFormat}
import HalFormat._
import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.ValidationError
import play.api.http.MimeTypes
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.{Accepting, Action, Controller}

import scala.concurrent.Future

object Issues extends Controller with ConditionalLayout {

  import wiring.ApplicationConfig._

  implicit val ec = issuesDbExecutionContext

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
  
  implicit val issueReads = Json.reads[IssueData]

  def index = Action {
    Redirect(routes.Issues.issues())
  }

  def issues(offset: Int, count: Int, projectName: String) = ConditionalLayoutAction.async { implicit req =>
    val issuesF = issueRepository.findByProjectName(projectName, offset, count)
    issuesF map { issues =>
      render {
        case Accepts.Html() =>
          val view =
            if (req.requiresLayout) views.html.issuesPage.render _ else views.html.issues.render _
          Ok(view(
            issues.items,
            Pagination.Navigation(issues, count, projectName),
            projectName))
        case HalFormat.accept() =>
          val halJson = HalFormat.issuesToHal(issues, count, projectName)
          Ok(halJson)
      }
    }
  }

  def load(id: String) = Action.async {
    val issue = issueRepository.findById(id)
    issue map { issue =>
      issue.fold(NotFound(""))(i => Ok(HalFormat.issueToHal(i)))
    }
  }

  def newIssue = Action {
    Ok(views.html.issueFormPage(issueForm.fill(IssueData(summary = "", reporter = "You", openDate = new DateTime))))
  }
  
  val multipartAccept = Accepting("application/x-www-form-urlencoded")

  def error(msg: String) = Json.obj("msg" -> msg)

  type JsonErrors = Seq[(JsPath, Seq[ValidationError])]

  def error(errors: JsonErrors) = {
    val errorFields = errors.map {
      case (path, vErrors) =>
        path.toString() -> JsArray(vErrors.map(error =>
          JsString(Messages.apply(error.message, error.args))))
    }
    Json.obj("validation" -> JsObject(errorFields))
  }

  def submit = Action.async { implicit request =>
    render.async {
      case multipartAccept() =>
        issueForm.bindFromRequest.fold(
          errors => Future.successful(BadRequest(views.html.issueFormPage(errors))),
          issueData => issueRepository.save(IssueData toNewIssue issueData) map (_ => Redirect(routes.Issues.issues()))
        )
      case Accepts.Json() =>
        request.body.asJson.map { json =>
          Logger.info(json.toString())
          val issueData = Json.fromJson[IssueData](json)
          issueData.fold(errors => Future.successful(BadRequest(error(errors))), issue => {
            val res = issueRepository.save(IssueData toNewIssue issue)
            res.map(r => Ok)
          })
        }.getOrElse(Future.successful(BadRequest(error("parse error -> json expected"))))
    }
  }

}
