package com.oschrenk.delight.ui

import com.oschrenk.delight.model

object Filters {

  def byTeacher: Set[String] => ClassFilter =
    teachers => c => !teachers.contains(c.teacher)

  def byExperience: Set[String] => ClassFilter =
    experiences => c => !c.experience.toSet.subsetOf(experiences)

  def byName: Set[String] => ClassFilter =
    names => c => !names.contains(c.name)

  def byLocation: Set[String] => ClassFilter =
    locations => c => !locations.contains(c.place.name)

  private def and[A](predicates: (A => Boolean)*) = (a:A) => predicates.forall(_(a))
  def all(teachers: Set[String], experiences: Set[String], names: Set[String], locations: Set[String]): (model.Class) => Boolean = {
    and(byTeacher(teachers), byExperience(experiences), byName(names), byLocation(locations))
  }
}

