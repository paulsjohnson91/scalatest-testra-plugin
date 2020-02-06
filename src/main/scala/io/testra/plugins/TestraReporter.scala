package io.testra.plugins

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import org.scalatest._
import org.scalatest.events._
import org.slf4j._
import scalaj.http.{Http, HttpResponse}
import spray.json.DefaultJsonProtocol._
import spray.json._

class TestraReporter extends Reporter with App {
  case class Project(
      id: String,
      name: String,
      description: String,
      projectType: String,
      creationDate: Long
  )
  case class ExecutionRequest(
      projectId: String,
      description: String,
      host: String,
      parallel: Boolean,
      environment: String,
      branch: String,
      buildRef: String,
      tags: List[String],
      domain: String
  )
  case class ExecutionResponse(id: String)
  case class ScenarioRequest(
      projectId: String,
      featureName: String,
      featureDescription: String,
      name: String,
      tags: List[String],
      steps: IndexedSeq[StepRequest]
  )
  case class ScenarioResponse(
      id: String,
      projectId: String,
      featureId: String,
      featureDescription: String,
      name: String,
      tags: List[String],
      steps: IndexedSeq[StepRequest]
  )
  case class TestResultRequest(
      targetId: String,
      groupId: String,
      resultType: String,
      status: String,
      error: String,
      durationInMs: Long,
      startTime: Long,
      endTime: Long,
      retryCount: Int,
      expectedToFail: Boolean,
      attachments: List[String],
      stepResults: IndexedSeq[StepResult]
  )
  case class StepResult(
      index: Int,
      status: String,
      durationInMs: Long,
      error: String
  )
  case class StepRequest(index: Int, text: String, dataTableRows: List[String])
  type SuiteName = { val suiteName: String }
  type TestName = { val testName: String }
  type RecordedEvents = {
    val recordedEvents: collection.immutable.IndexedSeq[RecordableEvent]
  }
  def log: Logger = LoggerFactory.getLogger("TestraReporter")

  var apiUrl = ""
  var project = ""
  var executionId = ""
  var projectId = ""
  var testraEnabled = false
  var tags = ""
  var buildRef = ""
  var env = ""
  var branch = ""
  var domain = ""
  var host = ""
  override def apply(event: Event): Unit = {
    tags = " "
    buildRef = " "
    env = " "
    branch = " "
    domain = " "
    host = " "
    event match {
      case e: RunStarting =>
        e.configMap.get("testraApi") match {
          case Some(i) => apiUrl = i.asInstanceOf[String]
          case _ => {
            println("No url found for testra"); testraEnabled = false
          }
        }
        e.configMap.get("project") match {
          case Some(i) => project = i.asInstanceOf[String]
          case _       => { println("No Project Found"); testraEnabled = false }
        }
        e.configMap.get("testra") match {
          case Some(i) => testraEnabled = true
          case _       => println("Testra disabled by default")
        }
        e.configMap.get("tags") match {
          case Some(i) => tags = i.asInstanceOf[String]
          case _       =>
        }
        e.configMap.get("buildRef") match {
          case Some(i) => buildRef = i.asInstanceOf[String]
          case _       =>
        }
        e.configMap.get("env") match {
          case Some(i) => env = i.asInstanceOf[String]
          case _       =>
        }
        e.configMap.get("branch") match {
          case Some(i) => branch = i.asInstanceOf[String]
          case _       =>
        }
        e.configMap.get("domain") match {
          case Some(i) => domain = i.asInstanceOf[String]
          case _       =>
        }
        e.configMap.get("host") match {
          case Some(i) => host = i.asInstanceOf[String]
          case _       =>
        }
        if (testraEnabled)
          initialiseTestra

      case e: TestSucceeded =>
        if (testraEnabled)
          createScenario(e)
      case e: TestFailed =>
        if (testraEnabled)
          createScenario(e)
      case _ =>
    }
  }

