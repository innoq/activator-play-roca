package models

case class Collection[T](items: List[T], offset: Int, total: Long) {

  def hasNext = nextOffset < total

  def hasPrevious = offset > 0

  def nextOffset = offset + items.size

  def displaying =
    if (!items.isEmpty) {
      val from = offset + 1
      val to = from + items.size
      s"Displaying $from to $to of $total."
    } else {
      "Nothing to display."
    }
}
