package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

case class Filters(teacher: Seq[String], experience: Seq[String])

object Config {
  import scala.collection.JavaConverters._

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

  private val FilterTeacher = "filter.teacher"
  private val FilterExperience = "filter.experience"
  val filters: Filters =
    (config.hasPath(FilterTeacher), config.hasPath(FilterExperience)) match {
      case (false, false) => Filters(List.empty, List.empty)
      case (true, false) => Filters(config.getStringList(FilterTeacher).asScala.toSeq, List.empty)
      case (false, true) => Filters(List.empty, config.getStringList(FilterExperience).asScala.toSeq)
      case (true, true) => Filters(config.getStringList(FilterTeacher).asScala.toSeq, config.getStringList(FilterExperience).asScala.toSeq)
    }
}
