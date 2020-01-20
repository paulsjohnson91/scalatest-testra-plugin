package org.scalatest.tools

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import org.scalatest._
import org.scalatest.events._
import scalaj.http.{HttpResponse, Http}
import spray.json._
import DefaultJsonProtocol._
import org.slf4j._

class JUnitReporter extends Reporter with App {
  case class Project(
      id: String,
      name: String,
      description: String,
      projectType: String,
      creationDate: Long
  )
  def log: Logger = LoggerFactory.getLogger(this.getClass())

  var apiUrl = ""
  var project = ""
  var executionId = ""
  var projectId = ""
  override def apply(event: Event): Unit = {
    event match {
      case e: RunStarting =>
        log.info(e.configMap.get("testraApi").get)
        initialiseTestra

      case e: TestSucceeded =>
        e.recordedEvents.foreach {
          case r: InfoProvided =>
            println(r.message)
          case _ =>
        }
      case _ =>
    }
  }

  def initialiseTestra: Unit = {
    project = "Companion Service"
    apiUrl = "http://localhost:8083/api/v1"
    getProjectId
  }

  def getProjectId: Unit = {
    implicit val projectFormat: JsonFormat[Project] = jsonFormat5(Project)
    val response: HttpResponse[String] = Http(
      apiUrl + "/projects/" + URLEncoder
        .encode(project, StandardCharsets.UTF_8.toString)
        .replace("+", "%20")
    ).asString
    projectId = response.body.parseJson.convertTo[Project].id
    log.info(s"Project ID $projectId found")
  }

}
