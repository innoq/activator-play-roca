package helpers

import com.innoq.rocaplay.domain.Page
import controllers.routes
import play.api.mvc.Call

object Pagination {

  case class Navigation(prev: Option[Call], next: Option[Call])
  object Navigation {
    def apply(page: Page[_], count: Int, projectName: String): Navigation = {
      val prev = prevLink(page, count, projectName)
      val next = nextLink(page, count, projectName)
      Navigation(prev, next)
    }
  }

  def prevLink(page: Page[_], count: Int, projectName: String) =
    if (page.hasPrevious)
      Some(routes.Issues.issues(Math.max(0, page.offset - count), count, projectName))
    else None

  def nextLink(page: Page[_], count: Int, projectName: String) =
    if (page.hasNext)
      Some(routes.Issues.issues(page.nextOffset, count, projectName))
    else None


}
