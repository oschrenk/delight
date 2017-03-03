package com.oschrenk.delight

import better.files.File
import com.typesafe.config.{Config => TypesafeConfig, ConfigFactory}

object Filters {

  def byTeacher: Set[String] => ClassFilter = teachers => c =>
    !teachers.contains(c.teacher)

  def byExperience: Set[String] => ClassFilter =
      experiences => c => !c.experience.toSet.subsetOf(experiences)

  def byName: Set[String] => ClassFilter =
    names => c => !names.contains(c.name)

  def byLocation: Set[String] => ClassFilter =
    locations => c => !locations.contains(c.place.name)

  private def and[A](predicates: (A => Boolean)*) = (a:A) => predicates.forall(_(a))
  def all(teachers: Set[String], experiences: Set[String], names: Set[String], locations: Set[String]): (Class) => Boolean = {
    and(byTeacher(teachers), byExperience(experiences), byName(names), byLocation(locations))
  }
}

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
  val filters: (Class) => Boolean = {
    val teachers = config.optStringSet(PathFilterTeacher)
    val experiences = config.optStringSet(PathFilterExperience)
    val names = config.optStringSet(PathFilterName)
    val locations = config.optStringSet(PathFilterLocation)
    Filters.all(teachers, experiences, names, locations)
  }

  private val PathFavoriteTeacher = "favourite.teacher"
  val favourites = config.optStringSet(PathFavoriteTeacher)
}
