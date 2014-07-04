import com.innoq.rocaplay.domain.issues.Issue
import com.softwaremill.macwire.{InstanceLookup, Macwire}
import controllers.Issues
import org.joda.time.DateTime
import play.api._
import scala.concurrent.ExecutionContext
import scala.util.Random
import wiring.ApplicationConfig

trait Global extends GlobalSettings with Macwire {

  def instanceLookup: InstanceLookup
  override def getControllerInstance[A](c: Class[A]) = instanceLookup.lookupSingleOrThrow(c)

  protected def applicationConfig: ApplicationConfig
  private def issueRepository = applicationConfig.issueRepository
  implicit private def ec: ExecutionContext = applicationConfig.issuesDbExecutionContext

  override def onStart(app: Application) {
    // Add new issues if less than 10 in the DB
    val issuesF = issueRepository.findAll(0, 10)
    issuesF.foreach { issues =>
      val users = List("Frank", "Michael", "Jacob")
      if(issues.total < 10) {
        for (i <- 1 to 11)
          issueRepository.save(Issue(Some("projectName"), Some("priority"), Some("issueType"), "summary", Some(new Exception().toString),
            Some("description"), users(Random.nextInt(users.size)), Some("componentName"), Some("componentVersion"), Some("processingState"), new DateTime, Some(new DateTime), Some("closeAction"),
            Some(users(Random.nextInt(users.size))), Some("comment")))
        Logger.debug("Added 11 new issues")
      }
    }
    Logger.info("Application has started")
  }
}

object Global extends Global {
  override def instanceLookup = InstanceLookup(valsByClass(ApplicationConfig))
  override def applicationConfig = ApplicationConfig
}
