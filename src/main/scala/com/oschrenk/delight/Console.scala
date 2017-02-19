package com.oschrenk.delight

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Console {
  import scala.Console.{BLUE, GREEN, MAGENTA, RESET, UNDERLINED}

  private val weekDayFormatter = DateTimeFormatter.ofPattern("EEE");
  def time(day: LocalDate): String = {
    day.format(weekDayFormatter)
  }

  def coloredLevel(name: String, experience: Option[String]): String = {
    experience match {
      case Some("Beginners") => s"${RESET}${GREEN}${name}${RESET}"
      case Some("All levels") => s"${RESET}${BLUE}${name}${RESET}"
      case Some("Experienced") => s"${RESET}${MAGENTA}${name}${RESET}"
      case _ => name
    }

  }
  def pretty(c: Class): String = {
    val id = c.id
    val day = time(c.time.start.toLocalDate)
    val start = c.time.start.toLocalTime.toString
    val name = coloredLevel(c.name, c.experience)
    val teacher = c.teacher

    s"${id} $day $start $name w/ $teacher"
  }

}

object Formatters {
  val pretty = (c: Class) => {
    Console.pretty(c)
  }
}
