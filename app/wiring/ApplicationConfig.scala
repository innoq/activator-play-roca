package wiring

import com.innoq.rocaplay.domain.issues.IssuesDomainModule
import com.innoq.rocaplay.infra.issues.AnormIssuesDomainModule
import scala.concurrent.ExecutionContext

trait ApplicationConfig extends IssuesDomainModule with AnormIssuesDomainModule {
  override implicit def currentApplication = play.api.Play.current
  override implicit def executionContext = ExecutionContext.global
}

object ApplicationConfig extends ApplicationConfig
