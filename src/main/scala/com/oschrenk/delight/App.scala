package com.oschrenk.delight

import better.files.File
import com.typesafe.config.ConfigFactory

sealed trait Mode
case object NoopMode extends Mode
case object ScheduleMode extends Mode
case object BookMode extends Mode
case object CancelMode extends Mode

case object Options {
  val default = Options(NoopMode, None)
}
case class Options(mode: Mode, classId: Option[Int])

object Config {
  private val delightPath: File = File.home /".delight"
  private val credentialsPath: File  = delightPath / "credentials"

  System.setProperty("config.file", credentialsPath.toString())
  ConfigFactory.invalidateCaches()
  private val credentials = ConfigFactory.load()

  val username: String = credentials.getString("username")
  val password: String = credentials.getString("password")
}

object DelightApp extends App {
  val parser = new scopt.OptionParser[Options]("scopt") {
    head("scopt", "0.1.0")
    cmd("schedule").text("fetch schedule for next week:\n")
      .action( (_, c) => c.copy(mode = ScheduleMode))
    cmd("book").text("book class with given id")
      .action( (_, c) => c.copy(mode = BookMode))
      .children(
        arg[Int]("<classId>")
          .action((x, c) => c.copy(classId = Some(x)) ).text("classId")
      )
    cmd("cancel").text("cancel class with given id")
      .action( (_, c) => c.copy(mode = CancelMode))
      .children(
        arg[Int]("<classId>")
          .action((x, c) => c.copy(classId = Some(x)) ).text("classId")
      )
  }

  parser.parse(args, Options.default) match {
    case Some(options) =>
      options.mode match {
        case NoopMode => println("Noop")
        case ScheduleMode => new ScheduleCommand().run()
        case BookMode => new BookCommand(Config.username, Config.password).run(options.classId.get)
        case CancelMode => new CancelCommand(Config.username, Config.password).run(options.classId.get)
      }
    case None =>
      println("error parsing")
  }
}
