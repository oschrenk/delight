package com.oschrenk.delight.ui

import java.time.{LocalDate, LocalTime}


object Select {
  def byTeacher: Set[String] => ClassFilter =
    teachers => c => teachers.contains(c.teacher)
  def byPreferredTime: Option[LocalTime] => ClassFilter =
    time => c => time.forall(t => c.time.start.toLocalTime.isAfter(t))
  def onDate: Option[LocalDate] => ClassFilter =
    date => c => date.forall(d => c.time.start.toLocalDate == d)
}
