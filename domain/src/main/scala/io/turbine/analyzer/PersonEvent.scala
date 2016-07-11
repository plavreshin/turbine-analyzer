package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Person.Movement

sealed trait PersonEvent {
  def timestamp: Instant
}

object PersonEvent {
  case class EnteredLocation(timestamp: Instant, movement: Movement) extends PersonEvent
  case class LeftLocation(timestamp: Instant, movement: Movement) extends PersonEvent
}
