package io.turbine.analyzer.actor

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.persistence._
import com.typesafe.scalalogging.LazyLogging
import io.turbine.analyzer.Person.CurrentStatus
import io.turbine.analyzer.PersonCommand.{EnterLocation, LeaveLocation}
import io.turbine.analyzer.PersonEvent.{EnteredLocation, LeftLocation}
import io.turbine.analyzer.{Person, PersonCommand, PersonEvent}

class PersonActor extends PersistentActor with LazyLogging {
  private lazy val personId = self.path.name
  private lazy val persistenceType = self.path.parent.parent.name
  private var state = Person.empty(personId)

  protected lazy val eventStream = context.system.eventStream

  override def receiveRecover: Receive = {
    case RecoveryCompleted    => logger.info(s"$persistenceId - Successfully recovered to state: $state")
    case offer: SnapshotOffer => offer.snapshot match {
      case snapshot: Person => if (state != snapshot) state = snapshot
      case other            =>
        logger.error(s"Received incorrect snapshot: $other, dropping messages up-to ${ offer.metadata.sequenceNr }")
        deleteSnapshot(offer.metadata.sequenceNr)
    }
    case e: PersonEvent       => updateState(e)
  }

  override def persistenceId: String = s"$persistenceType-$personId"

  override def receiveCommand: Receive = {
    case x: PersonCommand => onCommand(x) match {
      case Right(e) => persist(e)(updateState)
      case Left(f)  => logger.error(s"Failed to process movement command: $x for person state: $state")
    }
    case x: SaveSnapshotFailure => logger.error(s"Failed to save snapshot: ${x.metadata}", x.cause)
    case x: SaveSnapshotSuccess => logger.debug(s"Snapshot: ${x.metadata} saved")
  }

  private def onCommand(cmd: PersonCommand): Either[String, PersonEvent] = cmd match {
    case x: EnterLocation => Right(EnteredLocation(x.timestamp, x.movement))
    case x: LeaveLocation => Right(LeftLocation(x.timestamp, x.movement))
  }

  private def updateState(e: PersonEvent): Unit = {
    def onSideEffect() = {
      eventStream.publish(CurrentStatus(state))
    }

    state = state.updateState(e)
    if (lastSequenceNr % PersonActor.SnapshotFrequency == 0) {
      saveSnapshot(state)
    }

    onSideEffect()
  }
}

object PersonActor {
  val shardName: String = "PersonActor"
  val idExtractor: ShardRegion.ExtractEntityId = {
    case x: PersonCommand => (x.id, x)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case x: PersonCommand => (math.abs(x.id.hashCode) % 100).toString
  }

  val SnapshotFrequency: Int = 100

  def props: Props = Props(classOf[PersonActor])
}
