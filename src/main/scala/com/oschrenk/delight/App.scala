package com.oschrenk.delight

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

object DelightApp extends App {
  val home = System.getProperty("user.home")
  val credentialsPath = s"${home}/.delight"
  System.setProperty("config.file", credentialsPath)
  ConfigFactory.invalidateCaches()
  val credentials = ConfigFactory.load();

  val username = credentials.getString("username")
  val password = credentials.getString("password")

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
    case Some(config) =>
      config.mode match {
        case NoopMode => println("Noop")
        case ScheduleMode => new ScheduleCommand().run()
        case BookMode => new BookCommand(username, password).run(config.classId.get)
        case CancelMode => new CancelCommand(username, password).run(config.classId.get)
      }
    case None =>
      println("error parsing")
  }
}
