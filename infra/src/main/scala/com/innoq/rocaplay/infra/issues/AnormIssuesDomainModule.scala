package com.innoq.rocaplay
package infra.issues

import com.innoq.rocaplay.domain.issues.{IssueRepository, IssuesDomainModule}
import play.api.Application
import scala.concurrent.ExecutionContext

trait AnormIssuesDomainModule extends IssuesDomainModule {

  implicit def currentApplication: Application
  implicit def executionContext: ExecutionContext

  override def issueRepository: IssueRepository = new AnormIssueRepository()
}
