package com.oschrenk.delight

import java.time.LocalDateTime

import com.oschrenk.delight.commands._
import com.oschrenk.delight.network.{Network, SessionManager}
import com.oschrenk.delight.ui._

object Delight extends App {
  import Config._

  Cli.parser.parse(args, Settings.default) match {
    case Some(options) =>
      val network = new Network()
      val cookies = new SessionManager(network).authorize(username, password, sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(ScheduleCliCommand(favoritesOnly, format)) =>
          new ScheduleCommand(network, Config.filters(favoritesOnly), format).run()
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
