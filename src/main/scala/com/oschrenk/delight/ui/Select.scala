package com.oschrenk.delight.ui

object Select {
  def byTeacher: Set[String] => ClassFilter =
    teachers => c => teachers.contains(c.teacher)
}
