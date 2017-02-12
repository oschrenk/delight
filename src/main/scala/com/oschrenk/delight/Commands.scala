package com.oschrenk.delight

import better.files.File
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

  private val SessionKey = "PHPSESSID"

  private def loadCookies(sessionPath: File): Option[Map[String, String]] = {
    if (sessionPath.isRegularFile) {
      val cookies = sessionPath.lines.map { line =>
        line.split("=") match {
          case Array(k,v, _*) => Map(k.trim -> v.trim)
        }
    }.reduce(_ ++ _)
      if (cookies.contains(SessionKey)) {
        Some(cookies)
      } else {
        None
      }
    } else {
      None
    }
  }

  private def storeCookies(sessionPath: File, cookies: Map[String, String]): Unit = {
    val lines = cookies.map{case (k,v) => s"$k=$v"}.toSeq
    sessionPath
      .createIfNotExists(asDirectory = false, createParents = true)
      .overwrite("")
      .appendLines(lines:_*)
  }

  def authorize(username: String, password: String, sessionPath: File) = () => {
    loadCookies(sessionPath).getOrElse{
      val login = Jsoup.connect("https://delightyoga.com/validate")
        .method(Connection.Method.POST)
        // can be slow
        .timeout(10*1000)
        .data("_username", username)
        .data("_password", password)
        .execute()
      val cookies = login.cookies.asScala.toMap
      storeCookies(sessionPath, cookies)
      cookies
    }
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

    // TODO log response, check response for success
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
