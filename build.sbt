import Dependencies._

name := "plugins"
version := "0.1.1"
bintrayRepository := "testra-scalatest-plugin"

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "io.testra"
ThisBuild / organizationName := "testra"

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
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
     Test / testOptions += Tests.Argument("-Dtestra=true",
       "-DtestraApi=http://localhost:8083/api/v1",
       "-Dproject=ScalaTestProject", "-Dhost=myhost",
       "-Ddomain=mydomain","-Dbranch=mybranch",
       "-Denv=myenv", "-DbuildRef=mybuildref",
       "-Dtags=tags"),
  )
