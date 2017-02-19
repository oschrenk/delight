package com.oschrenk.delight

object DelightApp extends App {
  import Config._

  Cli.parser.parse(args, Options.default) match {
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
