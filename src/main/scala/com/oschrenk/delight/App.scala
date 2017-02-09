package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import java.time.LocalDate

object HelloWorld extends App {
  val browser = JsoupBrowser()
  val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
  val classes = new Classes().extract(doc, LocalDate.now)
  classes.foreach(println)
}
