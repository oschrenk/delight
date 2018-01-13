package com.oschrenk.delight.ui

import com.oschrenk.delight.model.Attendance
import com.oschrenk.delight.model

sealed trait CliCommand
case class ScheduleCliCommand(favorites: Boolean = false, preferred: Boolean = false, format: model.Class => String) extends CliCommand
case class UpcomingCliCommand(format: model.Class => String) extends CliCommand
case class PreviousCliCommand(format: Attendance => String) extends CliCommand
case object StatsCliCommand extends CliCommand
case class BookCliCommand(classIds: Seq[Int]) extends  CliCommand
case class CancelCliCommand(classIds: Seq[Int]) extends  CliCommand
