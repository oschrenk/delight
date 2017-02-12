package com.oschrenk.delight

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Document

import java.time.{LocalDate, LocalDateTime}

case object Time {
  def parse(day: LocalDate, start: String, end: String): Time = {
    def parse(day: LocalDate, time: String): LocalDateTime = {
      time.trim.split(":") match {
        case Array(h,m, _*) => day.atTime(h.toInt, m.toInt)
      }
    }
    Time(parse(day, start), parse(day, end))
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
      val time = (cell.head >> text("p")).split("-") match {
        case Array(s,e, _*) => Time.parse(day, s, e)
      }
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