  def initialiseTestra = {
    log.info(s"Testra Url $apiUrl")
    log.info(s"Project: $project")
    getProjectId
    if (testraEnabled)
      createExecutionId
  }

  def getProjectId {
    implicit val projectFormat: JsonFormat[Project] = jsonFormat5(Project)
    val response: HttpResponse[String] = Http(
      apiUrl + "/projects/" + URLEncoder
        .encode(project, StandardCharsets.UTF_8.toString)
        .replace("+", "%20")
    ).asString
    try {
      projectId = response.body.parseJson.convertTo[Project].id
      log.info(s"Project ID $projectId found")
    } catch {
      case _: DeserializationException => {
        log.error("Project not found"); testraEnabled = false
      }
    }
  }

  def createExecutionId: Unit = {
    implicit val execution: JsonFormat[ExecutionRequest] = jsonFormat9(
      ExecutionRequest
    )
    implicit val executionResponse: JsonFormat[ExecutionResponse] = jsonFormat1(
      ExecutionResponse
    )
    val executionRequest = ExecutionRequest(
      projectId,
      "Generated by Scalatest Plugin",
      host,
      false,
      env,
      branch,
      buildRef,
      List(tags),
      domain
    )
    try {
      executionId = Http(
        apiUrl + "/projects/" + projectId + "/executions"
      ).postData(executionRequest.toJson.compactPrint)
        .header("content-type", "application/json")
        .asString
        .body
        .parseJson
        .convertTo[ExecutionResponse]
        .id
      log.info("Execution Id set to " + executionId)
    } catch {
      case _: Exception => {
        log.error("Execution creation failed"); testraEnabled = false
      }
    }

  }

  def createScenario(
      event: Event with SuiteName with TestName with RecordedEvents
  ) {
    implicit val stepreq: JsonFormat[StepRequest] = jsonFormat3(
      StepRequest
    )
    implicit val scenarioreq: JsonFormat[ScenarioRequest] = jsonFormat6(
      ScenarioRequest
    )
    implicit val scenarioreqs: JsonFormat[ScenarioResponse] = jsonFormat7(
      ScenarioResponse
    )
    implicit val resultStepReq: JsonFormat[StepResult] = jsonFormat4(
      StepResult
    )
    implicit val resultReq: JsonFormat[TestResultRequest] = jsonFormat12(
      TestResultRequest
    )

    var counter = -1;
    val scenario = ScenarioRequest(
      projectId,
      event.suiteName,
      "",
      event.testName,
      List[String](),
      event.recordedEvents.collect {
        case a: InfoProvided =>
          counter += 1
          StepRequest(counter, a.message, List[String]())
      }
    )
    var scenarioResponse = Http(
      apiUrl + "/projects/" + projectId + "/scenarios"
    ).postData(scenario.toJson.compactPrint)
      .header("content-type", "application/json")
      .asString
      .body
      .parseJson
      .convertTo[ScenarioResponse]
    counter = -1
    val result = TestResultRequest(
      scenarioResponse.id,
      scenarioResponse.featureId,
      "SCENARIO",
      if (event.isInstanceOf[TestFailed]) "FAILED" else "PASSED",
      if (event.isInstanceOf[TestFailed]) event.asInstanceOf[TestFailed].message
      else "",
      0,
      0,
      0,
      0,
      false,
      List[String](),
      if (event.isInstanceOf[TestSucceeded])
        event.asInstanceOf[TestSucceeded].recordedEvents.collect {
          case a: InfoProvided =>
            counter += 1
            StepResult(counter, "PASSED", 0, "")
        }
      else
        event.asInstanceOf[TestFailed].recordedEvents.collect {
          case a: InfoProvided =>
            counter += 1
            StepResult(counter, if (counter == 0) "FAILED" else "PASSED", 0, "")
        }
    )
    var resultResponse = Http(
      apiUrl + "/projects/" + projectId + "/executions/" + executionId + "/results"
    ).postData(result.toJson.compactPrint)
      .header("content-type", "application/json")
      .asString
      .body

  }

}
