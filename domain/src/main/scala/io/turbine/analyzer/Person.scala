package io.turbine.analyzer

import java.time.Instant

import io.turbine.analyzer.Person.Movement
import io.turbine.analyzer.Person.Movement.{Location, Typ}
import io.turbine.analyzer.PersonEvent.{EnteredLocation, LeftLocation}

case class Person(
  id: String,
  lastActionTimestamp: Instant,
  movement: Option[Movement]) {

  def updateState(e: PersonEvent): Person = e match {
    case x: EnteredLocation =>
      copy(
        lastActionTimestamp = x.timestamp,
        movement = Some(x.movement))
    case x: LeftLocation    =>
      copy(
        lastActionTimestamp = x.timestamp,
        movement = Some(x.movement))
  }
}

object Person {

  def empty(id: String): Person = Person(
    id = id,
    lastActionTimestamp = Instant.now(),
    movement = None)

  case class Movement(location: Location, typ: Typ)

  object Movement {
    sealed trait Location {
      def value: String
    }
    case class Turbine(name: String) extends Location {
      def value: String = s"Turbine: $name"
    }

    case class Vessel(name: String) extends Location {
      def value: String = s"Vessel: $name"
    }

    sealed trait Typ {
      def value: String
    }
    case object Exit extends Typ {
      def value: String = "Exit"
    }
    case object Enter extends Typ {
      def value: String = "Enter"
    }
  }

  sealed trait Out
  case class CurrentStatus(person: Person) extends Out
}
