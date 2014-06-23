package helpers

object NavMenu {
  sealed trait Item
  case object AllIssues extends Item
  case object NewIssue extends Item
}
