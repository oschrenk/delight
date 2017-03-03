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
        case Some(PreviousCliCommand(format)) =>
          new PreviousCommand(authorize, format).run()
        case Some(BookCliCommand(classIds)) =>
          new BookCommand(authorize).run(classIds)
        case Some(CancelCliCommand(classIds)) =>
          new CancelCommand(authorize).run(classIds)
      }
    case None =>
      println("error parsing")
  }
}
