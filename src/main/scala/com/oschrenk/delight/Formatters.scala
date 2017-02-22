package com.oschrenk.delight

import java.time.format.DateTimeFormatter
import scala.Console.{BLUE, BOLD, GREEN, MAGENTA, RED, RESET}

object Formatters {
  object Class {
    def from(format: String): (Class) => String = format match {
      case "khal" => khal
      case "nocolor" => noColor
      case _ => pretty
    }

    private val DayFormatter = DateTimeFormatter.ofPattern("EEE")
    val pretty: (Class) => String = (c: Class) => {
      def coloredLevel(name: String, experience: Option[String]): String = {
        experience match {
          case Some("Beginners") => s"$RESET$GREEN$name$RESET"
          case Some("All levels") => s"$RESET$BLUE$name$RESET"
          case Some("Experienced") => s"$RESET$MAGENTA$name$RESET"
          case _ => name
        }
      }

      def highlightTeacher(name: String) = {
        // TODO Formatters should have no knowledge of Config
        if (Config.favourites.contains(name))
          s"$RESET$BOLD$name$RESET"
        else
          name
      }

      val id = c.id
      val day = c.time.start.toLocalDate.format(DayFormatter)
      val start = c.time.start.toLocalTime.toString
      val name = coloredLevel(c.name, c.experience)
      val teacher = highlightTeacher(c.teacher)
      val place = c.place.name

      s"$id $day $start $name w/ $teacher @ $place"
    }

    val noColor: (Class) => String = (c: Class) => {
      val id = c.id
      val day = c.time.start.toLocalDate.format(DayFormatter)
      val start = c.time.start.toLocalTime.toString
      val name = c.name
      val experience = c.experience match {
        case Some(exp) => s" (${exp}) "
        case None => " "
      }
      val teacher = c.teacher
      val place = c.place.name

      s"$id $day $start $name${experience}w/ $teacher @ $place"
    }

    private val KhalDayFormatter = DateTimeFormatter.ofPattern("dd.MM.")
    val khal: (Class) => String = (c: Class) => {
      val day = c.time.start.toLocalDate.format(KhalDayFormatter)
      val start = c.time.start.toLocalTime.toString
      val end = c.time.end.toLocalTime.toString
      val name = c.name
      val teacher = c.teacher
      val p = c.place
      val place = s""""Delight Yoga, ${p.street}, ${p.zipcode} ${p.city}, ${p.country}""""

      s"khal new $day $start $end $name w/ $teacher --location $place"
    }

    val default: (Class) => String = pretty
  }

  object Attendance {

    def from(format: String): (Attendance) => String = format match {
      case "nocolor" => noColor
      case _ => pretty
    }

    private def colorClass(name: String, attended: Boolean) = {
      val color = if (attended) s"$GREEN" else s"$RED"
      s"$RESET$color$name$RESET"
    }

    private val DayFormatter = DateTimeFormatter.ofPattern("dd.MM.")
    private def pretty: Attendance => String = (a: Attendance) => {
      val day = a.time.start.toLocalDate.format(DayFormatter)
      val start = a.time.start.toLocalTime.toString
      val teacher = a.teacher
      val place = a.place.name
      val present = a.present
      val name = colorClass(a.name, present)

      s"$day $start $name w/ $teacher @ $place"
    }

    val noColor: Attendance => String = (a: Attendance) => {
      val day = a.time.start.toLocalDate.format(DayFormatter)
      val start = a.time.start.toLocalTime.toString
      val teacher = a.teacher
      val place = a.place.name
      val present = if (a.present) "completed" else "absent"
      val name = a.name

      s"$day $start $name (${present}) w/ $teacher @ $place"
    }

    val default: Attendance => String = pretty
  }
}
