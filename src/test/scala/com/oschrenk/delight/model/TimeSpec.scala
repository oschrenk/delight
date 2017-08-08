package com.oschrenk.delight.model

import java.time.{LocalDate, LocalDateTime}

import org.scalatest.{FlatSpec, Matchers}

class TimeSpec extends FlatSpec with Matchers {

  "Time" should "extract times" in {
    val times = Time.parse(LocalDate.of(2017, 2, 1), "8:30 - 09:30")
    times shouldBe Time(LocalDateTime.of(2017, 2, 1, 8, 30), LocalDateTime.of(2017, 2, 1, 9, 30))
  }

}
