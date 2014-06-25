package com.innoq.rocaplay.domain
package issues

import scala.concurrent.Future

trait IssueRepository {

  def save(issue: Issue): Future[Unit]
  def findByProjectName(projectName: String, offset: Int, count: Int): Future[Page[Issue]]
  def findAll(offset: Int, count: Int): Future[Page[Issue]]
  def findById(issueId: String): Future[Option[Issue]]

}
