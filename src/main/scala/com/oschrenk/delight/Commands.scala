package com.oschrenk.delight

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import java.time.LocalDate

sealed trait Command {
  def run(): Unit
}
case object NoopCommand extends Command {
  override def run() = println("Noop")
}

class ScheduleCommand() extends Command {
  override def run() = {
    val browser = JsoupBrowser()
    val doc = browser.get("https://delightyoga.com/studio/schedule/amsterdam")
    Schedule.extract(doc, LocalDate.now).all.foreach(println)
  }
}
