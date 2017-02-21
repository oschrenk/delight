package com.oschrenk.delight

import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Document
import java.time.{LocalDate, LocalDateTime, LocalTime}


object Extractors {
  def publicWeek(document: Document, day: LocalDate = LocalDate.now): Schedule = {
    val ranges = (0 to 6).map(n => publicDay(document, day.plusDays(n)))
    Schedule(ranges.reduceLeft((l,r) => l  ++ r).toSeq)
  }

  def publicDay(document: Document, day: LocalDate): Iterator[Class] = {
    val selector = s"#accordion-$day > tr > td"
    val cells = (document >> elementList(selector)).grouped(7)
    cells.map{ cell =>
      val time = Time.parse(day, cell.head >> text("p"))
      val name =cell(1) >> text("p")
      val teacher =cell(2) >> text("p")
      val experience = Some(cell(3) >> text("p"))
      val place = Place.from((cell(4) >> text("p")).dropRight(2))
      val id = cell(5).attr("id").toInt
      Class(id, time, name, place, teacher, experience)
    }
  }

  def upcoming(document: Document): Seq[Class] = {
    val selector = s"#booked > div > div > div.table-body > div"
    val cells = document >> elementList(selector)
    cells.map { cell =>
      val children = cell >> elementList("div")
      val id = cell.attr("id").toInt
      val time = Time.parseFullDuration(children.head >> text("p > span.full-date"))
      val name = children(2) >> text("p")
      val teacher = children(3) >> text("p")
      val place = Place.from(children(4) >> text("p"))
      // delightyoga.com/my-delight doesn't show experience level
      val experience = None
      Class(id, time, name, place, teacher, experience)
    }
  }
}

