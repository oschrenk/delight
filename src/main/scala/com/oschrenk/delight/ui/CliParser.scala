package com.oschrenk.delight.ui

import java.time.LocalDate

import org.platzhaltr.parsing.datr.DateEvent
import scopt.OptionParser

import scala.util.Success

class CliParser(config: Config) {

  private val classFormatters = new Formatters.Class(config)

  val parser = new OptionParser[Settings]("delight") {
    head("delight", config.version)

    cmd("schedule").text("fetch schedule for next week:")
      .action( (_, s) => s.copy(command = Some(ScheduleCliCommand(format = classFormatters.default))))
      .children(
        opt[String]('f', "format")
          .action{(format, s) =>
            val oldCommand = s.command.get.asInstanceOf[ScheduleCliCommand]
            val newCommand = oldCommand.copy(format = classFormatters.from(format))
            s.copy(command = Some(newCommand))
          },
        opt[Unit]("favorites")
          .action{(_, s) =>
            val oldCommand = s.command.get.asInstanceOf[ScheduleCliCommand]
            val newCommand = oldCommand.copy(favorites = true)
            s.copy(command = Some(newCommand))
          },
        opt[Unit]("preferred")
          .action{(_, s) =>
            val oldCommand = s.command.get.asInstanceOf[ScheduleCliCommand]
            val newCommand = oldCommand.copy(preferred = true)
            s.copy(command = Some(newCommand))
          },
        arg[String]("<date>").optional()
            .action { (date, s) =>
              val oldCommand = s.command.get.asInstanceOf[ScheduleCliCommand]
              val parser = new org.platzhaltr.parsing.datr.DateParser(date)
              val maybeDate = parser.InputLine.run() match {
                case Success(result) =>
                  result match {
                    case dateEvent: DateEvent =>
                      Some(LocalDate.now.`with`(dateEvent))
                    case _ => None
                  }
                case _ => None
              }

              val newCommand = oldCommand.copy(date = maybeDate)
              s.copy(command = Some(newCommand))
            }

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
      .action( (_, c) => c.copy(command = Some(UpcomingCliCommand(classFormatters.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(UpcomingCliCommand(classFormatters.from(format))))))

    cmd("previous").text("previous")
      .action( (_, c) => c.copy(command = Some(PreviousCliCommand(Formatters.Attendance.default))))
      .children(
        opt[String]('f', "format")
          .action((format, c) => c.copy(command = Some(PreviousCliCommand(Formatters.Attendance.from(format))))))

    cmd("stats").text("stats")
      .action( (_, c) => c.copy(command = Some(StatsCliCommand)))
  }
}
