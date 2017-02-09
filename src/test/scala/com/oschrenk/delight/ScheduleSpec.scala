package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop._

import java.io.File
import java.time.LocalDate

class ClassesSpec extends FlatSpec with Matchers {

  val doc = JsoupBrowser() .parseFile( new File(getClass.getResource("/yoga.html").toURI))
  val day = LocalDate.of(2017, 2, 4)

  "Classes" should "extract classes for a given day" in {
    val classes = new Schedule().extractDay(doc, day)

    classes.toSeq.size should be (20)
  }

  it should "extract classes for a whole week" in {
    val classes = new Schedule().extract(doc, day)

    classes.toSeq.size should be (137)
  }

}
