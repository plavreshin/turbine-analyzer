package io.turbine.analyzer.actor

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import io.turbine.analyzer.Turbine.{CurrentStatus, Working}
import io.turbine.analyzer.TurbineCommand.Update
import io.turbine.analyzer.TurbineEvent.Updated
import io.turbine.analyzer.{Turbine, TurbineCommand, TurbineEvent}

class TurbineActor extends PersistentActor with LazyLogging {
  private lazy val turbineId = self.path.name
  private lazy val persistenceType = self.path.parent.parent.name
  private var state = Turbine.empty(turbineId)

  protected lazy val eventStream = context.system.eventStream

  override def receiveRecover: Receive = {
    case RecoveryCompleted =>
      logger.debug(s"Successfully recovered to state: $state")
    case x: SnapshotOffer  =>
    case e: TurbineEvent   => updateState(e)
  }

  private def updateState(e: TurbineEvent): Unit = {
    def onSideEffect() = {
      eventStream.publish(CurrentStatus(state))
    }

    state = state.updateState(e)

    if (lastSequenceNr % TurbineActor.SnapshotFrequency == 0) {
      saveSnapshot(state)
    }

    onSideEffect()
  }

  override def receiveCommand: Receive = {
    case x: TurbineCommand      => onCommand(x) match {
      case Right(e) => persist(e)(updateState)
      case Left(f)  => logger.error(s"Failed to process command: $x with {}", f)
    }
    case x: SaveSnapshotFailure => logger.error(s"Failed to save snapshot: ${ x.metadata }", x.cause)
    case x: SaveSnapshotSuccess => logger.debug(s"Saved snapshot: ${ x.metadata }")
  }

  private def onCommand(cmd: TurbineCommand): Either[String, TurbineEvent] = cmd match {
    case x: Update => Right(
      Updated(
        id = x.id,
        date = x.date,
        activePower = x.activePower,
        status = x.status))
  }

  override def persistenceId: String = s"$persistenceType-$turbineId"
}

object TurbineActor {
  val shardName: String = "TurbineActor"
  val idExtractor: ShardRegion.ExtractEntityId = {
    case x: TurbineCommand => (x.id, x)
  }
  val shardResolver: ShardRegion.ExtractShardId = {
    case x: TurbineCommand => (math.abs(x.id.hashCode) % 100).toString
  }
  val SnapshotFrequency: Int = 100

  def props: Props = Props(classOf[TurbineActor])
}
