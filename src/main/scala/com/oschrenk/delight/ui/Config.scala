package com.oschrenk.delight.ui

import better.files.File
import com.oschrenk.delight.model
import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}

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
  val username: String = config.getString("username")
  val password: String = config.getString("password")

  val sessionPath: File  = DelightPath / "session"
  val cachePath: File  = DelightPath / "schedule.cache"

  private val PathFilterTeacher = "filter.teacher"
  private val PathFilterExperience = "filter.experience"
  private val PathFilterName = "filter.name"
  private val PathFilterLocation = "filter.location"
  def filters(favoritesOnly:Boolean): (model.Class) => Boolean = {
    val teachers = config.optStringSet(PathFilterTeacher)
    val experiences = config.optStringSet(PathFilterExperience)
    val names = config.optStringSet(PathFilterName)
    val locations = config.optStringSet(PathFilterLocation)
    all(teachers, experiences, names, locations)
  }

  private def all(teachers: Set[String], experiences: Set[String], names: Set[String], locations: Set[String]): (model.Class) => Boolean = {
    import Filters._
    and(byTeacher(teachers), byExperience(experiences), byName(names), byLocation(locations))
  }

  private val PathFavoriteTeacher = "favourite.teacher"
  val favourites = config.optStringSet(PathFavoriteTeacher)
}
