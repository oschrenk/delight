package com.oschrenk.delight

import scopt.OptionParser

sealed trait CliCommand
case class ScheduleCliCommand(format: Class => String) extends CliCommand
case class UpcomingCliCommand(format: Class => String) extends CliCommand
case class BookCliCommand(classId: Int) extends  CliCommand
case class CancelCliCommand(classId: Int) extends  CliCommand

case object Options {
  val default = Options(None)
}
case class Options(command: Option[CliCommand])

object Cli {
  val parser = new OptionParser[Options]("delight") {
    head("delight", Config.version)
    cmd("schedule").text("fetch schedule for next week:\n")
      .action( (_, c) => c.copy(command = Some(ScheduleCliCommand(Formatters.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(ScheduleCliCommand(Formatters.from(format))))))
    cmd("book").text("book class with given id")
      .children(
        arg[Int]("<classId>")
          .action((classId, c) => c.copy(command = Some(BookCliCommand(classId)))).text("classId"))
    cmd("cancel").text("cancel class with given id")
      .children(
        arg[Int]("<classId>")
          .action((classId, c) => c.copy(command = Some(CancelCliCommand(classId)))).text("classId"))
    cmd("upcoming").text("upcoming")
      .action( (_, c) => c.copy(command = Some(UpcomingCliCommand(Formatters.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(UpcomingCliCommand(Formatters.from(format))))))
  }
}
