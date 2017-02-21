package com.oschrenk.delight

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime}

case object Time {

  def parse(day: LocalDate, times: String): Time = {
    val t = parseTimes(times)
    Time(day.atTime(t._1), day.atTime(t._2))
  }

  // 8:30 - 10:00
  def parseTimes(times: String): (LocalTime, LocalTime) = {
    times.split("-") match {
      case Array(s,e, _*) => (parseTime(s), parseTime(e))
    }
  }

  // 8:30
  def parseTime(time: String): LocalTime = {
    time.trim.split(":") match {
        case Array(h,m, _*) => LocalTime.of(h.toInt, m.toInt)
      }
  }

  // Sun 12 Feb 2017
  private val LocalDateFormatter =  DateTimeFormatter.ofPattern("EEE dd MMM uuuu")
  def parseDay(day: String): LocalDate = {
    LocalDate.parse(day, LocalDateFormatter)
  }

  // Sun 12 Feb 2017,  08:30 - 09:30
  def parseFullDuration(s: String): Time = {
    val Array(day,times, _*) = s.trim.split(",")
    parse(parseDay(day), times)
  }
}
case class Time(start: LocalDateTime, end: LocalDateTime)

object Place {
  def from(s: String): Place = {
    s match {
      case p @ "De Clercqstraat" => Place(p, "De Clercqstraat 68", "1052 NJ", "Amsterdam", "Netherlands")
      case p @ "Nieuwe Achtergracht" => Place(p, "Nieuwe Achtergracht 11", "1018 XV", "Amsterdam", "Netherlands")
      case p @ "Prinseneiland" => Place(p, "Prinseneiland 20G", "1013 LR", "Amsterdam", "Netherlands")
      case p @ "Weteringschans" => Place(p, "Weteringschans 53", "1017 RW", "Amsterdam", "Netherlands")
      case _ => throw new IllegalArgumentException("Unknown location")
    }
  }
}
case class Place(name: String, street: String, zipcode: String, city: String, country: String)

case class Class(id: Int, time: Time, name: String, place: Place, teacher: String, experience: Option[String])


case class Schedule(private val classes: Seq[Class]) {
  val all: Seq[Class] = classes
}
