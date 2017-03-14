package com.oschrenk.delight

import java.time.{LocalDate, LocalDateTime}

import com.typesafe.scalalogging.LazyLogging
import io.circe._
import io.circe.generic.semiauto._
// Needed but marked as unsued by IntelliJi
// import io.circe.java8.time._
// if removed you get error, like:
// could not find Lazy implicit value of type io.circe.generic.decoding.DerivedDecoder[A]
// [error]   implicit val classDecoder: Decoder[JsonClass] = deriveDecoder
import io.circe.java8.time._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

object Extractors extends LazyLogging {

import scala.math.Ordering.Implicits._

  implicit val classDecoder: Decoder[JsonClass] = deriveDecoder
  implicit val staffDecoder: Decoder[JsonStaff] = deriveDecoder
  implicit val locationDecoder: Decoder[JsonLocation] = deriveDecoder
  implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isBefore _)

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

  def decode(json: String): Seq[JsonClass] = {
    parser.decode[Seq[JsonClass]](json) match {
      case Right(classes) =>
        classes
      case Left(e) =>
        logger.error(s"Parse error $e")
        Seq.empty
    }
  }

  def upcoming(json: String, today: LocalDateTime): Seq[Class] = {
    decode(json)
      .filter{ c => !c.SignedIn }
      .filter{ c => c.StartDateTime.isAfter(today)}
      .sortBy(_.StartDateTime)
      .map { c =>
      val l = c.Location

      val id = c.ClassID
      val time = Time(c.StartDateTime, c.EndDateTime)
      val name = c.Name
      val place = Place(l.Name, l.Address, l.PostalCode, l.City, "Netherlands")
      val teacher = c.Staff.Name
      val experience = None

      Class(id, time, name, place, teacher, experience)
    }
  }

  def previous(json: String, today: LocalDateTime): Seq[Attendance] = {
    decode(json)
      .filter{ c => c.StartDateTime.isBefore(today)}
      .map { c =>
      val l = c.Location

      val time = Time(c.StartDateTime, c.EndDateTime)
      val name = c.Name.trim
      val place = Place(l.Name, l.Address, l.PostalCode, l.City, "Netherlands")
      val teacher = c.Staff.Name
      val present = c.SignedIn

      Attendance(time, name, place, teacher, present)
    }
  }
}
