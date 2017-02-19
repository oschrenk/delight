package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

sealed trait CliCommand
trait NullaryCliCommand extends CliCommand
case object ScheduleCliCommand extends NullaryCliCommand
case object UpcomingCliCommand extends NullaryCliCommand

sealed trait UnaryCliCommand extends CliCommand {
  def classId: Int
}
case class BookCliCommand(classId: Int) extends  UnaryCliCommand
case class CancelCliCommand(classId: Int) extends  UnaryCliCommand

case object Options {
  val default = Options(None)
}
case class Options(command: Option[CliCommand])

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
      .action( (_, c) => c.copy(command = Some(ScheduleCliCommand)))
    cmd("book").text("book class with given id")
      .children(
        arg[Int]("<classId>")
          .action((classId, c) => c.copy(command = Some(BookCliCommand(classId)))).text("classId")
      )
    cmd("cancel").text("cancel class with given id")
      .children(
        arg[Int]("<classId>")
          .action((classId, c) => c.copy(command = Some(CancelCliCommand(classId)))).text("classId")
      )
    cmd("upcoming").text("upcoming")
      .action( (_, c) => c.copy(command = Some(UpcomingCliCommand)))
  }

  parser.parse(args, Options.default) match {
    case Some(options) =>
      val authorize = SessionManager.authorize(username, password, sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(cmd: NullaryCliCommand) => cmd match {
          case ScheduleCliCommand =>
            new ScheduleCommand(Config.filters).run()
          case UpcomingCliCommand =>
            new UpcomingCommand(authorize).run()
        }
          case Some(cmd: UnaryCliCommand) =>
            cmd match {
              case BookCliCommand(classId) =>
                new CancelCommand(authorize).run(classId)
              case CancelCliCommand(classId) =>
                new CancelCommand(authorize).run(classId)
            }

      }
    case None =>
      println("error parsing")
  }
}
