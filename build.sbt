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
    libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.0",

    Test / testOptions += Tests.Argument("-C", "org.scalatest.tools.JUnitReporter"),
    Test / testOptions += Tests.Argument("-DtestraApi=http://localhost:8083/api/v1"),
    // testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
  )

