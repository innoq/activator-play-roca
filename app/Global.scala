import play.api._
import models.Issue
import java.util.Date
import scala.util.Random

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    // Add new issues if less than 10 in the DB
    val issues = Issue.load(0, 10)
    val users = List("Frank", "Michael", "Jacob")
    if(issues.total < 10) {
      for (i <- 1 to 11)
        Issue.save(Issue(System.currentTimeMillis()+i, "projectName", "priority", "issueType", "summary", new Exception().toString,
          "description", "reporter", "componentName", "componentVersion", "processingState", new Date(), new Date(), "closeAction",
          users(Random.nextInt(3)), "comment"))
      Logger.debug("Added 11 new issues")
    }

    Logger.info("Application has started")
  }

}
