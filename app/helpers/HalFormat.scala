package helpers

import com.innoq.rocaplay.domain.Page
import com.innoq.rocaplay.domain.issues.Issue
import helpers.Pagination.Navigation
import org.joda.time.DateTime
import play.api.hal.{Hal, HalLink, HalResource}
import play.api.http.Writeable
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Accepting, Codec}
import play.api.hal.Hal._
import controllers.routes

object HalFormat {

  implicit val issueWrites = (
    (__ \ 'id).write[String] and
    (__ \ 'projectName).writeNullable[String] and
    (__ \ 'priority).writeNullable[String] and
    (__ \ 'issueType).writeNullable[String] and
    (__ \ 'summary).write[String] and
    (__ \ 'exceptionStackTrace).writeNullable[String] and
    (__ \ 'description).writeNullable[String] and
    (__ \ 'reporter).write[String] and
    (__ \ 'componentName).writeNullable[String] and
    (__ \ 'componentVersion).writeNullable[String] and
    (__ \ 'processingState).writeNullable[String] and
    (__ \ 'openDate).write[DateTime] and
    (__ \ 'closeDate).writeNullable[DateTime] and
    (__ \ 'closeAction).writeNullable[String] and
    (__ \ 'assignee).writeNullable[String] and
    (__ \ 'comment).writeNullable[String]
  )(unlift(Issue.unapply))

  def issueToHal(issue: Issue) = {
    Hal.state(issue) include HalLink("self", routes.Issues.load(issue.id).url)
  }

  def issuesToHal(page: Page[Issue], count: Int, projectName: String): HalResource = {
    val nav = Navigation(page, count, projectName)
    val self = HalLink("self", routes.Issues.issues(page.offset, count, projectName).url)
    val next = nav.next map (n => HalLink("next", n.url))
    val prev = nav.prev map (p => HalLink("previous", p.url))
    val links = List(Some(self), next, prev).flatten
    val issues = Hal.embedded("issues", toIssuesResources(page): _*)
    links.foldLeft(issues)(_ ++ _) include issueStats(page.items.size, count)
  }

  def issueStats(size: Int, max: Int) =
    Hal.state(Json.obj("nrOfIssues" -> size, "selectedMax" -> max))

  def toIssuesResources(page: Page[Issue]): List[HalResource] = {
    page.items map (i => issueToHal(i))
  }

  implicit def halWriter(implicit code: Codec): Writeable[HalResource] =
    Writeable(d => code.encode(Json.toJson(d).toString()), Some("application/hal+json"))

  val accept = Accepting("application/hal+json")

}
