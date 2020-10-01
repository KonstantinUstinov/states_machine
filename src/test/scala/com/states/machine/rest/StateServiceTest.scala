package com.states.machine.rest

import akka.actor.Props
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.states.machine.controler.{MatrixActor, StateActor}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.states.machine.model.Items.{ChangeStateResponse, ErrorDetail, FlowState, GraphState, InfoDetail, NewState}

class StateServiceTest extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with StateService
  with BeforeAndAfterAll {

  override lazy val matrixActor = system.actorOf(Props[MatrixActor])
  override lazy val stateActor = system.actorOf(Props[StateActor])

  override def afterAll: Unit = {
    system.terminate()
  }

  "Service" should "add SFM" in {
    Post(s"/state/add") ~> stateRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[InfoDetail] shouldBe InfoDetail(1, "Тew FSM added successfully")
    }
  }

  "Service" should "update SFM" in {
    Put(s"/state/change/1", NewState("Pending"))~> stateRoute ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[ChangeStateResponse] shouldBe ChangeStateResponse(1, "Pending", "Init")
    }
  }

}
