package com.oschrenk.delight

import better.files.File
import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.model.Document
import org.jsoup.{Connection, Jsoup}

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.collection.JavaConverters._
import scala.concurrent.duration._

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

object Fetch extends LazyLogging {

  private val DefaultTimeout = 15.seconds.toMillis.toInt

  private def fromCache(): Option[Document] = {
    if (Config.cachePath.isRegularFile) {
      val lastModified = Config.cachePath.lastModifiedTime
      val oneHourAgo = LocalDateTime.now.minusHours(1).toInstant(ZoneOffset.UTC)
      if (lastModified.isBefore(oneHourAgo)) {
        logger.debug("Fetching from cache")
        Some(browser.parseFile(Config.cachePath.toString))
      } else {
        logger.debug("cache expired")
        None
      }
    } else
      None
  }

  private def toCache(document: Document) = {
    logger.debug("writing to cache")
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
        logger.debug(doc.toHtml)
        toCache(doc)
        doc
    }
  }


  def myDelight(cookies: Map[String, String], timeout: Int = DefaultTimeout): Try[String] = {
    Try{
      logger.debug("Fetching my delight")
      val url = "https://delightyoga.com/api/mydelight/classes"
      val json = Jsoup.connect(url)
        .method(Connection.Method.POST)
        .ignoreContentType(true)
        .timeout(timeout)
        .cookies(cookies.asJava)
        .execute()
        .body()
      logger.debug(json)
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

  def authorize(username: String, password: String, sessionPath: File): () => Map[String, String] = () => {
    loadCookies(sessionPath).getOrElse{
      val login = Fetch.login(username, password)
      val cookies = login.cookies.asScala.toMap
      storeCookies(sessionPath, cookies)
      cookies
    }
  }
}

class ScheduleCommand(classFilter: ClassFilter, format: Class => String) extends LazyLogging  {
  def run(): Unit = {
    val doc = Fetch.schedule()
    Extractors.publicWeek(doc, LocalDate.now)
      .all
      .filter(classFilter)
      .foreach(c => println(format(c)))
  }
}

class BookCommand(cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => Fetch.book(classId, cookies()))
}

class CancelCommand(cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => JsoupDocument(Fetch.cancel(classId, cookies())))
}

class UpcomingCommand(cookies:() => Map[String,String], format: Class => String) {
  def run(today: LocalDateTime): Unit = {
    Fetch.myDelight(cookies()) match {
      case Success(classes) =>
        Extractors.upcoming(classes, today).foreach(c => println(format(c)))
      case Failure(ex) => println(s"Problem fetching url: ${ex.getMessage}")
    }
  }
}

class PreviousCommand(cookies:() => Map[String,String], format: Attendance => String) {
  def run(today: LocalDateTime): Unit = {

    def print(stats: Map[String, Int]) = {
      println()
      stats.toSeq.sortWith{ case ((_,v1), (_,v2)) => v1 > v2}.foreach {case (k, v) => printf("%3d %s\n", v, k)}
      println("---")
      printf("%3d\n", stats.values.sum)
    }

    Fetch.myDelight(cookies()) match {
      case Success(c) =>
        val classes = Extractors.previous(c, today)
        if (classes.nonEmpty) {
          val statsNames = classes.filter(_.present).groupBy(_.name).mapValues(_.size)
          val statsTeachers = classes.filter(_.present).groupBy(_.teacher).mapValues(_.size)

          classes.foreach(c => println(format(c)))
          print(statsNames)
          print(statsTeachers)
        } else {
          println("No classes")
        }
      case Failure(ex) =>
        println(s"Problem fetching url: ${ex.getMessage}")
        println(ex)
    }
  }
}
