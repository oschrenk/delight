package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

sealed trait Mode
case object NoopMode extends Mode
case object ScheduleMode extends Mode
case object BookMode extends Mode
case object CancelMode extends Mode
case object UpcomingMode extends Mode

case object Options {
  val default = Options(NoopMode, None)
}
case class Options(mode: Mode, classId: Option[Int])

case class Filters(teacher: Seq[String], experience: Seq[String])

object Config {
  import scala.collection.JavaConverters._

  private val DelightPath: File = File.home /".delight"
  private val ConfigPath: File  = DelightPath / "config"

  private def load(path: File): TypesafeConfig = {
    System.setProperty("config.file", path.toString())
    ConfigFactory.invalidateCaches()
    ConfigFactory.load()
  }

  private val config = load(ConfigPath)
  val sessionPath: File  = DelightPath / "session"
  val username: String = config.getString("username")
  val password: String = config.getString("password")

  private val FilterTeacher = "filter.teacher"
  private val FilterExperience = "filter.experience"
  val filters: Filters =
    (config.hasPath(FilterTeacher), config.hasPath(FilterExperience)) match {
      case (false, false) => Filters(List.empty, List.empty)
      case (true, false) => Filters(config.getStringList(FilterTeacher).asScala.toSeq, List.empty)
      case (false, true) => Filters(List.empty, config.getStringList(FilterExperience).asScala.toSeq)
      case (true, true) => Filters(config.getStringList(FilterTeacher).asScala.toSeq, config.getStringList(FilterExperience).asScala.toSeq)
    }
}

object DelightApp extends App {
  import Config._

  val parser = new scopt.OptionParser[Options]("scopt") {
    head("scopt", "0.1.0")
    cmd("schedule").text("fetch schedule for next week:\n")
      .action( (_, c) => c.copy(mode = ScheduleMode))
    cmd("book").text("book class with given id")
      .action( (_, c) => c.copy(mode = BookMode))
      .children(
        arg[Int]("<classId>")
          .action((x, c) => c.copy(classId = Some(x)) ).text("classId")
      )
    cmd("cancel").text("cancel class with given id")
      .action( (_, c) => c.copy(mode = CancelMode))
      .children(
        arg[Int]("<classId>")
          .action((x, c) => c.copy(classId = Some(x)) ).text("classId")
      )
    cmd("upcoming").text("upcoming")
      .action( (_, c) => c.copy(mode = UpcomingMode))
  }

  parser.parse(args, Options.default) match {
    case Some(options) =>
      options.mode match {
        case NoopMode => println("Noop")
        case ScheduleMode => new ScheduleCommand(Config.filters).run()
        case BookMode =>
          new BookCommand(SessionManager.authorize(username, password, sessionPath)).run(options.classId.get)
        case CancelMode =>
          new CancelCommand(SessionManager.authorize(username, password, sessionPath)).run(options.classId.get)
        case UpcomingMode =>
          new UpcomingCommand(SessionManager.authorize(username, password, sessionPath)).run()
      }
    case None =>
      println("error parsing")
  }
}
