package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import java.util.Date
import anorm.~

case class Issue(id: Long, projectName: Option[String], priority: Option[String], issueType: Option[String], summary: String,
                 exceptionStackTrace: Option[String], description: Option[String], reporter: String, componentName: Option[String],
                 componentVersion: Option[String], processingState: Option[String], openDate: Date, closeDate: Option[Date],
                 closeAction: Option[String], assignee: Option[String], comment: Option[String])

object Issue {

  def applyWithoutId(projectName: Option[String], priority: Option[String], issueType: Option[String], summary: String,
                     exceptionStackTrace: Option[String], description: Option[String], reporter: String, componentName: Option[String],
                     componentVersion: Option[String], processingState: Option[String], openDate: Date, closeDate: Option[Date],
                     closeAction: Option[String], assignee: Option[String], comment: Option[String]) =
    this(System.currentTimeMillis(), projectName, priority, issueType, summary, exceptionStackTrace, description, reporter,
      componentName, componentVersion, processingState, openDate, closeDate, closeAction, assignee, comment)

  def unapplyWithoutId(issue: Issue) = Some((issue.projectName, issue.priority, issue.issueType, issue.summary, issue.exceptionStackTrace,
    issue.description, issue.reporter, issue.componentName, issue.componentVersion, issue.processingState, issue.openDate, issue.closeDate,
    issue.closeAction, issue.assignee, issue.comment))

  private val issueParser: RowParser[Issue] = {
    get[Pk[Long]]("id") ~
      get[String]("project_name") ~
      get[Option[String]]("priority") ~
      get[Option[String]]("issue_type") ~
      get[String]("summary") ~
      get[Option[String]]("exception_stack_trace") ~
      get[Option[String]]("description") ~
      get[String]("reporter") ~
      get[Option[String]]("component_name") ~
      get[Option[String]]("component_version") ~
      get[Option[String]]("processing_state") ~
      get[Long]("open_date") ~
      get[Option[Long]]("close_date") ~
      get[Option[String]]("close_action") ~
      get[Option[String]]("assignee") ~
      get[Option[String]]("comment") map {
      case id ~ projectName ~ priority ~ issueType ~ summary ~ exceptionStackTrace ~ description ~ reporter ~
        componentName ~ componentVersion ~ processingState ~ openDate ~ closeDate ~ closeAction ~ assignee ~ comment =>
        Issue(id.get, Option(projectName), priority, issueType, summary, exceptionStackTrace,
          description, reporter, componentName, componentVersion, processingState,
          new Date(openDate), closeDate.map(new Date(_)), closeAction, assignee, comment)
    }
  }

  def load(offset: Int, count: Int) = {
    val issues = DB.withConnection {
      implicit c =>
        SQL("select * from issue limit {limit} offset {offset}")
          .on("limit" -> count, "offset" -> offset)
          .as(issueParser *)
    }
    val total = DB.withConnection {
      implicit c =>
        SQL("select count(*) from issue").as(scalar[Long].single)
    }
    Collection(issues, offset, total)
  }

  def save(issue: Issue) = {
    DB.withConnection {
      implicit connection =>
        SQL( """
            insert into issue(id, project_name, priority, issue_type, summary, exception_stack_trace, description,
            reporter, component_name, component_version, processing_state, open_date, close_date, close_action, assignee, comment)
            values({id}, {projectName}, {priority}, {issueType}, {summary}, {exceptionStackTrace}, {description},
            {reporter}, {componentName}, {componentVersion}, {processingState}, {openDate}, {closeDate}, {closeAction}, {assignee}, {comment})
             """).on(
          'id -> issue.id,
          'projectName -> issue.projectName,
          'priority -> issue.priority,
          'issueType -> issue.issueType,
          'summary -> issue.summary,
          'exceptionStackTrace -> issue.exceptionStackTrace,
          'description -> issue.description,
          'reporter -> issue.reporter,
          'componentName -> issue.componentName,
          'componentVersion -> issue.componentVersion,
          'processingState -> issue.processingState,
          'openDate -> issue.openDate.getTime,
          'closeDate -> issue.closeDate.map(_.getTime),
          'closeAction -> issue.closeAction,
          'assignee -> issue.assignee,
          'comment -> issue.comment).executeUpdate
    }
  }
}