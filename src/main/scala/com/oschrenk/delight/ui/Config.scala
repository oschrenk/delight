package com.oschrenk.delight.ui

import better.files.File
import com.oschrenk.delight.model
import com.typesafe.config.{ConfigException, ConfigFactory, Config => TypesafeConfig}
import pt.davidafsilva.apple.OSXKeychain

class Config {

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

  private val FilterTeacher = config.optStringSet("filter.teacher")
  private val FilterExperience = config.optStringSet("filter.experience")
  private val FilterName = config.optStringSet("filter.name")
  private val FilterLocation = config.optStringSet("filter.location")
  def filters(favoritesOnly:Boolean): (model.Class) => Boolean = {
    import Reject._
    import Predicates.and
    if (favoritesOnly) {
      and(Select.byTeacher(favourites), byExperience(FilterExperience), byName(FilterName), byLocation(FilterLocation))
    } else {
      and(byTeacher(FilterTeacher), byExperience(FilterExperience), byName(FilterName), byLocation(FilterLocation))
    }
  }

  private val SelectTeacher = config.optStringSet("favourite.teacher")
  val favourites = SelectTeacher
}
