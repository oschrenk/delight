package com.oschrenk.delight.ui

import scopt.OptionParser

object Cli {
  val parser = new OptionParser[Settings]("delight") {
    head("delight", Config.version)

    cmd("schedule").text("fetch schedule for next week:")
      .action( (_, s) => s.copy(command = Some(ScheduleCliCommand())))
      .children(
        opt[String]('f', "format")
          .action{(format, s) =>
            val oldFavorites = s.command.get.asInstanceOf[ScheduleCliCommand].favorites
            val newFormat = Formatters.Class.from(format)
            s.copy(command = Some(ScheduleCliCommand(oldFavorites, newFormat)))
          },
        opt[Unit]("favorites")
          .action{(format, s) =>
            val oldFormat = s.command.get.asInstanceOf[ScheduleCliCommand].format
            val newFavorites = true
            s.copy(command = Some(ScheduleCliCommand(newFavorites, oldFormat)))
          },
      )

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
        arg[Int]("<classId>...").unbounded()
          .action((classId, c) => c.copy(command = {
            val priorIds = c.command match {
              case Some(cmd: CancelCliCommand) => cmd.classIds
              case _ => Seq.empty
            }
            Some(CancelCliCommand(priorIds :+ classId))
          })).text("classId"))

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

    cmd("stats").text("stats")
      .action( (_, c) => c.copy(command = Some(StatsCliCommand)))
  }
}
