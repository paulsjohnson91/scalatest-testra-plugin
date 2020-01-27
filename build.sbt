import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scalatest-testra-plugin",
    libraryDependencies += scalaTest,
    libraryDependencies += "org.pegdown" % "pegdown" % "1.6.0" % "test",
    libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2",
    // libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.0",
    libraryDependencies += "io.spray" %%  "spray-json" % "1.3.4", 
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",

    Test / testOptions += Tests.Argument("-C", "io.testra.plugins.TestraReporter"),
    Test / testOptions += Tests.Argument("-DtestraApi=http://localhost:8083/api/v1", "-Dproject=CompanionService"),
  )

