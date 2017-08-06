package com.oschrenk.delight.ui

case object Settings {
  val default = Settings(None)
}
case class Settings(command: Option[CliCommand])


