package com.innoq.rocaplay

package object domain {

  case class Page[T](items: List[T], offset: Int, total: Long) {
    def hasNext = nextOffset < total
    def hasPrevious = offset > 0
    def nextOffset = offset + items.size
  }

}
