package helpers

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime

object DateTimeFormatting {
  def shortDateTime(dt: DateTime): String = DateTimeFormat.shortDateTime().print(dt)
}
