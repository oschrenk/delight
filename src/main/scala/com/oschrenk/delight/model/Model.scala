package com.oschrenk.delight.model

case class Class(id: Int, time: Time, name: String, place: Place, teacher: String, experience: Option[String], bookable: Boolean)

case class Attendance(time: Time, name: String, place: Place, teacher: String, present: Boolean)

case class Schedule(private val classes: Seq[Class]) {
  val all: Seq[Class] = classes
}
