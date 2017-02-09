package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import org.jsoup.{Connection,Jsoup}

import java.time.LocalDate

class ScheduleCommand() {
  def run() = {
    val browser = JsoupBrowser()
    val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
    Schedule.extract(doc, LocalDate.now).all.foreach(println)
  }
}

class BookCommand(val username: String, val password: String) {
  def run(classId: Int) = {
    val login = Jsoup.connect("https://delightyoga.com/validate")
      .method(Connection.Method.POST)
      // can be slow
      .timeout(10*1000)
      .data("_username", username)
      .data("_password", password)
      .execute()

    // POST https://delightyoga.com/studio/schedule/visit/ajax/book
    // classIds[0]:86594
    // clearShoppingCart:true
    // returns confirmation, to automatically comfirm, remove clearShoppingCart, and set
    // confirm:true
    val booking = JsoupDocument(Jsoup.connect("https://delightyoga.com/studio/schedule/visit/ajax/book")
      // can be slow
      .timeout(10*1000)
      .data("classIds[0]", classId.toString)
      .data("confirm", true.toString)
      .cookies(login.cookies)
      .post())

    println(booking)
  }
}
