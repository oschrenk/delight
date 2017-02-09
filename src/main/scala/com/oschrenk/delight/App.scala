package com.oschrenk.delight

case object Config {
  val default = Config(NoopCommand)
}
case class Config(command: Command)

object DelightApp extends App {
  val parser = new scopt.OptionParser[Config]("scopt") {
    head("scopt", "0.1.0")
    cmd("schedule").text("fetch schedule for next week:\n")
      .action( (_, c) => c.copy(command = new ScheduleCommand()) )
  }

  parser.parse(args, Config.default) match {
    case Some(config) =>
      config.command.run()
    case None =>
      println("error parsing")
  }
}
