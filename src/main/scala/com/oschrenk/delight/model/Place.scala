package com.oschrenk.delight.model

object Place {
  def from(s: String): Place = {
    s match {
      case p @ "De Clercqstraat" => Place(p, "De Clercqstraat 68", "1052 NJ", "Amsterdam", "Netherlands")
      case p @ "Nieuwe Achtergracht" => Place(p, "Nieuwe Achtergracht 11", "1018 XV", "Amsterdam", "Netherlands")
      case p @ "Prinseneiland" => Place(p, "Prinseneiland 20G", "1013 LR", "Amsterdam", "Netherlands")
      case p @ "Weteringschans" => Place(p, "Weteringschans 53", "1017 RW", "Amsterdam", "Netherlands")
      case _ => throw new IllegalArgumentException("Unknown location")
    }
  }
}
case class Place(name: String, street: String, zipcode: String, city: String, country: String)
