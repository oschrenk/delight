package com.oschrenk.delight.ui

import better.files.File
import com.oschrenk.delight.model
import com.typesafe.config.{ConfigException, ConfigFactory, Config => TypesafeConfig}
import java.time.LocalTime
import pt.davidafsilva.apple.OSXKeychain

class Config {

  val version: String = Option(getClass.getPackage.getImplementationVersion).getOrElse("")

  private val DelightPath: File = File.home / ".config" / "delight"
  private val ConfigPath: File  = DelightPath / "config"

  private def load(path: File): TypesafeConfig = {
    System.setProperty("config.file", path.toString())
    ConfigFactory.invalidateCaches()
    ConfigFactory.load()
  }

  private val config = load(ConfigPath)
  val username: String = config.getString("username")

  val password: String = {
    if(config.hasPath("password.keychain") && config.getBoolean("password.keychain")) {
      val keychain = OSXKeychain.getInstance()
      val maybePassword = keychain.findGenericPassword("delightyoga.com", username)
      if (maybePassword.isPresent)
        maybePassword.get()
      else {
          throw new ConfigException.BadValue("password", "No valid password in keychain")
      }

    } else {
      config.getString("password")
    }
  }

  val sessionPath: File  = DelightPath / "session"
  val cachePath: File  = DelightPath / "schedule.cache"

  private def preferredTime(): Option[LocalTime] = {
    def asLocalTime(s: String): LocalTime = {
      LocalTime.of(s.toInt, 0)
    }
    val t = config.getString("preferred.time").split("-").map(asLocalTime)
    t.length match {
      case 1 => Some(t.head)
      case _ => None
    }
  }
  private val FilterTeacher = config.optStringSet("filter.teacher")
  private val FilterExperience = config.optStringSet("filter.experience")
  private val FilterName = config.optStringSet("filter.name")
  private val FilterLocation = config.optStringSet("filter.location")
  def filters(favoritesOnly: Boolean, preferred: Boolean): (model.Class) => Boolean = {
    import Reject._
    import Predicates.and
    val standardFilters = and(byExperience(FilterExperience), byName(FilterName), byLocation(FilterLocation))
    val teacherFilter =
      if (favoritesOnly) Select.byTeacher(favourites)
      else byTeacher(FilterTeacher)
    val preferredTimeFilter =
      if(preferred) Select.byPreferredTime(preferredTime())
      else Select.byPreferredTime(None)
    and(teacherFilter, standardFilters, preferredTimeFilter)
  }

  private val SelectTeacher = config.optStringSet("favourite.teacher")
  val favourites: Set[String] = SelectTeacher
}
