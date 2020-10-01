package com.states.machine.rest

import akka.actor.Props
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.states.machine.controler.MatrixActor
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.states.machine.model.Items.{ErrorDetail, FlowState, GraphState}

class MatrixServiceTest  extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with MatrixService
  with BeforeAndAfterAll  {

  override lazy val matrixManager = system.actorOf(Props[MatrixActor])

  override def afterAll: Unit = {
    system.terminate()
  }

  "Service" should "get Flow" in {
    Get(s"/matrix") ~> matrixRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[FlowState] shouldBe FlowState("Init", List(GraphState("Init", List("Pending", "Finished")), GraphState("Finished", List("Closed")), GraphState("Pending", List("Closed"))))
    }
  }

  "Service" should "add Flow" in {
    val flow = FlowState("Init", List(GraphState("Init", List("Pending", "Finished")), GraphState("Finished", List("Closed")), GraphState("Pending", List("Closed"))))
    Post(s"/matrix", flow) ~> matrixRoute ~> check {
      status shouldBe OK
      //contentType shouldBe `application/json`
      responseAs[String] shouldBe "Success add flow"
    }
  }

  "Service" should "wrong  Flow" in {
    val flow = FlowState("Init1", List(GraphState("Init", List("Pending", "Finished")), GraphState("Finished", List("Closed")), GraphState("Pending", List("Closed"))))
    Post(s"/matrix", flow) ~> matrixRoute ~> check {
      status shouldBe InternalServerError
      contentType shouldBe `application/json`
      responseAs[ErrorDetail] shouldBe ErrorDetail(500, "java.lang.Exception: Wrong State Format", Some("Wrong State Format"), Some("Wrong State Format"))
    }
  }

}
