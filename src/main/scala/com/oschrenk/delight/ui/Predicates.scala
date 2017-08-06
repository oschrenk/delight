package com.oschrenk.delight.ui

object Predicates {
  def and[A](predicates: (A => Boolean)*) = (a:A) => predicates.forall(_(a))
}
