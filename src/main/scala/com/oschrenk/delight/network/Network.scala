package com.oschrenk.delight.network

import java.time.{Duration, ZonedDateTime}

import com.oschrenk.delight.ui.Config
import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.Try

class Network extends LazyLogging {

  private val DefaultTimeout = 15.seconds.toMillis.toInt
  private val MaxAge = 60.minutes.length

  private def fromCache(): Option[Document] = {
    if (Config.cachePath.isRegularFile) {
      val lastModified = Config.cachePath.lastModifiedTime
      val now = ZonedDateTime.now.toInstant
      val age = Duration.between(lastModified, now)
      if (age.toMinutes > MaxAge) {
        logger.info(s"Cache expired. age: $age. MaxAge: $MaxAge. lastModified: $lastModified. now: $now")
        None
      } else {
        logger.info(s"Fetching from cache expired. age: $age. MaxAge: $MaxAge. lastModified: $lastModified. now: $now")
        Some(browser.parseFile(Config.cachePath.toString))
      }
    } else
      None
  }

  private def toCache(document: Document) = {
    logger.info("Writing to cache")
    Config.cachePath
      .createIfNotExists(asDirectory = false, createParents = true)
      .overwrite("")
      .append(document.toHtml)
  }

  private val browser = JsoupBrowser()

  def schedule(): Document = {
    fromCache() match {
      case Some(document) => document
      case None =>
        val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
        logger.trace(doc.toHtml)
        toCache(doc)
        doc
    }
  }

  def myDelight(cookies: Map[String, String], timeout: Int = DefaultTimeout): Try[String] = {
    Try{
      logger.info("Fetching my delight")
      val url = "https://delightyoga.com/api/mydelight/classes"
      val json = Jsoup.connect(url)
        .method(Connection.Method.POST)
        .ignoreContentType(true)
        .timeout(timeout)
        .cookies(cookies.asJava)
        .execute()
        .body()
      logger.trace(json)
      json
    }
  }

  def book(classId: Int, cookies: Map[String, String]) = {
    // POST https://delightyoga.com/studio/schedule/visit/ajax/book
    // classIds[0]:86594
    // clearShoppingCart:true
    // returns confirmation, to automatically confirm, remove clearShoppingCart, and set
    // confirm:true
    Jsoup.connect("https://delightyoga.com/studio/schedule/visit/ajax/book")
      .timeout(DefaultTimeout)
      .data("classIds[0]",classId.toString)
      .data("confirm", true.toString)
      .cookies(cookies.asJava)
      .post()
  }

  def cancel(classId: Int, cookies: Map[String, String]) =
    // POST https://delightyoga.com/studio/schedule/visit/ajax/cancel
    // classId:78225
    // returns confirmation, to automatically confirm, also set
    // confirm:true
    Jsoup.connect("https://delightyoga.com/studio/schedule/visit/ajax/cancel")
      .timeout(DefaultTimeout)
      .data("classId", classId.toString)
      .data("confirm", true.toString)
      .cookies(cookies.asJava)
      .post()

  def login(username: String, password: String) =
    Jsoup.connect("https://delightyoga.com/validate")
      .method(Connection.Method.POST)
      // can be slow
        .timeout(DefaultTimeout)
        .data("_username", username)
        .data("_password", password)
        .execute()
}
