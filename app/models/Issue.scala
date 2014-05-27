package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import java.util.Date

case class Issue(id: Int, projectName: String, priority: String, issueType: String, summary: String,
                 exceptionStackTrace: String, description: String, reporter: String, componentName: String,
                 componentVersion: String, processingState: String, openDate: Date, closeDate: Date,
                 closeAction: String, user: Int, comment: String)

object Issue {

  private val issueParser: RowParser[Issue] = {
    get[Pk[Int]]("id") ~
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
      get[Int]("USER_NAME") ~
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
            INSERT INTO User(id, project_name, priority, issue_type, summary, exception_stack_trace, description, reporter, component_name, component_version, processing_state, open_date, close_date, close_action, user, comment)
            VALUES({username}, {email}, {age})
             """).on(
          /*'username -> user.username,
          'email -> user.email,
          'age -> user.age*/
        ).executeUpdate
    }

  }
}