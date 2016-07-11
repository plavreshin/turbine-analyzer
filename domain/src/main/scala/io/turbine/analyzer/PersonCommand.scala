package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Person.Movement

sealed trait PersonCommand {
  def id: String
  def timestamp: Instant
}

object PersonCommand {
  case class EnterLocation(
    id: String,
    timestamp: Instant,
    movement: Movement) extends PersonCommand
  case class LeaveLocation(
    id: String,
    timestamp: Instant,
    movement: Movement) extends PersonCommand
}