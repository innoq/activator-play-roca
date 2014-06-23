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

class AnormIssueRepository(implicit
    application: Application,
    executionContext: ExecutionContext) extends IssueRepository {

  override def save(issue: Issue) = Future {
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

  override def findByProjectName(projectName: String, offset: Int, count: Int) = Future {
    val issues = DB.withConnection {
      implicit c =>
        SQL(s"select * from issue where project_name like {projectName} limit {limit} offset {offset}")
          .on("limit" -> count, "offset" -> offset, "projectName" -> s"%$projectName%")
          .as(issueParser.*)
    }
    val total = DB.withConnection {
      implicit c =>
        SQL("select count(*) from issue where project_name like {projectName}")
          .on("projectName" -> s"%$projectName%")
          .as(scalar[Long].single)
    }
    Page(issues, offset, total)
  }


  override def findAll(offset: Int, count: Int): Future[Page[Issue]] = findByProjectName("", offset, count)

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


}
