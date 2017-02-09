package com.oschrenk.delight

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Document

import java.time.LocalDate

case class Class(id: Int, time: String, name: String, place: String, teacher: String, experience: String)

class Classes {
  def extract(document: Document, day: LocalDate) = {
    val selector = s"#accordion-${day} > tr > td"
    val cells = (document >> elementList(selector)).grouped(7)
    cells.map{ cell =>
      val time = cell(0) >> text("p")
      val name =cell(1) >> text("p")
      val teacher =cell(2) >> text("p")
      val experience = cell(3) >> text("p")
      val place = (cell(4) >> text("p")).dropRight(2)
      val id = (cell(5).attr("id")).toInt
      Class(id, time, name, place, teacher, experience)
    }
  }
}

