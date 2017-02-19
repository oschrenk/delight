package com.oschrenk.delight

import java.time.format.DateTimeFormatter

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

case class Class(id: Int, time: Time, name: String, place: String, teacher: String, experience: Option[String])

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
      val experience = Some(cell(3) >> text("p"))
      val place = (cell(4) >> text("p")).dropRight(2)
      val id = cell(5).attr("id").toInt
      Class(id, time, name, place, teacher, experience)
    }
  }
}

object MySchedule {
  def extract(document: Document): Seq[Class] = {
    val selector = s"#booked > div > div > div.table-body > div"
    val cells = document >> elementList(selector)
    cells.map { cell =>
      val children = cell >> elementList("div")
      val id = cell.attr("id").toInt
      val time = Time.parseFullDuration(children.head >> text("p > span.full-date"))
      val name = children(2) >> text("p")
      val teacher = children(3) >> text("p")
      val place = children(4) >> text("p")
      // delightyoga.com/my-delight doesn't show experience level
      val experience = None
      Class(id, time, name, place, teacher, experience)
    }
  }
}

case class Schedule(private val classes: Seq[Class]) {
  val all: Seq[Class] = classes
}
