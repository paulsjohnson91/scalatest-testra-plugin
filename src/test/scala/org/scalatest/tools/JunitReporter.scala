package org.scalatest.tools

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import net.liftweb.json
import org.scalatest._
import org.scalatest.events._
import scalaj.http._

class JUnitReporter extends Reporter with App{
  private var apiUrl = ""
  private var project = ""
  private var executionId=""
  private var projectId = ""
  override def apply(event: Event): Unit = {
    event match {
      case _: RunStarting =>
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
    apiUrl="http://localhost:8083/api/v1"
    project = "Companion Service"
    val getProjUrl = apiUrl + "/projects/" + URLEncoder.encode(project, StandardCharsets.UTF_8.toString).replace("+", "%20");
    val response: HttpResponse[String] = Http(getProjUrl).asString
    val j = json.parse(response.body)

    println(j)
  }

}
