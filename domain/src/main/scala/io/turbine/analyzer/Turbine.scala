package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Turbine.{Broken, Status}
import io.turbine.analyzer.TurbineEvent.Updated

case class Turbine(
  id: String,
  timestamp: Instant,
  activePower: BigDecimal,
  status: Option[Status]) {

  def updateState(event: TurbineEvent): Turbine = event match {
    case x: Updated => copy(
      status = Some(x.status),
      timestamp = x.date,
      activePower = x.activePower)
  }
}

object Turbine {
  def empty(turbineId: String): Turbine = Turbine(
    id = turbineId,
    timestamp = Instant.now(),
    activePower = BigDecimal(0),
    status = None)

  sealed trait Status {
    def value: String
  }
  case object Working extends Status {
    def value: String = "Working"
  }
  case object Broken extends Status {
    def value: String = "Broken"
  }

  sealed trait Out
  case class CurrentStatus(turbine: Turbine) extends Out
}
