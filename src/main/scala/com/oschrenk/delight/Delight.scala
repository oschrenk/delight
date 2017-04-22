package com.oschrenk.delight

import java.time.LocalDateTime

object Delight extends App {
  import Config._

  Cli.parser.parse(args, Options.default) match {
    case Some(options) =>
      val network = new Network()
      val cookies = new SessionManager(network).authorize(username, password, sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(ScheduleCliCommand(format)) =>
          new ScheduleCommand(network, Config.filters, format).run()
        case Some(UpcomingCliCommand(format)) =>
          new UpcomingCommand(network, cookies, format).run(LocalDateTime.now)
        case Some(PreviousCliCommand(format)) =>
          new PreviousCommand(network, cookies, format).run(LocalDateTime.now)
        case Some(StatsCliCommand) =>
          new StatsCommand(network, cookies).run(LocalDateTime.now)
        case Some(BookCliCommand(classIds)) =>
          new BookCommand(network, cookies).run(classIds)
        case Some(CancelCliCommand(classIds)) =>
          new CancelCommand(network, cookies).run(classIds)
      }
    case None =>
      println("error parsing")
  }
}
