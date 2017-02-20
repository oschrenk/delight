package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

case class Filters(teacher: Set[String], experience: Set[String])

object Config {

  val version: String = Option(getClass.getPackage.getImplementationVersion).getOrElse("")

  private val DelightPath: File = File.home /".delight"
  private val ConfigPath: File  = DelightPath / "config"

  private def load(path: File): TypesafeConfig = {
    System.setProperty("config.file", path.toString())
    ConfigFactory.invalidateCaches()
    ConfigFactory.load()
  }


  private val config = load(ConfigPath)
  val sessionPath: File  = DelightPath / "session"
  val username: String = config.getString("username")
  val password: String = config.getString("password")

  private val PathFilterTeacher = "filter.teacher"
  private val PathFilterExperience = "filter.experience"
  val filters: Filters = {
    val teacher = config.optStringSet(PathFilterTeacher)
    val experience = config.optStringSet(PathFilterExperience)
    Filters(teacher, experience)
  }
}
