package com.oschrenk.delight

import java.time.LocalDateTime

import com.oschrenk.delight.commands._
import com.oschrenk.delight.network.{Network, SessionManager}
import com.oschrenk.delight.ui._

object Delight extends App {

  private val config = new Config
  new CliParser(config).parser.parse(args, Settings.default) match {
    case Some(options) =>
      val network = new Network(config.cachePath)
      val cookies = new SessionManager(network).authorize(config.username, config.password, config.sessionPath)

      options.command match {
        case None => println("Noop")
        case Some(ScheduleCliCommand(favoritesOnly, preferred, date, format)) =>
          new ScheduleCommand(network, config.filters(favoritesOnly, preferred, date), format).run()
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
