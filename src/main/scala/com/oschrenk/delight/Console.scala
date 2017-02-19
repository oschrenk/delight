package com.oschrenk.delight

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Console {
  import scala.Console.{BLUE, GREEN, MAGENTA, RESET}

  def coloredLevel(name: String, experience: Option[String]): String = {
    experience match {
      case Some("Beginners") => s"$RESET$GREEN$name$RESET"
      case Some("All levels") => s"$RESET$BLUE$name$RESET"
      case Some("Experienced") => s"$RESET$MAGENTA$name$RESET"
      case _ => name
    }
  }

  private val DayFormatter = DateTimeFormatter.ofPattern("EEE")
  def pretty(c: Class): String = {
    val id = c.id
    val day = c.time.start.toLocalDate.format(DayFormatter)
    val start = c.time.start.toLocalTime.toString
    val name = coloredLevel(c.name, c.experience)
    val teacher = c.teacher
    val place = c.place.name

    s"$id $day $start $name w/ $teacher @ $place"
  }
}
object Khal {

  private val DayFormatter = DateTimeFormatter.ofPattern("dd.MM.")
  def format(c: Class): String = {
    val day = c.time.start.toLocalDate.format(DayFormatter)
    val start = c.time.start.toLocalTime.toString
    val end = c.time.end.toLocalTime.toString
    val name = c.name
    val teacher = c.teacher
    val p = c.place
    val place = s""""Delight Yoga, ${p.street}, ${p.zipcode} ${p.city}, ${p.country}""""

    s"khal new $day $start $end $name w/ $teacher --location $place"
  }
}

object Formatters {
  def from(format: String): (Class) => String = format match {
    case "khal" => Formatters.khal
    case _ => Formatters.pretty
  }

  val pretty: (Class) => String = (c: Class) => {
    Console.pretty(c)
  }
  val khal: (Class) => String = (c: Class) => {
    Khal.format(c)
  }

  val default: (Class) => String = pretty
}
