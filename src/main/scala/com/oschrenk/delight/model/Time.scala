package com.oschrenk.delight.model

import java.time.{LocalDate, LocalDateTime, LocalTime}

case object Time {

  def parse(day: LocalDate, times: String): Time = {
    val t = parseTimes(times)
    Time(day.atTime(t._1), day.atTime(t._2))
  }

  // 8:30 - 10:00
  private def parseTimes(times: String): (LocalTime, LocalTime) = {
    times.split("-") match {
      case Array(s,e, _*) => (parseTime(s), parseTime(e))
    }
  }

  // 8:30
  private def parseTime(time: String): LocalTime = {
    time.trim.split(":") match {
        case Array(h,m, _*) => LocalTime.of(h.toInt, m.toInt)
      }
  }
}
case class Time(start: LocalDateTime, end: LocalDateTime)
