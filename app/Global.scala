import org.joda.time.DateTime
import play.api._
import models.Issue
import scala.util.Random

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    // Add new issues if less than 10 in the DB
    val issues = Issue.load(0, 10)
    val users = List("Frank", "Michael", "Jacob")
    if(issues.total < 10) {
      for (i <- 1 to 11)
        Issue.save(Issue(System.currentTimeMillis()+i, Some("projectName"), Some("priority"), Some("issueType"), "summary", Some(new Exception().toString),
          Some("description"), users(Random.nextInt(users.size)), Some("componentName"), Some("componentVersion"), Some("processingState"), new DateTime, Some(new DateTime), Some("closeAction"),
          Some(users(Random.nextInt(users.size))), Some("comment")))
      Logger.debug("Added 11 new issues")
    }

    Logger.info("Application has started")
  }

}
