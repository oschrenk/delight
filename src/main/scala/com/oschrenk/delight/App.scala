package com.oschrenk.delight

import scopt.OptionParser

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


object DelightApp extends App {
  import Config._

  val parser = new OptionParser[Options]("scopt") {
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
