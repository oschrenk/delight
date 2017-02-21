package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import org.scalatest.{FlatSpec, Matchers}

import java.io.File
import java.time.LocalDate

class ExtractorsSpec extends FlatSpec with Matchers {

  private val PublicSchedule = JsoupBrowser().parseFile(new File(getClass.getResource("/public-schedule.html").toURI))
  private val PrivateSchedule = JsoupBrowser().parseFile( new File(getClass.getResource("/my-delight.html").toURI))
  private val Day = LocalDate.of(2017, 2, 4)

  "Extractors" should "extract classes for a given day" in {
    val classes = Extractors.publicDay(PublicSchedule, Day)

    classes.toSeq.size should be (20)
  }

  it should "extract classes for a whole week" in {
    val classes = Extractors.publicWeek(PublicSchedule, Day).all

    classes.size should be (137)
  }

  it should "extract upcoming classes" in {
    val classes = Extractors.upcoming(PrivateSchedule)
    classes.size should be (5)
  }

}
