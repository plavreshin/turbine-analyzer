import Dependencies._
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._

object Build extends Build {
  lazy val commonSettings = Seq(
    organization := "io.turbine.analyzer",
    version := "0.0.1",
    scalaVersion := "2.11.8",
    externalResolvers := Dependencies.libsResolvers,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Ywarn-dead-code",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "utf8"
    ),
    crossPaths := false,
    fork in run := true,
    fork in Test := false,
    parallelExecution in Test := true,
    mainClass in assembly := Some("io.turbine.analyzer.Main"),
    updateOptions := updateOptions.value.withCachedResolution(true)
  )

  lazy val assemblySettings = Seq(
    assemblyJarName in assembly := "analyzer.jar",
    assemblyMergeStrategy in assembly := {
      case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
      case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )

  // this is the root project, aggregating all sub projects
  lazy val analyzer = Project(
    id = "analyzer",
    base = file("."),
    // always run all commands on each sub project
    aggregate = Seq(core, domain)
  )
    .dependsOn(core, domain)
    .settings(commonSettings ++ assemblySettings)

  lazy val core = Project(
    id = "core",
    base = file("./core")
  )
    .dependsOn(domain)
    .settings(Defaults.coreDefaultSettings ++ commonSettings ++ Seq(libraryDependencies ++= coreDependencies))

  lazy val domain = Project(
    id = "domain",
    base = file("./domain")
  ).settings(Defaults.coreDefaultSettings ++ commonSettings ++ Seq(libraryDependencies ++= domainDependencies))

}
