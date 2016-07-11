package io.turbine.analyzer.eventual

import akka.actor.Actor
import io.turbine.analyzer.Person
import io.turbine.analyzer.Person.{CurrentStatus, Out}

import scala.collection.concurrent.TrieMap

class Persons {
  private val _persons: TrieMap[String, Person] = TrieMap.empty

  def person(personId: String): Option[Person] = _persons get personId

  def persons: Map[String, Person] = _persons.toMap

  private class PersonListener extends Actor {
    override def receive: Receive = {
      case out: Out => out match {
        case x: CurrentStatus => _persons + (x.person.id -> x)
      }
    }

    override def preStart(): Unit = {
      context.system.eventStream.subscribe(self, classOf[Person.Out])
    }
  }
}
