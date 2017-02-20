package com.oschrenk

import com.typesafe.config.{Config => TypesafeConfig}

package object delight {

  type ClassFilter = Class => Boolean

  import scala.collection.JavaConverters._
  implicit class RichConfig(val config: TypesafeConfig) extends AnyVal {
    def optStringSet(path: String): Set[String] = {
      if (config.hasPath(path))
        config.getStringList(path).asScala.toSet
      else
        Set.empty
    }
  }
}

