package com.innoq.rocaplay
package infra.issues

import domain.Page
import domain.issues.IssueRepository

import scala.concurrent.{ExecutionContext, Future}
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import com.innoq.rocaplay.domain.issues.Issue
import org.joda.time.DateTime
import play.api.Application

class AnormIssueRepository(
    application: Application,
    executionContext: ExecutionContext) extends IssueRepository {

  private implicit val app = application
  private implicit val ec = executionContext

  override def save(issue: Issue) = Future {
    DB.withTransaction {
      implicit connection =>
        val update =
          SQL"""
            update issue set project_name = ${issue.projectName}, priority = ${issue.priority},
            issue_type = ${issue.issueType}, summary = ${issue.summary},
            exception_stack_trace = ${issue.exceptionStackTrace}, description = ${issue.description},
            reporter = ${issue.reporter}, component_name = ${issue.componentName},
            component_version = ${issue.componentVersion}, processing_state = ${issue.processingState},
            open_date = ${issue.openDate.getMillis}, close_date = ${issue.closeDate.map(_.getMillis)},
            close_action = ${issue.closeAction},
            assignee = ${issue.assignee}, comment = ${issue.comment} where id = ${issue.id}
             """
        val insert =
        SQL"""
            insert into issue(id, project_name, priority, issue_type, summary, exception_stack_trace, description,
            reporter, component_name, component_version, processing_state, open_date, close_date, close_action,
            assignee, comment) values(${issue.id}, ${issue.projectName}, ${issue.priority}, ${issue.issueType},
            ${issue.summary}, ${issue.exceptionStackTrace}, ${issue.description},
            ${issue.reporter}, ${issue.componentName}, ${issue.componentVersion}, ${issue.processingState},
            ${issue.openDate.getMillis}, ${issue.closeDate.map(_.getMillis)}, ${issue.closeAction}, ${issue.assignee}, ${issue.comment})
             """
        val result = update.executeUpdate()
        if (result > 0) ()
        else insert.executeUpdate()
    }
  }

  override def findByProjectName(projectName: String, offset: Int, count: Int) = Future {
     DB.withConnection {
      implicit c => {
        val where = if (projectName.isEmpty) "" else "where project_name like {projectName}"
        val query = SQL("select * from issue " + where + "order by open_date limit {count} offset {offset}")
          .on(
            'projectName -> s"%$projectName%",
            'count -> count,
            'offset -> offset
          )
        val issues = query.as(issueParser.*)
        val total = SQL("select count(*) from issue " + where)
          .on('projectName -> s"%$projectName%")
          .as(scalar[Long].single)
        Page(issues, offset, total)
      }
     }
  }

  override def findAll(offset: Int, count: Int): Future[Page[Issue]] = findByProjectName("", offset, count)

  override def findById(issueId: String): Future[Option[Issue]] = Future {
    DB.withConnection {
      implicit c =>
        SQL"select * from issue where id = $issueId".as(issueParser.singleOpt)
    }
  }

  private val issueParser: RowParser[Issue] = {
    get[String]("id") ~
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
        Issue(id, projectName, priority, issueType, summary, exceptionStackTrace,
          description, reporter, componentName, componentVersion, processingState,
          new DateTime(openDate), closeDate.map(new DateTime(_)), closeAction, assignee, comment)
    }
  }


}
