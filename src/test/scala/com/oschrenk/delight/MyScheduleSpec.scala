package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class MyScheduleSpec extends FlatSpec with Matchers {

  private val Doc = JsoupBrowser() .parseFile( new File(getClass.getResource("/my-delight.html").toURI))

  "Classes" should "extract upcoming classes" in {
    val classes = MySchedule.extract(Doc)
    classes.size should be (5)
  }

}

