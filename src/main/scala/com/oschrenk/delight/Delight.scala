package com.oschrenk.delight

import java.time.LocalDateTime

object Delight extends App {
  import Config._

  Cli.parser.parse(args, Options.default) match {
    case Some(options) =>
      val network = new Network()
      val authorize = new SessionManager(network).authorize(username, password, sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(ScheduleCliCommand(format)) =>
          new ScheduleCommand(network, Config.filters, format).run()
        case Some(UpcomingCliCommand(format)) =>
          new UpcomingCommand(network, authorize, format).run(LocalDateTime.now)
        case Some(PreviousCliCommand(format)) =>
          new PreviousCommand(network, authorize, format).run(LocalDateTime.now)
        case Some(StatsCliCommand) =>
          new StatsCommand(network, authorize).run(LocalDateTime.now)
        case Some(BookCliCommand(classIds)) =>
          new BookCommand(network, authorize).run(classIds)
        case Some(CancelCliCommand(classIds)) =>
          new CancelCommand(network, authorize).run(classIds)
      }
    case None =>
      println("error parsing")
  }
}
