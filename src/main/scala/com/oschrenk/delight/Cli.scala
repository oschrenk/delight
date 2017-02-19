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

object Cli {
  val parser = new OptionParser[Options]("delight") {
    head("delight", Config.version)
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
}
