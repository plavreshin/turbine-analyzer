package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Turbine.Status

sealed trait TurbineCommand {
  def id: String
  def date: Instant
}

object TurbineCommand {
  case class Update(
    id: String,
    date: Instant,
    activePower: BigDecimal,
    status: Status) extends TurbineCommand
}
