package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import java.util.Date
import anorm.~

case class Issue(id: Long, projectName: String, priority: String, issueType: String, summary: String,
                 exceptionStackTrace: String, description: String, reporter: String, componentName: String,
                 componentVersion: String, processingState: String, openDate: Date, closeDate: Date,
                 closeAction: String, user: Long, comment: String)

object Issue {

  private val issueParser: RowParser[Issue] = {
    get[Pk[Long]]("id") ~
      get[String]("project_name") ~
      get[String]("priority") ~
      get[String]("issue_type") ~
      get[String]("summary") ~
      get[String]("exception_stack_trace") ~
      get[String]("description") ~
      get[String]("reporter") ~
      get[String]("component_name") ~
      get[String]("component_version") ~
      get[String]("processing_state") ~
      get[Long]("open_date") ~
      get[Long]("close_date") ~
      get[String]("close_action") ~
      get[Long]("userid") ~
      get[String]("comment") map {
      case id ~ projectName ~ priority ~ issueType ~ summary ~ exceptionStackTrace ~ description ~ reporter ~ componentName ~ componentVersion ~ processingState ~ openDate ~ closeDate ~ closeAction ~ user ~ comment =>
        Issue(id.get, projectName, priority, issueType, summary, exceptionStackTrace, description, reporter, componentName, componentVersion, processingState, new Date(openDate), new Date(closeDate), closeAction, user, comment)
    }
  }

  def list() = {
    DB.withConnection {
      implicit c =>
        SQL("Select * from Issue").as(issueParser *)
    }
  }

  def save(issue: Issue) = {
    DB.withConnection {
      implicit connection =>
        SQL( """
            INSERT INTO Issue(id, project_name, priority, issue_type, summary, exception_stack_trace, description, reporter, component_name, component_version, processing_state, open_date, close_date, close_action, userid, comment)
            VALUES({id}, {projectName}, {priority}, {issueType}, {summary}, {exceptionStackTrace}, {description}, {reporter}, {componentName}, {componentVersion}, {processingState}, {openDate}, {closeDate}, {closeAction}, {user}, {comment})
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
          'closeDate -> issue.closeDate.getTime,
          'closeAction -> issue.closeAction,
          'user -> issue.user,
          'comment -> issue.comment).executeUpdate
    }
  }
}