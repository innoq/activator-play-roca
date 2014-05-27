package models

case class Collection[T](items: List[T], offset: Int, total: Long) {

  def hasNext = (items.size + offset) < total

  def hasPrevious = offset > 0

  val maxCount = 10

  def nextOffset = offset + maxCount

  def displaying = {
    if (!items.isEmpty) {
      val from = offset + 1
      val to = from + items.size
      s"Displaying $from to $to of $total."
    } else {
      "Nothing to display."
    }
  }
}
