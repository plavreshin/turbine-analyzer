package io.turbine.analyzer.eventual

import akka.actor.Actor
import io.turbine.analyzer.Turbine
import io.turbine.analyzer.Turbine.{Broken, CurrentStatus, Out, Working}

import scala.collection.concurrent.TrieMap

class Turbines {
  private val _turbines: TrieMap[String, Turbine] = TrieMap.empty

  def turbine(turbineId: String): Option[Turbine] = _turbines get turbineId

  def brokenTurbines: List[Turbine] = _turbines.values.filter(_.status.exists(_ == Broken)).toList

  def workingTurbines: List[Turbine] = _turbines.values.filter(_.status.exists(_ == Working)).toList

  private class TurbineListener extends Actor {
    override def receive: Receive = {
      case out: Out => out match {
        case x: CurrentStatus =>
          _turbines + (x.turbine.id -> x.turbine)
      }
    }

    override def preStart(): Unit = {
      context.system.eventStream.subscribe(self, classOf[Turbine.Out])
    }
  }
}
