package com.oschrenk.delight.ui

object Predicates {
  def and[A](predicates: (A => Boolean)*) = (a:A) => predicates.forall(_(a))
}

object Reject {

  def byTeacher: Set[String] => ClassFilter =
    teachers => c => !teachers.contains(c.teacher)

  def byExperience: Set[String] => ClassFilter =
    experiences => c => !c.experience.toSet.subsetOf(experiences)

  def byName: Set[String] => ClassFilter =
    names => c => !names.contains(c.name)

  def byLocation: Set[String] => ClassFilter =
    locations => c => !locations.contains(c.place.name)

}

