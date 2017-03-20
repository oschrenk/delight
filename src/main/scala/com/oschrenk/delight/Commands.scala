package com.oschrenk.delight

import java.time.{LocalDate, LocalDateTime}

import better.files.File
import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

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
      val login = Network.login(username, password)
      val cookies = login.cookies.asScala.toMap
      storeCookies(sessionPath, cookies)
      cookies
    }
  }
}

class ScheduleCommand(classFilter: ClassFilter, format: Class => String) extends LazyLogging  {
  def run(): Unit = {
    val doc = Network.schedule()
    Extractors.publicWeek(doc, LocalDate.now)
      .all
      .filter(classFilter)
      .foreach(c => println(format(c)))
  }
}

class BookCommand(cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => Network.book(classId, cookies()))
}

class CancelCommand(cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => JsoupDocument(Network.cancel(classId, cookies())))
}

class UpcomingCommand(cookies:() => Map[String,String], format: Class => String) {
  def run(today: LocalDateTime): Unit = {
    Network.myDelight(cookies()) match {
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

    Network.myDelight(cookies()) match {
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
