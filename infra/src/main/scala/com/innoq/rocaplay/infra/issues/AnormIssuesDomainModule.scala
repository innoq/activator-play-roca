package com.innoq.rocaplay
package infra.issues

import com.innoq.rocaplay.domain.issues.{IssueRepository, IssuesDomainModule}
import play.api.Application
import scala.concurrent.ExecutionContext

trait AnormIssuesDomainModule extends IssuesDomainModule {

  import com.softwaremill.macwire.MacwireMacros._

  implicit def currentApplication: Application
  def issuesDbExecutionContext: ExecutionContext

  override lazy val issueRepository: IssueRepository = wire[AnormIssueRepository]
}
