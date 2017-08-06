package com.oschrenk.delight.model

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, Matchers}

class TimeSpec extends FlatSpec with Matchers {

  "Time" should "be extracted from full durations" in {
    val time = Time.parseFullDuration("Sun 12 Feb 2017,  08:30 - 09:30")
    time shouldBe Time(LocalDateTime.of(2017, 2, 12, 8, 30), LocalDateTime.of(2017, 2, 12, 9, 30))
  }

}
