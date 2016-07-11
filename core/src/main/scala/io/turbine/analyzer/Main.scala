package io.turbine.analyzer

import java.time.Instant

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import io.turbine.analyzer.Person.Movement
import io.turbine.analyzer.Person.Movement.{Enter, Vessel}
import io.turbine.analyzer.PersonCommand.EnterLocation
import io.turbine.analyzer.Turbine.Working
import io.turbine.analyzer.TurbineCommand.Update
import io.turbine.analyzer.actor.{PersonActor, TurbineActor}
import io.turbine.analyzer.eventual.{Persons, Turbines}

object Main extends App with LazyLogging {
  private val config: Config = ConfigFactory.load()
  private lazy val clusterSharding: ClusterSharding = ClusterSharding(system)
  private lazy val shardingSettings: ClusterShardingSettings = ClusterShardingSettings(system)

  implicit lazy val system = {
    val system = ActorSystem("TurbineAnalyzerSystem", config)
    system
  }

  lazy val personActor: () => ActorRef = {
    clusterSharding.start(
      typeName = PersonActor.shardName,
      entityProps = PersonActor.props,
      settings = shardingSettings,
      extractEntityId = PersonActor.idExtractor,
      extractShardId = PersonActor.shardResolver)
    () => clusterSharding.shardRegion(PersonActor.shardName)
  }

  lazy val turbineActor: () => ActorRef = {
    clusterSharding.start(
      typeName = TurbineActor.shardName,
      entityProps = TurbineActor.props,
      settings = shardingSettings,
      extractEntityId = TurbineActor.idExtractor,
      extractShardId = TurbineActor.shardResolver)
    () => clusterSharding.shardRegion(TurbineActor.shardName)
  }

  val persons = new Persons()
  val turbines = new Turbines()

  logger.info("App has started")

  turbineActor() ! Update("some", Instant.now(), BigDecimal(0), Working)
  personActor() ! EnterLocation("person1", Instant.now(), Movement(Vessel("testMe"), Enter))
}
