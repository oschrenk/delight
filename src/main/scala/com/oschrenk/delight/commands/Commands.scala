package com.oschrenk.delight.commands

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime}

import com.oschrenk.delight.model.Attendance
import com.oschrenk.delight.ui.ClassFilter
import com.oschrenk.delight.model
import com.oschrenk.delight.network.{Extractors, Network}
import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument

import scala.util.{Failure, Success}

class ScheduleCommand(network: Network, classFilter: ClassFilter, format: model.Class => String) extends LazyLogging  {
  def run(): Unit = {
    val doc = network.schedule()
    Extractors.publicWeek(doc, LocalDate.now)
      .all
      .filter(classFilter)
      .foreach(c => println(format(c)))
  }
}

class BookCommand(network: Network, cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => network.book(classId, cookies()))
}

class CancelCommand(network: Network, cookies:() => Map[String,String]) {
  def run(classIds: Seq[Int]): Unit =
    classIds.foreach(classId => JsoupDocument(network.cancel(classId, cookies())))
}

class UpcomingCommand(network: Network, cookies:() => Map[String,String], format: model.Class => String) {
  def run(today: LocalDateTime): Unit = {
    network.myDelight(cookies()) match {
      case Success(classes) =>
        Extractors.upcoming(classes, today).foreach(c => println(format(c)))
      case Failure(ex) => println(s"Problem fetching url: ${ex.getMessage}")
    }
  }
}

class PreviousCommand(network: Network, cookies:() => Map[String,String], format: Attendance => String) {
  def run(today: LocalDateTime): Unit = {

    network.myDelight(cookies()) match {
      case Success(c) =>
        Extractors.previous(c, today).foreach( c =>
          println(format(c))
        )
      case Failure(ex) =>
        println(s"Problem fetching url: ${ex.getMessage}")
        println(ex)
    }
  }
}

class StatsCommand(network: Network, cookies:() => Map[String,String]) {
  def run(today: LocalDateTime): Unit = {

    def print(stats: Map[String, Int]) = {
      println()
      stats.toSeq.sortBy{ case  (k,v) => (-v,k)}.foreach {case (k, v) => printf("%3d %s\n", v, k)}
      println("---")
      printf("%3d\n", stats.values.sum)
    }

    network.myDelight(cookies()) match {
      case Success(c) =>
        val classes = Extractors.previous(c, today)
        if (classes.nonEmpty) {
          val attendedClasses = classes.filter(_.present)
          val statsNames = attendedClasses.groupBy(_.name).mapValues(_.size)
          val statsTeachers = attendedClasses.groupBy(_.teacher).mapValues(_.size)

          val numberOfClasses = attendedClasses.length.toFloat
          val daysOfYoga = attendedClasses.last.time.start
            .until(LocalDateTime.now, ChronoUnit.DAYS)
          val yogaPerWeek = "%.1f".format( numberOfClasses/ (daysOfYoga / 7))

          print(statsNames)
          print(statsTeachers)

          println
          println(s"Doing Yoga for $daysOfYoga days, averaging $yogaPerWeek per week")
        } else {
          println("No classes")
        }
      case Failure(ex) =>
        println(s"Problem fetching url: ${ex.getMessage}")
        println(ex)
    }
  }
}
