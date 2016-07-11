package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Turbine.Status

sealed trait TurbineEvent {
  def id: String
  def date: Instant
}

object TurbineEvent {
  case class Updated(id: String, date: Instant, activePower: BigDecimal, status: Status) extends TurbineEvent
}