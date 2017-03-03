package com.oschrenk.delight

import scopt.OptionParser

sealed trait CliCommand
case class ScheduleCliCommand(format: Class => String) extends CliCommand
case class UpcomingCliCommand(format: Class => String) extends CliCommand
case class PreviousCliCommand(format: Attendance => String) extends CliCommand
case class BookCliCommand(classIds: Seq[Int]) extends  CliCommand
case class CancelCliCommand(classId: Int) extends  CliCommand

case object Options {
  val default = Options(None)
}
case class Options(command: Option[CliCommand])

object Cli {
  val parser = new OptionParser[Options]("delight") {
    head("delight", Config.version)
    cmd("schedule").text("fetch schedule for next week:")
      .action( (_, c) => c.copy(command = Some(ScheduleCliCommand(Formatters.Class.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(ScheduleCliCommand(Formatters.Class.from(format))))))
    cmd("book").text("book class(es) with given id(s)")
      .children(
        arg[Int]("<classId>...").unbounded()
          .action((classId, c) => c.copy(command = {
            val priorIds = c.command match {
              case Some(cmd: BookCliCommand) => cmd.classIds
              case _ => Seq.empty
            }
            Some(BookCliCommand(priorIds :+ classId))
          })).text("classId"))
    cmd("cancel").text("cancel class with given id")
      .children(
        arg[Int]("<classId>")
          .action((classId, c) => c.copy(command = Some(CancelCliCommand(classId)))).text("classId"))
    cmd("upcoming").text("upcoming")
      .action( (_, c) => c.copy(command = Some(UpcomingCliCommand(Formatters.Class.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(UpcomingCliCommand(Formatters.Class.from(format))))))
    cmd("previous").text("previous")
      .action( (_, c) => c.copy(command = Some(PreviousCliCommand(Formatters.Attendance.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(PreviousCliCommand(Formatters.Attendance.from(format))))))
  }
}
