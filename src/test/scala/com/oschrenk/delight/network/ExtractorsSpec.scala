package com.oschrenk.delight.network

import java.io.File
import java.time.{LocalDate, LocalDateTime}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ExtractorsSpec extends FlatSpec with Matchers {

  private val PublicSchedule = JsoupBrowser().parseFile(new File(getClass.getResource("/public-schedule.html").toURI))
  private val PrivateSchedule = Source.fromResource("classes.json").mkString
  private val Day = LocalDate.of(2017, 2, 4)
  private val Cutoff = LocalDateTime.of(2017, 3, 12, 0, 0 ,0)

  "Extractors" should "extract classes for a given day" in {
    val classes = Extractors.publicDay(PublicSchedule, Day)

    classes.toSeq.size should be (20)
  }

  it should "extract classes for a whole week" in {
    val classes = Extractors.publicWeek(PublicSchedule, Day).all

    classes.size should be (137)
  }

  it should "know about non bookable classes" in {
    val classes = Extractors.publicWeek(PublicSchedule, Day).all.filter { c =>
      !c.bookable
    }

    classes.size should be (7)
  }

  it should "extract upcoming classes" in {
    val classes = Extractors.upcoming(PrivateSchedule, Cutoff)
    classes.size should be (3)
  }

  it should "extract previous classes" in {
    val classes = Extractors.previous(PrivateSchedule, Cutoff)
    classes.size should be (2)
  }

}
