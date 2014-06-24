package wiring

import com.innoq.rocaplay.domain.issues.IssuesDomainModule
import com.innoq.rocaplay.infra.issues.AnormIssuesDomainModule
import play.api.libs.concurrent.Akka

trait ApplicationConfig extends IssuesDomainModule with AnormIssuesDomainModule {
  override implicit def currentApplication = play.api.Play.current
  override def issuesDbExecutionContext = Akka.system.dispatchers.lookup("issues-db-context")
}

object ApplicationConfig extends ApplicationConfig
