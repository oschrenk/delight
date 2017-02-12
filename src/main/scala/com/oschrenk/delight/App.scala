package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

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
  private val DelightPath: File = File.home /".delight"
  private val CredentialsPath: File  = DelightPath / "credentials"

  private def load(path: File): TypesafeConfig = {
    System.setProperty("config.file", path.toString())
    ConfigFactory.invalidateCaches()
    ConfigFactory.load()
  }

  private val credentials = load(CredentialsPath)

  val sessionPath: File  = DelightPath / "session"
  val username: String = credentials.getString("username")
  val password: String = credentials.getString("password")
}

object DelightApp extends App {
  import Config._

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
        case BookMode =>
          new BookCommand(SessionManager.authorize(username, password, sessionPath)).run(options.classId.get)
        case CancelMode => new CancelCommand(SessionManager.authorize(username, password, sessionPath)).run(options.classId.get)
      }
    case None =>
      println("error parsing")
  }
}
