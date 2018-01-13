package com.oschrenk.delight.ui

import java.time.LocalTime


object Select {
  def byTeacher: Set[String] => ClassFilter =
    teachers => c => teachers.contains(c.teacher)
  def byPreferredTime: Option[LocalTime] => ClassFilter =
    time => c => time.map(t => c.time.start.toLocalTime.isAfter(t)).getOrElse(true)
}
