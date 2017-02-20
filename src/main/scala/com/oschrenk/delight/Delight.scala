package com.oschrenk.delight

object Delight extends App {
  import Config._

  Cli.parser.parse(args, Options.default) match {
    case Some(options) =>
      val authorize = SessionManager.authorize(username, password, sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(ScheduleCliCommand(format)) =>
          new ScheduleCommand(Config.filters, format).run()
        case Some(UpcomingCliCommand(format)) =>
          new UpcomingCommand(authorize, format).run()
        case Some(BookCliCommand(classId)) =>
          new BookCommand(authorize).run(classId)
        case Some(CancelCliCommand(classId)) =>
          new CancelCommand(authorize).run(classId)
      }
    case None =>
      println("error parsing")
  }
}
