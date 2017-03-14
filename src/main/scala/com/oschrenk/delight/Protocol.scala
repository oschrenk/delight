package com.oschrenk.delight

import java.time.LocalDateTime

case class JsonClass(ClassID: Int, StartDateTime: LocalDateTime, EndDateTime: LocalDateTime, Name: String, WebSignup: Boolean, SignedIn: Boolean, LateCancelled: Boolean, Staff: JsonStaff, Location: JsonLocation)

case class JsonStaff(Name: String)

case class JsonLocation(Name: String, Address: String, PostalCode: String, City: String)
