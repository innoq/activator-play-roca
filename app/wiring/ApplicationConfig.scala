package wiring

import com.innoq.rocaplay.domain.issues.IssuesDomainModule
import com.innoq.rocaplay.infra.issues.AnormIssuesDomainModule
import com.softwaremill.macwire.MacwireMacros._
import controllers.Issues
import play.api.libs.concurrent.Akka

trait ApplicationConfig extends IssuesDomainModule with AnormIssuesDomainModule {
  override implicit def currentApplication = play.api.Play.current
  override def issuesDbExecutionContext = Akka.system.dispatchers.lookup("issues-db-context")
  val issues = wire[Issues]
}

object ApplicationConfig extends ApplicationConfig
