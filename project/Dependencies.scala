import sbt.Keys._
import sbt._

object Dependencies {
  val configVersion = "1.3.0"
  val akkaVersion = "2.4.7"
  val scalatestVersion = "2.2.6"
  val playJsonVersion = "2.5.3"

  val libsResolvers = Seq(
    "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "jdgoldie at bintray" at "http://dl.bintray.com/jdgoldie/maven",
    "RoundEights" at "http://maven.spikemark.net/roundeights",
    "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
//    Resolver.jcenterRepo
  )

  private val config = "com.typesafe" % "config" % configVersion
  private val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion exclude("org.scala-lang", "scala-library") withSources()
  private val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion exclude("org.slf4j", "slf4j-api") exclude("org.scala-lang", "scala-library")

  private val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion withSources()
  private val akkaPersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion withSources()
  private val kryoSerializer = "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1"

  private val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion withSources()
  private val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion withSources()

  private val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  private val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion

  private val playJson = "com.typesafe.play" %% "play-json" % playJsonVersion
  private val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.1.7"
  private val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  private val scalaCsv = "com.github.tototoshi" %% "scala-csv" % "1.3.3"

  private val akkaTestKitSimple = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  private val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion

  private val mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"
  private val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion % "test"

  val domainDependencies = Seq(playJson, scalaTest)

  val coreDependencies = Seq(
    kryoSerializer,
    config,
    akkaActor,
    akkaSlf4j,
    akkaPersistence,
    akkaPersistenceQuery,
    akkaCluster,
    akkaClusterSharding,
    akkaTestKitSimple,
    mockito,
    scalaTest,
    akkaStream,
    akkaHttp,
    akkaHttpTestkit,
    logBackClassic,
    scalaLogging,
    playJson,
    scalaCsv)
}