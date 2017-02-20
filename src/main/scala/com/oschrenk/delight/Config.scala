package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

case class Filters(teacher: Set[String], experience: Set[String])

object Config {
  import scala.collection.JavaConverters._

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

  // TODO monkeypatch?
  private def asStringSet(config: TypesafeConfig, path: String): Set[String] = {
    if (config.hasPath(path))
      config.getStringList(path).asScala.toSet
    else
      Set.empty
  }

  private val PathFilterTeacher = "filter.teacher"
  private val PathFilterExperience = "filter.experience"
  val filters: Filters = {
    val teacher = asStringSet(config, PathFilterTeacher)
    val experience = asStringSet(config, PathFilterExperience)
    Filters(teacher, experience)
  }
}
