package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import java.time.LocalDate

case object Config {
  val default = Config(NoopCommand)
}
case class Config(command: Command)

sealed trait Command
case object NoopCommand extends Command
case object ScheduleCommand extends Command

object DelightApp extends App {
  val parser = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "0.1.0")
    cmd("schedule").text("fetch schedule for next week:\n")
      .action( (_, c) => c.copy(command = ScheduleCommand) )
  }

  parser.parse(args, Config.default) match {
    case Some(config) =>
      config.command match {
        case ScheduleCommand =>
          val browser = JsoupBrowser()
          val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
          Schedule.extract(doc, LocalDate.now).all.foreach(println)
        case NoopCommand =>
          println("No command")
      }
    case None =>
      println("error")
  }

}
