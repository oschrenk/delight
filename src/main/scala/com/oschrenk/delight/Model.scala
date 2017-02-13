package com.oschrenk.delight

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Document
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
}
case class Time(start: LocalDateTime, end: LocalDateTime)

case class Class(id: Int, time: Time, name: String, place: String, teacher: String, experience: String)

object Schedule {
  def extract(document: Document, day: LocalDate = LocalDate.now): Schedule = {
    val ranges = (0 to 6).map(n => extractDay(document, day.plusDays(n)))
    Schedule(ranges.reduceLeft((l,r) => l  ++ r).toSeq)
  }

  def extractDay(document: Document, day: LocalDate): Iterator[Class] = {
    val selector = s"#accordion-$day > tr > td"
    val cells = (document >> elementList(selector)).grouped(7)
    cells.map{ cell =>
      val time = Time.parse(day, cell.head >> text("p"))
      val name =cell(1) >> text("p")
      val teacher =cell(2) >> text("p")
      val experience = cell(3) >> text("p")
      val place = (cell(4) >> text("p")).dropRight(2)
      val id = cell(5).attr("id").toInt
      Class(id, time, name, place, teacher, experience)
    }
  }
}

case class Schedule(private val classes: Seq[Class]) {
  val all: Seq[Class] = classes
}


