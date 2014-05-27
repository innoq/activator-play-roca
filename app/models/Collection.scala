package models

case class Collection[T](items: List[T], offset: Int, total: Long)
