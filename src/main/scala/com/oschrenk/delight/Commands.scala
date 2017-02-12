package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import org.jsoup.{Connection,Jsoup}

import scala.collection.JavaConverters._

import java.time.LocalDate

class ScheduleCommand() {
  def run() = {
    val browser = JsoupBrowser()
    val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
    Schedule.extract(doc, LocalDate.now).all.foreach(println)
  }
}

object SessionManager {
  def authorize(username: String, password: String) = () => {
    val login = Jsoup.connect("https://delightyoga.com/validate")
      .method(Connection.Method.POST)
      // can be slow
      .timeout(10*1000)
      .data("_username", username)
      .data("_password", password)
      .execute()
    login.cookies.asScala.toMap
  }
}


// POST https://delightyoga.com/studio/schedule/visit/ajax/book
// classIds[0]:86594
// clearShoppingCart:true
// returns confirmation, to automatically comfirm, remove clearShoppingCart, and set
// confirm:true
class BookCommand(cookies:() => Map[String,String]) {
  def run(classId: Int) = {
    val booking = JsoupDocument(Jsoup.connect("https://delightyoga.com/studio/schedule/visit/ajax/book")
      // can be slow
      .timeout(10*1000)
      .data("classIds[0]", classId.toString)
      .data("confirm", true.toString)
      .cookies(cookies().asJava)
      .post())

    println(booking)
  }
}

// POST https://delightyoga.com/studio/schedule/visit/ajax/cancel
// classId:78225
// returns confirmation, to automatically confirm, also set
// confirm:true
class CancelCommand(cookies:() => Map[String,String]) {
  def run(classId: Int) = {
    val cancel = JsoupDocument(Jsoup.connect("https://delightyoga.com/studio/schedule/visit/ajax/cancel")
      // can be slow
      .timeout(10*1000)
      .data("classId", classId.toString)
      .data("confirm", true.toString)
      .cookies(cookies().asJava)
      .post())
    println(cancel)
  }
}
