package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop._

import java.io.File
import java.time.LocalDate

class ClassesSpec extends FlatSpec with Matchers {

  "Classes" should "extract classes from a given html document" in {
    val doc = JsoupBrowser() .parseFile( new File(getClass.getResource("/yoga.html").toURI))
    val day = LocalDate.of(2017, 2, 4)
    val classes = new Classes().extract(doc, day)

    classes.toSeq.size should be (20)
  }

}
