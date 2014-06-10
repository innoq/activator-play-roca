package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime

case class Issue(id: Long, projectName: Option[String], priority: Option[String], issueType: Option[String], summary: String,
                 exceptionStackTrace: Option[String], description: Option[String], reporter: String, componentName: Option[String],
                 componentVersion: Option[String], processingState: Option[String], openDate: DateTime, closeDate: Option[DateTime],
                 closeAction: Option[String], assignee: Option[String], comment: Option[String])

object Issue {

  def applyWithoutId(projectName: Option[String] = None, priority: Option[String] = None, issueType: Option[String] = None,
                     summary: String, exceptionStackTrace: Option[String] = None, description: Option[String] = None,
                     reporter: String, componentName: Option[String] = None, componentVersion: Option[String] = None,
                     processingState: Option[String] = None, openDate: DateTime, closeDate: Option[DateTime] = None,
                     closeAction: Option[String] = None, assignee: Option[String] = None, comment: Option[String] = None) =
    this(System.currentTimeMillis(), projectName, priority, issueType, summary, exceptionStackTrace, description, reporter,
      componentName, componentVersion, processingState, openDate, closeDate, closeAction, assignee, comment)

  def unapplyWithoutId(issue: Issue) = Some((issue.projectName, issue.priority, issue.issueType, issue.summary, issue.exceptionStackTrace,
    issue.description, issue.reporter, issue.componentName, issue.componentVersion, issue.processingState, issue.openDate, issue.closeDate,
    issue.closeAction, issue.assignee, issue.comment))

  private val issueParser: RowParser[Issue] = {
    get[Pk[Long]]("id") ~
      get[Option[String]]("project_name") ~
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
        Issue(id.get, projectName, priority, issueType, summary, exceptionStackTrace,
          description, reporter, componentName, componentVersion, processingState,
          new DateTime(openDate), closeDate.map(new DateTime(_)), closeAction, assignee, comment)
    }
  }

  def load(offset: Int, count: Int, sqlInjection: String = "") = {
    val issues = DB.withConnection {
      implicit c =>
        // TODO named parameter didn't work with "like %{filter}%" or "like '%{filter}%'"
        SQL(s"select * from issue where project_name like '%$sqlInjection%' limit {limit} offset {offset}")
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
          'openDate -> issue.openDate.getMillis,
          'closeDate -> issue.closeDate.map(_.getMillis),
          'closeAction -> issue.closeAction,
          'assignee -> issue.assignee,
          'comment -> issue.comment).executeUpdate
    }
  }
}